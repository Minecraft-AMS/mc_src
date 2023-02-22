/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryCodec;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final ObjectList<T> rawIdToEntry = new ObjectArrayList(256);
    private final Object2IntMap<T> entryToRawId = new Object2IntOpenCustomHashMap(Util.identityHashStrategy());
    private final BiMap<Identifier, T> idToEntry;
    private final BiMap<RegistryKey<T>, T> keyToEntry;
    private final Map<T, Lifecycle> entryToLifecycle;
    private Lifecycle lifecycle;
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
        this.entryToRawId.defaultReturnValue(-1);
        this.idToEntry = HashBiMap.create();
        this.keyToEntry = HashBiMap.create();
        this.entryToLifecycle = Maps.newIdentityHashMap();
        this.lifecycle = lifecycle;
    }

    public static <T> MapCodec<RegistryManagerEntry<T>> createRegistryManagerEntryCodec(RegistryKey<? extends Registry<T>> key, MapCodec<T> entryCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.xmap(RegistryKey.createKeyFactory(key), RegistryKey::getValue).fieldOf("name").forGetter(registryManagerEntry -> registryManagerEntry.key), (App)Codec.INT.fieldOf("id").forGetter(registryManagerEntry -> registryManagerEntry.rawId), (App)entryCodec.forGetter(registryManagerEntry -> registryManagerEntry.entry)).apply((Applicative)instance, RegistryManagerEntry::new));
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle) {
        return this.set(rawId, key, entry, lifecycle, true);
    }

    private <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle, boolean checkDuplicateKeys) {
        Validate.notNull(key);
        Validate.notNull(entry);
        this.rawIdToEntry.size(Math.max(this.rawIdToEntry.size(), rawId + 1));
        this.rawIdToEntry.set(rawId, entry);
        this.entryToRawId.put(entry, rawId);
        this.randomEntries = null;
        if (checkDuplicateKeys && this.keyToEntry.containsKey(key)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", key);
        }
        if (this.idToEntry.containsValue(entry)) {
            LOGGER.error("Adding duplicate value '{}' to registry", entry);
        }
        this.idToEntry.put((Object)key.getValue(), entry);
        this.keyToEntry.put(key, entry);
        this.entryToLifecycle.put(entry, lifecycle);
        this.lifecycle = this.lifecycle.add(lifecycle);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        return entry;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle) {
        return this.set(this.nextId, key, entry, lifecycle);
    }

    @Override
    public <V extends T> V replace(OptionalInt rawId, RegistryKey<T> key, V newEntry, Lifecycle lifecycle) {
        int i;
        Validate.notNull(key);
        Validate.notNull(newEntry);
        Object object = this.keyToEntry.get(key);
        if (object == null) {
            i = rawId.isPresent() ? rawId.getAsInt() : this.nextId;
        } else {
            i = this.entryToRawId.getInt(object);
            if (rawId.isPresent() && rawId.getAsInt() != i) {
                throw new IllegalStateException("ID mismatch");
            }
            this.entryToRawId.removeInt(object);
            this.entryToLifecycle.remove(object);
        }
        return this.set(i, key, newEntry, lifecycle, false);
    }

    @Override
    @Nullable
    public Identifier getId(T entry) {
        return (Identifier)this.idToEntry.inverse().get(entry);
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T entry) {
        return Optional.ofNullable(this.keyToEntry.inverse().get(entry));
    }

    @Override
    public int getRawId(@Nullable T entry) {
        return this.entryToRawId.getInt(entry);
    }

    @Override
    @Nullable
    public T get(@Nullable RegistryKey<T> key) {
        return (T)this.keyToEntry.get(key);
    }

    @Override
    @Nullable
    public T get(int index) {
        if (index < 0 || index >= this.rawIdToEntry.size()) {
            return null;
        }
        return (T)this.rawIdToEntry.get(index);
    }

    @Override
    public Lifecycle getEntryLifecycle(T entry) {
        return this.entryToLifecycle.get(entry);
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter((Iterator)this.rawIdToEntry.iterator(), Objects::nonNull);
    }

    @Override
    @Nullable
    public T get(@Nullable Identifier id) {
        return (T)this.idToEntry.get((Object)id);
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.idToEntry.keySet());
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntries() {
        return Collections.unmodifiableMap(this.keyToEntry).entrySet();
    }

    @Nullable
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Set collection = this.idToEntry.values();
            if (collection.isEmpty()) {
                return null;
            }
            this.randomEntries = collection.toArray(new Object[collection.size()]);
        }
        return (T)Util.getRandom(this.randomEntries, random);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsId(Identifier id) {
        return this.idToEntry.containsKey((Object)id);
    }

    public static <T> Codec<SimpleRegistry<T>> createRegistryManagerCodec(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, Codec<T> entryCodec) {
        return SimpleRegistry.createRegistryManagerEntryCodec(key, entryCodec.fieldOf("element")).codec().listOf().xmap(list -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(key, lifecycle);
            for (RegistryManagerEntry registryManagerEntry : list) {
                simpleRegistry.set(registryManagerEntry.rawId, registryManagerEntry.key, registryManagerEntry.entry, lifecycle);
            }
            return simpleRegistry;
        }, simpleRegistry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : simpleRegistry) {
                builder.add(new RegistryManagerEntry(simpleRegistry.getKey(object).get(), simpleRegistry.getRawId(object), object));
            }
            return builder.build();
        });
    }

    public static <T> Codec<SimpleRegistry<T>> createRegistryCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> entryCodec) {
        return RegistryCodec.of(registryRef, lifecycle, entryCodec);
    }

    public static <T> Codec<SimpleRegistry<T>> createCodec(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, Codec<T> entryCodec) {
        return Codec.unboundedMap((Codec)Identifier.CODEC.xmap(RegistryKey.createKeyFactory(key), RegistryKey::getValue), entryCodec).xmap(map -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(key, lifecycle);
            map.forEach((? super K registryKey, ? super V object) -> simpleRegistry.add((RegistryKey)registryKey, (Object)object, lifecycle));
            return simpleRegistry;
        }, simpleRegistry -> ImmutableMap.copyOf(simpleRegistry.keyToEntry));
    }

    public static class RegistryManagerEntry<T> {
        public final RegistryKey<T> key;
        public final int rawId;
        public final T entry;

        public RegistryManagerEntry(RegistryKey<T> key, int rawId, T entry) {
            this.key = key;
            this.rawId = rawId;
            this.entry = entry;
        }
    }
}

