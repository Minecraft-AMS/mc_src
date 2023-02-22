/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk.light;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LevelPropagator;
import net.minecraft.world.chunk.light.LightStorage;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkLightProvider<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>>
extends LevelPropagator
implements ChunkLightingView {
    private static final Direction[] DIRECTIONS = Direction.values();
    protected final ChunkProvider chunkProvider;
    protected final LightType type;
    protected final S lightStorage;
    private boolean field_15794;
    protected final BlockPos.Mutable field_19284 = new BlockPos.Mutable();
    private final long[] field_17397 = new long[2];
    private final BlockView[] field_17398 = new BlockView[2];

    public ChunkLightProvider(ChunkProvider chunkProvider, LightType type, S lightStorage) {
        super(16, 256, 8192);
        this.chunkProvider = chunkProvider;
        this.type = type;
        this.lightStorage = lightStorage;
        this.method_17530();
    }

    @Override
    protected void resetLevel(long id) {
        ((LightStorage)this.lightStorage).updateAll();
        if (((LightStorage)this.lightStorage).hasLight(ChunkSectionPos.fromGlobalPos(id))) {
            super.resetLevel(id);
        }
    }

    @Nullable
    private BlockView getChunk(int chunkX, int chunkZ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
        for (int i = 0; i < 2; ++i) {
            if (l != this.field_17397[i]) continue;
            return this.field_17398[i];
        }
        BlockView blockView = this.chunkProvider.getChunk(chunkX, chunkZ);
        for (int j = 1; j > 0; --j) {
            this.field_17397[j] = this.field_17397[j - 1];
            this.field_17398[j] = this.field_17398[j - 1];
        }
        this.field_17397[0] = l;
        this.field_17398[0] = blockView;
        return blockView;
    }

    private void method_17530() {
        Arrays.fill(this.field_17397, ChunkPos.MARKER);
        Arrays.fill(this.field_17398, null);
    }

    protected BlockState method_20479(long l, @Nullable AtomicInteger atomicInteger) {
        boolean bl;
        int j;
        if (l == Long.MAX_VALUE) {
            if (atomicInteger != null) {
                atomicInteger.set(0);
            }
            return Blocks.AIR.getDefaultState();
        }
        int i = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(l));
        BlockView blockView = this.getChunk(i, j = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(l)));
        if (blockView == null) {
            if (atomicInteger != null) {
                atomicInteger.set(16);
            }
            return Blocks.BEDROCK.getDefaultState();
        }
        this.field_19284.set(l);
        BlockState blockState = blockView.getBlockState(this.field_19284);
        boolean bl2 = bl = blockState.isOpaque() && blockState.hasSidedTransparency();
        if (atomicInteger != null) {
            atomicInteger.set(blockState.getOpacity(this.chunkProvider.getWorld(), this.field_19284));
        }
        return bl ? blockState : Blocks.AIR.getDefaultState();
    }

    protected VoxelShape method_20710(BlockState blockState, long l, Direction direction) {
        return blockState.isOpaque() ? blockState.getCullingFace(this.chunkProvider.getWorld(), this.field_19284.set(l), direction) : VoxelShapes.empty();
    }

    public static int method_20049(BlockView blockView, BlockState blockState, BlockPos blockPos, BlockState blockState2, BlockPos blockPos2, Direction direction, int i) {
        VoxelShape voxelShape2;
        boolean bl2;
        boolean bl = blockState.isOpaque() && blockState.hasSidedTransparency();
        boolean bl3 = bl2 = blockState2.isOpaque() && blockState2.hasSidedTransparency();
        if (!bl && !bl2) {
            return i;
        }
        VoxelShape voxelShape = bl ? blockState.getCullingShape(blockView, blockPos) : VoxelShapes.empty();
        VoxelShape voxelShape3 = voxelShape2 = bl2 ? blockState2.getCullingShape(blockView, blockPos2) : VoxelShapes.empty();
        if (VoxelShapes.method_1080(voxelShape, voxelShape2, direction)) {
            return 16;
        }
        return i;
    }

    @Override
    protected boolean isMarker(long id) {
        return id == Long.MAX_VALUE;
    }

    @Override
    protected int recalculateLevel(long id, long excludedId, int maxLevel) {
        return 0;
    }

    @Override
    protected int getLevel(long id) {
        if (id == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - ((LightStorage)this.lightStorage).get(id);
    }

    protected int getCurrentLevelFromArray(ChunkNibbleArray array, long blockPos) {
        return 15 - array.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    @Override
    protected void setLevel(long id, int level) {
        ((LightStorage)this.lightStorage).set(id, Math.min(15, 15 - level));
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        return 0;
    }

    public boolean hasUpdates() {
        return this.hasPendingUpdates() || ((LevelPropagator)this.lightStorage).hasPendingUpdates() || ((LightStorage)this.lightStorage).hasLightUpdates();
    }

    public int doLightUpdates(int maxSteps, boolean doSkylight, boolean skipEdgeLightPropagation) {
        if (!this.field_15794) {
            if (((LevelPropagator)this.lightStorage).hasPendingUpdates() && (maxSteps = ((LevelPropagator)this.lightStorage).applyPendingUpdates(maxSteps)) == 0) {
                return maxSteps;
            }
            ((LightStorage)this.lightStorage).updateLightArrays(this, doSkylight, skipEdgeLightPropagation);
        }
        this.field_15794 = true;
        if (this.hasPendingUpdates()) {
            maxSteps = this.applyPendingUpdates(maxSteps);
            this.method_17530();
            if (maxSteps == 0) {
                return maxSteps;
            }
        }
        this.field_15794 = false;
        ((LightStorage)this.lightStorage).notifyChunkProvider();
        return maxSteps;
    }

    protected void setLightArray(long pos, @Nullable ChunkNibbleArray lightArray) {
        ((LightStorage)this.lightStorage).setLightArray(pos, lightArray);
    }

    @Override
    @Nullable
    public ChunkNibbleArray getLightArray(ChunkSectionPos pos) {
        return ((LightStorage)this.lightStorage).method_20533(pos.asLong());
    }

    @Override
    public int getLightLevel(BlockPos blockPos) {
        return ((LightStorage)this.lightStorage).getLight(blockPos.asLong());
    }

    @Environment(value=EnvType.CLIENT)
    public String method_15520(long l) {
        return "" + ((LightStorage)this.lightStorage).getLevel(l);
    }

    public void checkBlock(BlockPos pos) {
        long l = pos.asLong();
        this.resetLevel(l);
        for (Direction direction : DIRECTIONS) {
            this.resetLevel(BlockPos.offset(l, direction));
        }
    }

    public void method_15514(BlockPos blockPos, int i) {
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
        ((LightStorage)this.lightStorage).updateSectionStatus(pos.asLong(), status);
    }

    public void method_15512(ChunkPos chunkPos, boolean bl) {
        long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
        ((LightStorage)this.lightStorage).updateAll();
        ((LightStorage)this.lightStorage).method_15535(l, bl);
    }

    public void method_20599(ChunkPos chunkPos, boolean bl) {
        long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
        ((LightStorage)this.lightStorage).method_20600(l, bl);
    }
}

