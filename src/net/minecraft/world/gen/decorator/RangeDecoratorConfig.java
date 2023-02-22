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

public class RangeDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;

    public RangeDecoratorConfig(int count, int bottomOffset, int topOffset, int maximum) {
        this.count = count;
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
        this.maximum = maximum;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("count"), (Object)ops.createInt(this.count), (Object)ops.createString("bottom_offset"), (Object)ops.createInt(this.bottomOffset), (Object)ops.createString("top_offset"), (Object)ops.createInt(this.topOffset), (Object)ops.createString("maximum"), (Object)ops.createInt(this.maximum))));
    }

    public static RangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        int j = dynamic.get("bottom_offset").asInt(0);
        int k = dynamic.get("top_offset").asInt(0);
        int l = dynamic.get("maximum").asInt(0);
        return new RangeDecoratorConfig(i, j, k, l);
    }
}

