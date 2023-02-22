/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.registry.entry.RegistryFixedCodec;

public class RegistryCodecs {
    private static <T> MapCodec<RegistryManagerEntry<T>> managerEntry(RegistryKey<? extends Registry<T>> registryRef, MapCodec<T> elementCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec(registryRef).fieldOf("name").forGetter(RegistryManagerEntry::key), (App)Codec.INT.fieldOf("id").forGetter(RegistryManagerEntry::rawId), (App)elementCodec.forGetter(RegistryManagerEntry::value)).apply((Applicative)instance, RegistryManagerEntry::new));
    }

    public static <T> Codec<Registry<T>> createRegistryCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
        return RegistryCodecs.managerEntry(registryRef, elementCodec.fieldOf("element")).codec().listOf().xmap(entries -> {
            SimpleRegistry mutableRegistry = new SimpleRegistry(registryRef, lifecycle);
            for (RegistryManagerEntry registryManagerEntry : entries) {
                mutableRegistry.set(registryManagerEntry.rawId(), registryManagerEntry.key(), registryManagerEntry.value(), lifecycle);
            }
            return mutableRegistry;
        }, registry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : registry) {
                builder.add(new RegistryManagerEntry(registry.getKey(object).get(), registry.getRawId(object), object));
            }
            return builder.build();
        });
    }

    public static <E> Codec<Registry<E>> createKeyedRegistryCodec(RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, Codec<E> elementCodec) {
        UnboundedMapCodec codec = Codec.unboundedMap(RegistryKey.createCodec(registryRef), elementCodec);
        return codec.xmap(entries -> {
            SimpleRegistry mutableRegistry = new SimpleRegistry(registryRef, lifecycle);
            entries.forEach((key, value) -> mutableRegistry.add(key, value, lifecycle));
            return mutableRegistry.freeze();
        }, registry -> ImmutableMap.copyOf(registry.getEntrySet()));
    }

    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return RegistryCodecs.entryList(registryRef, elementCodec, false);
    }

    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean alwaysSerializeAsList) {
        return RegistryEntryListCodec.create(registryRef, RegistryElementCodec.of(registryRef, elementCodec), alwaysSerializeAsList);
    }

    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef) {
        return RegistryCodecs.entryList(registryRef, false);
    }

    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, boolean alwaysSerializeAsList) {
        return RegistryEntryListCodec.create(registryRef, RegistryFixedCodec.of(registryRef), alwaysSerializeAsList);
    }

    record RegistryManagerEntry<T>(RegistryKey<T> key, int rawId, T value) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryManagerEntry.class, "key;id;value", "key", "rawId", "value"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryManagerEntry.class, "key;id;value", "key", "rawId", "value"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryManagerEntry.class, "key;id;value", "key", "rawId", "value"}, this, object);
        }
    }
}

