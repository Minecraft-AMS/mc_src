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
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class VillageFeatureConfig
implements FeatureConfig {
    public final Identifier startPool;
    public final int size;

    public VillageFeatureConfig(String startPool, int size) {
        this.startPool = new Identifier(startPool);
        this.size = size;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("start_pool"), (Object)ops.createString(this.startPool.toString()), (Object)ops.createString("size"), (Object)ops.createInt(this.size))));
    }

    public static <T> VillageFeatureConfig deserialize(Dynamic<T> dynamic) {
        String string = dynamic.get("start_pool").asString("");
        int i = dynamic.get("size").asInt(6);
        return new VillageFeatureConfig(string, i);
    }
}

