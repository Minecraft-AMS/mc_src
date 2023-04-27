/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Maps
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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.resource.DependencyTracker;
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

    private Either<Collection<TrackedEntry>, Collection<T>> resolveAll(TagEntry.ValueGetter<T> valueGetter, List<TrackedEntry> entries) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        ArrayList<TrackedEntry> list = new ArrayList<TrackedEntry>();
        for (TrackedEntry trackedEntry : entries) {
            if (trackedEntry.entry().resolve(valueGetter, arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0))) continue;
            list.add(trackedEntry);
        }
        return list.isEmpty() ? Either.right((Object)builder.build()) : Either.left(list);
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
        DependencyTracker<Identifier, class_8522> dependencyTracker = new DependencyTracker<Identifier, class_8522>();
        map.forEach((id, list) -> dependencyTracker.add((Identifier)id, new class_8522((List<TrackedEntry>)list)));
        dependencyTracker.traverse((id, arg) -> this.resolveAll(valueGetter, arg.entries).ifLeft(missingReferences -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", id, (Object)missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight(resolvedEntries -> map2.put(id, resolvedEntries)));
        return map2;
    }

    public Map<Identifier, Collection<T>> load(ResourceManager manager) {
        return this.buildGroup(this.loadTags(manager));
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

    static final class class_8522
    extends Record
    implements DependencyTracker.Dependencies<Identifier> {
        final List<TrackedEntry> entries;

        class_8522(List<TrackedEntry> list) {
            this.entries = list;
        }

        @Override
        public void forDependencies(Consumer<Identifier> callback) {
            this.entries.forEach(trackedEntry -> trackedEntry.entry.forEachRequiredTagId(callback));
        }

        @Override
        public void forOptionalDependencies(Consumer<Identifier> callback) {
            this.entries.forEach(trackedEntry -> trackedEntry.entry.forEachOptionalTagId(callback));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{class_8522.class, "entries", "entries"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{class_8522.class, "entries", "entries"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{class_8522.class, "entries", "entries"}, this, object);
        }

        public List<TrackedEntry> entries() {
            return this.entries;
        }
    }
}

