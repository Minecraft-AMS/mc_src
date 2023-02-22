/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.integrated;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class IntegratedServerLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    private final LevelStorage storage;

    public IntegratedServerLoader(MinecraftClient client, LevelStorage storage) {
        this.client = client;
        this.storage = storage;
    }

    public void start(Screen parent, String levelName) {
        this.start(parent, levelName, false, true);
    }

    public void createAndStart(String levelName, LevelInfo levelInfo, GeneratorOptions dynamicRegistryManager, Function<DynamicRegistryManager, DimensionOptionsRegistryHolder> dimensionsRegistrySupplier) {
        LevelStorage.Session session = this.createSession(levelName);
        if (session == null) {
            return;
        }
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        DataConfiguration dataConfiguration = levelInfo.getDataConfiguration();
        try {
            SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataConfiguration, false, false);
            SaveLoader saveLoader = this.load(dataPacks, context -> {
                DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = ((DimensionOptionsRegistryHolder)dimensionsRegistrySupplier.apply(context.worldGenRegistryManager())).toConfig(context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION));
                return new SaveLoading.LoadContext<LevelProperties>(new LevelProperties(levelInfo, dynamicRegistryManager, dimensionsConfig.specialWorldProperty(), dimensionsConfig.getLifecycle()), dimensionsConfig.toDynamicRegistryManager());
            }, SaveLoader::new);
            this.client.startIntegratedServer(levelName, session, resourcePackManager, saveLoader, true);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
            IntegratedServerLoader.close(session, levelName);
        }
    }

    @Nullable
    private LevelStorage.Session createSession(String levelName) {
        try {
            return this.storage.createSession(levelName);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to read level {} data", (Object)levelName, (Object)iOException);
            SystemToast.addWorldAccessFailureToast(this.client, levelName);
            this.client.setScreen(null);
            return null;
        }
    }

    public void start(LevelStorage.Session session, DataPackContents dataPackContents, CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistryManager, SaveProperties saveProperties) {
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        LifecycledResourceManager lifecycledResourceManager = (LifecycledResourceManager)new SaveLoading.DataPacks(resourcePackManager, saveProperties.getDataConfiguration(), false, false).load().getSecond();
        this.client.startIntegratedServer(session.getDirectoryName(), session, resourcePackManager, new SaveLoader(lifecycledResourceManager, dataPackContents, dynamicRegistryManager, saveProperties), true);
    }

    private SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager) throws Exception {
        SaveLoading.DataPacks dataPacks = this.createDataPackConfig(session, safeMode, dataPackManager);
        return this.load(dataPacks, context -> {
            RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.worldGenRegistryManager());
            Registry<DimensionOptions> registry = context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION);
            Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> pair = session.readLevelProperties(dynamicOps, context.dataConfiguration(), registry, context.worldGenRegistryManager().getRegistryLifecycle());
            if (pair == null) {
                throw new IllegalStateException("Failed to load world");
            }
            return new SaveLoading.LoadContext<SaveProperties>((SaveProperties)pair.getFirst(), ((DimensionOptionsRegistryHolder.DimensionsConfig)pair.getSecond()).toDynamicRegistryManager());
        }, SaveLoader::new);
    }

    public Pair<LevelInfo, GeneratorOptionsHolder> loadForRecreation(LevelStorage.Session session) throws Exception {
        @Environment(value=EnvType.CLIENT)
        final class CurrentSettings
        extends Record {
            final LevelInfo levelInfo;
            final GeneratorOptions options;
            final Registry<DimensionOptions> existingDimensionRegistry;

            CurrentSettings(LevelInfo levelInfo, GeneratorOptions generatorOptions, Registry<DimensionOptions> registry) {
                this.levelInfo = levelInfo;
                this.options = generatorOptions;
                this.existingDimensionRegistry = registry;
            }

            @Override
            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{CurrentSettings.class, "levelSettings;options;existingDimensions", "levelInfo", "options", "existingDimensionRegistry"}, this);
            }

            @Override
            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CurrentSettings.class, "levelSettings;options;existingDimensions", "levelInfo", "options", "existingDimensionRegistry"}, this);
            }

            @Override
            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CurrentSettings.class, "levelSettings;options;existingDimensions", "levelInfo", "options", "existingDimensionRegistry"}, this, object);
            }

            public LevelInfo levelInfo() {
                return this.levelInfo;
            }

            public GeneratorOptions options() {
                return this.options;
            }

            public Registry<DimensionOptions> existingDimensionRegistry() {
                return this.existingDimensionRegistry;
            }
        }
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        SaveLoading.DataPacks dataPacks = this.createDataPackConfig(session, false, resourcePackManager);
        return this.load(dataPacks, context -> {
            RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.worldGenRegistryManager());
            Registry<DimensionOptions> registry = new SimpleRegistry<DimensionOptions>(RegistryKeys.DIMENSION, Lifecycle.stable()).freeze();
            Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> pair = session.readLevelProperties(dynamicOps, context.dataConfiguration(), registry, context.worldGenRegistryManager().getRegistryLifecycle());
            if (pair == null) {
                throw new IllegalStateException("Failed to load world");
            }
            return new SaveLoading.LoadContext<CurrentSettings>(new CurrentSettings(((SaveProperties)pair.getFirst()).getLevelInfo(), ((SaveProperties)pair.getFirst()).getGeneratorOptions(), ((DimensionOptionsRegistryHolder.DimensionsConfig)pair.getSecond()).dimensions()), context.dimensionsRegistryManager());
        }, (resourceManager, dataPackContents, combinedRegistryManager, currentSettings) -> {
            resourceManager.close();
            return Pair.of((Object)currentSettings.levelInfo, (Object)new GeneratorOptionsHolder(currentSettings.options, new DimensionOptionsRegistryHolder(currentSettings.existingDimensionRegistry), combinedRegistryManager, dataPackContents, currentSettings.levelInfo.getDataConfiguration()));
        });
    }

    private SaveLoading.DataPacks createDataPackConfig(LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager) {
        DataConfiguration dataConfiguration = session.getDataPackSettings();
        if (dataConfiguration == null) {
            throw new IllegalStateException("Failed to load data pack config");
        }
        return new SaveLoading.DataPacks(dataPackManager, dataConfiguration, safeMode, false);
    }

    public SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode) throws Exception {
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        return this.createSaveLoader(session, safeMode, resourcePackManager);
    }

    private <D, R> R load(SaveLoading.DataPacks dataPacks, SaveLoading.LoadContextSupplier<D> loadContextSupplier, SaveLoading.SaveApplierFactory<D, R> saveApplierFactory) throws Exception {
        SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
        CompletableFuture<R> completableFuture = SaveLoading.load(serverConfig, loadContextSupplier, saveApplierFactory, Util.getMainWorkerExecutor(), this.client);
        this.client.runTasks(completableFuture::isDone);
        return completableFuture.get();
    }

    private void start(Screen parent, String levelName, boolean safeMode, boolean canShowBackupPrompt) {
        boolean bl2;
        SaveLoader saveLoader;
        LevelStorage.Session session = this.createSession(levelName);
        if (session == null) {
            return;
        }
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        try {
            saveLoader = this.createSaveLoader(session, safeMode, resourcePackManager);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", (Throwable)exception);
            this.client.setScreen(new DatapackFailureScreen(() -> this.start(parent, levelName, true, canShowBackupPrompt)));
            IntegratedServerLoader.close(session, levelName);
            return;
        }
        SaveProperties saveProperties = saveLoader.saveProperties();
        boolean bl = saveProperties.getGeneratorOptions().isLegacyCustomizedType();
        boolean bl3 = bl2 = saveProperties.getLifecycle() != Lifecycle.stable();
        if (canShowBackupPrompt && (bl || bl2)) {
            this.showBackupPromptScreen(parent, levelName, bl, () -> this.start(parent, levelName, safeMode, false));
            saveLoader.close();
            IntegratedServerLoader.close(session, levelName);
            return;
        }
        ((CompletableFuture)((CompletableFuture)((CompletableFuture)this.client.getServerResourcePackProvider().loadServerPack(session).thenApply(void_ -> true)).exceptionallyComposeAsync(throwable -> {
            LOGGER.warn("Failed to load pack: ", throwable);
            return this.showPackLoadFailureScreen();
        }, (Executor)this.client)).thenAcceptAsync(proceed -> {
            if (proceed.booleanValue()) {
                this.client.startIntegratedServer(levelName, session, resourcePackManager, saveLoader, false);
            } else {
                saveLoader.close();
                IntegratedServerLoader.close(session, levelName);
                this.client.getServerResourcePackProvider().clear().thenRunAsync(() -> this.client.setScreen(parent), this.client);
            }
        }, (Executor)this.client)).exceptionally(throwable -> {
            this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Load world"));
            return null;
        });
    }

    private CompletableFuture<Boolean> showPackLoadFailureScreen() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<Boolean>();
        this.client.setScreen(new ConfirmScreen(completableFuture::complete, Text.translatable("multiplayer.texturePrompt.failure.line1"), Text.translatable("multiplayer.texturePrompt.failure.line2"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
        return completableFuture;
    }

    private static void close(LevelStorage.Session session, String levelName) {
        try {
            session.close();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to unlock access to level {}", (Object)levelName, (Object)iOException);
        }
    }

    private void showBackupPromptScreen(Screen parent, String levelName, boolean customized, Runnable callback) {
        MutableText text2;
        MutableText text;
        if (customized) {
            text = Text.translatable("selectWorld.backupQuestion.customized");
            text2 = Text.translatable("selectWorld.backupWarning.customized");
        } else {
            text = Text.translatable("selectWorld.backupQuestion.experimental");
            text2 = Text.translatable("selectWorld.backupWarning.experimental");
        }
        this.client.setScreen(new BackupPromptScreen(parent, (backup, eraseCache) -> {
            if (backup) {
                EditWorldScreen.onBackupConfirm(this.storage, levelName);
            }
            callback.run();
        }, text, text2, false));
    }

    public static void tryLoad(MinecraftClient client, CreateWorldScreen parent, Lifecycle lifecycle, Runnable loader) {
        BooleanConsumer booleanConsumer = confirmed -> {
            if (confirmed) {
                loader.run();
            } else {
                client.setScreen(parent);
            }
        };
        if (lifecycle == Lifecycle.stable()) {
            loader.run();
        } else if (lifecycle == Lifecycle.experimental()) {
            client.setScreen(new ConfirmScreen(booleanConsumer, Text.translatable("selectWorld.warning.experimental.title"), Text.translatable("selectWorld.warning.experimental.question")));
        } else {
            client.setScreen(new ConfirmScreen(booleanConsumer, Text.translatable("selectWorld.warning.deprecated.title"), Text.translatable("selectWorld.warning.deprecated.question")));
        }
    }
}

