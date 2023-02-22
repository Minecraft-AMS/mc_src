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
package net.minecraft.util.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

public class RegistryEntryListCodec<E>
implements Codec<RegistryEntryList<E>> {
    private final RegistryKey<? extends Registry<E>> registry;
    private final Codec<RegistryEntry<E>> entryCodec;
    private final Codec<List<RegistryEntry<E>>> directEntryListCodec;
    private final Codec<Either<TagKey<E>, List<RegistryEntry<E>>>> entryListStorageCodec;

    private static <E> Codec<List<RegistryEntry<E>>> createDirectEntryListCodec(Codec<RegistryEntry<E>> entryCodec, boolean alwaysSerializeAsList) {
        Function function = Codecs.createEqualTypeChecker(RegistryEntry::getType);
        Codec codec = entryCodec.listOf().flatXmap(function, function);
        if (alwaysSerializeAsList) {
            return codec;
        }
        return Codec.either((Codec)codec, entryCodec).xmap(either -> (List)either.map(entries -> entries, List::of), entries -> entries.size() == 1 ? Either.right((Object)((RegistryEntry)entries.get(0))) : Either.left((Object)entries));
    }

    public static <E> Codec<RegistryEntryList<E>> create(RegistryKey<? extends Registry<E>> registryRef, Codec<RegistryEntry<E>> entryCodec, boolean alwaysSerializeAsList) {
        return new RegistryEntryListCodec<E>(registryRef, entryCodec, alwaysSerializeAsList);
    }

    private RegistryEntryListCodec(RegistryKey<? extends Registry<E>> registry, Codec<RegistryEntry<E>> entryCodec, boolean alwaysSerializeAsList) {
        this.registry = registry;
        this.entryCodec = entryCodec;
        this.directEntryListCodec = RegistryEntryListCodec.createDirectEntryListCodec(entryCodec, alwaysSerializeAsList);
        this.entryListStorageCodec = Codec.either(TagKey.codec(registry), this.directEntryListCodec);
    }

    public <T> DataResult<Pair<RegistryEntryList<E>, T>> decode(DynamicOps<T> ops, T input) {
        RegistryOps registryOps;
        Optional optional;
        if (ops instanceof RegistryOps && (optional = (registryOps = (RegistryOps)ops).getRegistry(this.registry)).isPresent()) {
            Registry registry = optional.get();
            return this.entryListStorageCodec.decode(ops, input).map(pair -> pair.mapFirst(either -> (RegistryEntryList)either.map(registry::getOrCreateEntryList, RegistryEntryList::of)));
        }
        return this.decodeDirect(ops, input);
    }

    public <T> DataResult<T> encode(RegistryEntryList<E> registryEntryList, DynamicOps<T> dynamicOps, T object) {
        RegistryOps registryOps;
        Optional optional;
        if (dynamicOps instanceof RegistryOps && (optional = (registryOps = (RegistryOps)dynamicOps).getRegistry(this.registry)).isPresent()) {
            if (!registryEntryList.isOf(optional.get())) {
                return DataResult.error((String)("HolderSet " + registryEntryList + " is not valid in current registry set"));
            }
            return this.entryListStorageCodec.encode((Object)registryEntryList.getStorage().mapRight(List::copyOf), dynamicOps, object);
        }
        return this.encodeDirect(registryEntryList, dynamicOps, object);
    }

    private <T> DataResult<Pair<RegistryEntryList<E>, T>> decodeDirect(DynamicOps<T> ops, T input) {
        return this.entryCodec.listOf().decode(ops, input).flatMap(pair -> {
            ArrayList<RegistryEntry.Direct> list = new ArrayList<RegistryEntry.Direct>();
            for (RegistryEntry registryEntry : (List)pair.getFirst()) {
                if (registryEntry instanceof RegistryEntry.Direct) {
                    RegistryEntry.Direct direct = (RegistryEntry.Direct)registryEntry;
                    list.add(direct);
                    continue;
                }
                return DataResult.error((String)("Can't decode element " + registryEntry + " without registry"));
            }
            return DataResult.success((Object)new Pair(RegistryEntryList.of(list), pair.getSecond()));
        });
    }

    private <T> DataResult<T> encodeDirect(RegistryEntryList<E> entryList, DynamicOps<T> ops, T prefix) {
        return this.directEntryListCodec.encode(entryList.stream().toList(), ops, prefix);
    }

    public /* synthetic */ DataResult encode(Object entryList, DynamicOps ops, Object prefix) {
        return this.encode((RegistryEntryList)entryList, ops, prefix);
    }
}

