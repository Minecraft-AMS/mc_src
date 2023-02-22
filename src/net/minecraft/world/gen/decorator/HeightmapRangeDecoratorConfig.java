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

public class HeightmapRangeDecoratorConfig
implements DecoratorConfig {
    public final int min;
    public final int max;

    public HeightmapRangeDecoratorConfig(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("min"), (Object)ops.createInt(this.min), (Object)ops.createString("max"), (Object)ops.createInt(this.max))));
    }

    public static HeightmapRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("min").asInt(0);
        int j = dynamic.get("max").asInt(0);
        return new HeightmapRangeDecoratorConfig(i, j);
    }
}

