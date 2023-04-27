/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package net.minecraft.world.chunk.light;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.BlockLightStorage;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightSourceView;

public final class ChunkBlockLightProvider
extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

    public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
        this(chunkProvider, new BlockLightStorage(chunkProvider));
    }

    @VisibleForTesting
    public ChunkBlockLightProvider(ChunkProvider chunkProvider, BlockLightStorage blockLightStorage) {
        super(chunkProvider, blockLightStorage);
    }

    @Override
    protected void method_51529(long blockPos) {
        int j;
        long l = ChunkSectionPos.fromBlockPos(blockPos);
        if (!((BlockLightStorage)this.lightStorage).hasSection(l)) {
            return;
        }
        BlockState blockState = this.getStateForLighting(this.mutablePos.set(blockPos));
        int i = this.getLightSourceLuminance(blockPos, blockState);
        if (i < (j = ((BlockLightStorage)this.lightStorage).get(blockPos))) {
            ((BlockLightStorage)this.lightStorage).set(blockPos, 0);
            this.method_51565(blockPos, ChunkLightProvider.class_8531.packWithAllDirectionsSet(j));
        } else {
            this.method_51565(blockPos, field_44731);
        }
        if (i > 0) {
            this.method_51566(blockPos, ChunkLightProvider.class_8531.method_51573(i, ChunkBlockLightProvider.isTrivialForLighting(blockState)));
        }
    }

    @Override
    protected void method_51531(long blockPos, long l, int i) {
        BlockState blockState = null;
        for (Direction direction : DIRECTIONS) {
            int j;
            int k;
            long m;
            if (!ChunkLightProvider.class_8531.isDirectionBitSet(l, direction) || !((BlockLightStorage)this.lightStorage).hasSection(ChunkSectionPos.fromBlockPos(m = BlockPos.offset(blockPos, direction))) || (k = i - 1) <= (j = ((BlockLightStorage)this.lightStorage).get(m))) continue;
            this.mutablePos.set(m);
            BlockState blockState2 = this.getStateForLighting(this.mutablePos);
            int n = i - this.getOpacity(blockState2, this.mutablePos);
            if (n <= j) continue;
            if (blockState == null) {
                BlockState blockState3 = blockState = ChunkLightProvider.class_8531.isTrivial(l) ? Blocks.AIR.getDefaultState() : this.getStateForLighting(this.mutablePos.set(blockPos));
            }
            if (this.shapesCoverFullCube(blockPos, blockState, m, blockState2, direction)) continue;
            ((BlockLightStorage)this.lightStorage).set(m, n);
            if (n <= 1) continue;
            this.method_51566(m, ChunkLightProvider.class_8531.method_51574(n, ChunkBlockLightProvider.isTrivialForLighting(blockState2), direction.getOpposite()));
        }
    }

    @Override
    protected void method_51530(long blockPos, long l) {
        int i = ChunkLightProvider.class_8531.getLightLevel(l);
        for (Direction direction : DIRECTIONS) {
            int j;
            long m;
            if (!ChunkLightProvider.class_8531.isDirectionBitSet(l, direction) || !((BlockLightStorage)this.lightStorage).hasSection(ChunkSectionPos.fromBlockPos(m = BlockPos.offset(blockPos, direction))) || (j = ((BlockLightStorage)this.lightStorage).get(m)) == 0) continue;
            if (j <= i - 1) {
                BlockState blockState = this.getStateForLighting(this.mutablePos.set(m));
                int k = this.getLightSourceLuminance(m, blockState);
                ((BlockLightStorage)this.lightStorage).set(m, 0);
                if (k < j) {
                    this.method_51565(m, ChunkLightProvider.class_8531.packWithOneDirectionCleared(j, direction.getOpposite()));
                }
                if (k <= 0) continue;
                this.method_51566(m, ChunkLightProvider.class_8531.method_51573(k, ChunkBlockLightProvider.isTrivialForLighting(blockState)));
                continue;
            }
            this.method_51566(m, ChunkLightProvider.class_8531.method_51579(j, false, direction.getOpposite()));
        }
    }

    private int getLightSourceLuminance(long blockPos, BlockState blockState) {
        int i = blockState.getLuminance();
        if (i > 0 && ((BlockLightStorage)this.lightStorage).isSectionInEnabledColumn(ChunkSectionPos.fromBlockPos(blockPos))) {
            return i;
        }
        return 0;
    }

    @Override
    public void propagateLight(ChunkPos chunkPos) {
        this.setColumnEnabled(chunkPos, true);
        LightSourceView lightSourceView = this.chunkProvider.getChunk(chunkPos.x, chunkPos.z);
        if (lightSourceView != null) {
            lightSourceView.forEachLightSource((blockPos, blockState) -> {
                int i = blockState.getLuminance();
                this.method_51566(blockPos.asLong(), ChunkLightProvider.class_8531.method_51573(i, ChunkBlockLightProvider.isTrivialForLighting(blockState)));
            });
        }
    }
}

