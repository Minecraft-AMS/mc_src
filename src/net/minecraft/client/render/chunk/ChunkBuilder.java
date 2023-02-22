/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.primitives.Doubles
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBufferRenderer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderWorker;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER)).build();
    private final int bufferCount;
    private final List<Thread> workerThreads = Lists.newArrayList();
    private final List<ChunkRenderWorker> workers = Lists.newArrayList();
    private final PriorityBlockingQueue<ChunkRenderTask> pendingChunks = Queues.newPriorityBlockingQueue();
    private final BlockingQueue<BlockBufferBuilderStorage> availableBuffers;
    private final BufferRenderer displayListBufferRenderer = new BufferRenderer();
    private final GlBufferRenderer vboBufferRenderer = new GlBufferRenderer();
    private final Queue<ChunkUploadTask> uploadQueue = Queues.newPriorityQueue();
    private final ChunkRenderWorker clientThreadWorker;
    private Vec3d cameraPosition = Vec3d.ZERO;

    public ChunkBuilder(boolean bl) {
        int n;
        int m;
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 0xA00000 - 1);
        int j = Runtime.getRuntime().availableProcessors();
        int k = bl ? j : Math.min(j, 4);
        int l = Math.max(1, Math.min(k * 2, i));
        this.clientThreadWorker = new ChunkRenderWorker(this, new BlockBufferBuilderStorage());
        ArrayList list = Lists.newArrayListWithExpectedSize((int)l);
        try {
            for (m = 0; m < l; ++m) {
                list.add(new BlockBufferBuilderStorage());
            }
        }
        catch (OutOfMemoryError outOfMemoryError) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)list.size(), (Object)l);
            n = list.size() * 2 / 3;
            for (int o = 0; o < n; ++o) {
                list.remove(list.size() - 1);
            }
            System.gc();
        }
        this.bufferCount = list.size();
        this.availableBuffers = Queues.newArrayBlockingQueue((int)this.bufferCount);
        this.availableBuffers.addAll(list);
        m = Math.min(k, this.bufferCount);
        if (m > 1) {
            for (n = 0; n < m; ++n) {
                ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
                Thread thread = THREAD_FACTORY.newThread(chunkRenderWorker);
                thread.start();
                this.workers.add(chunkRenderWorker);
                this.workerThreads.add(thread);
            }
        }
    }

    public String getDebugString() {
        if (this.workerThreads.isEmpty()) {
            return String.format("pC: %03d, single-threaded", this.pendingChunks.size());
        }
        return String.format("pC: %03d, pU: %02d, aB: %02d", this.pendingChunks.size(), this.uploadQueue.size(), this.availableBuffers.size());
    }

    public void setCameraPosition(Vec3d cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean runTasksSync(long endTime) {
        boolean bl2;
        boolean bl = false;
        do {
            ChunkRenderTask chunkRenderTask;
            bl2 = false;
            if (this.workerThreads.isEmpty() && (chunkRenderTask = this.pendingChunks.poll()) != null) {
                try {
                    this.clientThreadWorker.runTask(chunkRenderTask);
                    bl2 = true;
                }
                catch (InterruptedException interruptedException) {
                    LOGGER.warn("Skipped task due to interrupt");
                }
            }
            int i = 0;
            Queue<ChunkUploadTask> queue = this.uploadQueue;
            synchronized (queue) {
                ChunkUploadTask chunkUploadTask;
                while (i < 10 && (chunkUploadTask = this.uploadQueue.poll()) != null) {
                    if (chunkUploadTask.task.isDone()) continue;
                    chunkUploadTask.task.run();
                    bl2 = true;
                    bl = true;
                    ++i;
                }
            }
        } while (endTime != 0L && bl2 && endTime >= Util.getMeasuringTimeNano());
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean rebuild(ChunkRenderer renderer) {
        renderer.getLock().lock();
        try {
            ChunkRenderTask chunkRenderTask = renderer.startRebuild();
            chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
            boolean bl = this.pendingChunks.offer(chunkRenderTask);
            if (!bl) {
                chunkRenderTask.cancel();
            }
            boolean bl2 = bl;
            return bl2;
        }
        finally {
            renderer.getLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean rebuildSync(ChunkRenderer renderer) {
        renderer.getLock().lock();
        try {
            ChunkRenderTask chunkRenderTask = renderer.startRebuild();
            try {
                this.clientThreadWorker.runTask(chunkRenderTask);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            boolean bl = true;
            return bl;
        }
        finally {
            renderer.getLock().unlock();
        }
    }

    public void reset() {
        this.clear();
        ArrayList list = Lists.newArrayList();
        while (list.size() != this.bufferCount) {
            this.runTasksSync(Long.MAX_VALUE);
            try {
                list.add(this.getNextAvailableBuffer());
            }
            catch (InterruptedException interruptedException) {}
        }
        this.availableBuffers.addAll(list);
    }

    public void addAvailableBuffer(BlockBufferBuilderStorage blockBufferBuilderStorage) {
        this.availableBuffers.add(blockBufferBuilderStorage);
    }

    public BlockBufferBuilderStorage getNextAvailableBuffer() throws InterruptedException {
        return this.availableBuffers.take();
    }

    public ChunkRenderTask getNextChunkRenderDataTask() throws InterruptedException {
        return this.pendingChunks.take();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean resortTransparency(ChunkRenderer renderer) {
        renderer.getLock().lock();
        try {
            ChunkRenderTask chunkRenderTask = renderer.startResortTransparency();
            if (chunkRenderTask != null) {
                chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
                boolean bl = this.pendingChunks.offer(chunkRenderTask);
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            renderer.getLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ListenableFuture<Void> upload(RenderLayer layer, BufferBuilder bufferBuilder, ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, double d) {
        if (MinecraftClient.getInstance().isOnThread()) {
            if (GLX.useVbo()) {
                this.uploadVbo(bufferBuilder, chunkRenderer.getGlBuffer(layer.ordinal()));
            } else {
                this.uploadDisplayList(bufferBuilder, ((DisplayListChunkRenderer)chunkRenderer).method_3639(layer, chunkRenderData));
            }
            bufferBuilder.setOffset(0.0, 0.0, 0.0);
            return Futures.immediateFuture(null);
        }
        ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(() -> this.upload(layer, bufferBuilder, chunkRenderer, chunkRenderData, d), null);
        Queue<ChunkUploadTask> queue = this.uploadQueue;
        synchronized (queue) {
            this.uploadQueue.add(new ChunkUploadTask((ListenableFutureTask<Void>)listenableFutureTask, d));
        }
        return listenableFutureTask;
    }

    private void uploadDisplayList(BufferBuilder bufferBuilder, int index) {
        GlStateManager.newList(index, 4864);
        this.displayListBufferRenderer.draw(bufferBuilder);
        GlStateManager.endList();
    }

    private void uploadVbo(BufferBuilder bufferBuilder, VertexBuffer glBuffer) {
        this.vboBufferRenderer.setGlBuffer(glBuffer);
        this.vboBufferRenderer.draw(bufferBuilder);
    }

    public void clear() {
        while (!this.pendingChunks.isEmpty()) {
            ChunkRenderTask chunkRenderTask = this.pendingChunks.poll();
            if (chunkRenderTask == null) continue;
            chunkRenderTask.cancel();
        }
    }

    public boolean isEmpty() {
        return this.pendingChunks.isEmpty() && this.uploadQueue.isEmpty();
    }

    public void stop() {
        this.clear();
        for (ChunkRenderWorker chunkRenderWorker : this.workers) {
            chunkRenderWorker.stop();
        }
        for (Thread thread : this.workerThreads) {
            try {
                thread.interrupt();
                thread.join();
            }
            catch (InterruptedException interruptedException) {
                LOGGER.warn("Interrupted whilst waiting for worker to die", (Throwable)interruptedException);
            }
        }
        this.availableBuffers.clear();
    }

    @Environment(value=EnvType.CLIENT)
    class ChunkUploadTask
    implements Comparable<ChunkUploadTask> {
        private final ListenableFutureTask<Void> task;
        private final double priority;

        public ChunkUploadTask(ListenableFutureTask<Void> priority, double d) {
            this.task = priority;
            this.priority = d;
        }

        @Override
        public int compareTo(ChunkUploadTask chunkUploadTask) {
            return Doubles.compare((double)this.priority, (double)chunkUploadTask.priority);
        }

        @Override
        public /* synthetic */ int compareTo(Object object) {
            return this.compareTo((ChunkUploadTask)object);
        }
    }
}

