/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
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

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
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
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
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
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
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
            ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
            try {
                SaveLoader.FunctionLoaderConfig functionLoaderConfig = new SaveLoader.FunctionLoaderConfig(resourcePackManager, CommandManager.RegistrationEnvironment.DEDICATED, serverPropertiesLoader.getPropertiesHandler().functionPermissionLevel, bl);
                saveLoader = SaveLoader.ofLoaded(functionLoaderConfig, () -> {
                    DataPackSettings dataPackSettings = session.getDataPackSettings();
                    return dataPackSettings == null ? DataPackSettings.SAFE_MODE : dataPackSettings;
                }, (arg_0, arg_1) -> Main.method_40373(session, optionSet, (OptionSpec)optionSpec3, serverPropertiesLoader, (OptionSpec)optionSpec4, arg_0, arg_1), Util.getMainWorkerExecutor(), Runnable::run).get();
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
                resourcePackManager.close();
                return;
            }
            saveLoader.refresh();
            DynamicRegistryManager.Immutable immutable = saveLoader.dynamicRegistryManager();
            serverPropertiesLoader.getPropertiesHandler().getGeneratorOptions(immutable);
            SaveProperties saveProperties = saveLoader.saveProperties();
            if (optionSet.has((OptionSpec)optionSpec5)) {
                Main.forceUpgradeWorld(session, Schemas.getFixer(), optionSet.has((OptionSpec)optionSpec6), () -> true, saveProperties.getGeneratorOptions());
            }
            session.backupLevelDataFile(immutable, saveProperties);
            final MinecraftDedicatedServer minecraftDedicatedServer = MinecraftServer.startServer(arg_0 -> Main.method_29734(session, resourcePackManager, saveLoader, serverPropertiesLoader, minecraftSessionService, gameProfileRepository, userCache, optionSet, (OptionSpec)optionSpec9, (OptionSpec)optionSpec12, (OptionSpec)optionSpec3, (OptionSpec)optionSpec13, (OptionSpec)optionSpec, (OptionSpec)optionSpec15, arg_0));
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

    private static void forceUpgradeWorld(LevelStorage.Session session, DataFixer dataFixer, boolean eraseCache, BooleanSupplier continueCheck, GeneratorOptions generatorOptions) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, generatorOptions, eraseCache);
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

    private static /* synthetic */ MinecraftDedicatedServer method_29734(LevelStorage.Session session, ResourcePackManager resourcePackManager, SaveLoader saveLoader, ServerPropertiesLoader serverPropertiesLoader, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, OptionSet optionSet, OptionSpec optionSpec, OptionSpec optionSpec2, OptionSpec optionSpec3, OptionSpec optionSpec4, OptionSpec optionSpec5, OptionSpec optionSpec6, Thread thread) {
        boolean bl;
        MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(thread, session, resourcePackManager, saveLoader, serverPropertiesLoader, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new);
        minecraftDedicatedServer.setSinglePlayerName((String)optionSet.valueOf(optionSpec));
        minecraftDedicatedServer.setServerPort((Integer)optionSet.valueOf(optionSpec2));
        minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
        minecraftDedicatedServer.setServerId((String)optionSet.valueOf(optionSpec4));
        boolean bl2 = bl = !optionSet.has(optionSpec5) && !optionSet.valuesOf(optionSpec6).contains("nogui");
        if (bl && !GraphicsEnvironment.isHeadless()) {
            minecraftDedicatedServer.createGui();
        }
        return minecraftDedicatedServer;
    }

    private static /* synthetic */ Pair method_40373(LevelStorage.Session session, OptionSet optionSet, OptionSpec optionSpec, ServerPropertiesLoader serverPropertiesLoader, OptionSpec optionSpec2, ResourceManager resourceManager, DataPackSettings dataPackSettings) {
        GeneratorOptions generatorOptions;
        LevelInfo levelInfo;
        DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
        RegistryOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, mutable, resourceManager);
        SaveProperties saveProperties = session.readLevelProperties(dynamicOps, dataPackSettings, mutable.getRegistryLifecycle());
        if (saveProperties != null) {
            return Pair.of((Object)saveProperties, (Object)mutable.toImmutable());
        }
        if (optionSet.has(optionSpec)) {
            levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
            generatorOptions = GeneratorOptions.createDemo(mutable);
        } else {
            ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
            levelInfo = new LevelInfo(serverPropertiesHandler.levelName, serverPropertiesHandler.gameMode, serverPropertiesHandler.hardcore, serverPropertiesHandler.difficulty, false, new GameRules(), dataPackSettings);
            generatorOptions = optionSet.has(optionSpec2) ? serverPropertiesHandler.getGeneratorOptions(mutable).withBonusChest() : serverPropertiesHandler.getGeneratorOptions(mutable);
        }
        LevelProperties levelProperties = new LevelProperties(levelInfo, generatorOptions, Lifecycle.stable());
        return Pair.of((Object)levelProperties, (Object)mutable.toImmutable());
    }
}

