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

public class SeaPickleFeatureConfig
implements FeatureConfig {
    public final int count;

    public SeaPickleFeatureConfig(int count) {
        this.count = count;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("count"), (Object)ops.createInt(this.count))));
    }

    public static <T> SeaPickleFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("count").asInt(0);
        return new SeaPickleFeatureConfig(i);
    }
}

