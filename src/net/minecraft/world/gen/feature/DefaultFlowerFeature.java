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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

public class DefaultFlowerFeature
extends FlowerFeature {
    public DefaultFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public BlockState getFlowerToPlace(Random random, BlockPos pos) {
        if (random.nextFloat() > 0.6666667f) {
            return Blocks.DANDELION.getDefaultState();
        }
        return Blocks.POPPY.getDefaultState();
    }
}

