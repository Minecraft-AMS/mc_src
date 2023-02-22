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

public class PillagerOutpostFeatureConfig
implements FeatureConfig {
    public final double probability;

    public PillagerOutpostFeatureConfig(double probability) {
        this.probability = probability;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("probability"), (Object)ops.createDouble(this.probability))));
    }

    public static <T> PillagerOutpostFeatureConfig deserialize(Dynamic<T> dynamic) {
        float f = dynamic.get("probability").asFloat(0.0f);
        return new PillagerOutpostFeatureConfig(f);
    }
}

