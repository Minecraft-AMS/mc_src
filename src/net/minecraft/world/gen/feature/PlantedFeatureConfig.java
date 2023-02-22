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

public class PlantedFeatureConfig
implements FeatureConfig {
    public final boolean planted;

    public PlantedFeatureConfig(boolean planted) {
        this.planted = planted;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("planted"), (Object)ops.createBoolean(this.planted))));
    }

    public static <T> PlantedFeatureConfig deserialize(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("planted").asBoolean(false);
        return new PlantedFeatureConfig(bl);
    }
}

