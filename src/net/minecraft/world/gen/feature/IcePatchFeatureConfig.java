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

public class IcePatchFeatureConfig
implements FeatureConfig {
    public final int radius;

    public IcePatchFeatureConfig(int radius) {
        this.radius = radius;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("radius"), (Object)ops.createInt(this.radius))));
    }

    public static <T> IcePatchFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("radius").asInt(0);
        return new IcePatchFeatureConfig(i);
    }
}

