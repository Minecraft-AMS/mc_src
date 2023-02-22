/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.OutputStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.updater.WorldUpdater;
import org.slf4j.Logger;

public class Main {
    private static final Logger LOGGER = LogUtils.getLogger();

    @DontObfuscate
    public static void main(String[] args) {
        SharedConstants.createGameVersion();
        OptionParser optionParser = new OptionParser();
        OptionSpecBuilder optionSpec = optionParser.accepts("nogui");
        OptionSpecBuilder optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("demo");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("bonusChest");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("forceUpgrade");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("eraseCache");
        OptionSpecBuilder optionSpec7 = optionParser.accepts("safeMode", "Loads level with vanilla datapack only");
        AbstractOptionSpec optionSpec8 = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec10 = optionParser.accepts("universe").withRequiredArg().defaultsTo((Object)".", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec11 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec12 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)-1, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec13 = optionParser.accepts("serverId").withRequiredArg();
        OptionSpecBuilder optionSpec14 = optionParser.accepts("jfrProfile");
        NonOptionArgumentSpec optionSpec15 = optionParser.nonOptions();
        try {
            SaveLoader saveLoader;
            boolean bl;
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has((OptionSpec)optionSpec8)) {
                optionParser.printHelpOn((OutputStream)System.err);
                return;
            }
            CrashReport.initCrashReport();
            if (optionSet.has((OptionSpec)optionSpec14)) {
                FlightProfiler.INSTANCE.start(InstanceType.SERVER);
            }
            Bootstrap.initialize();
            Bootstrap.logMissing();
            Util.startTimerHack();
            Path path = Paths.get("server.properties", new String[0]);
            ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(path);
            serverPropertiesLoader.store();
            Path path2 = Paths.get("eula.txt", new String[0]);
            EulaReader eulaReader = new EulaReader(path2);
            if (optionSet.has((OptionSpec)optionSpec2)) {
                LOGGER.info("Initialized '{}' and '{}'", (Object)path.toAbsolutePath(), (Object)path2.toAbsolutePath());
                return;
            }
            if (!eulaReader.isEulaAgreedTo()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            File file = new File((String)optionSet.valueOf((OptionSpec)optionSpec10));
            ApiServices apiServices = ApiServices.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), file);
            String string = Optional.ofNullable((String)optionSet.valueOf((OptionSpec)optionSpec11)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
            LevelStorage levelStorage = LevelStorage.create(file.toPath());
            LevelStorage.Session session = levelStorage.createSession(string);
            LevelSummary levelSummary = session.getLevelSummary();
            if (levelSummary != null) {
                if (levelSummary.requiresConversion()) {
                    LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                    return;
                }
                if (!levelSummary.isVersionAvailable()) {
                    LOGGER.info("This world was created by an incompatible version.");
                    return;
                }
            }
            if (bl = optionSet.has((OptionSpec)optionSpec7)) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }
            ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session.getDirectory(WorldSavePath.DATAPACKS));
            try {
                SaveLoading.ServerConfig serverConfig = Main.createServerConfig(serverPropertiesLoader.getPropertiesHandler(), session, bl, resourcePackManager);
                saveLoader = (SaveLoader)Util.waitAndApply(arg_0 -> Main.method_43612(serverConfig, session, optionSet, (OptionSpec)optionSpec3, serverPropertiesLoader, (OptionSpec)optionSpec4, arg_0)).get();
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
                return;
            }
            DynamicRegistryManager.Immutable immutable = saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
            if (optionSet.has((OptionSpec)optionSpec5)) {
                Main.forceUpgradeWorld(session, Schemas.getFixer(), optionSet.has((OptionSpec)optionSpec6), () -> true, immutable.get(RegistryKeys.DIMENSION));
            }
            SaveProperties saveProperties = saveLoader.saveProperties();
            session.backupLevelDataFile(immutable, saveProperties);
            final MinecraftDedicatedServer minecraftDedicatedServer = MinecraftServer.startServer(arg_0 -> Main.method_29734(session, resourcePackManager, saveLoader, serverPropertiesLoader, apiServices, optionSet, (OptionSpec)optionSpec9, (OptionSpec)optionSpec12, (OptionSpec)optionSpec3, (OptionSpec)optionSpec13, (OptionSpec)optionSpec, (OptionSpec)optionSpec15, arg_0));
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    minecraftDedicatedServer.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        }
        catch (Exception exception2) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", (Throwable)exception2);
        }
    }

    private static SaveLoading.ServerConfig createServerConfig(ServerPropertiesHandler serverPropertiesHandler, LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager) {
        DataConfiguration dataConfiguration2;
        boolean bl;
        DataConfiguration dataConfiguration = session.getDataPackSettings();
        if (dataConfiguration != null) {
            bl = false;
            dataConfiguration2 = dataConfiguration;
        } else {
            bl = true;
            dataConfiguration2 = new DataConfiguration(serverPropertiesHandler.dataPackSettings, FeatureFlags.DEFAULT_ENABLED_FEATURES);
        }
        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration2, safeMode, bl);
        return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.DEDICATED, serverPropertiesHandler.functionPermissionLevel);
    }

    private static void forceUpgradeWorld(LevelStorage.Session session, DataFixer dataFixer, boolean eraseCache, BooleanSupplier continueCheck, Registry<DimensionOptions> dimensionOptionsRegistry) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, dimensionOptionsRegistry, eraseCache);
        Text text = null;
        while (!worldUpdater.isDone()) {
            int i;
            Text text2 = worldUpdater.getStatus();
            if (text != text2) {
                text = text2;
                LOGGER.info(worldUpdater.getStatus().getString());
            }
            if ((i = worldUpdater.getTotalChunkCount()) > 0) {
                int j = worldUpdater.getUpgradedChunkCount() + worldUpdater.getSkippedChunkCount();
                LOGGER.info("{}% completed ({} / {} chunks)...", new Object[]{MathHelper.floor((float)j / (float)i * 100.0f), j, i});
            }
            if (!continueCheck.getAsBoolean()) {
                worldUpdater.cancel();
                continue;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private static /* synthetic */ MinecraftDedicatedServer method_29734(LevelStorage.Session session, ResourcePackManager resourcePackManager, SaveLoader saveLoader, ServerPropertiesLoader serverPropertiesLoader, ApiServices apiServices, OptionSet optionSet, OptionSpec optionSpec, OptionSpec optionSpec2, OptionSpec optionSpec3, OptionSpec optionSpec4, OptionSpec optionSpec5, OptionSpec optionSpec6, Thread thread) {
        boolean bl;
        MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(thread, session, resourcePackManager, saveLoader, serverPropertiesLoader, Schemas.getFixer(), apiServices, WorldGenerationProgressLogger::new);
        minecraftDedicatedServer.setHostProfile(optionSet.has(optionSpec) ? new GameProfile(null, (String)optionSet.valueOf(optionSpec)) : null);
        minecraftDedicatedServer.setServerPort((Integer)optionSet.valueOf(optionSpec2));
        minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
        minecraftDedicatedServer.setServerId((String)optionSet.valueOf(optionSpec4));
        boolean bl2 = bl = !optionSet.has(optionSpec5) && !optionSet.valuesOf(optionSpec6).contains("nogui");
        if (bl && !GraphicsEnvironment.isHeadless()) {
            minecraftDedicatedServer.createGui();
        }
        return minecraftDedicatedServer;
    }

    private static /* synthetic */ CompletableFuture method_43612(SaveLoading.ServerConfig serverConfig, LevelStorage.Session session, OptionSet optionSet, OptionSpec optionSpec, ServerPropertiesLoader serverPropertiesLoader, OptionSpec optionSpec2, Executor applyExecutor) {
        return SaveLoading.load(serverConfig, context -> {
            DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder;
            GeneratorOptions generatorOptions;
            LevelInfo levelInfo;
            Registry<DimensionOptions> registry = context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION);
            RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.worldGenRegistryManager());
            Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> pair = session.readLevelProperties(dynamicOps, context.dataConfiguration(), registry, context.worldGenRegistryManager().getRegistryLifecycle());
            if (pair != null) {
                return new SaveLoading.LoadContext<SaveProperties>((SaveProperties)pair.getFirst(), ((DimensionOptionsRegistryHolder.DimensionsConfig)pair.getSecond()).toDynamicRegistryManager());
            }
            if (optionSet.has(optionSpec)) {
                levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
                generatorOptions = GeneratorOptions.DEMO_OPTIONS;
                dimensionOptionsRegistryHolder = WorldPresets.createDemoOptions(context.worldGenRegistryManager());
            } else {
                ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
                levelInfo = new LevelInfo(serverPropertiesHandler.levelName, serverPropertiesHandler.gameMode, serverPropertiesHandler.hardcore, serverPropertiesHandler.difficulty, false, new GameRules(), context.dataConfiguration());
                generatorOptions = optionSet.has(optionSpec2) ? serverPropertiesHandler.generatorOptions.withBonusChest(true) : serverPropertiesHandler.generatorOptions;
                dimensionOptionsRegistryHolder = serverPropertiesHandler.createDimensionsRegistryHolder(context.worldGenRegistryManager());
            }
            DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = dimensionOptionsRegistryHolder.toConfig(registry);
            Lifecycle lifecycle = dimensionsConfig.getLifecycle().add(context.worldGenRegistryManager().getRegistryLifecycle());
            return new SaveLoading.LoadContext<LevelProperties>(new LevelProperties(levelInfo, generatorOptions, dimensionsConfig.specialWorldProperty(), lifecycle), dimensionsConfig.toDynamicRegistryManager());
        }, SaveLoader::new, Util.getMainWorkerExecutor(), applyExecutor);
    }
}

