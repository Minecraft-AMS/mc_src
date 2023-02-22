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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public final ConfiguredFeature<?, ?> featureTrue;
    public final ConfiguredFeature<?, ?> featureFalse;

    public RandomBooleanFeatureConfig(ConfiguredFeature<?, ?> configuredFeature, ConfiguredFeature<?, ?> configuredFeature2) {
        this.featureTrue = configuredFeature;
        this.featureFalse = configuredFeature2;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("feature_true"), (Object)this.featureTrue.serialize(ops).getValue(), (Object)ops.createString("feature_false"), (Object)this.featureFalse.serialize(ops).getValue())));
    }

    public static <T> RandomBooleanFeatureConfig deserialize(Dynamic<T> dynamic) {
        ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("feature_true").orElseEmptyMap());
        ConfiguredFeature<?, ?> configuredFeature2 = ConfiguredFeature.deserialize(dynamic.get("feature_false").orElseEmptyMap());
        return new RandomBooleanFeatureConfig(configuredFeature, configuredFeature2);
    }
}

