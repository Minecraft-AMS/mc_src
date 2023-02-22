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
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeatureConfig
implements FeatureConfig {
    public final List<RandomFeatureEntry<?>> features;
    public final ConfiguredFeature<?> defaultFeature;

    public RandomFeatureConfig(List<RandomFeatureEntry<?>> features, ConfiguredFeature<?> defaultFeature) {
        this.features = features;
        this.defaultFeature = defaultFeature;
    }

    public RandomFeatureConfig(Feature<?>[] features, FeatureConfig[] configs, float[] chances, Feature<?> defaultFeature, FeatureConfig featureConfig) {
        this(IntStream.range(0, features.length).mapToObj(i -> RandomFeatureConfig.makeEntry(features[i], configs[i], chances[i])).collect(Collectors.toList()), RandomFeatureConfig.configure(defaultFeature, featureConfig));
    }

    private static <FC extends FeatureConfig> RandomFeatureEntry<FC> makeEntry(Feature<FC> feature, FeatureConfig config, float chance) {
        return new RandomFeatureEntry<FeatureConfig>(feature, config, Float.valueOf(chance));
    }

    private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig config) {
        return new ConfiguredFeature<FeatureConfig>(feature, config);
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        Object object = ops.createList(this.features.stream().map(randomFeatureEntry -> randomFeatureEntry.serialize(ops).getValue()));
        Object object2 = this.defaultFeature.serialize(ops).getValue();
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("features"), (Object)object, (Object)ops.createString("default"), (Object)object2)));
    }

    public static <T> RandomFeatureConfig deserialize(Dynamic<T> dynamic) {
        List list = dynamic.get("features").asList(RandomFeatureEntry::deserialize);
        ConfiguredFeature<?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
        return new RandomFeatureConfig(list, configuredFeature);
    }
}

