/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SpringFeature
extends Feature<SpringFeatureConfig> {
    public SpringFeature(Codec<SpringFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SpringFeatureConfig> context) {
        BlockPos blockPos;
        SpringFeatureConfig springFeatureConfig = context.getConfig();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        if (!structureWorldAccess.getBlockState((blockPos = context.getOrigin()).up()).isIn(springFeatureConfig.validBlocks)) {
            return false;
        }
        if (springFeatureConfig.requiresBlockBelow && !structureWorldAccess.getBlockState(blockPos.down()).isIn(springFeatureConfig.validBlocks)) {
            return false;
        }
        BlockState blockState = structureWorldAccess.getBlockState(blockPos);
        if (!blockState.isAir() && !blockState.isIn(springFeatureConfig.validBlocks)) {
            return false;
        }
        int i = 0;
        int j = 0;
        if (structureWorldAccess.getBlockState(blockPos.west()).isIn(springFeatureConfig.validBlocks)) {
            ++j;
        }
        if (structureWorldAccess.getBlockState(blockPos.east()).isIn(springFeatureConfig.validBlocks)) {
            ++j;
        }
        if (structureWorldAccess.getBlockState(blockPos.north()).isIn(springFeatureConfig.validBlocks)) {
            ++j;
        }
        if (structureWorldAccess.getBlockState(blockPos.south()).isIn(springFeatureConfig.validBlocks)) {
            ++j;
        }
        if (structureWorldAccess.getBlockState(blockPos.down()).isIn(springFeatureConfig.validBlocks)) {
            ++j;
        }
        int k = 0;
        if (structureWorldAccess.isAir(blockPos.west())) {
            ++k;
        }
        if (structureWorldAccess.isAir(blockPos.east())) {
            ++k;
        }
        if (structureWorldAccess.isAir(blockPos.north())) {
            ++k;
        }
        if (structureWorldAccess.isAir(blockPos.south())) {
            ++k;
        }
        if (structureWorldAccess.isAir(blockPos.down())) {
            ++k;
        }
        if (j == springFeatureConfig.rockCount && k == springFeatureConfig.holeCount) {
            structureWorldAccess.setBlockState(blockPos, springFeatureConfig.state.getBlockState(), 2);
            structureWorldAccess.createAndScheduleFluidTick(blockPos, springFeatureConfig.state.getFluid(), 0);
            ++i;
        }
        return i > 0;
    }
}

