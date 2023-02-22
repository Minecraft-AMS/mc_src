/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryElementCodec<E>
implements Codec<Supplier<E>> {
    private final RegistryKey<? extends Registry<E>> registryRef;
    private final Codec<E> elementCodec;
    private final boolean allowInlineDefinitions;

    public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return RegistryElementCodec.of(registryRef, elementCodec, true);
    }

    public static <E> Codec<List<Supplier<E>>> method_31194(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return Codec.either((Codec)RegistryElementCodec.of(registryRef, elementCodec, false).listOf(), (Codec)elementCodec.xmap(object -> () -> object, Supplier::get).listOf()).xmap(either -> (List)either.map(list -> list, list -> list), Either::left);
    }

    private static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
        return new RegistryElementCodec<E>(registryRef, elementCodec, allowInlineDefinitions);
    }

    private RegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
        this.registryRef = registryRef;
        this.elementCodec = elementCodec;
        this.allowInlineDefinitions = allowInlineDefinitions;
    }

    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
        if (dynamicOps instanceof RegistryReadingOps) {
            return ((RegistryReadingOps)dynamicOps).encodeOrId(supplier.get(), object, this.registryRef, this.elementCodec);
        }
        return this.elementCodec.encode(supplier.get(), dynamicOps, object);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> ops, T input) {
        if (ops instanceof RegistryOps) {
            return ((RegistryOps)ops).decodeOrId(input, this.registryRef, this.elementCodec, this.allowInlineDefinitions);
        }
        return this.elementCodec.decode(ops, input).map(pair -> pair.mapFirst(object -> () -> object));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((Supplier)input, ops, prefix);
    }
}

