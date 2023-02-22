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

public class CountExtraChanceDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public CountExtraChanceDecoratorConfig(int count, float extraChance, int extraCount) {
        this.count = count;
        this.extraChance = extraChance;
        this.extraCount = extraCount;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("count"), (Object)ops.createInt(this.count), (Object)ops.createString("extra_chance"), (Object)ops.createFloat(this.extraChance), (Object)ops.createString("extra_count"), (Object)ops.createInt(this.extraCount))));
    }

    public static CountExtraChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        float f = dynamic.get("extra_chance").asFloat(0.0f);
        int j = dynamic.get("extra_count").asInt(0);
        return new CountExtraChanceDecoratorConfig(i, f, j);
    }
}

