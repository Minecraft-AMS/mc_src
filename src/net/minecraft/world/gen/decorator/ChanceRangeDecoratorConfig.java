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

public class ChanceRangeDecoratorConfig
implements DecoratorConfig {
    public final float chance;
    public final int bottomOffset;
    public final int topOffset;
    public final int top;

    public ChanceRangeDecoratorConfig(float chance, int bottomOffset, int topOffset, int top) {
        this.chance = chance;
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
        this.top = top;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("chance"), (Object)ops.createFloat(this.chance), (Object)ops.createString("bottom_offset"), (Object)ops.createInt(this.bottomOffset), (Object)ops.createString("top_offset"), (Object)ops.createInt(this.topOffset), (Object)ops.createString("top"), (Object)ops.createInt(this.top))));
    }

    public static ChanceRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        float f = dynamic.get("chance").asFloat(0.0f);
        int i = dynamic.get("bottom_offset").asInt(0);
        int j = dynamic.get("top_offset").asInt(0);
        int k = dynamic.get("top").asInt(0);
        return new ChanceRangeDecoratorConfig(f, i, j, k);
    }
}

