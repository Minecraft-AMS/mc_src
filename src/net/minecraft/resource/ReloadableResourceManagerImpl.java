/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

public class ReloadableResourceManagerImpl
implements ResourceManager,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private LifecycledResourceManager activeManager;
    private final List<ResourceReloader> reloaders = Lists.newArrayList();
    private final ResourceType type;

    public ReloadableResourceManagerImpl(ResourceType type) {
        this.type = type;
        this.activeManager = new LifecycledResourceManagerImpl(type, List.of());
    }

    @Override
    public void close() {
        this.activeManager.close();
    }

    public void registerReloader(ResourceReloader reloader) {
        this.reloaders.add(reloader);
    }

    public ResourceReload reload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs) {
        LOGGER.info("Reloading ResourceManager: {}", LogUtils.defer(() -> packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", "))));
        this.activeManager.close();
        this.activeManager = new LifecycledResourceManagerImpl(this.type, packs);
        return SimpleResourceReload.start(this.activeManager, this.reloaders, prepareExecutor, applyExecutor, initialStage, LOGGER.isDebugEnabled());
    }

    @Override
    public Resource getResource(Identifier identifier) throws IOException {
        return this.activeManager.getResource(identifier);
    }

    @Override
    public Set<String> getAllNamespaces() {
        return this.activeManager.getAllNamespaces();
    }

    @Override
    public boolean containsResource(Identifier id) {
        return this.activeManager.containsResource(id);
    }

    @Override
    public List<Resource> getAllResources(Identifier id) throws IOException {
        return this.activeManager.getAllResources(id);
    }

    @Override
    public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
        return this.activeManager.findResources(startingPath, pathPredicate);
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return this.activeManager.streamResourcePacks();
    }
}

