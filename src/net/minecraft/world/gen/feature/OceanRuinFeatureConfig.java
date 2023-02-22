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
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;

public class OceanRuinFeatureConfig
implements FeatureConfig {
    public final OceanRuinFeature.BiomeType biomeType;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType biomeType, float largeProbability, float clusterProbability) {
        this.biomeType = biomeType;
        this.largeProbability = largeProbability;
        this.clusterProbability = clusterProbability;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("biome_temp"), (Object)ops.createString(this.biomeType.getName()), (Object)ops.createString("large_probability"), (Object)ops.createFloat(this.largeProbability), (Object)ops.createString("cluster_probability"), (Object)ops.createFloat(this.clusterProbability))));
    }

    public static <T> OceanRuinFeatureConfig deserialize(Dynamic<T> dynamic) {
        OceanRuinFeature.BiomeType biomeType = OceanRuinFeature.BiomeType.byName(dynamic.get("biome_temp").asString(""));
        float f = dynamic.get("large_probability").asFloat(0.0f);
        float g = dynamic.get("cluster_probability").asFloat(0.0f);
        return new OceanRuinFeatureConfig(biomeType, f, g);
    }
}

