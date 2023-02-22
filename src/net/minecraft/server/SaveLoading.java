/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public class SaveLoading {
    public static <D, R> CompletableFuture<R> load(ServerConfig serverConfig, LoadContextSupplier<D> loadContextSupplier, SaveApplierFactory<D, R> saveApplierFactory, Executor prepareExecutor, Executor applyExecutor) {
        try {
            Pair<DataPackSettings, LifecycledResourceManager> pair = serverConfig.dataPacks.load();
            LifecycledResourceManager lifecycledResourceManager = (LifecycledResourceManager)pair.getSecond();
            Pair<D, DynamicRegistryManager.Immutable> pair2 = loadContextSupplier.get(lifecycledResourceManager, (DataPackSettings)pair.getFirst());
            Object object = pair2.getFirst();
            DynamicRegistryManager.Immutable immutable = (DynamicRegistryManager.Immutable)pair2.getSecond();
            return ((CompletableFuture)DataPackContents.reload(lifecycledResourceManager, immutable, serverConfig.commandEnvironment(), serverConfig.functionPermissionLevel(), prepareExecutor, applyExecutor).whenComplete((dataPackContents, throwable) -> {
                if (throwable != null) {
                    lifecycledResourceManager.close();
                }
            })).thenApplyAsync(dataPackContents -> {
                dataPackContents.refresh(immutable);
                return saveApplierFactory.create(lifecycledResourceManager, (DataPackContents)dataPackContents, immutable, (Object)object);
            }, applyExecutor);
        }
        catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    public static final class ServerConfig
    extends Record {
        final DataPacks dataPacks;
        private final CommandManager.RegistrationEnvironment commandEnvironment;
        private final int functionPermissionLevel;

        public ServerConfig(DataPacks dataPacks, CommandManager.RegistrationEnvironment registrationEnvironment, int i) {
            this.dataPacks = dataPacks;
            this.commandEnvironment = registrationEnvironment;
            this.functionPermissionLevel = i;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerConfig.class, "packConfig;commandSelection;functionCompilationLevel", "dataPacks", "commandEnvironment", "functionPermissionLevel"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerConfig.class, "packConfig;commandSelection;functionCompilationLevel", "dataPacks", "commandEnvironment", "functionPermissionLevel"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerConfig.class, "packConfig;commandSelection;functionCompilationLevel", "dataPacks", "commandEnvironment", "functionPermissionLevel"}, this, object);
        }

        public DataPacks dataPacks() {
            return this.dataPacks;
        }

        public CommandManager.RegistrationEnvironment commandEnvironment() {
            return this.commandEnvironment;
        }

        public int functionPermissionLevel() {
            return this.functionPermissionLevel;
        }
    }

    public record DataPacks(ResourcePackManager manager, DataPackSettings settings, boolean safeMode) {
        public Pair<DataPackSettings, LifecycledResourceManager> load() {
            DataPackSettings dataPackSettings = MinecraftServer.loadDataPacks(this.manager, this.settings, this.safeMode);
            List<ResourcePack> list = this.manager.createResourcePacks();
            LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
            return Pair.of((Object)dataPackSettings, (Object)lifecycledResourceManager);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DataPacks.class, "packRepository;initialDataPacks;safeMode", "manager", "settings", "safeMode"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DataPacks.class, "packRepository;initialDataPacks;safeMode", "manager", "settings", "safeMode"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DataPacks.class, "packRepository;initialDataPacks;safeMode", "manager", "settings", "safeMode"}, this, object);
        }
    }

    @FunctionalInterface
    public static interface LoadContextSupplier<D> {
        public Pair<D, DynamicRegistryManager.Immutable> get(ResourceManager var1, DataPackSettings var2);
    }

    @FunctionalInterface
    public static interface SaveApplierFactory<D, R> {
        public R create(LifecycledResourceManager var1, DataPackContents var2, DynamicRegistryManager.Immutable var3, D var4);
    }
}

