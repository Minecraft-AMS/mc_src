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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractPileFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class IcePileFeature
extends AbstractPileFeature {
    public IcePileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected BlockState getPileBlockState(IWorld world) {
        return world.getRandom().nextInt(7) == 0 ? Blocks.BLUE_ICE.getDefaultState() : Blocks.PACKED_ICE.getDefaultState();
    }
}
