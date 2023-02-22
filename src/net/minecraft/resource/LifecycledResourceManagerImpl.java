/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceFilter;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LifecycledResourceManagerImpl
implements LifecycledResourceManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, NamespaceResourceManager> subManagers;
    private final List<ResourcePack> packs;

    public LifecycledResourceManagerImpl(ResourceType type, List<ResourcePack> packs) {
        this.packs = List.copyOf(packs);
        HashMap<String, NamespaceResourceManager> map = new HashMap<String, NamespaceResourceManager>();
        List list = packs.stream().flatMap(pack -> pack.getNamespaces(type).stream()).distinct().toList();
        for (ResourcePack resourcePack : packs) {
            ResourceFilter resourceFilter = this.parseResourceFilter(resourcePack);
            Set<String> set = resourcePack.getNamespaces(type);
            Predicate<Identifier> predicate = resourceFilter != null ? id -> resourceFilter.isPathBlocked(id.getPath()) : null;
            for (String string : list) {
                boolean bl2;
                boolean bl = set.contains(string);
                boolean bl3 = bl2 = resourceFilter != null && resourceFilter.isNamespaceBlocked(string);
                if (!bl && !bl2) continue;
                NamespaceResourceManager namespaceResourceManager = (NamespaceResourceManager)map.get(string);
                if (namespaceResourceManager == null) {
                    namespaceResourceManager = new NamespaceResourceManager(type, string);
                    map.put(string, namespaceResourceManager);
                }
                if (bl && bl2) {
                    namespaceResourceManager.addPack(resourcePack, predicate);
                    continue;
                }
                if (bl) {
                    namespaceResourceManager.addPack(resourcePack);
                    continue;
                }
                namespaceResourceManager.addPack(resourcePack.getName(), predicate);
            }
        }
        this.subManagers = map;
    }

    @Nullable
    private ResourceFilter parseResourceFilter(ResourcePack pack) {
        try {
            return pack.parseMetadata(ResourceFilter.SERIALIZER);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to get filter section from pack {}", (Object)pack.getName());
            return null;
        }
    }

    @Override
    public Set<String> getAllNamespaces() {
        return this.subManagers.keySet();
    }

    @Override
    public Optional<Resource> getResource(Identifier identifier) {
        ResourceManager resourceManager = this.subManagers.get(identifier.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getResource(identifier);
        }
        return Optional.empty();
    }

    @Override
    public List<Resource> getAllResources(Identifier id) {
        ResourceManager resourceManager = this.subManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getAllResources(id);
        }
        return List.of();
    }

    @Override
    public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        LifecycledResourceManagerImpl.validateStartingPath(startingPath);
        TreeMap<Identifier, Resource> map = new TreeMap<Identifier, Resource>();
        for (NamespaceResourceManager namespaceResourceManager : this.subManagers.values()) {
            map.putAll(namespaceResourceManager.findResources(startingPath, allowedPathPredicate));
        }
        return map;
    }

    @Override
    public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        LifecycledResourceManagerImpl.validateStartingPath(startingPath);
        TreeMap<Identifier, List<Resource>> map = new TreeMap<Identifier, List<Resource>>();
        for (NamespaceResourceManager namespaceResourceManager : this.subManagers.values()) {
            map.putAll(namespaceResourceManager.findAllResources(startingPath, allowedPathPredicate));
        }
        return map;
    }

    private static void validateStartingPath(String startingPath) {
        if (startingPath.endsWith("/")) {
            throw new IllegalArgumentException("Trailing slash in path " + startingPath);
        }
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packs.stream();
    }

    @Override
    public void close() {
        this.packs.forEach(ResourcePack::close);
    }
}

