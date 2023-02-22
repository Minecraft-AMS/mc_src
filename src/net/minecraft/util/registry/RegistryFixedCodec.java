/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryFixedCodec<E>
implements Codec<RegistryEntry<E>> {
    private final RegistryKey<? extends Registry<E>> registry;

    public static <E> RegistryFixedCodec<E> of(RegistryKey<? extends Registry<E>> registry) {
        return new RegistryFixedCodec<E>(registry);
    }

    private RegistryFixedCodec(RegistryKey<? extends Registry<E>> registry) {
        this.registry = registry;
    }

    public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> dynamicOps, T object) {
        RegistryOps registryOps;
        Optional optional;
        if (dynamicOps instanceof RegistryOps && (optional = (registryOps = (RegistryOps)dynamicOps).getRegistry(this.registry)).isPresent()) {
            if (!registryEntry.matchesRegistry(optional.get())) {
                return DataResult.error((String)("Element " + registryEntry + " is not valid in current registry set"));
            }
            return (DataResult)registryEntry.getKeyOrValue().map(registryKey -> Identifier.CODEC.encode((Object)registryKey.getValue(), dynamicOps, object), value -> DataResult.error((String)("Elements from registry " + this.registry + " can't be serialized to a value")));
        }
        return DataResult.error((String)("Can't access registry " + this.registry));
    }

    public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
        RegistryOps registryOps;
        Optional optional;
        if (ops instanceof RegistryOps && (optional = (registryOps = (RegistryOps)ops).getRegistry(this.registry)).isPresent()) {
            return Identifier.CODEC.decode(ops, input).map(pair -> pair.mapFirst(value -> ((Registry)optional.get()).getOrCreateEntry(RegistryKey.of(this.registry, value))));
        }
        return DataResult.error((String)("Can't access registry " + this.registry));
    }

    public String toString() {
        return "RegistryFixedCodec[" + this.registry + "]";
    }

    public /* synthetic */ DataResult encode(Object entry, DynamicOps ops, Object prefix) {
        return this.encode((RegistryEntry)entry, ops, prefix);
    }
}

