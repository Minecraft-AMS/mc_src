/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NamespaceResourceManager
implements ResourceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    protected final List<FilterablePack> packList = Lists.newArrayList();
    final ResourceType type;
    private final String namespace;

    public NamespaceResourceManager(ResourceType type, String namespace) {
        this.type = type;
        this.namespace = namespace;
    }

    public void addPack(ResourcePack pack) {
        this.addPack(pack.getName(), pack, null);
    }

    public void addPack(ResourcePack pack, Predicate<Identifier> filter) {
        this.addPack(pack.getName(), pack, filter);
    }

    public void addPack(String name, Predicate<Identifier> filter) {
        this.addPack(name, null, filter);
    }

    private void addPack(String name, @Nullable ResourcePack underlyingPack, @Nullable Predicate<Identifier> filter) {
        this.packList.add(new FilterablePack(name, underlyingPack, filter));
    }

    @Override
    public Set<String> getAllNamespaces() {
        return ImmutableSet.of((Object)this.namespace);
    }

    @Override
    public Optional<Resource> getResource(Identifier identifier) {
        if (!this.isPathAbsolute(identifier)) {
            return Optional.empty();
        }
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            FilterablePack filterablePack = this.packList.get(i);
            ResourcePack resourcePack = filterablePack.underlying;
            if (resourcePack != null && resourcePack.contains(this.type, identifier)) {
                return Optional.of(new Resource(resourcePack.getName(), this.createOpener(identifier, resourcePack), this.createMetadataSupplier(identifier, i)));
            }
            if (!filterablePack.isFiltered(identifier)) continue;
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", (Object)identifier, (Object)filterablePack.name);
            return Optional.empty();
        }
        return Optional.empty();
    }

    Resource.InputSupplier<InputStream> createOpener(Identifier id, ResourcePack pack) {
        if (LOGGER.isDebugEnabled()) {
            return () -> {
                InputStream inputStream = pack.open(this.type, id);
                return new DebugInputStream(inputStream, id, pack.getName());
            };
        }
        return () -> pack.open(this.type, id);
    }

    private boolean isPathAbsolute(Identifier id) {
        return !id.getPath().contains("..");
    }

    @Override
    public List<Resource> getAllResources(Identifier id) {
        if (!this.isPathAbsolute(id)) {
            return List.of();
        }
        ArrayList list = Lists.newArrayList();
        Identifier identifier = NamespaceResourceManager.getMetadataPath(id);
        String string = null;
        for (FilterablePack filterablePack : this.packList) {
            ResourcePack resourcePack;
            if (filterablePack.isFiltered(id)) {
                if (!list.isEmpty()) {
                    string = filterablePack.name;
                }
                list.clear();
            } else if (filterablePack.isFiltered(identifier)) {
                list.forEach(Entry::ignoreMetadata);
            }
            if ((resourcePack = filterablePack.underlying) == null || !resourcePack.contains(this.type, id)) continue;
            list.add(new Entry(id, identifier, resourcePack));
        }
        if (list.isEmpty() && string != null) {
            LOGGER.info("Resource {} was filtered by pack {}", (Object)id, string);
        }
        return list.stream().map(Entry::toReference).toList();
    }

    @Override
    public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        int i = this.packList.size();
        for (int j = 0; j < i; ++j) {
            FilterablePack filterablePack = this.packList.get(j);
            filterablePack.removeFiltered((Collection<Identifier>)object2IntMap.keySet());
            if (filterablePack.underlying == null) continue;
            for (Identifier identifier : filterablePack.underlying.findResources(this.type, this.namespace, startingPath, allowedPathPredicate)) {
                object2IntMap.put((Object)identifier, j);
            }
        }
        TreeMap map = Maps.newTreeMap();
        for (Object2IntMap.Entry entry : Object2IntMaps.fastIterable((Object2IntMap)object2IntMap)) {
            int k = entry.getIntValue();
            Identifier identifier2 = (Identifier)entry.getKey();
            ResourcePack resourcePack = this.packList.get((int)k).underlying;
            map.put(identifier2, new Resource(resourcePack.getName(), this.createOpener(identifier2, resourcePack), this.createMetadataSupplier(identifier2, k)));
        }
        return map;
    }

    private Resource.InputSupplier<ResourceMetadata> createMetadataSupplier(Identifier id, int index) {
        return () -> {
            Identifier identifier2 = NamespaceResourceManager.getMetadataPath(id);
            for (int j = this.packList.size() - 1; j >= index; --j) {
                FilterablePack filterablePack = this.packList.get(j);
                ResourcePack resourcePack = filterablePack.underlying;
                if (resourcePack != null && resourcePack.contains(this.type, identifier2)) {
                    try (InputStream inputStream = resourcePack.open(this.type, identifier2);){
                        ResourceMetadata resourceMetadata = ResourceMetadata.create(inputStream);
                        return resourceMetadata;
                    }
                }
                if (filterablePack.isFiltered(identifier2)) break;
            }
            return ResourceMetadata.NONE;
        };
    }

    private static void applyFilter(FilterablePack pack, Map<Identifier, EntryList> idToEntryList) {
        Iterator<Map.Entry<Identifier, EntryList>> iterator = idToEntryList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Identifier, EntryList> entry = iterator.next();
            Identifier identifier = entry.getKey();
            EntryList entryList = entry.getValue();
            if (pack.isFiltered(identifier)) {
                iterator.remove();
                continue;
            }
            if (!pack.isFiltered(entryList.metadataId())) continue;
            entryList.entries.forEach(Entry::ignoreMetadata);
        }
    }

    private void findAndAdd(FilterablePack pack, String startingPath, Predicate<Identifier> allowedPathPredicate, Map<Identifier, EntryList> idToEntryList) {
        ResourcePack resourcePack = pack.underlying;
        if (resourcePack == null) {
            return;
        }
        for (Identifier identifier : resourcePack.findResources(this.type, this.namespace, startingPath, allowedPathPredicate)) {
            Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier);
            idToEntryList.computeIfAbsent(identifier, id -> new EntryList(identifier2, Lists.newArrayList())).entries().add(new Entry(identifier, identifier2, resourcePack));
        }
    }

    @Override
    public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        HashMap map = Maps.newHashMap();
        for (FilterablePack filterablePack : this.packList) {
            NamespaceResourceManager.applyFilter(filterablePack, map);
            this.findAndAdd(filterablePack, startingPath, allowedPathPredicate, map);
        }
        TreeMap treeMap = Maps.newTreeMap();
        map.forEach((id, entryList) -> treeMap.put(id, entryList.toReferenceList()));
        return treeMap;
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packList.stream().map(pack -> pack.underlying).filter(Objects::nonNull);
    }

    static Identifier getMetadataPath(Identifier id) {
        return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
    }

    static final class FilterablePack
    extends Record {
        final String name;
        @Nullable
        final ResourcePack underlying;
        @Nullable
        private final Predicate<Identifier> filter;

        FilterablePack(String string, @Nullable ResourcePack resourcePack, @Nullable Predicate<Identifier> predicate) {
            this.name = string;
            this.underlying = resourcePack;
            this.filter = predicate;
        }

        public void removeFiltered(Collection<Identifier> ids) {
            if (this.filter != null) {
                ids.removeIf(this.filter);
            }
        }

        public boolean isFiltered(Identifier id) {
            return this.filter != null && this.filter.test(id);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FilterablePack.class, "name;resources;filter", "name", "underlying", "filter"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FilterablePack.class, "name;resources;filter", "name", "underlying", "filter"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FilterablePack.class, "name;resources;filter", "name", "underlying", "filter"}, this, object);
        }

        public String name() {
            return this.name;
        }

        @Nullable
        public ResourcePack underlying() {
            return this.underlying;
        }

        @Nullable
        public Predicate<Identifier> filter() {
            return this.filter;
        }
    }

    class Entry {
        private final Identifier id;
        private final Identifier metadataId;
        private final ResourcePack pack;
        private boolean checksMetadata = true;

        Entry(Identifier id, Identifier metadataId, ResourcePack pack) {
            this.pack = pack;
            this.id = id;
            this.metadataId = metadataId;
        }

        public void ignoreMetadata() {
            this.checksMetadata = false;
        }

        public Resource toReference() {
            String string = this.pack.getName();
            if (this.checksMetadata) {
                return new Resource(string, NamespaceResourceManager.this.createOpener(this.id, this.pack), () -> {
                    if (this.pack.contains(NamespaceResourceManager.this.type, this.metadataId)) {
                        try (InputStream inputStream = this.pack.open(NamespaceResourceManager.this.type, this.metadataId);){
                            ResourceMetadata resourceMetadata = ResourceMetadata.create(inputStream);
                            return resourceMetadata;
                        }
                    }
                    return ResourceMetadata.NONE;
                });
            }
            return new Resource(string, NamespaceResourceManager.this.createOpener(this.id, this.pack));
        }
    }

    static final class EntryList
    extends Record {
        private final Identifier metadataId;
        final List<Entry> entries;

        EntryList(Identifier identifier, List<Entry> list) {
            this.metadataId = identifier;
            this.entries = list;
        }

        List<Resource> toReferenceList() {
            return this.entries().stream().map(Entry::toReference).toList();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntryList.class, "metadataLocation;entries", "metadataId", "entries"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntryList.class, "metadataLocation;entries", "metadataId", "entries"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntryList.class, "metadataLocation;entries", "metadataId", "entries"}, this, object);
        }

        public Identifier metadataId() {
            return this.metadataId;
        }

        public List<Entry> entries() {
            return this.entries;
        }
    }

    static class DebugInputStream
    extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream parent, Identifier id, String packName) {
            super(parent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + byteArrayOutputStream;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                LOGGER.warn(this.leakMessage);
            }
            super.finalize();
        }
    }
}

