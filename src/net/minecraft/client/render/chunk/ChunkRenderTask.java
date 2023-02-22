/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Doubles
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderTask
implements Comparable<ChunkRenderTask> {
    private final ChunkRenderer chunkRenderer;
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Runnable> completionActions = Lists.newArrayList();
    private final Mode mode;
    private final double squaredCameraDistance;
    @Nullable
    private ChunkRendererRegion region;
    private BlockBufferBuilderStorage bufferBuilder;
    private ChunkRenderData renderData;
    private Stage stage = Stage.PENDING;
    private boolean cancelled;

    public ChunkRenderTask(ChunkRenderer chunkRenderer, Mode mode, double squaredCameraDistance, @Nullable ChunkRendererRegion region) {
        this.chunkRenderer = chunkRenderer;
        this.mode = mode;
        this.squaredCameraDistance = squaredCameraDistance;
        this.region = region;
    }

    public Stage getStage() {
        return this.stage;
    }

    public ChunkRenderer getChunkRenderer() {
        return this.chunkRenderer;
    }

    @Nullable
    public ChunkRendererRegion takeRegion() {
        ChunkRendererRegion chunkRendererRegion = this.region;
        this.region = null;
        return chunkRendererRegion;
    }

    public ChunkRenderData getRenderData() {
        return this.renderData;
    }

    public void setRenderData(ChunkRenderData renderData) {
        this.renderData = renderData;
    }

    public BlockBufferBuilderStorage getBufferBuilders() {
        return this.bufferBuilder;
    }

    public void setBufferBuilders(BlockBufferBuilderStorage blockBufferBuilderStorage) {
        this.bufferBuilder = blockBufferBuilderStorage;
    }

    public void setStage(Stage stage) {
        this.lock.lock();
        try {
            this.stage = stage;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void cancel() {
        this.lock.lock();
        try {
            this.region = null;
            if (this.mode == Mode.REBUILD_CHUNK && this.stage != Stage.DONE) {
                this.chunkRenderer.scheduleRebuild(false);
            }
            this.cancelled = true;
            this.stage = Stage.DONE;
            for (Runnable runnable : this.completionActions) {
                runnable.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void addCompletionAction(Runnable action) {
        this.lock.lock();
        try {
            this.completionActions.add(action);
            if (this.cancelled) {
                action.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public ReentrantLock getLock() {
        return this.lock;
    }

    public Mode getMode() {
        return this.mode;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public int compareTo(ChunkRenderTask chunkRenderTask) {
        return Doubles.compare((double)this.squaredCameraDistance, (double)chunkRenderTask.squaredCameraDistance);
    }

    public double getSquaredCameraDistance() {
        return this.squaredCameraDistance;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((ChunkRenderTask)object);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Stage {
        PENDING,
        COMPILING,
        UPLOADING,
        DONE;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum Mode {
        REBUILD_CHUNK,
        RESORT_TRANSPARENCY;

    }
}

