/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;

public class SeaPickleFeature
extends Feature<SeaPickleFeatureConfig> {
    public SeaPickleFeature(Function<Dynamic<?>, ? extends SeaPickleFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockPos blockPos, SeaPickleFeatureConfig seaPickleFeatureConfig) {
        int i = 0;
        for (int j = 0; j < seaPickleFeatureConfig.count; ++j) {
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int m = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
            BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
            BlockState blockState = (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (iWorld.getBlockState(blockPos2).getBlock() != Blocks.WATER || !blockState.canPlaceAt(iWorld, blockPos2)) continue;
            iWorld.setBlockState(blockPos2, blockState, 2);
            ++i;
        }
        return i > 0;
    }
}

