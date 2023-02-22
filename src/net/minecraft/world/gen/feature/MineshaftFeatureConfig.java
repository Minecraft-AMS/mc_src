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
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig
implements FeatureConfig {
    public final double probability;
    public final MineshaftFeature.Type type;

    public MineshaftFeatureConfig(double probability, MineshaftFeature.Type type) {
        this.probability = probability;
        this.type = type;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("probability"), (Object)ops.createDouble(this.probability), (Object)ops.createString("type"), (Object)ops.createString(this.type.getName()))));
    }

    public static <T> MineshaftFeatureConfig deserialize(Dynamic<T> dynamic) {
        float f = dynamic.get("probability").asFloat(0.0f);
        MineshaftFeature.Type type = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
        return new MineshaftFeatureConfig(f, type);
    }
}

