/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.common.primitives.Doubles
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_32831 = 4;
    private static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
    private static final int field_35300 = 2;
    private final PriorityBlockingQueue<BuiltChunk.Task> prioritizedTaskQueue = Queues.newPriorityBlockingQueue();
    private final Queue<BuiltChunk.Task> taskQueue = Queues.newLinkedBlockingDeque();
    private int processablePrioritizedTaskCount = 2;
    private final Queue<BlockBufferBuilderStorage> threadBuffers;
    private final Queue<Runnable> uploadQueue = Queues.newConcurrentLinkedQueue();
    private volatile int queuedTaskCount;
    private volatile int bufferCount;
    final BlockBufferBuilderStorage buffers;
    private final TaskExecutor<Runnable> mailbox;
    private final Executor executor;
    ClientWorld world;
    final WorldRenderer worldRenderer;
    private Vec3d cameraPosition = Vec3d.ZERO;

    public ChunkBuilder(ClientWorld world, WorldRenderer worldRenderer, Executor executor, boolean is64Bits, BlockBufferBuilderStorage buffers) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderLayer.getBlockLayers().stream().mapToInt(RenderLayer::getExpectedBufferSize).sum() * 4) - 1);
        int j = Runtime.getRuntime().availableProcessors();
        int k = is64Bits ? j : Math.min(j, 4);
        int l = Math.max(1, Math.min(k, i));
        this.buffers = buffers;
        ArrayList list = Lists.newArrayListWithExpectedSize((int)l);
        try {
            for (int m = 0; m < l; ++m) {
                list.add(new BlockBufferBuilderStorage());
            }
        }
        catch (OutOfMemoryError outOfMemoryError) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)list.size(), (Object)l);
            int n = Math.min(list.size() * 2 / 3, list.size() - 1);
            for (int o = 0; o < n; ++o) {
                list.remove(list.size() - 1);
            }
            System.gc();
        }
        this.threadBuffers = Queues.newArrayDeque((Iterable)list);
        this.bufferCount = this.threadBuffers.size();
        this.executor = executor;
        this.mailbox = TaskExecutor.create(executor, "Chunk Renderer");
        this.mailbox.send(this::scheduleRunTasks);
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }

    private void scheduleRunTasks() {
        if (this.threadBuffers.isEmpty()) {
            return;
        }
        BuiltChunk.Task task = this.pollTask();
        if (task == null) {
            return;
        }
        BlockBufferBuilderStorage blockBufferBuilderStorage = this.threadBuffers.poll();
        this.queuedTaskCount = this.prioritizedTaskQueue.size() + this.taskQueue.size();
        this.bufferCount = this.threadBuffers.size();
        ((CompletableFuture)CompletableFuture.supplyAsync(Util.debugSupplier(task.getName(), () -> task.run(blockBufferBuilderStorage)), this.executor).thenCompose(future -> future)).whenComplete((result, throwable) -> {
            if (throwable != null) {
                CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
                MinecraftClient.getInstance().setCrashReportSupplier(() -> MinecraftClient.getInstance().addDetailsToCrashReport(crashReport));
                return;
            }
            this.mailbox.send(() -> {
                if (result == Result.SUCCESSFUL) {
                    blockBufferBuilderStorage.clear();
                } else {
                    blockBufferBuilderStorage.reset();
                }
                this.threadBuffers.add(blockBufferBuilderStorage);
                this.bufferCount = this.threadBuffers.size();
                this.scheduleRunTasks();
            });
        });
    }

    @Nullable
    private BuiltChunk.Task pollTask() {
        BuiltChunk.Task task;
        if (this.processablePrioritizedTaskCount <= 0 && (task = this.taskQueue.poll()) != null) {
            this.processablePrioritizedTaskCount = 2;
            return task;
        }
        task = this.prioritizedTaskQueue.poll();
        if (task != null) {
            --this.processablePrioritizedTaskCount;
            return task;
        }
        this.processablePrioritizedTaskCount = 2;
        return this.taskQueue.poll();
    }

    public String getDebugString() {
        return String.format("pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.bufferCount);
    }

    public int getToBatchCount() {
        return this.queuedTaskCount;
    }

    public int getChunksToUpload() {
        return this.uploadQueue.size();
    }

    public int getFreeBufferCount() {
        return this.bufferCount;
    }

    public void setCameraPosition(Vec3d cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    public void upload() {
        Runnable runnable;
        while ((runnable = this.uploadQueue.poll()) != null) {
            runnable.run();
        }
    }

    public void rebuild(BuiltChunk chunk, ChunkRendererRegionBuilder builder) {
        chunk.rebuild(builder);
    }

    public void reset() {
        this.clear();
    }

    public void send(BuiltChunk.Task task) {
        this.mailbox.send(() -> {
            if (task.prioritized) {
                this.prioritizedTaskQueue.offer(task);
            } else {
                this.taskQueue.offer(task);
            }
            this.queuedTaskCount = this.prioritizedTaskQueue.size() + this.taskQueue.size();
            this.scheduleRunTasks();
        });
    }

    public CompletableFuture<Void> scheduleUpload(BufferBuilder buffer, VertexBuffer glBuffer) {
        return CompletableFuture.runAsync(() -> {}, this.uploadQueue::add).thenCompose(void_ -> this.upload(buffer, glBuffer));
    }

    private CompletableFuture<Void> upload(BufferBuilder buffer, VertexBuffer glBuffer) {
        return glBuffer.submitUpload(buffer);
    }

    private void clear() {
        BuiltChunk.Task task;
        while (!this.prioritizedTaskQueue.isEmpty()) {
            task = this.prioritizedTaskQueue.poll();
            if (task == null) continue;
            task.cancel();
        }
        while (!this.taskQueue.isEmpty()) {
            task = this.taskQueue.poll();
            if (task == null) continue;
            task.cancel();
        }
        this.queuedTaskCount = 0;
    }

    public boolean isEmpty() {
        return this.queuedTaskCount == 0 && this.uploadQueue.isEmpty();
    }

    public void stop() {
        this.clear();
        this.mailbox.close();
        this.threadBuffers.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public class BuiltChunk {
        public static final int field_32832 = 16;
        public final int index;
        public final AtomicReference<ChunkData> data = new AtomicReference<ChunkData>(ChunkData.EMPTY);
        final AtomicInteger field_36374 = new AtomicInteger(0);
        @Nullable
        private RebuildTask rebuildTask;
        @Nullable
        private SortTask sortTask;
        private final Set<BlockEntity> blockEntities = Sets.newHashSet();
        private final Map<RenderLayer, VertexBuffer> buffers = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new VertexBuffer()));
        private Box boundingBox;
        private boolean needsRebuild = true;
        final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
        private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], mutables -> {
            for (int i = 0; i < ((BlockPos.Mutable[])mutables).length; ++i) {
                mutables[i] = new BlockPos.Mutable();
            }
        });
        private boolean needsImportantRebuild;

        public BuiltChunk(int index, int originX, int originY, int originZ) {
            this.index = index;
            this.setOrigin(originX, originY, originZ);
        }

        private boolean isChunkNonEmpty(BlockPos pos) {
            return ChunkBuilder.this.world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false) != null;
        }

        public boolean shouldBuild() {
            int i = 24;
            if (this.getSquaredCameraDistance() > 576.0) {
                return this.isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()]);
            }
            return true;
        }

        public Box getBoundingBox() {
            return this.boundingBox;
        }

        public VertexBuffer getBuffer(RenderLayer layer) {
            return this.buffers.get(layer);
        }

        public void setOrigin(int x, int y, int z) {
            this.clear();
            this.origin.set(x, y, z);
            this.boundingBox = new Box(x, y, z, x + 16, y + 16, z + 16);
            for (Direction direction : Direction.values()) {
                this.neighborPositions[direction.ordinal()].set(this.origin).move(direction, 16);
            }
        }

        protected double getSquaredCameraDistance() {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            double d = this.boundingBox.minX + 8.0 - camera.getPos().x;
            double e = this.boundingBox.minY + 8.0 - camera.getPos().y;
            double f = this.boundingBox.minZ + 8.0 - camera.getPos().z;
            return d * d + e * e + f * f;
        }

        void beginBufferBuilding(BufferBuilder buffer) {
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        }

        public ChunkData getData() {
            return this.data.get();
        }

        private void clear() {
            this.cancel();
            this.data.set(ChunkData.EMPTY);
            this.needsRebuild = true;
        }

        public void delete() {
            this.clear();
            this.buffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public void scheduleRebuild(boolean important) {
            boolean bl = this.needsRebuild;
            this.needsRebuild = true;
            this.needsImportantRebuild = important | (bl && this.needsImportantRebuild);
        }

        public void cancelRebuild() {
            this.needsRebuild = false;
            this.needsImportantRebuild = false;
        }

        public boolean needsRebuild() {
            return this.needsRebuild;
        }

        public boolean needsImportantRebuild() {
            return this.needsRebuild && this.needsImportantRebuild;
        }

        public BlockPos getNeighborPosition(Direction direction) {
            return this.neighborPositions[direction.ordinal()];
        }

        public boolean scheduleSort(RenderLayer layer, ChunkBuilder chunkRenderer) {
            ChunkData chunkData = this.getData();
            if (this.sortTask != null) {
                this.sortTask.cancel();
            }
            if (!chunkData.initializedLayers.contains(layer)) {
                return false;
            }
            this.sortTask = new SortTask(this.getSquaredCameraDistance(), chunkData);
            chunkRenderer.send(this.sortTask);
            return true;
        }

        protected boolean cancel() {
            boolean bl = false;
            if (this.rebuildTask != null) {
                this.rebuildTask.cancel();
                this.rebuildTask = null;
                bl = true;
            }
            if (this.sortTask != null) {
                this.sortTask.cancel();
                this.sortTask = null;
            }
            return bl;
        }

        public Task createRebuildTask(ChunkRendererRegionBuilder builder) {
            boolean bl2;
            boolean bl = this.cancel();
            BlockPos blockPos = this.origin.toImmutable();
            boolean i = true;
            ChunkRendererRegion chunkRendererRegion = builder.build(ChunkBuilder.this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
            boolean bl3 = bl2 = this.data.get() == ChunkData.EMPTY;
            if (bl2 && bl) {
                this.field_36374.incrementAndGet();
            }
            this.rebuildTask = new RebuildTask(this.getSquaredCameraDistance(), chunkRendererRegion, !bl2 || this.field_36374.get() > 2);
            return this.rebuildTask;
        }

        public void scheduleRebuild(ChunkBuilder chunkRenderer, ChunkRendererRegionBuilder builder) {
            Task task = this.createRebuildTask(builder);
            chunkRenderer.send(task);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void setNoCullingBlockEntities(Set<BlockEntity> noCullingBlockEntities) {
            HashSet set2;
            HashSet set = Sets.newHashSet(noCullingBlockEntities);
            Set<BlockEntity> set3 = this.blockEntities;
            synchronized (set3) {
                set2 = Sets.newHashSet(this.blockEntities);
                set.removeAll(this.blockEntities);
                set2.removeAll(noCullingBlockEntities);
                this.blockEntities.clear();
                this.blockEntities.addAll(noCullingBlockEntities);
            }
            ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set2, set);
        }

        public void rebuild(ChunkRendererRegionBuilder builder) {
            Task task = this.createRebuildTask(builder);
            task.run(ChunkBuilder.this.buffers);
        }

        @Environment(value=EnvType.CLIENT)
        class SortTask
        extends Task {
            private final ChunkData data;

            public SortTask(double distance, ChunkData data) {
                super(distance, true);
                this.data = data;
            }

            @Override
            protected String getName() {
                return "rend_chk_sort";
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferBuilderStorage buffers) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!BuiltChunk.this.shouldBuild()) {
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
                float f = (float)vec3d.x;
                float g = (float)vec3d.y;
                float h = (float)vec3d.z;
                BufferBuilder.State state = this.data.bufferState;
                if (state == null || !this.data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                BufferBuilder bufferBuilder = buffers.get(RenderLayer.getTranslucent());
                BuiltChunk.this.beginBufferBuilding(bufferBuilder);
                bufferBuilder.restoreState(state);
                bufferBuilder.sortFrom(f - (float)BuiltChunk.this.origin.getX(), g - (float)BuiltChunk.this.origin.getY(), h - (float)BuiltChunk.this.origin.getZ());
                this.data.bufferState = bufferBuilder.popState();
                bufferBuilder.end();
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                CompletionStage completableFuture = ChunkBuilder.this.scheduleUpload(buffers.get(RenderLayer.getTranslucent()), BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())).thenApply(void_ -> Result.CANCELLED);
                return ((CompletableFuture)completableFuture).handle((result, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        CrashReport crashReport = CrashReport.create(throwable, "Rendering chunk");
                        MinecraftClient.getInstance().setCrashReportSupplier(() -> crashReport);
                    }
                    return this.cancelled.get() ? Result.CANCELLED : Result.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.cancelled.set(true);
            }
        }

        @Environment(value=EnvType.CLIENT)
        abstract class Task
        implements Comparable<Task> {
            protected final double distance;
            protected final AtomicBoolean cancelled = new AtomicBoolean(false);
            protected final boolean prioritized;

            public Task(double distance, boolean prioritized) {
                this.distance = distance;
                this.prioritized = prioritized;
            }

            public abstract CompletableFuture<Result> run(BlockBufferBuilderStorage var1);

            public abstract void cancel();

            protected abstract String getName();

            @Override
            public int compareTo(Task task) {
                return Doubles.compare((double)this.distance, (double)task.distance);
            }

            @Override
            public /* synthetic */ int compareTo(Object other) {
                return this.compareTo((Task)other);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class RebuildTask
        extends Task {
            @Nullable
            protected ChunkRendererRegion region;

            public RebuildTask(@Nullable double distance, ChunkRendererRegion region, boolean prioritized) {
                super(distance, prioritized);
                this.region = region;
            }

            @Override
            protected String getName() {
                return "rend_chk_rebuild";
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferBuilderStorage buffers) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!BuiltChunk.this.shouldBuild()) {
                    this.region = null;
                    BuiltChunk.this.scheduleRebuild(false);
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
                float f = (float)vec3d.x;
                float g = (float)vec3d.y;
                float h = (float)vec3d.z;
                ChunkData chunkData = new ChunkData();
                Set<BlockEntity> set = this.render(f, g, h, chunkData, buffers);
                BuiltChunk.this.setNoCullingBlockEntities(set);
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                ArrayList list = Lists.newArrayList();
                chunkData.initializedLayers.forEach(renderLayer -> list.add(ChunkBuilder.this.scheduleUpload(buffers.get((RenderLayer)renderLayer), BuiltChunk.this.getBuffer((RenderLayer)renderLayer))));
                return Util.combine(list).handle((results, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        CrashReport crashReport = CrashReport.create(throwable, "Rendering chunk");
                        MinecraftClient.getInstance().setCrashReportSupplier(() -> crashReport);
                    }
                    if (this.cancelled.get()) {
                        return Result.CANCELLED;
                    }
                    BuiltChunk.this.data.set(chunkData);
                    BuiltChunk.this.field_36374.set(0);
                    ChunkBuilder.this.worldRenderer.addBuiltChunk(BuiltChunk.this);
                    return Result.SUCCESSFUL;
                });
            }

            private Set<BlockEntity> render(float cameraX, float cameraY, float cameraZ, ChunkData data, BlockBufferBuilderStorage buffers) {
                boolean i = true;
                BlockPos blockPos = BuiltChunk.this.origin.toImmutable();
                BlockPos blockPos2 = blockPos.add(15, 15, 15);
                ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
                HashSet set = Sets.newHashSet();
                ChunkRendererRegion chunkRendererRegion = this.region;
                this.region = null;
                MatrixStack matrixStack = new MatrixStack();
                if (chunkRendererRegion != null) {
                    BlockModelRenderer.enableBrightnessCache();
                    Random random = new Random();
                    BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                    for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
                        BufferBuilder bufferBuilder;
                        RenderLayer renderLayer;
                        BlockState blockState2;
                        FluidState fluidState;
                        BlockEntity blockEntity;
                        BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
                        if (blockState.isOpaqueFullCube(chunkRendererRegion, blockPos3)) {
                            chunkOcclusionDataBuilder.markClosed(blockPos3);
                        }
                        if (blockState.hasBlockEntity() && (blockEntity = chunkRendererRegion.getBlockEntity(blockPos3)) != null) {
                            this.addBlockEntity(data, set, blockEntity);
                        }
                        if (!(fluidState = (blockState2 = chunkRendererRegion.getBlockState(blockPos3)).getFluidState()).isEmpty()) {
                            renderLayer = RenderLayers.getFluidLayer(fluidState);
                            bufferBuilder = buffers.get(renderLayer);
                            if (data.initializedLayers.add(renderLayer)) {
                                BuiltChunk.this.beginBufferBuilding(bufferBuilder);
                            }
                            if (blockRenderManager.renderFluid(blockPos3, chunkRendererRegion, bufferBuilder, blockState2, fluidState)) {
                                data.empty = false;
                                data.nonEmptyLayers.add(renderLayer);
                            }
                        }
                        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) continue;
                        renderLayer = RenderLayers.getBlockLayer(blockState);
                        bufferBuilder = buffers.get(renderLayer);
                        if (data.initializedLayers.add(renderLayer)) {
                            BuiltChunk.this.beginBufferBuilding(bufferBuilder);
                        }
                        matrixStack.push();
                        matrixStack.translate(blockPos3.getX() & 0xF, blockPos3.getY() & 0xF, blockPos3.getZ() & 0xF);
                        if (blockRenderManager.renderBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilder, true, random)) {
                            data.empty = false;
                            data.nonEmptyLayers.add(renderLayer);
                        }
                        matrixStack.pop();
                    }
                    if (data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
                        BufferBuilder bufferBuilder2 = buffers.get(RenderLayer.getTranslucent());
                        bufferBuilder2.sortFrom(cameraX - (float)blockPos.getX(), cameraY - (float)blockPos.getY(), cameraZ - (float)blockPos.getZ());
                        data.bufferState = bufferBuilder2.popState();
                    }
                    data.initializedLayers.stream().map(buffers::get).forEach(BufferBuilder::end);
                    BlockModelRenderer.disableBrightnessCache();
                }
                data.occlusionGraph = chunkOcclusionDataBuilder.build();
                return set;
            }

            private <E extends BlockEntity> void addBlockEntity(ChunkData data, Set<BlockEntity> blockEntities, E blockEntity) {
                BlockEntityRenderer<E> blockEntityRenderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(blockEntity);
                if (blockEntityRenderer != null) {
                    data.blockEntities.add(blockEntity);
                    if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
                        blockEntities.add(blockEntity);
                    }
                }
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.cancelled.compareAndSet(false, true)) {
                    BuiltChunk.this.scheduleRebuild(false);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Result
    extends Enum<Result> {
        public static final /* enum */ Result SUCCESSFUL = new Result();
        public static final /* enum */ Result CANCELLED = new Result();
        private static final /* synthetic */ Result[] field_21440;

        public static Result[] values() {
            return (Result[])field_21440.clone();
        }

        public static Result valueOf(String string) {
            return Enum.valueOf(Result.class, string);
        }

        private static /* synthetic */ Result[] method_36923() {
            return new Result[]{SUCCESSFUL, CANCELLED};
        }

        static {
            field_21440 = Result.method_36923();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChunkData {
        public static final ChunkData EMPTY = new ChunkData(){

            @Override
            public boolean isVisibleThrough(Direction from, Direction to) {
                return false;
            }
        };
        final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet();
        final Set<RenderLayer> initializedLayers = new ObjectArraySet();
        boolean empty = true;
        final List<BlockEntity> blockEntities = Lists.newArrayList();
        ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
        @Nullable
        BufferBuilder.State bufferState;

        public boolean isEmpty() {
            return this.empty;
        }

        public boolean isEmpty(RenderLayer layer) {
            return !this.nonEmptyLayers.contains(layer);
        }

        public List<BlockEntity> getBlockEntities() {
            return this.blockEntities;
        }

        public boolean isVisibleThrough(Direction from, Direction to) {
            return this.occlusionGraph.isVisibleThrough(from, to);
        }
    }
}

