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

public class SeagrassFeatureConfig
implements FeatureConfig {
    public final int count;
    public final double tallSeagrassProbability;

    public SeagrassFeatureConfig(int count, double tallSeagrassProbability) {
        this.count = count;
        this.tallSeagrassProbability = tallSeagrassProbability;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("count"), (Object)ops.createInt(this.count), (Object)ops.createString("tall_seagrass_probability"), (Object)ops.createDouble(this.tallSeagrassProbability))));
    }

    public static <T> SeagrassFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("count").asInt(0);
        double d = dynamic.get("tall_seagrass_probability").asDouble(0.0);
        return new SeagrassFeatureConfig(i, d);
    }
}

