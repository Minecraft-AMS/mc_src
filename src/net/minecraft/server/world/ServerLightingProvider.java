/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerLightingProvider
extends LightingProvider
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TaskExecutor<Runnable> processor;
    private final ObjectList<Pair<class_3901, Runnable>> pendingTasks = new ObjectArrayList();
    private final ThreadedAnvilChunkStorage chunkStorage;
    private final MessageListener<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> executor;
    private volatile int taskBatchSize = 5;
    private final AtomicBoolean field_18812 = new AtomicBoolean();

    public ServerLightingProvider(ChunkProvider chunkProvider, ThreadedAnvilChunkStorage chunkStorage, boolean bl, TaskExecutor<Runnable> processor, MessageListener<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> executor) {
        super(chunkProvider, true, bl);
        this.chunkStorage = chunkStorage;
        this.executor = executor;
        this.processor = processor;
    }

    @Override
    public void close() {
    }

    @Override
    public int doLightUpdates(int maxUpdateCount, boolean doSkylight, boolean skipEdgeLightPropagation) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }

    @Override
    public void addLightSource(BlockPos pos, int level) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }

    @Override
    public void checkBlock(BlockPos pos) {
        BlockPos blockPos = pos.toImmutable();
        this.enqueue(pos.getX() >> 4, pos.getZ() >> 4, class_3901.field_17262, Util.debugRunnable(() -> super.checkBlock(blockPos), () -> "checkBlock " + blockPos));
    }

    protected void method_20386(ChunkPos chunkPos) {
        this.enqueue(chunkPos.x, chunkPos.z, () -> 0, class_3901.field_17261, Util.debugRunnable(() -> {
            int i;
            super.method_20601(chunkPos, false);
            super.setLightEnabled(chunkPos, false);
            for (i = -1; i < 17; ++i) {
                super.queueData(LightType.BLOCK, ChunkSectionPos.from(chunkPos, i), null);
                super.queueData(LightType.SKY, ChunkSectionPos.from(chunkPos, i), null);
            }
            for (i = 0; i < 16; ++i) {
                super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), true);
            }
        }, () -> "updateChunkStatus " + chunkPos + " " + true));
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
        this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> 0, class_3901.field_17261, Util.debugRunnable(() -> super.updateSectionStatus(pos, status), () -> "updateSectionStatus " + pos + " " + status));
    }

    @Override
    public void setLightEnabled(ChunkPos pos, boolean lightEnabled) {
        this.enqueue(pos.x, pos.z, class_3901.field_17261, Util.debugRunnable(() -> super.setLightEnabled(pos, lightEnabled), () -> "enableLight " + pos + " " + lightEnabled));
    }

    @Override
    public void queueData(LightType lightType, ChunkSectionPos chunkSectionPos, @Nullable ChunkNibbleArray chunkNibbleArray) {
        this.enqueue(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), () -> 0, class_3901.field_17261, Util.debugRunnable(() -> super.queueData(lightType, chunkSectionPos, chunkNibbleArray), () -> "queueData " + chunkSectionPos));
    }

    private void enqueue(int x, int z, class_3901 stage, Runnable task) {
        this.enqueue(x, z, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(x, z)), stage, task);
    }

    private void enqueue(int x, int z, IntSupplier completedLevelSupplier, class_3901 stage, Runnable task) {
        this.executor.send(ChunkTaskPrioritySystem.createMessage(() -> {
            this.pendingTasks.add((Object)Pair.of((Object)((Object)stage), (Object)task));
            if (this.pendingTasks.size() >= this.taskBatchSize) {
                this.runTasks();
            }
        }, ChunkPos.toLong(x, z), completedLevelSupplier));
    }

    @Override
    public void method_20601(ChunkPos chunkPos, boolean bl) {
        this.enqueue(chunkPos.x, chunkPos.z, () -> 0, class_3901.field_17261, Util.debugRunnable(() -> super.method_20601(chunkPos, bl), () -> "retainData " + chunkPos));
    }

    public CompletableFuture<Chunk> light(Chunk chunk, boolean bl) {
        ChunkPos chunkPos = chunk.getPos();
        chunk.setLightOn(false);
        this.enqueue(chunkPos.x, chunkPos.z, class_3901.field_17261, Util.debugRunnable(() -> {
            ChunkSection[] chunkSections = chunk.getSectionArray();
            for (int i = 0; i < 16; ++i) {
                ChunkSection chunkSection = chunkSections[i];
                if (ChunkSection.isEmpty(chunkSection)) continue;
                super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), false);
            }
            super.setLightEnabled(chunkPos, true);
            if (!bl) {
                chunk.getLightSourcesStream().forEach(blockPos -> super.addLightSource((BlockPos)blockPos, chunk.getLuminance((BlockPos)blockPos)));
            }
            this.chunkStorage.method_20441(chunkPos);
        }, () -> "lightChunk " + chunkPos + " " + bl));
        return CompletableFuture.supplyAsync(() -> {
            chunk.setLightOn(true);
            super.method_20601(chunkPos, false);
            return chunk;
        }, runnable -> this.enqueue(chunkPos.x, chunkPos.z, class_3901.field_17262, runnable));
    }

    public void tick() {
        if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.field_18812.compareAndSet(false, true)) {
            this.processor.send(() -> {
                this.runTasks();
                this.field_18812.set(false);
            });
        }
    }

    private void runTasks() {
        Pair pair;
        int j;
        int i = Math.min(this.pendingTasks.size(), this.taskBatchSize);
        ObjectListIterator objectListIterator = this.pendingTasks.iterator();
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() != class_3901.field_17261) continue;
            ((Runnable)pair.getSecond()).run();
        }
        objectListIterator.back(j);
        super.doLightUpdates(Integer.MAX_VALUE, true, true);
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() == class_3901.field_17262) {
                ((Runnable)pair.getSecond()).run();
            }
            objectListIterator.remove();
        }
    }

    public void setTaskBatchSize(int taskBatchSize) {
        this.taskBatchSize = taskBatchSize;
    }

    static enum class_3901 {
        field_17261,
        field_17262;

    }
}

