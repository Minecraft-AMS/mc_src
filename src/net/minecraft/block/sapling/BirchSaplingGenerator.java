/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class BirchSaplingGenerator
extends SaplingGenerator {
    @Override
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return bees ? TreeConfiguredFeatures.BIRCH_BEES_005 : TreeConfiguredFeatures.BIRCH;
    }
}

