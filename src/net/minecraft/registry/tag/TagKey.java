/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Interner
 *  com.google.common.collect.Interners
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.registry.tag;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public record TagKey<T>(RegistryKey<? extends Registry<T>> registry, Identifier id) {
    private static final Interner<TagKey<?>> INTERNER = Interners.newWeakInterner();

    public static <T> Codec<TagKey<T>> unprefixedCodec(RegistryKey<? extends Registry<T>> registry) {
        return Identifier.CODEC.xmap(id -> TagKey.of(registry, id), TagKey::id);
    }

    public static <T> Codec<TagKey<T>> codec(RegistryKey<? extends Registry<T>> registry) {
        return Codec.STRING.comapFlatMap(string -> string.startsWith("#") ? Identifier.validate(string.substring(1)).map(id -> TagKey.of(registry, id)) : DataResult.error((String)"Not a tag id"), string -> "#" + string.id);
    }

    public static <T> TagKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier id) {
        return (TagKey)INTERNER.intern(new TagKey<T>(registry, id));
    }

    public boolean isOf(RegistryKey<? extends Registry<?>> registryRef) {
        return this.registry == registryRef;
    }

    public <E> Optional<TagKey<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
    }

    @Override
    public String toString() {
        return "TagKey[" + this.registry.getValue() + " / " + this.id + "]";
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TagKey.class, "registry;location", "registry", "id"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TagKey.class, "registry;location", "registry", "id"}, this, object);
    }
}

