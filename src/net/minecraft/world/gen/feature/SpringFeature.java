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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

public class SpringFeature
extends Feature<SpringFeatureConfig> {
    public SpringFeature(Function<Dynamic<?>, ? extends SpringFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SpringFeatureConfig springFeatureConfig) {
        if (!springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.up()).getBlock())) {
            return false;
        }
        if (springFeatureConfig.requiresBlockBelow && !springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.down()).getBlock())) {
            return false;
        }
        BlockState blockState = iWorld.getBlockState(blockPos);
        if (!blockState.isAir() && !springFeatureConfig.validBlocks.contains(blockState.getBlock())) {
            return false;
        }
        int i = 0;
        int j = 0;
        if (springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.west()).getBlock())) {
            ++j;
        }
        if (springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.east()).getBlock())) {
            ++j;
        }
        if (springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.north()).getBlock())) {
            ++j;
        }
        if (springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.south()).getBlock())) {
            ++j;
        }
        if (springFeatureConfig.validBlocks.contains(iWorld.getBlockState(blockPos.down()).getBlock())) {
            ++j;
        }
        int k = 0;
        if (iWorld.isAir(blockPos.west())) {
            ++k;
        }
        if (iWorld.isAir(blockPos.east())) {
            ++k;
        }
        if (iWorld.isAir(blockPos.north())) {
            ++k;
        }
        if (iWorld.isAir(blockPos.south())) {
            ++k;
        }
        if (iWorld.isAir(blockPos.down())) {
            ++k;
        }
        if (j == springFeatureConfig.rockCount && k == springFeatureConfig.holeCount) {
            iWorld.setBlockState(blockPos, springFeatureConfig.state.getBlockState(), 2);
            iWorld.getFluidTickScheduler().schedule(blockPos, springFeatureConfig.state.getFluid(), 0);
            ++i;
        }
        return i > 0;
    }
}

