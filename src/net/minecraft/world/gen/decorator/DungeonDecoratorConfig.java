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

public class DungeonDecoratorConfig
implements DecoratorConfig {
    public final int chance;

    public DungeonDecoratorConfig(int chance) {
        this.chance = chance;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("chance"), (Object)ops.createInt(this.chance))));
    }

    public static DungeonDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("chance").asInt(0);
        return new DungeonDecoratorConfig(i);
    }
}

