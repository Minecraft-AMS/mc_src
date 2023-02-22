/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractPileFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class HayPileFeature
extends AbstractPileFeature {
    public HayPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected BlockState getPileBlockState(IWorld world) {
        Direction.Axis axis = Direction.Axis.method_16699(world.getRandom());
        return (BlockState)Blocks.HAY_BLOCK.getDefaultState().with(PillarBlock.AXIS, axis);
    }
}

