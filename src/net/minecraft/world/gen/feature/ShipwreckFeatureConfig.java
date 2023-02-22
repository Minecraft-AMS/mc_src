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

public class ShipwreckFeatureConfig
implements FeatureConfig {
    public final boolean isBeached;

    public ShipwreckFeatureConfig(boolean isBeached) {
        this.isBeached = isBeached;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("is_beached"), (Object)ops.createBoolean(this.isBeached))));
    }

    public static <T> ShipwreckFeatureConfig deserialize(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("is_beached").asBoolean(false);
        return new ShipwreckFeatureConfig(bl);
    }
}

