/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountDepthDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final int baseline;
    public final int spread;

    public CountDepthDecoratorConfig(int count, int baseline, int spread) {
        this.count = count;
        this.baseline = baseline;
        this.spread = spread;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("count"), (Object)ops.createInt(this.count), (Object)ops.createString("baseline"), (Object)ops.createInt(this.baseline), (Object)ops.createString("spread"), (Object)ops.createInt(this.spread))));
    }

    public static CountDepthDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        int j = dynamic.get("baseline").asInt(0);
        int k = dynamic.get("spread").asInt(0);
        return new CountDepthDecoratorConfig(i, j, k);
    }
}

