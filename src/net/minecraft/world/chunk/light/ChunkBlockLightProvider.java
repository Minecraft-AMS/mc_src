/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.light;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.BlockLightStorage;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public final class ChunkBlockLightProvider
extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

    public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
        super(chunkProvider, LightType.BLOCK, new BlockLightStorage(chunkProvider));
    }

    private int getLightSourceLuminance(long blockPos) {
        int i = BlockPos.unpackLongX(blockPos);
        int j = BlockPos.unpackLongY(blockPos);
        int k = BlockPos.unpackLongZ(blockPos);
        BlockView blockView = this.chunkProvider.getChunk(i >> 4, k >> 4);
        if (blockView != null) {
            return blockView.getLuminance(this.mutablePos.set(i, j, k));
        }
        return 0;
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        VoxelShape voxelShape2;
        int k;
        int j;
        if (targetId == Long.MAX_VALUE) {
            return 15;
        }
        if (sourceId == Long.MAX_VALUE) {
            return level + 15 - this.getLightSourceLuminance(targetId);
        }
        if (level >= 15) {
            return level;
        }
        int i = Integer.signum(BlockPos.unpackLongX(targetId) - BlockPos.unpackLongX(sourceId));
        Direction direction = Direction.fromVector(i, j = Integer.signum(BlockPos.unpackLongY(targetId) - BlockPos.unpackLongY(sourceId)), k = Integer.signum(BlockPos.unpackLongZ(targetId) - BlockPos.unpackLongZ(sourceId)));
        if (direction == null) {
            return 15;
        }
        AtomicInteger atomicInteger = new AtomicInteger();
        BlockState blockState = this.method_20479(targetId, atomicInteger);
        if (atomicInteger.get() >= 15) {
            return 15;
        }
        BlockState blockState2 = this.method_20479(sourceId, null);
        VoxelShape voxelShape = this.method_20710(blockState2, sourceId, direction);
        if (VoxelShapes.method_20713(voxelShape, voxelShape2 = this.method_20710(blockState, targetId, direction.getOpposite()))) {
            return 15;
        }
        return level + Math.max(1, atomicInteger.get());
    }

    @Override
    protected void propagateLevel(long id, int level, boolean decrease) {
        long l = ChunkSectionPos.fromGlobalPos(id);
        for (Direction direction : DIRECTIONS) {
            long m = BlockPos.offset(id, direction);
            long n = ChunkSectionPos.fromGlobalPos(m);
            if (l != n && !((BlockLightStorage)this.lightStorage).hasLight(n)) continue;
            this.propagateLevel(id, m, level, decrease);
        }
    }

    @Override
    protected int recalculateLevel(long id, long excludedId, int maxLevel) {
        int i = maxLevel;
        if (Long.MAX_VALUE != excludedId) {
            int j = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
            if (i > j) {
                i = j;
            }
            if (i == 0) {
                return i;
            }
        }
        long l = ChunkSectionPos.fromGlobalPos(id);
        ChunkNibbleArray chunkNibbleArray = ((BlockLightStorage)this.lightStorage).getLightArray(l, true);
        for (Direction direction : DIRECTIONS) {
            long n;
            ChunkNibbleArray chunkNibbleArray2;
            long m = BlockPos.offset(id, direction);
            if (m == excludedId || (chunkNibbleArray2 = l == (n = ChunkSectionPos.fromGlobalPos(m)) ? chunkNibbleArray : ((BlockLightStorage)this.lightStorage).getLightArray(n, true)) == null) continue;
            int k = this.getPropagatedLevel(m, id, this.getCurrentLevelFromArray(chunkNibbleArray2, m));
            if (i > k) {
                i = k;
            }
            if (i != 0) continue;
            return i;
        }
        return i;
    }

    @Override
    public void method_15514(BlockPos blockPos, int i) {
        ((BlockLightStorage)this.lightStorage).updateAll();
        this.updateLevel(Long.MAX_VALUE, blockPos.asLong(), 15 - i, true);
    }
}
