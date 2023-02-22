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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomFeatureEntry<FC extends FeatureConfig> {
    public final Feature<FC> feature;
    public final FC config;
    public final Float chance;

    public RandomFeatureEntry(Feature<FC> feature, FC config, Float chance) {
        this.feature = feature;
        this.config = config;
        this.chance = chance;
    }

    public RandomFeatureEntry(Feature<FC> feature, Dynamic<?> dynamic, float f) {
        this(feature, feature.deserializeConfig(dynamic), Float.valueOf(f));
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("name"), (Object)dynamicOps.createString(Registry.FEATURE.getId(this.feature).toString()), (Object)dynamicOps.createString("config"), (Object)this.config.serialize(dynamicOps).getValue(), (Object)dynamicOps.createString("chance"), (Object)dynamicOps.createFloat(this.chance.floatValue()))));
    }

    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos) {
        return this.feature.generate(world, chunkGenerator, random, blockPos, this.config);
    }

    public static <T> RandomFeatureEntry<?> deserialize(Dynamic<T> dynamic) {
        Feature<?> feature = Registry.FEATURE.get(new Identifier(dynamic.get("name").asString("")));
        return new RandomFeatureEntry(feature, dynamic.get("config").orElseEmptyMap(), dynamic.get("chance").asFloat(0.0f));
    }
}

