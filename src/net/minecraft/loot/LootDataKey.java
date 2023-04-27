/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.loot.LootDataType;
import net.minecraft.util.Identifier;

public record LootDataKey<T>(LootDataType<T> type, Identifier id) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LootDataKey.class, "type;location", "type", "id"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LootDataKey.class, "type;location", "type", "id"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LootDataKey.class, "type;location", "type", "id"}, this, object);
    }
}

