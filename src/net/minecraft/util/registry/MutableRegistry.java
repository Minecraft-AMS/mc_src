/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public abstract class MutableRegistry<T>
extends Registry<T> {
    public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    public abstract RegistryEntry<T> set(int var1, RegistryKey<T> var2, T var3, Lifecycle var4);

    public abstract RegistryEntry<T> add(RegistryKey<T> var1, T var2, Lifecycle var3);

    public abstract RegistryEntry<T> replace(OptionalInt var1, RegistryKey<T> var2, T var3, Lifecycle var4);

    public abstract boolean isEmpty();
}

