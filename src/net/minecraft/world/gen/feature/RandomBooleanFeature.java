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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;

public class RandomBooleanFeature
extends Feature<RandomBooleanFeatureConfig> {
    public RandomBooleanFeature(Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, RandomBooleanFeatureConfig randomBooleanFeatureConfig) {
        boolean bl = random.nextBoolean();
        if (bl) {
            return randomBooleanFeatureConfig.featureTrue.generate(iWorld, chunkGenerator, random, blockPos);
        }
        return randomBooleanFeatureConfig.featureFalse.generate(iWorld, chunkGenerator, random, blockPos);
    }
}

