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

public class CountChanceDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final float chance;

    public CountChanceDecoratorConfig(int count, float chance) {
        this.count = count;
        this.chance = chance;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("count"), (Object)ops.createInt(this.count), (Object)ops.createString("chance"), (Object)ops.createFloat(this.chance))));
    }

    public static CountChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        float f = dynamic.get("chance").asFloat(0.0f);
        return new CountChanceDecoratorConfig(i, f);
    }
}

