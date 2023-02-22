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
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class KelpFeature
extends Feature<DefaultFeatureConfig> {
    public KelpFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        int i = 0;
        int j = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
        if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER) {
            BlockState blockState = Blocks.KELP.getDefaultState();
            BlockState blockState2 = Blocks.KELP_PLANT.getDefaultState();
            int k = 1 + random.nextInt(10);
            for (int l = 0; l <= k; ++l) {
                if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER && iWorld.getBlockState(blockPos2.up()).getBlock() == Blocks.WATER && blockState2.canPlaceAt(iWorld, blockPos2)) {
                    if (l == k) {
                        iWorld.setBlockState(blockPos2, (BlockState)blockState.with(KelpBlock.AGE, random.nextInt(23)), 2);
                        ++i;
                    } else {
                        iWorld.setBlockState(blockPos2, blockState2, 2);
                    }
                } else if (l > 0) {
                    BlockPos blockPos3 = blockPos2.down();
                    if (!blockState.canPlaceAt(iWorld, blockPos3) || iWorld.getBlockState(blockPos3.down()).getBlock() == Blocks.KELP) break;
                    iWorld.setBlockState(blockPos3, (BlockState)blockState.with(KelpBlock.AGE, random.nextInt(23)), 2);
                    ++i;
                    break;
                }
                blockPos2 = blockPos2.up();
            }
        }
        return i > 0;
    }
}
