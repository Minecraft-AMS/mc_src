/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomFeatureEntry<FC extends FeatureConfig> {
    public final ConfiguredFeature<FC, ?> feature;
    public final float chance;

    public RandomFeatureEntry(ConfiguredFeature<FC, ?> feature, float chance) {
        this.feature = feature;
        this.chance = chance;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("name"), (Object)dynamicOps.createString(Registry.FEATURE.getId((Feature<?>)this.feature.feature).toString()), (Object)dynamicOps.createString("config"), (Object)this.feature.config.serialize(dynamicOps).getValue(), (Object)dynamicOps.createString("chance"), (Object)dynamicOps.createFloat(this.chance))));
    }

    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos) {
        return this.feature.generate(world, chunkGenerator, random, blockPos);
    }

    public static <T> RandomFeatureEntry<?> deserialize(Dynamic<T> dynamic) {
        return ConfiguredFeature.deserialize(dynamic).withChance(dynamic.get("chance").asFloat(0.0f));
    }
}

