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
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;

public class JungleTreeFeature
extends OakTreeFeature {
    public JungleTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int height, BlockState log, BlockState leaves, boolean bl2) {
        super(function, bl, height, log, leaves, bl2);
    }

    @Override
    protected int getTreeHeight(Random random) {
        return this.height + random.nextInt(7);
    }
}

