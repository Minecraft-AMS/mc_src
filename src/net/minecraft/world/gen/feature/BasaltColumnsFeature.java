/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class BasaltColumnsFeature
extends Feature<BasaltColumnsFeatureConfig> {
    private static final ImmutableList<Block> field_24132 = ImmutableList.of((Object)Blocks.LAVA, (Object)Blocks.BEDROCK, (Object)Blocks.MAGMA_BLOCK, (Object)Blocks.SOUL_SAND, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);

    public BasaltColumnsFeature(Codec<BasaltColumnsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BasaltColumnsFeatureConfig basaltColumnsFeatureConfig) {
        int i = chunkGenerator.getSeaLevel();
        if (!BasaltColumnsFeature.method_30379(structureWorldAccess, i, blockPos.mutableCopy())) {
            return false;
        }
        int j = basaltColumnsFeatureConfig.getHeight().getValue(random);
        boolean bl = random.nextFloat() < 0.9f;
        int k = Math.min(j, bl ? 5 : 8);
        int l = bl ? 50 : 15;
        boolean bl2 = false;
        for (BlockPos blockPos2 : BlockPos.iterateRandomly(random, l, blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k)) {
            int m = j - blockPos2.getManhattanDistance(blockPos);
            if (m < 0) continue;
            bl2 |= this.method_27096(structureWorldAccess, i, blockPos2, m, basaltColumnsFeatureConfig.getReach().getValue(random));
        }
        return bl2;
    }

    private boolean method_27096(WorldAccess worldAccess, int i, BlockPos blockPos, int j, int k) {
        boolean bl = false;
        block0: for (BlockPos blockPos2 : BlockPos.iterate(blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k)) {
            BlockPos blockPos3;
            int l = blockPos2.getManhattanDistance(blockPos);
            BlockPos blockPos4 = blockPos3 = BasaltColumnsFeature.method_27095(worldAccess, i, blockPos2) ? BasaltColumnsFeature.method_27094(worldAccess, i, blockPos2.mutableCopy(), l) : BasaltColumnsFeature.method_27098(worldAccess, blockPos2.mutableCopy(), l);
            if (blockPos3 == null) continue;
            BlockPos.Mutable mutable = blockPos3.mutableCopy();
            for (int m = j - l / 2; m >= 0; --m) {
                if (BasaltColumnsFeature.method_27095(worldAccess, i, mutable)) {
                    this.setBlockState(worldAccess, mutable, Blocks.BASALT.getDefaultState());
                    mutable.move(Direction.UP);
                    bl = true;
                    continue;
                }
                if (!worldAccess.getBlockState(mutable).isOf(Blocks.BASALT)) continue block0;
                mutable.move(Direction.UP);
            }
        }
        return bl;
    }

    @Nullable
    private static BlockPos method_27094(WorldAccess worldAccess, int i, BlockPos.Mutable mutable, int j) {
        while (mutable.getY() > 1 && j > 0) {
            --j;
            if (BasaltColumnsFeature.method_30379(worldAccess, i, mutable)) {
                return mutable;
            }
            mutable.move(Direction.DOWN);
        }
        return null;
    }

    private static boolean method_30379(WorldAccess worldAccess, int i, BlockPos.Mutable mutable) {
        if (BasaltColumnsFeature.method_27095(worldAccess, i, mutable)) {
            BlockState blockState = worldAccess.getBlockState(mutable.move(Direction.DOWN));
            mutable.move(Direction.UP);
            return !blockState.isAir() && !field_24132.contains((Object)blockState.getBlock());
        }
        return false;
    }

    @Nullable
    private static BlockPos method_27098(WorldAccess worldAccess, BlockPos.Mutable mutable, int i) {
        while (mutable.getY() < worldAccess.getHeight() && i > 0) {
            --i;
            BlockState blockState = worldAccess.getBlockState(mutable);
            if (field_24132.contains((Object)blockState.getBlock())) {
                return null;
            }
            if (blockState.isAir()) {
                return mutable;
            }
            mutable.move(Direction.UP);
        }
        return null;
    }

    private static boolean method_27095(WorldAccess worldAccess, int i, BlockPos blockPos) {
        BlockState blockState = worldAccess.getBlockState(blockPos);
        return blockState.isAir() || blockState.isOf(Blocks.LAVA) && blockPos.getY() <= i;
    }
}
