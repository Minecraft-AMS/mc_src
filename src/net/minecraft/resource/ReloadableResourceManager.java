/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager
extends ResourceManager,
AutoCloseable {
    default public CompletableFuture<Unit> reload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
        return this.reload(prepareExecutor, applyExecutor, initialStage, packs).whenComplete();
    }

    public ResourceReload reload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<ResourcePack> var4);

    public void registerReloader(ResourceReloader var1);

    @Override
    public void close();
}

