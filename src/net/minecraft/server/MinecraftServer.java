/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.buffer.Unpooled
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.imageio.ImageIO;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ScoreboardSynchronizer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.ServerTask;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.SecondaryServerWorld;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class MinecraftServer
extends ReentrantThreadExecutor<ServerTask>
implements SnooperListener,
CommandOutput,
AutoCloseable,
Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File USER_CACHE_FILE = new File("usercache.json");
    private static final CompletableFuture<Unit> field_20279 = CompletableFuture.completedFuture(Unit.INSTANCE);
    public static final LevelInfo DEMO_LEVEL_INFO = new LevelInfo("North Carolina".hashCode(), GameMode.SURVIVAL, true, false, LevelGeneratorType.DEFAULT).setBonusChest();
    private final LevelStorage levelStorage;
    private final Snooper snooper = new Snooper("server", this, Util.getMeasuringTimeMs());
    private final File gameDir;
    private final List<Runnable> serverGuiTickables = Lists.newArrayList();
    private final DisableableProfiler profiler = new DisableableProfiler(this::getTicks);
    private final ServerNetworkIo networkIo;
    protected final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    private final ServerMetadata metadata = new ServerMetadata();
    private final Random random = new Random();
    private final DataFixer dataFixer;
    private String serverIp;
    private int serverPort = -1;
    private final Map<DimensionType, ServerWorld> worlds = Maps.newIdentityHashMap();
    private PlayerManager playerManager;
    private volatile boolean running = true;
    private boolean stopped;
    private int ticks;
    protected final Proxy field_4599;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean spawnAnimals;
    private boolean spawnNpcs;
    private boolean pvpEnabled;
    private boolean flightEnabled;
    @Nullable
    private String motd;
    private int worldHeight;
    private int playerIdleTimeout;
    public final long[] lastTickLengths = new long[100];
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private String userName;
    private final String levelName;
    @Nullable
    @Environment(value=EnvType.CLIENT)
    private String displayName;
    private boolean demo;
    private boolean bonusChest;
    private String resourcePackUrl = "";
    private String resourcePackHash = "";
    private volatile boolean loading;
    private long field_4557;
    @Nullable
    private Text loadingStage;
    private boolean profilerStartQueued;
    private boolean forceGameMode;
    @Nullable
    private final YggdrasilAuthenticationService authService;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository gameProfileRepo;
    private final UserCache userCache;
    private long lastPlayerSampleUpdate;
    protected final Thread serverThread = Util.make(new Thread((Runnable)this, "Server thread"), thread2 -> thread2.setUncaughtExceptionHandler((thread, throwable) -> LOGGER.error((Object)throwable)));
    private long timeReference = Util.getMeasuringTimeMs();
    private long field_19248;
    private boolean field_19249;
    @Environment(value=EnvType.CLIENT)
    private boolean iconFilePresent;
    private final ReloadableResourceManager dataManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA, this.serverThread);
    private final ResourcePackManager<ResourcePackProfile> dataPackContainerManager = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new);
    @Nullable
    private FileResourcePackProvider dataPackCreator;
    private final CommandManager commandManager;
    private final RecipeManager recipeManager = new RecipeManager();
    private final RegistryTagManager tagManager = new RegistryTagManager();
    private final ServerScoreboard scoreboard = new ServerScoreboard(this);
    private final BossBarManager bossBarManager = new BossBarManager(this);
    private final LootManager lootManager = new LootManager();
    private final ServerAdvancementLoader advancementManager = new ServerAdvancementLoader();
    private final CommandFunctionManager commandFunctionManager = new CommandFunctionManager(this);
    private final MetricsData metricsData = new MetricsData();
    private boolean whitelistEnabled;
    private boolean forceWorldUpgrade;
    private boolean eraseCache;
    private float tickTime;
    private final Executor workerExecutor;
    @Nullable
    private String serverId;

    public MinecraftServer(File gameDir, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, YggdrasilAuthenticationService authService, MinecraftSessionService sessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, String levelName) {
        super("Server");
        this.field_4599 = proxy;
        this.commandManager = commandManager;
        this.authService = authService;
        this.sessionService = sessionService;
        this.gameProfileRepo = gameProfileRepository;
        this.userCache = userCache;
        this.gameDir = gameDir;
        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
        this.levelStorage = new LevelStorage(gameDir.toPath(), gameDir.toPath().resolve("../backups"), dataFixer);
        this.dataFixer = dataFixer;
        this.dataManager.registerListener(this.tagManager);
        this.dataManager.registerListener(this.recipeManager);
        this.dataManager.registerListener(this.lootManager);
        this.dataManager.registerListener(this.commandFunctionManager);
        this.dataManager.registerListener(this.advancementManager);
        this.workerExecutor = Util.getServerWorkerExecutor();
        this.levelName = levelName;
    }

    private void initScoreboard(PersistentStateManager persistentStateManager) {
        ScoreboardState scoreboardState = persistentStateManager.getOrCreate(ScoreboardState::new, "scoreboard");
        scoreboardState.setScoreboard(this.getScoreboard());
        this.getScoreboard().addUpdateListener(new ScoreboardSynchronizer(scoreboardState));
    }

    protected abstract boolean setupServer() throws IOException;

    protected void upgradeWorld(String string) {
        if (this.getLevelStorage().requiresConversion(string)) {
            LOGGER.info("Converting map!");
            this.setLoadingStage(new TranslatableText("menu.convertingLevel", new Object[0]));
            this.getLevelStorage().convertLevel(string, new ProgressListener(){
                private long lastProgressUpdate = Util.getMeasuringTimeMs();

                @Override
                public void method_15412(Text text) {
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public void method_15413(Text text) {
                }

                @Override
                public void progressStagePercentage(int i) {
                    if (Util.getMeasuringTimeMs() - this.lastProgressUpdate >= 1000L) {
                        this.lastProgressUpdate = Util.getMeasuringTimeMs();
                        LOGGER.info("Converting... {}%", (Object)i);
                    }
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public void setDone() {
                }

                @Override
                public void method_15414(Text text) {
                }
            });
        }
        if (this.forceWorldUpgrade) {
            LOGGER.info("Forcing world upgrade!");
            LevelProperties levelProperties = this.getLevelStorage().getLevelProperties(this.getLevelName());
            if (levelProperties != null) {
                WorldUpdater worldUpdater = new WorldUpdater(this.getLevelName(), this.getLevelStorage(), levelProperties, this.eraseCache);
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
                        LOGGER.info("{}% completed ({} / {} chunks)...", (Object)MathHelper.floor((float)j / (float)i * 100.0f), (Object)j, (Object)i);
                    }
                    if (this.isStopped()) {
                        worldUpdater.cancel();
                        continue;
                    }
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        }
    }

    protected synchronized void setLoadingStage(Text loadingStage) {
        this.loadingStage = loadingStage;
    }

    protected void loadWorld(String name, String serverName, long seed, LevelGeneratorType generatorType, JsonElement generatorSettings) {
        LevelInfo levelInfo;
        this.upgradeWorld(name);
        this.setLoadingStage(new TranslatableText("menu.loadingLevel", new Object[0]));
        WorldSaveHandler worldSaveHandler = this.getLevelStorage().createSaveHandler(name, this);
        this.loadWorldResourcePack(this.getLevelName(), worldSaveHandler);
        LevelProperties levelProperties = worldSaveHandler.readProperties();
        if (levelProperties == null) {
            if (this.isDemo()) {
                levelInfo = DEMO_LEVEL_INFO;
            } else {
                levelInfo = new LevelInfo(seed, this.getDefaultGameMode(), this.shouldGenerateStructures(), this.isHardcore(), generatorType);
                levelInfo.setGeneratorOptions(generatorSettings);
                if (this.bonusChest) {
                    levelInfo.setBonusChest();
                }
            }
            levelProperties = new LevelProperties(levelInfo, serverName);
        } else {
            levelProperties.setLevelName(serverName);
            levelInfo = new LevelInfo(levelProperties);
        }
        this.loadWorldDataPacks(worldSaveHandler.getWorldDir(), levelProperties);
        WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
        this.createWorlds(worldSaveHandler, levelProperties, levelInfo, worldGenerationProgressListener);
        this.setDifficulty(this.getDefaultDifficulty(), true);
        this.prepareStartRegion(worldGenerationProgressListener);
    }

    protected void createWorlds(WorldSaveHandler worldSaveHandler, LevelProperties properties, LevelInfo levelInfo, WorldGenerationProgressListener worldGenerationProgressListener) {
        if (this.isDemo()) {
            properties.loadLevelInfo(DEMO_LEVEL_INFO);
        }
        ServerWorld serverWorld = new ServerWorld(this, this.workerExecutor, worldSaveHandler, properties, DimensionType.OVERWORLD, this.profiler, worldGenerationProgressListener);
        this.worlds.put(DimensionType.OVERWORLD, serverWorld);
        this.initScoreboard(serverWorld.getPersistentStateManager());
        serverWorld.getWorldBorder().load(properties);
        ServerWorld serverWorld2 = this.getWorld(DimensionType.OVERWORLD);
        if (!properties.isInitialized()) {
            try {
                serverWorld2.init(levelInfo);
                if (properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
                    this.setToDebugWorldProperties(properties);
                }
                properties.setInitialized(true);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Exception initializing level");
                try {
                    serverWorld2.addDetailsToCrashReport(crashReport);
                }
                catch (Throwable throwable2) {
                    // empty catch block
                }
                throw new CrashException(crashReport);
            }
            properties.setInitialized(true);
        }
        this.getPlayerManager().setMainWorld(serverWorld2);
        if (properties.getCustomBossEvents() != null) {
            this.getBossBarManager().fromTag(properties.getCustomBossEvents());
        }
        for (DimensionType dimensionType : DimensionType.getAll()) {
            if (dimensionType == DimensionType.OVERWORLD) continue;
            this.worlds.put(dimensionType, new SecondaryServerWorld(serverWorld2, this, this.workerExecutor, worldSaveHandler, dimensionType, (Profiler)this.profiler, worldGenerationProgressListener));
        }
    }

    private void setToDebugWorldProperties(LevelProperties properties) {
        properties.setStructures(false);
        properties.setCommandsAllowed(true);
        properties.setRaining(false);
        properties.setThundering(false);
        properties.setClearWeatherTime(1000000000);
        properties.setTimeOfDay(6000L);
        properties.setGameMode(GameMode.SPECTATOR);
        properties.setHardcore(false);
        properties.setDifficulty(Difficulty.PEACEFUL);
        properties.setDifficultyLocked(true);
        properties.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, this);
    }

    protected void loadWorldDataPacks(File worldDir, LevelProperties levelProperties) {
        this.dataPackContainerManager.registerProvider(new VanillaDataPackProvider());
        this.dataPackCreator = new FileResourcePackProvider(new File(worldDir, "datapacks"));
        this.dataPackContainerManager.registerProvider(this.dataPackCreator);
        this.dataPackContainerManager.scanPacks();
        ArrayList list = Lists.newArrayList();
        for (String string : levelProperties.getEnabledDataPacks()) {
            ResourcePackProfile resourcePackProfile = this.dataPackContainerManager.getProfile(string);
            if (resourcePackProfile != null) {
                list.add(resourcePackProfile);
                continue;
            }
            LOGGER.warn("Missing data pack {}", (Object)string);
        }
        this.dataPackContainerManager.setEnabledProfiles(list);
        this.reloadDataPacks(levelProperties);
    }

    protected void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener) {
        this.setLoadingStage(new TranslatableText("menu.generatingTerrain", new Object[0]));
        ServerWorld serverWorld = this.getWorld(DimensionType.OVERWORLD);
        LOGGER.info("Preparing start region for dimension " + DimensionType.getId(serverWorld.dimension.getType()));
        BlockPos blockPos = serverWorld.getSpawnPos();
        worldGenerationProgressListener.start(new ChunkPos(blockPos));
        ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
        serverChunkManager.getLightingProvider().setTaskBatchSize(500);
        this.timeReference = Util.getMeasuringTimeMs();
        serverChunkManager.addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);
        while (serverChunkManager.getTotalChunksLoadedCount() != 441) {
            this.timeReference = Util.getMeasuringTimeMs() + 10L;
            this.method_16208();
        }
        this.timeReference = Util.getMeasuringTimeMs() + 10L;
        this.method_16208();
        for (DimensionType dimensionType : DimensionType.getAll()) {
            ForcedChunkState forcedChunkState = this.getWorld(dimensionType).getPersistentStateManager().method_20786(ForcedChunkState::new, "chunks");
            if (forcedChunkState == null) continue;
            ServerWorld serverWorld2 = this.getWorld(dimensionType);
            LongIterator longIterator = forcedChunkState.getChunks().iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                ChunkPos chunkPos = new ChunkPos(l);
                serverWorld2.getChunkManager().setChunkForced(chunkPos, true);
            }
        }
        this.timeReference = Util.getMeasuringTimeMs() + 10L;
        this.method_16208();
        worldGenerationProgressListener.stop();
        serverChunkManager.getLightingProvider().setTaskBatchSize(5);
    }

    protected void loadWorldResourcePack(String worldName, WorldSaveHandler worldSaveHandler) {
        File file = new File(worldSaveHandler.getWorldDir(), "resources.zip");
        if (file.isFile()) {
            try {
                this.setResourcePack("level://" + URLEncoder.encode(worldName, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                LOGGER.warn("Something went wrong url encoding {}", (Object)worldName);
            }
        }
    }

    public abstract boolean shouldGenerateStructures();

    public abstract GameMode getDefaultGameMode();

    public abstract Difficulty getDefaultDifficulty();

    public abstract boolean isHardcore();

    public abstract int getOpPermissionLevel();

    public abstract int method_21714();

    public abstract boolean shouldBroadcastRconToOps();

    public boolean save(boolean bl, boolean bl2, boolean bl3) {
        boolean bl4 = false;
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (!bl) {
                LOGGER.info("Saving chunks for level '{}'/{}", (Object)serverWorld.getLevelProperties().getLevelName(), (Object)DimensionType.getId(serverWorld.dimension.getType()));
            }
            try {
                serverWorld.save(null, bl2, serverWorld.savingDisabled && !bl3);
            }
            catch (SessionLockException sessionLockException) {
                LOGGER.warn(sessionLockException.getMessage());
            }
            bl4 = true;
        }
        ServerWorld serverWorld2 = this.getWorld(DimensionType.OVERWORLD);
        LevelProperties levelProperties = serverWorld2.getLevelProperties();
        serverWorld2.getWorldBorder().save(levelProperties);
        levelProperties.setCustomBossEvents(this.getBossBarManager().toTag());
        serverWorld2.getSaveHandler().saveWorld(levelProperties, this.getPlayerManager().getUserData());
        return bl4;
    }

    @Override
    public void close() {
        this.shutdown();
    }

    protected void shutdown() {
        LOGGER.info("Stopping server");
        if (this.getNetworkIo() != null) {
            this.getNetworkIo().stop();
        }
        if (this.playerManager != null) {
            LOGGER.info("Saving players");
            this.playerManager.saveAllPlayerData();
            this.playerManager.disconnectAllPlayers();
        }
        LOGGER.info("Saving worlds");
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld == null) continue;
            serverWorld.savingDisabled = false;
        }
        this.save(false, true, false);
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld == null) continue;
            try {
                serverWorld.close();
            }
            catch (IOException iOException) {
                LOGGER.error("Exception closing the level", (Throwable)iOException);
            }
        }
        if (this.snooper.isActive()) {
            this.snooper.cancel();
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void stop(boolean bl) {
        this.running = false;
        if (bl) {
            try {
                this.serverThread.join();
            }
            catch (InterruptedException interruptedException) {
                LOGGER.error("Error while shutting down", (Throwable)interruptedException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            if (this.setupServer()) {
                this.timeReference = Util.getMeasuringTimeMs();
                this.metadata.setDescription(new LiteralText(this.motd));
                this.metadata.setVersion(new ServerMetadata.Version(SharedConstants.getGameVersion().getName(), SharedConstants.getGameVersion().getProtocolVersion()));
                this.setFavicon(this.metadata);
                while (this.running) {
                    long l = Util.getMeasuringTimeMs() - this.timeReference;
                    if (l > 2000L && this.timeReference - this.field_4557 >= 15000L) {
                        long m = l / 50L;
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", (Object)l, (Object)m);
                        this.timeReference += m * 50L;
                        this.field_4557 = this.timeReference;
                    }
                    this.timeReference += 50L;
                    if (this.profilerStartQueued) {
                        this.profilerStartQueued = false;
                        this.profiler.getController().enable();
                    }
                    this.profiler.startTick();
                    this.profiler.push("tick");
                    this.tick(this::shouldKeepTicking);
                    this.profiler.swap("nextTickWait");
                    this.field_19249 = true;
                    this.field_19248 = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
                    this.method_16208();
                    this.profiler.pop();
                    this.profiler.endTick();
                    this.loading = true;
                }
            } else {
                this.setCrashReport(null);
            }
        }
        catch (Throwable throwable) {
            LOGGER.error("Encountered an unexpected exception", throwable);
            CrashReport crashReport = throwable instanceof CrashException ? this.populateCrashReport(((CrashException)throwable).getReport()) : this.populateCrashReport(new CrashReport("Exception in server tick loop", throwable));
            File file = new File(new File(this.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
            if (crashReport.writeToFile(file)) {
                LOGGER.error("This crash report has been saved to: {}", (Object)file.getAbsolutePath());
            } else {
                LOGGER.error("We were unable to save this crash report to disk.");
            }
            this.setCrashReport(crashReport);
        }
        finally {
            try {
                this.stopped = true;
                this.shutdown();
            }
            catch (Throwable throwable) {
                LOGGER.error("Exception stopping the server", throwable);
            }
            finally {
                this.exit();
            }
        }
    }

    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.field_19249 ? this.field_19248 : this.timeReference);
    }

    protected void method_16208() {
        this.runTasks();
        this.runTasks(() -> !this.shouldKeepTicking());
    }

    @Override
    protected ServerTask createTask(Runnable runnable) {
        return new ServerTask(this.ticks, runnable);
    }

    @Override
    protected boolean canExecute(ServerTask serverTask) {
        return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
    }

    @Override
    public boolean runTask() {
        boolean bl;
        this.field_19249 = bl = this.method_20415();
        return bl;
    }

    private boolean method_20415() {
        if (super.runTask()) {
            return true;
        }
        if (this.shouldKeepTicking()) {
            for (ServerWorld serverWorld : this.getWorlds()) {
                if (!serverWorld.getChunkManager().executeQueuedTasks()) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFavicon(ServerMetadata metadata) {
        File file = this.getFile("server-icon.png");
        if (!file.exists()) {
            file = this.getLevelStorage().resolveFile(this.getLevelName(), "icon.png");
        }
        if (file.isFile()) {
            ByteBuf byteBuf = Unpooled.buffer();
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Validate.validState((bufferedImage.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                Validate.validState((bufferedImage.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                ImageIO.write((RenderedImage)bufferedImage, "PNG", (OutputStream)new ByteBufOutputStream(byteBuf));
                ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
                metadata.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't load server icon", (Throwable)exception);
            }
            finally {
                byteBuf.release();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasIconFile() {
        this.iconFilePresent = this.iconFilePresent || this.getIconFile().isFile();
        return this.iconFilePresent;
    }

    @Environment(value=EnvType.CLIENT)
    public File getIconFile() {
        return this.getLevelStorage().resolveFile(this.getLevelName(), "icon.png");
    }

    public File getRunDirectory() {
        return new File(".");
    }

    protected void setCrashReport(CrashReport crashReport) {
    }

    protected void exit() {
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        long l = Util.getMeasuringTimeNano();
        ++this.ticks;
        this.tickWorlds(booleanSupplier);
        if (l - this.lastPlayerSampleUpdate >= 5000000000L) {
            this.lastPlayerSampleUpdate = l;
            this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
            GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);
            for (int j = 0; j < gameProfiles.length; ++j) {
                gameProfiles[j] = this.playerManager.getPlayerList().get(i + j).getGameProfile();
            }
            Collections.shuffle(Arrays.asList(gameProfiles));
            this.metadata.getPlayers().setSample(gameProfiles);
        }
        if (this.ticks % 6000 == 0) {
            LOGGER.debug("Autosave started");
            this.profiler.push("save");
            this.playerManager.saveAllPlayerData();
            this.save(true, false, false);
            this.profiler.pop();
            LOGGER.debug("Autosave finished");
        }
        this.profiler.push("snooper");
        if (!this.snooper.isActive() && this.ticks > 100) {
            this.snooper.method_5482();
        }
        if (this.ticks % 6000 == 0) {
            this.snooper.update();
        }
        this.profiler.pop();
        this.profiler.push("tallying");
        long l2 = Util.getMeasuringTimeNano() - l;
        this.lastTickLengths[this.ticks % 100] = l2;
        long m = l2;
        this.tickTime = this.tickTime * 0.8f + (float)m / 1000000.0f * 0.19999999f;
        long n = Util.getMeasuringTimeNano();
        this.metricsData.pushSample(n - l);
        this.profiler.pop();
    }

    protected void tickWorlds(BooleanSupplier booleanSupplier) {
        this.profiler.push("commandFunctions");
        this.getCommandFunctionManager().tick();
        this.profiler.swap("levels");
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld.dimension.getType() != DimensionType.OVERWORLD && !this.isNetherAllowed()) continue;
            this.profiler.push(() -> serverWorld.getLevelProperties().getLevelName() + " " + Registry.DIMENSION.getId(serverWorld.dimension.getType()));
            if (this.ticks % 20 == 0) {
                this.profiler.push("timeSync");
                this.playerManager.sendToDimension(new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), serverWorld.dimension.getType());
                this.profiler.pop();
            }
            this.profiler.push("tick");
            try {
                serverWorld.tick(booleanSupplier);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Exception ticking world");
                serverWorld.addDetailsToCrashReport(crashReport);
                throw new CrashException(crashReport);
            }
            this.profiler.pop();
            this.profiler.pop();
        }
        this.profiler.swap("connection");
        this.getNetworkIo().tick();
        this.profiler.swap("players");
        this.playerManager.updatePlayerLatency();
        this.profiler.swap("server gui refresh");
        for (int i = 0; i < this.serverGuiTickables.size(); ++i) {
            this.serverGuiTickables.get(i).run();
        }
        this.profiler.pop();
    }

    public boolean isNetherAllowed() {
        return true;
    }

    public void addServerGuiTickable(Runnable tickable) {
        this.serverGuiTickables.add(tickable);
    }

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();
        OptionSpecBuilder optionSpec = optionParser.accepts("nogui");
        OptionSpecBuilder optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("demo");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("bonusChest");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("forceUpgrade");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("eraseCache");
        AbstractOptionSpec optionSpec7 = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec optionSpec8 = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("universe").withRequiredArg().defaultsTo((Object)".", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec10 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec11 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)-1, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec12 = optionParser.accepts("serverId").withRequiredArg();
        NonOptionArgumentSpec optionSpec13 = optionParser.nonOptions();
        try {
            boolean bl;
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has((OptionSpec)optionSpec7)) {
                optionParser.printHelpOn((OutputStream)System.err);
                return;
            }
            Path path = Paths.get("server.properties", new String[0]);
            ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(path);
            serverPropertiesLoader.store();
            Path path2 = Paths.get("eula.txt", new String[0]);
            EulaReader eulaReader = new EulaReader(path2);
            if (optionSet.has((OptionSpec)optionSpec2)) {
                LOGGER.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path2.toAbsolutePath().toString() + "'");
                return;
            }
            if (!eulaReader.isEulaAgreedTo()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            Bootstrap.initialize();
            Bootstrap.logMissingTranslations();
            String string = (String)optionSet.valueOf((OptionSpec)optionSpec9);
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(string, USER_CACHE_FILE.getName()));
            String string2 = (String)Optional.ofNullable(optionSet.valueOf((OptionSpec)optionSpec10)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
            final MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(new File(string), serverPropertiesLoader, Schemas.getFixer(), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new, string2);
            minecraftDedicatedServer.setUserName((String)optionSet.valueOf((OptionSpec)optionSpec8));
            minecraftDedicatedServer.setServerPort((Integer)optionSet.valueOf((OptionSpec)optionSpec11));
            minecraftDedicatedServer.setDemo(optionSet.has((OptionSpec)optionSpec3));
            minecraftDedicatedServer.setBonusChest(optionSet.has((OptionSpec)optionSpec4));
            minecraftDedicatedServer.setForceWorldUpgrade(optionSet.has((OptionSpec)optionSpec5));
            minecraftDedicatedServer.setEraseCache(optionSet.has((OptionSpec)optionSpec6));
            minecraftDedicatedServer.setServerId((String)optionSet.valueOf((OptionSpec)optionSpec12));
            boolean bl2 = bl = !optionSet.has((OptionSpec)optionSpec) && !optionSet.valuesOf((OptionSpec)optionSpec13).contains("nogui");
            if (bl && !GraphicsEnvironment.isHeadless()) {
                minecraftDedicatedServer.createGui();
            }
            minecraftDedicatedServer.start();
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    minecraftDedicatedServer.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        }
        catch (Exception exception) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
        }
    }

    protected void setServerId(String serverId) {
        this.serverId = serverId;
    }

    protected void setForceWorldUpgrade(boolean bl) {
        this.forceWorldUpgrade = bl;
    }

    protected void setEraseCache(boolean eraseCache) {
        this.eraseCache = eraseCache;
    }

    public void start() {
        this.serverThread.start();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isStopping() {
        return !this.serverThread.isAlive();
    }

    public File getFile(String string) {
        return new File(this.getRunDirectory(), string);
    }

    public void info(String string) {
        LOGGER.info(string);
    }

    public void warn(String string) {
        LOGGER.warn(string);
    }

    public ServerWorld getWorld(DimensionType dimensionType) {
        return this.worlds.get(dimensionType);
    }

    public Iterable<ServerWorld> getWorlds() {
        return this.worlds.values();
    }

    public String getVersion() {
        return SharedConstants.getGameVersion().getName();
    }

    public int getCurrentPlayerCount() {
        return this.playerManager.getCurrentPlayerCount();
    }

    public int getMaxPlayerCount() {
        return this.playerManager.getMaxPlayerCount();
    }

    public String[] getPlayerNames() {
        return this.playerManager.getPlayerNames();
    }

    public boolean isDebuggingEnabled() {
        return false;
    }

    public void logError(String string) {
        LOGGER.error(string);
    }

    public void log(String string) {
        if (this.isDebuggingEnabled()) {
            LOGGER.info(string);
        }
    }

    public String getServerModName() {
        return "vanilla";
    }

    public CrashReport populateCrashReport(CrashReport crashReport) {
        if (this.playerManager != null) {
            crashReport.getSystemDetailsSection().add("Player Count", () -> this.playerManager.getCurrentPlayerCount() + " / " + this.playerManager.getMaxPlayerCount() + "; " + this.playerManager.getPlayerList());
        }
        crashReport.getSystemDetailsSection().add("Data Packs", () -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (ResourcePackProfile resourcePackProfile : this.dataPackContainerManager.getEnabledProfiles()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(resourcePackProfile.getName());
                if (resourcePackProfile.getCompatibility().isCompatible()) continue;
                stringBuilder.append(" (incompatible)");
            }
            return stringBuilder.toString();
        });
        if (this.serverId != null) {
            crashReport.getSystemDetailsSection().add("Server Id", () -> this.serverId);
        }
        return crashReport;
    }

    public boolean hasGameDir() {
        return this.gameDir != null;
    }

    @Override
    public void sendMessage(Text message) {
        LOGGER.info(message.getString());
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSinglePlayer() {
        return this.userName != null;
    }

    public String getLevelName() {
        return this.levelName;
    }

    @Environment(value=EnvType.CLIENT)
    public void setServerName(String serverName) {
        this.displayName = serverName;
    }

    @Environment(value=EnvType.CLIENT)
    public String getServerName() {
        return this.displayName;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public void setDifficulty(Difficulty difficulty, boolean bl) {
        for (ServerWorld serverWorld : this.getWorlds()) {
            LevelProperties levelProperties = serverWorld.getLevelProperties();
            if (!bl && levelProperties.isDifficultyLocked()) continue;
            if (levelProperties.isHardcore()) {
                levelProperties.setDifficulty(Difficulty.HARD);
                serverWorld.setMobSpawnOptions(true, true);
                continue;
            }
            if (this.isSinglePlayer()) {
                levelProperties.setDifficulty(difficulty);
                serverWorld.setMobSpawnOptions(serverWorld.getDifficulty() != Difficulty.PEACEFUL, true);
                continue;
            }
            levelProperties.setDifficulty(difficulty);
            serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.spawnAnimals);
        }
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }

    public void setDifficultyLocked(boolean locked) {
        for (ServerWorld serverWorld : this.getWorlds()) {
            LevelProperties levelProperties = serverWorld.getLevelProperties();
            levelProperties.setDifficultyLocked(locked);
        }
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }

    private void sendDifficulty(ServerPlayerEntity player) {
        LevelProperties levelProperties = player.getServerWorld().getLevelProperties();
        player.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
    }

    protected boolean isMonsterSpawningEnabled() {
        return true;
    }

    public boolean isDemo() {
        return this.demo;
    }

    public void setDemo(boolean demo) {
        this.demo = demo;
    }

    public void setBonusChest(boolean bonusChest) {
        this.bonusChest = bonusChest;
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    public String getResourcePackUrl() {
        return this.resourcePackUrl;
    }

    public String getResourcePackHash() {
        return this.resourcePackHash;
    }

    public void setResourcePack(String url, String hash) {
        this.resourcePackUrl = url;
        this.resourcePackHash = hash;
    }

    @Override
    public void addSnooperInfo(Snooper snooper) {
        snooper.addInfo("whitelist_enabled", false);
        snooper.addInfo("whitelist_count", 0);
        if (this.playerManager != null) {
            snooper.addInfo("players_current", this.getCurrentPlayerCount());
            snooper.addInfo("players_max", this.getMaxPlayerCount());
            snooper.addInfo("players_seen", this.getWorld(DimensionType.OVERWORLD).getSaveHandler().getSavedPlayerIds().length);
        }
        snooper.addInfo("uses_auth", this.onlineMode);
        snooper.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
        snooper.addInfo("run_time", (Util.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
        snooper.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
        int i = 0;
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld == null) continue;
            LevelProperties levelProperties = serverWorld.getLevelProperties();
            snooper.addInfo("world[" + i + "][dimension]", serverWorld.dimension.getType());
            snooper.addInfo("world[" + i + "][mode]", (Object)levelProperties.getGameMode());
            snooper.addInfo("world[" + i + "][difficulty]", (Object)serverWorld.getDifficulty());
            snooper.addInfo("world[" + i + "][hardcore]", levelProperties.isHardcore());
            snooper.addInfo("world[" + i + "][generator_name]", levelProperties.getGeneratorType().getName());
            snooper.addInfo("world[" + i + "][generator_version]", levelProperties.getGeneratorType().getVersion());
            snooper.addInfo("world[" + i + "][height]", this.worldHeight);
            snooper.addInfo("world[" + i + "][chunks_loaded]", serverWorld.getChunkManager().getLoadedChunkCount());
            ++i;
        }
        snooper.addInfo("worlds", i);
    }

    public abstract boolean isDedicated();

    public boolean isOnlineMode() {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }

    public boolean shouldPreventProxyConnections() {
        return this.preventProxyConnections;
    }

    public void setPreventProxyConnections(boolean preventProxyConnections) {
        this.preventProxyConnections = preventProxyConnections;
    }

    public boolean shouldSpawnAnimals() {
        return this.spawnAnimals;
    }

    public void setSpawnAnimals(boolean spawnAnimals) {
        this.spawnAnimals = spawnAnimals;
    }

    public boolean shouldSpawnNpcs() {
        return this.spawnNpcs;
    }

    public abstract boolean isUsingNativeTransport();

    public void setSpawnNpcs(boolean spawnNpcs) {
        this.spawnNpcs = spawnNpcs;
    }

    public boolean isPvpEnabled() {
        return this.pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public boolean isFlightEnabled() {
        return this.flightEnabled;
    }

    public void setFlightEnabled(boolean flightEnabled) {
        this.flightEnabled = flightEnabled;
    }

    public abstract boolean areCommandBlocksEnabled();

    public String getServerMotd() {
        return this.motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getWorldHeight() {
        return this.worldHeight;
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public abstract boolean isRemote();

    public void setDefaultGameMode(GameMode gameMode) {
        for (ServerWorld serverWorld : this.getWorlds()) {
            serverWorld.getLevelProperties().setGameMode(gameMode);
        }
    }

    @Nullable
    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLoading() {
        return this.loading;
    }

    public boolean hasGui() {
        return false;
    }

    public abstract boolean openToLan(GameMode var1, boolean var2, int var3);

    public int getTicks() {
        return this.ticks;
    }

    public void enableProfiler() {
        this.profilerStartQueued = true;
    }

    @Environment(value=EnvType.CLIENT)
    public Snooper getSnooper() {
        return this.snooper;
    }

    public int getSpawnProtectionRadius() {
        return 16;
    }

    public boolean isSpawnProtected(World world, BlockPos blockPos, PlayerEntity playerEntity) {
        return false;
    }

    public void setForceGameMode(boolean forceGameMode) {
        this.forceGameMode = forceGameMode;
    }

    public boolean shouldForceGameMode() {
        return this.forceGameMode;
    }

    public int getPlayerIdleTimeout() {
        return this.playerIdleTimeout;
    }

    public void setPlayerIdleTimeout(int playerIdleTimeout) {
        this.playerIdleTimeout = playerIdleTimeout;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public GameProfileRepository getGameProfileRepo() {
        return this.gameProfileRepo;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }

    public void forcePlayerSampleUpdate() {
        this.lastPlayerSampleUpdate = 0L;
    }

    public int getMaxWorldBorderRadius() {
        return 29999984;
    }

    @Override
    public boolean shouldExecuteAsync() {
        return super.shouldExecuteAsync() && !this.isStopped();
    }

    @Override
    public Thread getThread() {
        return this.serverThread;
    }

    public int getNetworkCompressionThreshold() {
        return 256;
    }

    public long getServerStartTime() {
        return this.timeReference;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public int getSpawnRadius(@Nullable ServerWorld world) {
        if (world != null) {
            return world.getGameRules().getInt(GameRules.SPAWN_RADIUS);
        }
        return 10;
    }

    public ServerAdvancementLoader getAdvancementManager() {
        return this.advancementManager;
    }

    public CommandFunctionManager getCommandFunctionManager() {
        return this.commandFunctionManager;
    }

    public void reload() {
        if (!this.isOnThread()) {
            this.execute(this::reload);
            return;
        }
        this.getPlayerManager().saveAllPlayerData();
        this.dataPackContainerManager.scanPacks();
        this.reloadDataPacks(this.getWorld(DimensionType.OVERWORLD).getLevelProperties());
        this.getPlayerManager().onDataPacksReloaded();
    }

    private void reloadDataPacks(LevelProperties levelProperties) {
        ArrayList list = Lists.newArrayList(this.dataPackContainerManager.getEnabledProfiles());
        for (ResourcePackProfile resourcePackProfile2 : this.dataPackContainerManager.getProfiles()) {
            if (levelProperties.getDisabledDataPacks().contains(resourcePackProfile2.getName()) || list.contains(resourcePackProfile2)) continue;
            LOGGER.info("Found new data pack {}, loading it automatically", (Object)resourcePackProfile2.getName());
            resourcePackProfile2.getInitialPosition().insert(list, resourcePackProfile2, resourcePackProfile -> resourcePackProfile, false);
        }
        this.dataPackContainerManager.setEnabledProfiles(list);
        ArrayList list2 = Lists.newArrayList();
        this.dataPackContainerManager.getEnabledProfiles().forEach(resourcePackProfile -> list2.add(resourcePackProfile.createResourcePack()));
        CompletableFuture<Unit> completableFuture = this.dataManager.beginReload(this.workerExecutor, this, list2, field_20279);
        this.runTasks(completableFuture::isDone);
        try {
            completableFuture.get();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to reload data packs", (Throwable)exception);
        }
        levelProperties.getEnabledDataPacks().clear();
        levelProperties.getDisabledDataPacks().clear();
        this.dataPackContainerManager.getEnabledProfiles().forEach(resourcePackProfile -> levelProperties.getEnabledDataPacks().add(resourcePackProfile.getName()));
        this.dataPackContainerManager.getProfiles().forEach(resourcePackProfile -> {
            if (!this.dataPackContainerManager.getEnabledProfiles().contains(resourcePackProfile)) {
                levelProperties.getDisabledDataPacks().add(resourcePackProfile.getName());
            }
        });
    }

    public void kickNonWhitelistedPlayers(ServerCommandSource source) {
        if (!this.isWhitelistEnabled()) {
            return;
        }
        PlayerManager playerManager = source.getMinecraftServer().getPlayerManager();
        Whitelist whitelist = playerManager.getWhitelist();
        if (!whitelist.isEnabled()) {
            return;
        }
        ArrayList list = Lists.newArrayList(playerManager.getPlayerList());
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (whitelist.isAllowed(serverPlayerEntity.getGameProfile())) continue;
            serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted", new Object[0]));
        }
    }

    public ReloadableResourceManager getDataManager() {
        return this.dataManager;
    }

    public ResourcePackManager<ResourcePackProfile> getDataPackContainerManager() {
        return this.dataPackContainerManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(this, this.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(this.getWorld(DimensionType.OVERWORLD).getSpawnPos()), Vec2f.ZERO, this.getWorld(DimensionType.OVERWORLD), 4, "Server", new LiteralText("Server"), this, null);
    }

    @Override
    public boolean sendCommandFeedback() {
        return true;
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    public RegistryTagManager getTagManager() {
        return this.tagManager;
    }

    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public LootManager getLootManager() {
        return this.lootManager;
    }

    public GameRules getGameRules() {
        return this.getWorld(DimensionType.OVERWORLD).getGameRules();
    }

    public BossBarManager getBossBarManager() {
        return this.bossBarManager;
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
    }

    public float getTickTime() {
        return this.tickTime;
    }

    public int getPermissionLevel(GameProfile profile) {
        if (this.getPlayerManager().isOperator(profile)) {
            OperatorEntry operatorEntry = (OperatorEntry)this.getPlayerManager().getOpList().get(profile);
            if (operatorEntry != null) {
                return operatorEntry.getPermissionLevel();
            }
            if (this.isOwner(profile)) {
                return 4;
            }
            if (this.isSinglePlayer()) {
                return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
            }
            return this.getOpPermissionLevel();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public MetricsData getMetricsData() {
        return this.metricsData;
    }

    public DisableableProfiler getProfiler() {
        return this.profiler;
    }

    public Executor getWorkerExecutor() {
        return this.workerExecutor;
    }

    public abstract boolean isOwner(GameProfile var1);

    public void dump(Path path) throws IOException {
        Path path2 = path.resolve("levels");
        for (Map.Entry<DimensionType, ServerWorld> entry : this.worlds.entrySet()) {
            Identifier identifier = DimensionType.getId(entry.getKey());
            Path path3 = path2.resolve(identifier.getNamespace()).resolve(identifier.getPath());
            Files.createDirectories(path3, new FileAttribute[0]);
            entry.getValue().method_21625(path3);
        }
        this.dumpGamerules(path.resolve("gamerules.txt"));
        this.dumpClasspath(path.resolve("classpath.txt"));
        this.dumpExampleCrash(path.resolve("example_crash.txt"));
        this.dumpStats(path.resolve("stats.txt"));
        this.dumpThreads(path.resolve("threads.txt"));
    }

    private void dumpStats(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            writer.write(String.format("pending_tasks: %d\n", this.method_21684()));
            writer.write(String.format("average_tick_time: %f\n", Float.valueOf(this.getTickTime())));
            writer.write(String.format("tick_times: %s\n", Arrays.toString(this.lastTickLengths)));
            writer.write(String.format("queue: %s\n", Util.getServerWorkerExecutor()));
        }
    }

    private void dumpExampleCrash(Path path) throws IOException {
        CrashReport crashReport = new CrashReport("Server dump", new Exception("dummy"));
        this.populateCrashReport(crashReport);
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            writer.write(crashReport.asString());
        }
    }

    private void dumpGamerules(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            final ArrayList list = Lists.newArrayList();
            final GameRules gameRules = this.getGameRules();
            GameRules.forEachType(new GameRules.RuleConsumer(){

                @Override
                public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
                    list.add(String.format("%s=%s\n", key.getName(), ((GameRules.Rule)gameRules.get(key)).toString()));
                }
            });
            for (String string : list) {
                writer.write(string);
            }
        }
    }

    private void dumpClasspath(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            String string = System.getProperty("java.class.path");
            String string2 = System.getProperty("path.separator");
            for (String string3 : Splitter.on((String)string2).split((CharSequence)string)) {
                writer.write(string3);
                writer.write("\n");
            }
        }
    }

    private void dumpThreads(Path path) throws IOException {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        Arrays.sort(threadInfos, Comparator.comparing(ThreadInfo::getThreadName));
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            for (ThreadInfo threadInfo : threadInfos) {
                writer.write(threadInfo.toString());
                ((Writer)writer).write(10);
            }
        }
    }

    @Override
    public /* synthetic */ boolean canExecute(Runnable task) {
        return this.canExecute((ServerTask)task);
    }

    @Override
    public /* synthetic */ Runnable createTask(Runnable runnable) {
        return this.createTask(runnable);
    }
}

