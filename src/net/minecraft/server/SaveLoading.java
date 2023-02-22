/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;

public class SaveLoading {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <D, R> CompletableFuture<R> load(ServerConfig serverConfig, LoadContextSupplier<D> loadContextSupplier, SaveApplierFactory<D, R> saveApplierFactory, Executor prepareExecutor, Executor applyExecutor) {
        try {
            Pair<DataConfiguration, LifecycledResourceManager> pair = serverConfig.dataPacks.load();
            LifecycledResourceManager lifecycledResourceManager = (LifecycledResourceManager)pair.getSecond();
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = ServerDynamicRegistryType.createCombinedDynamicRegistries();
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries2 = SaveLoading.withRegistriesLoaded(lifecycledResourceManager, combinedDynamicRegistries, ServerDynamicRegistryType.WORLDGEN, RegistryLoader.DYNAMIC_REGISTRIES);
            DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries2.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS);
            DynamicRegistryManager.Immutable immutable2 = RegistryLoader.load(lifecycledResourceManager, immutable, RegistryLoader.DIMENSION_REGISTRIES);
            DataConfiguration dataConfiguration = (DataConfiguration)pair.getFirst();
            LoadContext<D> loadContext = loadContextSupplier.get(new LoadContextSupplierContext(lifecycledResourceManager, dataConfiguration, immutable, immutable2));
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries3 = combinedDynamicRegistries2.with(ServerDynamicRegistryType.DIMENSIONS, loadContext.dimensionsRegistryManager);
            DynamicRegistryManager.Immutable immutable3 = combinedDynamicRegistries3.getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE);
            return ((CompletableFuture)DataPackContents.reload(lifecycledResourceManager, immutable3, dataConfiguration.enabledFeatures(), serverConfig.commandEnvironment(), serverConfig.functionPermissionLevel(), prepareExecutor, applyExecutor).whenComplete((dataPackContents, throwable) -> {
                if (throwable != null) {
                    lifecycledResourceManager.close();
                }
            })).thenApplyAsync(dataPackContents -> {
                dataPackContents.refresh(immutable3);
                return saveApplierFactory.create(lifecycledResourceManager, (DataPackContents)dataPackContents, combinedDynamicRegistries3, loadContext.extraData);
            }, applyExecutor);
        }
        catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    private static DynamicRegistryManager.Immutable loadDynamicRegistryManager(ResourceManager resourceManager, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, ServerDynamicRegistryType type, List<RegistryLoader.Entry<?>> entries) {
        DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(type);
        return RegistryLoader.load(resourceManager, immutable, entries);
    }

    private static CombinedDynamicRegistries<ServerDynamicRegistryType> withRegistriesLoaded(ResourceManager resourceManager, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, ServerDynamicRegistryType type, List<RegistryLoader.Entry<?>> entries) {
        DynamicRegistryManager.Immutable immutable = SaveLoading.loadDynamicRegistryManager(resourceManager, combinedDynamicRegistries, type, entries);
        return combinedDynamicRegistries.with(type, immutable);
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

    public record DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
        public Pair<DataConfiguration, LifecycledResourceManager> load() {
            FeatureSet featureSet = this.initMode ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : this.initialDataConfig.enabledFeatures();
            DataConfiguration dataConfiguration = MinecraftServer.loadDataPacks(this.manager, this.initialDataConfig.dataPacks(), this.safeMode, featureSet);
            if (!this.initMode) {
                dataConfiguration = dataConfiguration.withFeaturesAdded(this.initialDataConfig.enabledFeatures());
            }
            List<ResourcePack> list = this.manager.createResourcePacks();
            LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
            return Pair.of((Object)dataConfiguration, (Object)lifecycledResourceManager);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DataPacks.class, "packRepository;initialDataConfig;safeMode;initMode", "manager", "initialDataConfig", "safeMode", "initMode"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DataPacks.class, "packRepository;initialDataConfig;safeMode;initMode", "manager", "initialDataConfig", "safeMode", "initMode"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DataPacks.class, "packRepository;initialDataConfig;safeMode;initMode", "manager", "initialDataConfig", "safeMode", "initMode"}, this, object);
        }
    }

    public record LoadContextSupplierContext(ResourceManager resourceManager, DataConfiguration dataConfiguration, DynamicRegistryManager.Immutable worldGenRegistryManager, DynamicRegistryManager.Immutable dimensionsRegistryManager) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LoadContextSupplierContext.class, "resources;dataConfiguration;datapackWorldgen;datapackDimensions", "resourceManager", "dataConfiguration", "worldGenRegistryManager", "dimensionsRegistryManager"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LoadContextSupplierContext.class, "resources;dataConfiguration;datapackWorldgen;datapackDimensions", "resourceManager", "dataConfiguration", "worldGenRegistryManager", "dimensionsRegistryManager"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LoadContextSupplierContext.class, "resources;dataConfiguration;datapackWorldgen;datapackDimensions", "resourceManager", "dataConfiguration", "worldGenRegistryManager", "dimensionsRegistryManager"}, this, object);
        }
    }

    @FunctionalInterface
    public static interface LoadContextSupplier<D> {
        public LoadContext<D> get(LoadContextSupplierContext var1);
    }

    public static final class LoadContext<D>
    extends Record {
        final D extraData;
        final DynamicRegistryManager.Immutable dimensionsRegistryManager;

        public LoadContext(D object, DynamicRegistryManager.Immutable immutable) {
            this.extraData = object;
            this.dimensionsRegistryManager = immutable;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LoadContext.class, "cookie;finalDimensions", "extraData", "dimensionsRegistryManager"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LoadContext.class, "cookie;finalDimensions", "extraData", "dimensionsRegistryManager"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LoadContext.class, "cookie;finalDimensions", "extraData", "dimensionsRegistryManager"}, this, object);
        }

        public D extraData() {
            return this.extraData;
        }

        public DynamicRegistryManager.Immutable dimensionsRegistryManager() {
            return this.dimensionsRegistryManager;
        }
    }

    @FunctionalInterface
    public static interface SaveApplierFactory<D, R> {
        public R create(LifecycledResourceManager var1, DataPackContents var2, CombinedDynamicRegistries<ServerDynamicRegistryType> var3, D var4);
    }
}

