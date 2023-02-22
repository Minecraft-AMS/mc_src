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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleRandomFeatureConfig
implements FeatureConfig {
    public final List<ConfiguredFeature<?>> features;

    public SimpleRandomFeatureConfig(List<ConfiguredFeature<?>> features) {
        this.features = features;
    }

    public SimpleRandomFeatureConfig(Feature<?>[] features, FeatureConfig[] configs) {
        this(IntStream.range(0, features.length).mapToObj(i -> SimpleRandomFeatureConfig.configure(features[i], configs[i])).collect(Collectors.toList()));
    }

    private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig config) {
        return new ConfiguredFeature<FeatureConfig>(feature, config);
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("features"), (Object)ops.createList(this.features.stream().map(configuredFeature -> configuredFeature.serialize(ops).getValue())))));
    }

    public static <T> SimpleRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
        List list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
        return new SimpleRandomFeatureConfig(list);
    }
}

