/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryLookupCodec<E>
extends MapCodec<Registry<E>> {
    private final RegistryKey<? extends Registry<E>> registryKey;

    public static <E> RegistryLookupCodec<E> of(RegistryKey<? extends Registry<E>> registryKey) {
        return new RegistryLookupCodec<E>(registryKey);
    }

    private RegistryLookupCodec(RegistryKey<? extends Registry<E>> registryKey) {
        this.registryKey = registryKey;
    }

    public <T> RecordBuilder<T> encode(Registry<E> registry, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
        return recordBuilder;
    }

    public <T> DataResult<Registry<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        if (dynamicOps instanceof RegistryOps) {
            return ((RegistryOps)dynamicOps).getRegistry(this.registryKey);
        }
        return DataResult.error((String)"Not a registry ops");
    }

    public String toString() {
        return "RegistryLookupCodec[" + this.registryKey + "]";
    }

    public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
        return Stream.empty();
    }

    public /* synthetic */ RecordBuilder encode(Object registry, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
        return this.encode((Registry)registry, dynamicOps, recordBuilder);
    }
}

