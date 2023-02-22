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

public class NetherSpringFeatureConfig
implements FeatureConfig {
    public final boolean insideRock;

    public NetherSpringFeatureConfig(boolean insideRock) {
        this.insideRock = insideRock;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("inside_rock"), (Object)ops.createBoolean(this.insideRock))));
    }

    public static <T> NetherSpringFeatureConfig deserialize(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("inside_rock").asBoolean(false);
        return new NetherSpringFeatureConfig(bl);
    }
}

