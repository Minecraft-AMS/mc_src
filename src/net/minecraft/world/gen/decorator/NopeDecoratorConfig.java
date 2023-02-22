/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NopeDecoratorConfig
implements DecoratorConfig {
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.emptyMap());
    }

    public static NopeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        return new NopeDecoratorConfig();
    }
}

