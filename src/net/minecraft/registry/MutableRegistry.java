/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public interface MutableRegistry<T>
extends Registry<T> {
    public RegistryEntry<T> set(int var1, RegistryKey<T> var2, T var3, Lifecycle var4);

    public RegistryEntry.Reference<T> add(RegistryKey<T> var1, T var2, Lifecycle var3);

    public boolean isEmpty();

    public RegistryEntryLookup<T> createMutableEntryLookup();
}

