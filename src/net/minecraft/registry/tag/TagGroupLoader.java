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
package net.minecraft.registry.tag;

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
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final Function<Identifier, Optional<? extends T>> registryGetter;
    private final String dataType;

    public TagGroupLoader(Function<Identifier, Optional<? extends T>> registryGetter, String dataType) {
        this.registryGetter = registryGetter;
        this.dataType = dataType;
    }

    public Map<Identifier, List<TrackedEntry>> loadTags(ResourceManager resourceManager) {
        HashMap map = Maps.newHashMap();
        ResourceFinder resourceFinder = ResourceFinder.json(this.dataType);
        for (Map.Entry<Identifier, List<Resource>> entry2 : resourceFinder.findAllResources(resourceManager).entrySet()) {
            Identifier identifier = entry2.getKey();
            Identifier identifier2 = resourceFinder.toResourceId(identifier);
            for (Resource resource : entry2.getValue()) {
                try {
                    BufferedReader reader = resource.getReader();
                    try {
                        JsonElement jsonElement = JsonParser.parseReader((Reader)reader);
                        List list = map.computeIfAbsent(identifier2, id -> new ArrayList());
                        TagFile tagFile = (TagFile)TagFile.CODEC.parse(new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement)).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0));
                        if (tagFile.replace()) {
                            list.clear();
                        }
                        String string = resource.getResourcePackName();
                        tagFile.entries().forEach(entry -> list.add(new TrackedEntry((TagEntry)entry, string)));
                    }
                    finally {
                        if (reader == null) continue;
                        ((Reader)reader).close();
                    }
                }
                catch (Exception exception) {
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{identifier2, identifier, resource.getResourcePackName(), exception});
                }
            }
        }
        return map;
    }

    private static void resolveAll(Map<Identifier, List<TrackedEntry>> tags, Multimap<Identifier, Identifier> referencedTagIdsByTagId, Set<Identifier> alreadyResolved, Identifier tagId, BiConsumer<Identifier, List<TrackedEntry>> resolver) {
        if (!alreadyResolved.add(tagId)) {
            return;
        }
        referencedTagIdsByTagId.get((Object)tagId).forEach(resolvedTagId -> TagGroupLoader.resolveAll(tags, referencedTagIdsByTagId, alreadyResolved, resolvedTagId, resolver));
        List<TrackedEntry> list = tags.get(tagId);
        if (list != null) {
            resolver.accept(tagId, list);
        }
    }

    private static boolean hasCircularDependency(Multimap<Identifier, Identifier> referencedTagIdsByTagId, Identifier tagId, Identifier referencedTagId) {
        Collection collection = referencedTagIdsByTagId.get((Object)referencedTagId);
        if (collection.contains(tagId)) {
            return true;
        }
        return collection.stream().anyMatch(id -> TagGroupLoader.hasCircularDependency(referencedTagIdsByTagId, tagId, id));
    }

    private static void addReference(Multimap<Identifier, Identifier> referencedTagIdsByTagId, Identifier tagId, Identifier referencedTagId) {
        if (!TagGroupLoader.hasCircularDependency(referencedTagIdsByTagId, tagId, referencedTagId)) {
            referencedTagIdsByTagId.put((Object)tagId, (Object)referencedTagId);
        }
    }

    private Either<Collection<TrackedEntry>, Collection<T>> resolveAll(TagEntry.ValueGetter<T> valueGetter, List<TrackedEntry> entries) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        ArrayList<TrackedEntry> list = new ArrayList<TrackedEntry>();
        for (TrackedEntry trackedEntry : entries) {
            if (trackedEntry.entry().resolve(valueGetter, arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0))) continue;
            list.add(trackedEntry);
        }
        return list.isEmpty() ? Either.right((Object)builder.build()) : Either.left(list);
    }

    public Map<Identifier, Collection<T>> buildGroup(Map<Identifier, List<TrackedEntry>> tags) {
        final HashMap map = Maps.newHashMap();
        TagEntry.ValueGetter valueGetter = new TagEntry.ValueGetter<T>(){

            @Override
            @Nullable
            public T direct(Identifier id) {
                return TagGroupLoader.this.registryGetter.apply(id).orElse(null);
            }

            @Override
            @Nullable
            public Collection<T> tag(Identifier id) {
                return (Collection)map.get(id);
            }
        };
        HashMultimap multimap = HashMultimap.create();
        tags.forEach((arg_0, arg_1) -> TagGroupLoader.method_32843((Multimap)multimap, arg_0, arg_1));
        tags.forEach((arg_0, arg_1) -> TagGroupLoader.method_32835((Multimap)multimap, arg_0, arg_1));
        HashSet set = Sets.newHashSet();
        tags.keySet().forEach(arg_0 -> this.method_32838(tags, (Multimap)multimap, set, valueGetter, map, arg_0));
        return map;
    }

    public Map<Identifier, Collection<T>> load(ResourceManager manager) {
        return this.buildGroup(this.loadTags(manager));
    }

    private /* synthetic */ void method_32838(Map map, Multimap multimap, Set set, TagEntry.ValueGetter valueGetter, Map map2, Identifier tagId) {
        TagGroupLoader.resolveAll(map, (Multimap<Identifier, Identifier>)multimap, set, tagId, (tagId2, entries) -> this.resolveAll(valueGetter, (List<TrackedEntry>)entries).ifLeft(missingReferences -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", tagId2, (Object)missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight(resolvedEntries -> map2.put(tagId2, resolvedEntries)));
    }

    private static /* synthetic */ void method_32835(Multimap multimap, Identifier tagId, List entries) {
        entries.forEach(entry -> entry.entry.forEachOptionalTagId(referencedTagId -> TagGroupLoader.addReference((Multimap<Identifier, Identifier>)multimap, tagId, referencedTagId)));
    }

    private static /* synthetic */ void method_32843(Multimap multimap, Identifier tagId, List entries) {
        entries.forEach(entry -> entry.entry.forEachRequiredTagId(referencedTagId -> TagGroupLoader.addReference((Multimap<Identifier, Identifier>)multimap, tagId, referencedTagId)));
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

