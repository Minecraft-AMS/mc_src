/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ProfilingResourceReloader;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl
implements ReloadableResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.newHashMap();
    private final List<ResourceReloadListener> listeners = Lists.newArrayList();
    private final List<ResourceReloadListener> initialListeners = Lists.newArrayList();
    private final Set<String> namespaces = Sets.newLinkedHashSet();
    private final ResourceType type;
    private final Thread mainThread;

    public ReloadableResourceManagerImpl(ResourceType type, Thread mainThread) {
        this.type = type;
        this.mainThread = mainThread;
    }

    public void addPack(ResourcePack pack) {
        for (String string : pack.getNamespaces(this.type)) {
            this.namespaces.add(string);
            NamespaceResourceManager namespaceResourceManager = this.namespaceManagers.get(string);
            if (namespaceResourceManager == null) {
                namespaceResourceManager = new NamespaceResourceManager(this.type, string);
                this.namespaceManagers.put(string, namespaceResourceManager);
            }
            namespaceResourceManager.addPack(pack);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return this.namespaces;
    }

    @Override
    public Resource getResource(Identifier id) throws IOException {
        ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getResource(id);
        }
        throw new FileNotFoundException(id.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier id) {
        ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.containsResource(id);
        }
        return false;
    }

    @Override
    public List<Resource> getAllResources(Identifier id) throws IOException {
        ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getAllResources(id);
        }
        throw new FileNotFoundException(id.toString());
    }

    @Override
    public Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate) {
        HashSet set = Sets.newHashSet();
        for (NamespaceResourceManager namespaceResourceManager : this.namespaceManagers.values()) {
            set.addAll(namespaceResourceManager.findResources(resourceType, pathPredicate));
        }
        ArrayList list = Lists.newArrayList((Iterable)set);
        Collections.sort(list);
        return list;
    }

    private void clear() {
        this.namespaceManagers.clear();
        this.namespaces.clear();
    }

    @Override
    public CompletableFuture<Unit> beginReload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
        ResourceReloadMonitor resourceReloadMonitor = this.beginMonitoredReload(prepareExecutor, applyExecutor, initialStage, packs);
        return resourceReloadMonitor.whenComplete();
    }

    @Override
    public void registerListener(ResourceReloadListener listener) {
        this.listeners.add(listener);
        this.initialListeners.add(listener);
    }

    protected ResourceReloadMonitor beginReloadInner(Executor prepareExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Unit> initialStage) {
        ProfilingResourceReloader resourceReloadMonitor = LOGGER.isDebugEnabled() ? new ProfilingResourceReloader(this, Lists.newArrayList(listeners), prepareExecutor, applyExecutor, initialStage) : ResourceReloader.create(this, Lists.newArrayList(listeners), prepareExecutor, applyExecutor, initialStage);
        this.initialListeners.clear();
        return resourceReloadMonitor;
    }

    @Override
    public ResourceReloadMonitor beginMonitoredReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs) {
        this.clear();
        LOGGER.info("Reloading ResourceManager: {}", (Object)packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));
        for (ResourcePack resourcePack : packs) {
            try {
                this.addPack(resourcePack);
            }
            catch (Exception exception) {
                LOGGER.error("Failed to add resource pack {}", (Object)resourcePack.getName(), (Object)exception);
                return new FailedResourceReloadMonitor(new PackAdditionFailedException(resourcePack, (Throwable)exception));
            }
        }
        return this.beginReloadInner(prepareExecutor, applyExecutor, this.listeners, initialStage);
    }

    static class FailedResourceReloadMonitor
    implements ResourceReloadMonitor {
        private final PackAdditionFailedException exception;
        private final CompletableFuture<Unit> future;

        public FailedResourceReloadMonitor(PackAdditionFailedException exception) {
            this.exception = exception;
            this.future = new CompletableFuture();
            this.future.completeExceptionally(exception);
        }

        @Override
        public CompletableFuture<Unit> whenComplete() {
            return this.future;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public float getProgress() {
            return 0.0f;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean isPrepareStageComplete() {
            return false;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean isApplyStageComplete() {
            return true;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void throwExceptions() {
            throw this.exception;
        }
    }

    public static class PackAdditionFailedException
    extends RuntimeException {
        private final ResourcePack pack;

        public PackAdditionFailedException(ResourcePack pack, Throwable cause) {
            super(pack.getName(), cause);
            this.pack = pack;
        }

        @Environment(value=EnvType.CLIENT)
        public ResourcePack getPack() {
            return this.pack;
        }
    }
}

