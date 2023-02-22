/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
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
import net.minecraft.world.gen.feature.DeltaFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DeltaFeature
extends Feature<DeltaFeatureConfig> {
    private static final ImmutableList<Block> field_24133 = ImmutableList.of((Object)Blocks.BEDROCK, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);
    private static final Direction[] DIRECTIONS = Direction.values();

    public DeltaFeature(Codec<DeltaFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig) {
        boolean bl = false;
        boolean bl2 = random.nextDouble() < 0.9;
        int i = bl2 ? deltaFeatureConfig.getRimSize().getValue(random) : 0;
        int j = bl2 ? deltaFeatureConfig.getRimSize().getValue(random) : 0;
        boolean bl3 = bl2 && i != 0 && j != 0;
        int k = deltaFeatureConfig.getSize().getValue(random);
        int l = deltaFeatureConfig.getSize().getValue(random);
        int m = Math.max(k, l);
        for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, k, 0, l)) {
            BlockPos blockPos3;
            if (blockPos2.getManhattanDistance(blockPos) > m) break;
            if (!DeltaFeature.method_27103(structureWorldAccess, blockPos2, deltaFeatureConfig)) continue;
            if (bl3) {
                bl = true;
                this.setBlockState(structureWorldAccess, blockPos2, deltaFeatureConfig.getRim());
            }
            if (!DeltaFeature.method_27103(structureWorldAccess, blockPos3 = blockPos2.add(i, 0, j), deltaFeatureConfig)) continue;
            bl = true;
            this.setBlockState(structureWorldAccess, blockPos3, deltaFeatureConfig.getContents());
        }
        return bl;
    }

    private static boolean method_27103(WorldAccess worldAccess, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig) {
        BlockState blockState = worldAccess.getBlockState(blockPos);
        if (blockState.isOf(deltaFeatureConfig.getContents().getBlock())) {
            return false;
        }
        if (field_24133.contains((Object)blockState.getBlock())) {
            return false;
        }
        for (Direction direction : DIRECTIONS) {
            boolean bl = worldAccess.getBlockState(blockPos.offset(direction)).isAir();
            if ((!bl || direction == Direction.UP) && (bl || direction != Direction.UP)) continue;
            return false;
        }
        return true;
    }
}

