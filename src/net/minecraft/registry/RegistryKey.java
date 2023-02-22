/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 *  com.mojang.serialization.Codec
 */
package net.minecraft.registry;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistryKey<T> {
    private static final ConcurrentMap<RegistryIdPair, RegistryKey<?>> INSTANCES = new MapMaker().weakValues().makeMap();
    private final Identifier registry;
    private final Identifier value;

    public static <T> Codec<RegistryKey<T>> createCodec(RegistryKey<? extends Registry<T>> registry) {
        return Identifier.CODEC.xmap(id -> RegistryKey.of(registry, id), RegistryKey::getValue);
    }

    public static <T> RegistryKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier value) {
        return RegistryKey.of(registry.value, value);
    }

    public static <T> RegistryKey<Registry<T>> ofRegistry(Identifier registry) {
        return RegistryKey.of(Registries.ROOT_KEY, registry);
    }

    private static <T> RegistryKey<T> of(Identifier registry, Identifier value) {
        return INSTANCES.computeIfAbsent(new RegistryIdPair(registry, value), pair -> new RegistryKey(pair.registry, pair.id));
    }

    private RegistryKey(Identifier registry, Identifier value) {
        this.registry = registry;
        this.value = value;
    }

    public String toString() {
        return "ResourceKey[" + this.registry + " / " + this.value + "]";
    }

    public boolean isOf(RegistryKey<? extends Registry<?>> registry) {
        return this.registry.equals(registry.getValue());
    }

    public <E> Optional<RegistryKey<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
    }

    public Identifier getValue() {
        return this.value;
    }

    public Identifier getRegistry() {
        return this.registry;
    }

    static final class RegistryIdPair
    extends Record {
        final Identifier registry;
        final Identifier id;

        RegistryIdPair(Identifier identifier, Identifier identifier2) {
            this.registry = identifier;
            this.id = identifier2;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryIdPair.class, "registry;location", "registry", "id"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryIdPair.class, "registry;location", "registry", "id"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryIdPair.class, "registry;location", "registry", "id"}, this, object);
        }

        public Identifier registry() {
            return this.registry;
        }

        public Identifier id() {
            return this.id;
        }
    }
}

