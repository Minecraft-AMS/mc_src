/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;
import org.jetbrains.annotations.Nullable;

public class JungleSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return new OakTreeFeature((Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig>)((Function<Dynamic<?>, BranchedTreeFeatureConfig>)BranchedTreeFeatureConfig::deserialize)).configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG);
    }

    @Override
    @Nullable
    protected ConfiguredFeature<MegaTreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
        return Feature.MEGA_JUNGLE_TREE.configure(DefaultBiomeFeatures.MEGA_JUNGLE_TREE_CONFIG);
    }
}

