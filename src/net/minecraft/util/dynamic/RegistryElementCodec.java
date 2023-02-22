/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLoader;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryElementCodec<E>
implements Codec<RegistryEntry<E>> {
    private final RegistryKey<? extends Registry<E>> registryRef;
    private final Codec<E> elementCodec;
    private final boolean allowInlineDefinitions;

    public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return RegistryElementCodec.of(registryRef, elementCodec, true);
    }

    private static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
        return new RegistryElementCodec<E>(registryRef, elementCodec, allowInlineDefinitions);
    }

    private RegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
        this.registryRef = registryRef;
        this.elementCodec = elementCodec;
        this.allowInlineDefinitions = allowInlineDefinitions;
    }

    public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> dynamicOps, T object) {
        RegistryOps registryOps;
        Optional optional;
        if (dynamicOps instanceof RegistryOps && (optional = (registryOps = (RegistryOps)dynamicOps).getRegistry(this.registryRef)).isPresent()) {
            if (!registryEntry.matchesRegistry(optional.get())) {
                return DataResult.error((String)("Element " + registryEntry + " is not valid in current registry set"));
            }
            return (DataResult)registryEntry.getKeyOrValue().map(key -> Identifier.CODEC.encode((Object)key.getValue(), dynamicOps, object), value -> this.elementCodec.encode(value, dynamicOps, object));
        }
        return this.elementCodec.encode(registryEntry.value(), dynamicOps, object);
    }

    public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
        if (ops instanceof RegistryOps) {
            RegistryOps registryOps = (RegistryOps)ops;
            Optional optional = registryOps.getRegistry(this.registryRef);
            if (optional.isEmpty()) {
                return DataResult.error((String)("Registry does not exist: " + this.registryRef));
            }
            Registry registry = optional.get();
            DataResult dataResult = Identifier.CODEC.decode(ops, input);
            if (dataResult.result().isEmpty()) {
                if (!this.allowInlineDefinitions) {
                    return DataResult.error((String)"Inline definitions not allowed here");
                }
                return this.elementCodec.decode(ops, input).map(pair -> pair.mapFirst(RegistryEntry::of));
            }
            Pair pair2 = (Pair)dataResult.result().get();
            RegistryKey registryKey = RegistryKey.of(this.registryRef, (Identifier)pair2.getFirst());
            Optional<RegistryLoader.LoaderAccess> optional2 = registryOps.getLoaderAccess();
            if (optional2.isPresent()) {
                return optional2.get().load(this.registryRef, this.elementCodec, registryKey, registryOps.getEntryOps()).map(entry -> Pair.of((Object)entry, (Object)pair2.getSecond()));
            }
            RegistryEntry registryEntry = registry.getOrCreateEntry(registryKey);
            return DataResult.success((Object)Pair.of(registryEntry, (Object)pair2.getSecond()), (Lifecycle)Lifecycle.stable());
        }
        return this.elementCodec.decode(ops, input).map(pair -> pair.mapFirst(RegistryEntry::of));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((RegistryEntry)input, ops, prefix);
    }
}

