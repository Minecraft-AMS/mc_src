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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

public class SimpleRandomFeature
extends Feature<SimpleRandomFeatureConfig> {
    public SimpleRandomFeature(Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SimpleRandomFeatureConfig simpleRandomFeatureConfig) {
        int i = random.nextInt(simpleRandomFeatureConfig.features.size());
        ConfiguredFeature<?, ?> configuredFeature = simpleRandomFeatureConfig.features.get(i);
        return configuredFeature.generate(iWorld, chunkGenerator, random, blockPos);
    }
}

