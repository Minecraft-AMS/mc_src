/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagFile;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String JSON_EXTENSION = ".json";
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    final Function<Identifier, Optional<T>> registryGetter;
    private final String dataType;

    public TagGroupLoader(Function<Identifier, Optional<T>> registryGetter, String dataType) {
        this.registryGetter = registryGetter;
        this.dataType = dataType;
    }

    public Map<Identifier, List<TrackedEntry>> loadTags(ResourceManager manager) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<Identifier, List<Resource>> entry : manager.findAllResources(this.dataType, id -> id.getPath().endsWith(JSON_EXTENSION)).entrySet()) {
            Identifier identifier2 = entry.getKey();
            String string = identifier2.getPath();
            Identifier identifier22 = new Identifier(identifier2.getNamespace(), string.substring(this.dataType.length() + 1, string.length() - JSON_EXTENSION_LENGTH));
            for (Resource resource : entry.getValue()) {
                try {
                    BufferedReader reader = resource.getReader();
                    try {
                        JsonElement jsonElement = JsonParser.parseReader((Reader)reader);
                        List list = map.computeIfAbsent(identifier22, identifier -> new ArrayList());
                        TagFile tagFile = (TagFile)TagFile.CODEC.parse(new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement)).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0));
                        if (tagFile.replace()) {
                            list.clear();
                        }
                        String string2 = resource.getResourcePackName();
                        tagFile.entries().forEach(tagEntry -> list.add(new TrackedEntry((TagEntry)tagEntry, string2)));
                    }
                    finally {
                        if (reader == null) continue;
                        ((Reader)reader).close();
                    }
                }
                catch (Exception exception) {
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{identifier22, identifier2, resource.getResourcePackName(), exception});
                }
            }
        }
        return map;
    }

    private static void method_32839(Map<Identifier, List<TrackedEntry>> map, Multimap<Identifier, Identifier> multimap, Set<Identifier> set, Identifier identifier2, BiConsumer<Identifier, List<TrackedEntry>> biConsumer) {
        if (!set.add(identifier2)) {
            return;
        }
        multimap.get((Object)identifier2).forEach(identifier -> TagGroupLoader.method_32839(map, multimap, set, identifier, biConsumer));
        List<TrackedEntry> list = map.get(identifier2);
        if (list != null) {
            biConsumer.accept(identifier2, list);
        }
    }

    private static boolean method_32836(Multimap<Identifier, Identifier> multimap, Identifier identifier, Identifier identifier22) {
        Collection collection = multimap.get((Object)identifier22);
        if (collection.contains(identifier)) {
            return true;
        }
        return collection.stream().anyMatch(identifier2 -> TagGroupLoader.method_32836(multimap, identifier, identifier2));
    }

    private static void method_32844(Multimap<Identifier, Identifier> multimap, Identifier identifier, Identifier identifier2) {
        if (!TagGroupLoader.method_32836(multimap, identifier, identifier2)) {
            multimap.put((Object)identifier, (Object)identifier2);
        }
    }

    private Either<Collection<TrackedEntry>, Collection<T>> method_43952(TagEntry.ValueGetter<T> valueGetter, List<TrackedEntry> list) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        ArrayList<TrackedEntry> list2 = new ArrayList<TrackedEntry>();
        for (TrackedEntry trackedEntry : list) {
            if (trackedEntry.entry().resolve(valueGetter, arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0))) continue;
            list2.add(trackedEntry);
        }
        return list2.isEmpty() ? Either.right((Object)builder.build()) : Either.left(list2);
    }

    public Map<Identifier, Collection<T>> buildGroup(Map<Identifier, List<TrackedEntry>> map) {
        final HashMap map2 = Maps.newHashMap();
        TagEntry.ValueGetter valueGetter = new TagEntry.ValueGetter<T>(){

            @Override
            @Nullable
            public T direct(Identifier id) {
                return TagGroupLoader.this.registryGetter.apply(id).orElse(null);
            }

            @Override
            @Nullable
            public Collection<T> tag(Identifier id) {
                return (Collection)map2.get(id);
            }
        };
        HashMultimap multimap = HashMultimap.create();
        map.forEach((arg_0, arg_1) -> TagGroupLoader.method_32843((Multimap)multimap, arg_0, arg_1));
        map.forEach((arg_0, arg_1) -> TagGroupLoader.method_32835((Multimap)multimap, arg_0, arg_1));
        HashSet set = Sets.newHashSet();
        map.keySet().forEach(arg_0 -> this.method_32838(map, (Multimap)multimap, set, valueGetter, map2, arg_0));
        return map2;
    }

    public Map<Identifier, Collection<T>> load(ResourceManager manager) {
        return this.buildGroup(this.loadTags(manager));
    }

    private /* synthetic */ void method_32838(Map map, Multimap multimap, Set set, TagEntry.ValueGetter valueGetter, Map map2, Identifier identifier2) {
        TagGroupLoader.method_32839(map, (Multimap<Identifier, Identifier>)multimap, set, identifier2, (identifier, list) -> this.method_43952(valueGetter, (List<TrackedEntry>)list).ifLeft(collection -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", identifier, (Object)collection.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight(collection -> map2.put(identifier, collection)));
    }

    private static /* synthetic */ void method_32835(Multimap multimap, Identifier identifier, List list) {
        list.forEach(trackedEntry -> trackedEntry.entry.forEachOptionalTagId(identifier2 -> TagGroupLoader.method_32844((Multimap<Identifier, Identifier>)multimap, identifier, identifier2)));
    }

    private static /* synthetic */ void method_32843(Multimap multimap, Identifier identifier, List list) {
        list.forEach(trackedEntry -> trackedEntry.entry.forEachRequiredTagId(identifier2 -> TagGroupLoader.method_32844((Multimap<Identifier, Identifier>)multimap, identifier, identifier2)));
    }

    public static final class TrackedEntry
    extends Record {
        final TagEntry entry;
        private final String source;

        public TrackedEntry(TagEntry tagEntry, String source) {
            this.entry = tagEntry;
            this.source = source;
        }

        @Override
        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrackedEntry.class, "entry;source", "entry", "source"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrackedEntry.class, "entry;source", "entry", "source"}, this, object);
        }

        public TagEntry entry() {
            return this.entry;
        }

        public String source() {
            return this.source;
        }
    }
}

