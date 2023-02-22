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

public class ConfiguredFeature<FC extends FeatureConfig> {
    public final Feature<FC> feature;
    public final FC config;

    public ConfiguredFeature(Feature<FC> feature, FC config) {
        this.feature = feature;
        this.config = config;
    }

    public ConfiguredFeature(Feature<FC> feature, Dynamic<?> dynamic) {
        this(feature, feature.deserializeConfig(dynamic));
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("name"), (Object)ops.createString(Registry.FEATURE.getId(this.feature).toString()), (Object)ops.createString("config"), (Object)this.config.serialize(ops).getValue())));
    }

    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos blockPos) {
        return this.feature.generate(world, generator, random, blockPos, this.config);
    }

    public static <T> ConfiguredFeature<?> deserialize(Dynamic<T> dynamic) {
        Feature<?> feature = Registry.FEATURE.get(new Identifier(dynamic.get("name").asString("")));
        return new ConfiguredFeature(feature, dynamic.get("config").orElseEmptyMap());
    }
}

