/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;

public abstract class SaplingGenerator {
    @Nullable
    protected abstract AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random var1);

    public boolean generate(IWorld world, BlockPos pos, BlockState state, Random random) {
        AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature = this.createTreeFeature(random);
        if (abstractTreeFeature == null) {
            return false;
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
        if (abstractTreeFeature.generate(world, world.getChunkManager().getChunkGenerator(), random, pos, FeatureConfig.DEFAULT)) {
            return true;
        }
        world.setBlockState(pos, state, 4);
        return false;
    }
}

