/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.SaveLoading;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;

public record SaveLoader(LifecycledResourceManager resourceManager, DataPackContents dataPackContents, DynamicRegistryManager.Immutable dynamicRegistryManager, SaveProperties saveProperties) implements AutoCloseable
{
    public static CompletableFuture<SaveLoader> load(SaveLoading.ServerConfig serverConfig, SaveLoading.LoadContextSupplier<SaveProperties> savePropertiesSupplier, Executor prepareExecutor, Executor applyExecutor) {
        return SaveLoading.load(serverConfig, savePropertiesSupplier, SaveLoader::new, prepareExecutor, applyExecutor);
    }

    @Override
    public void close() {
        this.resourceManager.close();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveLoader.class, "resourceManager;dataPackResources;registryAccess;worldData", "resourceManager", "dataPackContents", "dynamicRegistryManager", "saveProperties"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveLoader.class, "resourceManager;dataPackResources;registryAccess;worldData", "resourceManager", "dataPackContents", "dynamicRegistryManager", "saveProperties"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveLoader.class, "resourceManager;dataPackResources;registryAccess;worldData", "resourceManager", "dataPackContents", "dynamicRegistryManager", "saveProperties"}, this, object);
    }
}

