/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.server.dedicated.PendingServerCommand;
import net.minecraft.server.dedicated.ServerCommandOutput;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.server.rcon.RconServer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.UncaughtExceptionHandler;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class MinecraftDedicatedServer
extends MinecraftServer
implements DedicatedServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<PendingServerCommand> commandQueue = Collections.synchronizedList(Lists.newArrayList());
    private QueryResponseHandler queryResponseHandler;
    private final ServerCommandOutput rconCommandOutput;
    private RconServer rconServer;
    private final ServerPropertiesLoader propertiesLoader;
    private GameMode defaultGameMode;
    @Nullable
    private DedicatedServerGui gui;

    public MinecraftDedicatedServer(File file, ServerPropertiesLoader serverPropertiesLoader, DataFixer dataFixer, YggdrasilAuthenticationService yggdrasilAuthenticationService, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, String string) {
        super(file, Proxy.NO_PROXY, dataFixer, new CommandManager(true), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory, string);
        this.propertiesLoader = serverPropertiesLoader;
        this.rconCommandOutput = new ServerCommandOutput(this);
        new Thread("Server Infinisleeper"){
            {
                this.setDaemon(true);
                this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
                this.start();
            }

            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(Integer.MAX_VALUE);
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        continue;
                    }
                    break;
                }
            }
        };
    }

    @Override
    public boolean setupServer() throws IOException {
        Thread thread = new Thread("Server console handler"){

            @Override
            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                try {
                    String string;
                    while (!MinecraftDedicatedServer.this.isStopped() && MinecraftDedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
                        MinecraftDedicatedServer.this.enqueueCommand(string, MinecraftDedicatedServer.this.getCommandSource());
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Exception handling console input", (Throwable)iOException);
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
        LOGGER.info("Starting minecraft server version " + SharedConstants.getGameVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        LOGGER.info("Loading properties");
        ServerPropertiesHandler serverPropertiesHandler = this.propertiesLoader.getPropertiesHandler();
        if (this.isSinglePlayer()) {
            this.setServerIp("127.0.0.1");
        } else {
            this.setOnlineMode(serverPropertiesHandler.onlineMode);
            this.setPreventProxyConnections(serverPropertiesHandler.preventProxyConnections);
            this.setServerIp(serverPropertiesHandler.serverIp);
        }
        this.setSpawnAnimals(serverPropertiesHandler.spawnAnimals);
        this.setSpawnNpcs(serverPropertiesHandler.spawnNpcs);
        this.setPvpEnabled(serverPropertiesHandler.pvp);
        this.setFlightEnabled(serverPropertiesHandler.allowFlight);
        this.setResourcePack(serverPropertiesHandler.resourcePack, this.createResourcePackHash());
        this.setMotd(serverPropertiesHandler.motd);
        this.setForceGameMode(serverPropertiesHandler.forceGameMode);
        super.setPlayerIdleTimeout(serverPropertiesHandler.playerIdleTimeout.get());
        this.setWhitelistEnabled(serverPropertiesHandler.enforceWhitelist);
        this.defaultGameMode = serverPropertiesHandler.gameMode;
        LOGGER.info("Default game type: {}", (Object)this.defaultGameMode);
        InetAddress inetAddress = null;
        if (!this.getServerIp().isEmpty()) {
            inetAddress = InetAddress.getByName(this.getServerIp());
        }
        if (this.getServerPort() < 0) {
            this.setServerPort(serverPropertiesHandler.serverPort);
        }
        LOGGER.info("Generating keypair");
        this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
        LOGGER.info("Starting Minecraft server on {}:{}", (Object)(this.getServerIp().isEmpty() ? "*" : this.getServerIp()), (Object)this.getServerPort());
        try {
            this.getNetworkIo().bind(inetAddress, this.getServerPort());
        }
        catch (IOException iOException) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", (Object)iOException.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.isOnlineMode()) {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }
        if (this.convertData()) {
            this.getUserCache().save();
        }
        if (!ServerConfigHandler.checkSuccess(this)) {
            return false;
        }
        this.setPlayerManager(new DedicatedPlayerManager(this));
        long l = Util.getMeasuringTimeNano();
        String string = serverPropertiesHandler.levelSeed;
        String string2 = serverPropertiesHandler.generatorSettings;
        long m = new Random().nextLong();
        if (!string.isEmpty()) {
            try {
                long n = Long.parseLong(string);
                if (n != 0L) {
                    m = n;
                }
            }
            catch (NumberFormatException numberFormatException) {
                m = string.hashCode();
            }
        }
        LevelGeneratorType levelGeneratorType = serverPropertiesHandler.levelType;
        this.setWorldHeight(serverPropertiesHandler.maxBuildHeight);
        SkullBlockEntity.setUserCache(this.getUserCache());
        SkullBlockEntity.setSessionService(this.getSessionService());
        UserCache.setUseRemote(this.isOnlineMode());
        LOGGER.info("Preparing level \"{}\"", (Object)this.getLevelName());
        JsonObject jsonObject = new JsonObject();
        if (levelGeneratorType == LevelGeneratorType.FLAT) {
            jsonObject.addProperty("flat_world_options", string2);
        } else if (!string2.isEmpty()) {
            jsonObject = JsonHelper.deserialize(string2);
        }
        this.loadWorld(this.getLevelName(), this.getLevelName(), m, levelGeneratorType, (JsonElement)jsonObject);
        long o = Util.getMeasuringTimeNano() - l;
        String string3 = String.format(Locale.ROOT, "%.3fs", (double)o / 1.0E9);
        LOGGER.info("Done ({})! For help, type \"help\"", (Object)string3);
        if (serverPropertiesHandler.announcePlayerAchievements != null) {
            this.getGameRules().get(GameRules.ANNOUNCE_ADVANCEMENTS).set(serverPropertiesHandler.announcePlayerAchievements, this);
        }
        if (serverPropertiesHandler.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryResponseHandler = new QueryResponseHandler(this);
            this.queryResponseHandler.start();
        }
        if (serverPropertiesHandler.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconServer = new RconServer(this);
            this.rconServer.start();
        }
        if (this.getMaxTickTime() > 0L) {
            Thread thread2 = new Thread(new DedicatedServerWatchdog(this));
            thread2.setUncaughtExceptionHandler(new UncaughtExceptionHandler(LOGGER));
            thread2.setName("Server Watchdog");
            thread2.setDaemon(true);
            thread2.start();
        }
        Items.AIR.appendStacks(ItemGroup.SEARCH, DefaultedList.of());
        return true;
    }

    public String createResourcePackHash() {
        String string;
        ServerPropertiesHandler serverPropertiesHandler = this.propertiesLoader.getPropertiesHandler();
        if (!serverPropertiesHandler.resourcePackSha1.isEmpty()) {
            string = serverPropertiesHandler.resourcePackSha1;
            if (!Strings.isNullOrEmpty((String)serverPropertiesHandler.resourcePackHash)) {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty((String)serverPropertiesHandler.resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            string = serverPropertiesHandler.resourcePackHash;
        } else {
            string = "";
        }
        if (!string.isEmpty() && !SHA1_PATTERN.matcher(string).matches()) {
            LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }
        if (!serverPropertiesHandler.resourcePack.isEmpty() && string.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }
        return string;
    }

    @Override
    public void setDefaultGameMode(GameMode gameMode) {
        super.setDefaultGameMode(gameMode);
        this.defaultGameMode = gameMode;
    }

    @Override
    public ServerPropertiesHandler getProperties() {
        return this.propertiesLoader.getPropertiesHandler();
    }

    @Override
    public boolean shouldGenerateStructures() {
        return this.getProperties().generateStructures;
    }

    @Override
    public GameMode getDefaultGameMode() {
        return this.defaultGameMode;
    }

    @Override
    public Difficulty getDefaultDifficulty() {
        return this.getProperties().difficulty;
    }

    @Override
    public boolean isHardcore() {
        return this.getProperties().hardcore;
    }

    @Override
    public CrashReport populateCrashReport(CrashReport crashReport) {
        crashReport = super.populateCrashReport(crashReport);
        crashReport.getSystemDetailsSection().add("Is Modded", () -> {
            String string = this.getServerModName();
            if (!"vanilla".equals(string)) {
                return "Definitely; Server brand changed to '" + string + "'";
            }
            return "Unknown (can't tell)";
        });
        crashReport.getSystemDetailsSection().add("Type", () -> "Dedicated Server (map_server.txt)");
        return crashReport;
    }

    @Override
    public void exit() {
        if (this.gui != null) {
            this.gui.stop();
        }
        if (this.rconServer != null) {
            this.rconServer.stop();
        }
        if (this.queryResponseHandler != null) {
            this.queryResponseHandler.stop();
        }
    }

    @Override
    public void tickWorlds(BooleanSupplier booleanSupplier) {
        super.tickWorlds(booleanSupplier);
        this.executeQueuedCommands();
    }

    @Override
    public boolean isNetherAllowed() {
        return this.getProperties().allowNether;
    }

    @Override
    public boolean isMonsterSpawningEnabled() {
        return this.getProperties().spawnMonsters;
    }

    @Override
    public void addSnooperInfo(Snooper snooper) {
        snooper.addInfo("whitelist_enabled", this.getPlayerManager().isWhitelistEnabled());
        snooper.addInfo("whitelist_count", this.getPlayerManager().getWhitelistedNames().length);
        super.addSnooperInfo(snooper);
    }

    public void enqueueCommand(String string, ServerCommandSource serverCommandSource) {
        this.commandQueue.add(new PendingServerCommand(string, serverCommandSource));
    }

    public void executeQueuedCommands() {
        while (!this.commandQueue.isEmpty()) {
            PendingServerCommand pendingServerCommand = this.commandQueue.remove(0);
            this.getCommandManager().execute(pendingServerCommand.source, pendingServerCommand.command);
        }
    }

    @Override
    public boolean isDedicated() {
        return true;
    }

    @Override
    public boolean isUsingNativeTransport() {
        return this.getProperties().useNativeTransport;
    }

    @Override
    public DedicatedPlayerManager getPlayerManager() {
        return (DedicatedPlayerManager)super.getPlayerManager();
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public String getHostname() {
        return this.getServerIp();
    }

    @Override
    public int getPort() {
        return this.getServerPort();
    }

    @Override
    public String getMotd() {
        return this.getServerMotd();
    }

    public void createGui() {
        if (this.gui == null) {
            this.gui = DedicatedServerGui.create(this);
        }
    }

    @Override
    public boolean hasGui() {
        return this.gui != null;
    }

    @Override
    public boolean openToLan(GameMode gameMode, boolean cheatsAllowed, int port) {
        return false;
    }

    @Override
    public boolean areCommandBlocksEnabled() {
        return this.getProperties().enableCommandBlock;
    }

    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }

    @Override
    public boolean isSpawnProtected(World world, BlockPos blockPos, PlayerEntity playerEntity) {
        int j;
        if (world.dimension.getType() != DimensionType.OVERWORLD) {
            return false;
        }
        if (this.getPlayerManager().getOpList().isEmpty()) {
            return false;
        }
        if (this.getPlayerManager().isOperator(playerEntity.getGameProfile())) {
            return false;
        }
        if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        }
        BlockPos blockPos2 = world.getSpawnPos();
        int i = MathHelper.abs(blockPos.getX() - blockPos2.getX());
        int k = Math.max(i, j = MathHelper.abs(blockPos.getZ() - blockPos2.getZ()));
        return k <= this.getSpawnProtectionRadius();
    }

    @Override
    public int getOpPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }

    @Override
    public int method_21714() {
        return this.getProperties().field_20324;
    }

    @Override
    public void setPlayerIdleTimeout(int playerIdleTimeout) {
        super.setPlayerIdleTimeout(playerIdleTimeout);
        this.propertiesLoader.apply(serverPropertiesHandler -> (ServerPropertiesHandler)serverPropertiesHandler.playerIdleTimeout.set(playerIdleTimeout));
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return this.getProperties().broadcastRconToOps;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.getProperties().broadcastConsoleToOps;
    }

    @Override
    public int getMaxWorldBorderRadius() {
        return this.getProperties().maxWorldSize;
    }

    @Override
    public int getNetworkCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }

    protected boolean convertData() {
        int i;
        boolean bl = false;
        for (i = 0; !bl && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl = ServerConfigHandler.convertBannedPlayers(this);
        }
        boolean bl2 = false;
        for (i = 0; !bl2 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl2 = ServerConfigHandler.convertBannedIps(this);
        }
        boolean bl3 = false;
        for (i = 0; !bl3 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl3 = ServerConfigHandler.convertOperators(this);
        }
        boolean bl4 = false;
        for (i = 0; !bl4 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl4 = ServerConfigHandler.convertWhitelist(this);
        }
        boolean bl5 = false;
        for (i = 0; !bl5 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl5 = ServerConfigHandler.convertPlayerFiles(this);
        }
        return bl || bl2 || bl3 || bl4 || bl5;
    }

    private void sleepFiveSeconds() {
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedException) {
            return;
        }
    }

    public long getMaxTickTime() {
        return this.getProperties().maxTickTime;
    }

    @Override
    public String getPlugins() {
        return "";
    }

    @Override
    public String executeRconCommand(String string) {
        this.rconCommandOutput.clear();
        this.submitAndJoin(() -> this.getCommandManager().execute(this.rconCommandOutput.createReconCommandSource(), string));
        return this.rconCommandOutput.asString();
    }

    public void setUseWhitelist(boolean bl) {
        this.propertiesLoader.apply(serverPropertiesHandler -> (ServerPropertiesHandler)serverPropertiesHandler.whiteList.set(bl));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Util.shutdownServerWorkerExecutor();
    }

    @Override
    public boolean isOwner(GameProfile profile) {
        return false;
    }

    @Override
    public /* synthetic */ PlayerManager getPlayerManager() {
        return this.getPlayerManager();
    }
}
