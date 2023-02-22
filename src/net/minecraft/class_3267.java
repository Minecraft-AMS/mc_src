/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class class_3267
implements DecoratorConfig {
    public final int field_14192;

    public class_3267(int i) {
        this.field_14192 = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("chance"), (Object)ops.createInt(this.field_14192))));
    }

    public static class_3267 method_14415(Dynamic<?> dynamic) {
        int i = dynamic.get("chance").asInt(0);
        return new class_3267(i);
    }
}

