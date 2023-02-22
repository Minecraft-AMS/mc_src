/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.Whitelist;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerManager {
    public static final File BANNED_PLAYERS_FILE = new File("banned-players.json");
    public static final File BANNED_IPS_FILE = new File("banned-ips.json");
    public static final File OPERATORS_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    private final List<ServerPlayerEntity> players = Lists.newArrayList();
    private final Map<UUID, ServerPlayerEntity> playerMap = Maps.newHashMap();
    private final BannedPlayerList bannedProfiles = new BannedPlayerList(BANNED_PLAYERS_FILE);
    private final BannedIpList bannedIps = new BannedIpList(BANNED_IPS_FILE);
    private final OperatorList ops = new OperatorList(OPERATORS_FILE);
    private final Whitelist whitelist = new Whitelist(WHITELIST_FILE);
    private final Map<UUID, ServerStatHandler> statisticsMap = Maps.newHashMap();
    private final Map<UUID, PlayerAdvancementTracker> advancementTrackers = Maps.newHashMap();
    private PlayerSaveHandler saveHandler;
    private boolean whitelistEnabled;
    protected final int maxPlayers;
    private int viewDistance;
    private GameMode gameMode;
    private boolean cheatsAllowed;
    private int latencyUpdateTimer;

    public PlayerManager(MinecraftServer server, int maxPlayers) {
        this.server = server;
        this.maxPlayers = maxPlayers;
        this.getUserBanList().setEnabled(true);
        this.getIpBanList().setEnabled(true);
    }

    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
        CompoundTag compoundTag2;
        Entity entity2;
        GameProfile gameProfile = player.getGameProfile();
        UserCache userCache = this.server.getUserCache();
        GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
        String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
        userCache.add(gameProfile);
        CompoundTag compoundTag = this.loadPlayerData(player);
        ServerWorld serverWorld = this.server.getWorld(player.dimension);
        player.setWorld(serverWorld);
        player.interactionManager.setWorld((ServerWorld)player.world);
        String string2 = "local";
        if (connection.getAddress() != null) {
            string2 = connection.getAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", (Object)player.getName().getString(), (Object)string2, (Object)player.getEntityId(), (Object)player.x, (Object)player.y, (Object)player.z);
        LevelProperties levelProperties = serverWorld.getLevelProperties();
        this.setGameMode(player, null, serverWorld);
        ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player);
        serverPlayNetworkHandler.sendPacket(new GameJoinS2CPacket(player.getEntityId(), player.interactionManager.getGameMode(), levelProperties.isHardcore(), serverWorld.dimension.getType(), this.getMaxPlayerCount(), levelProperties.getGeneratorType(), this.viewDistance, serverWorld.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO)));
        serverPlayNetworkHandler.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
        serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
        serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
        serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
        serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
        serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        this.sendCommandTree(player);
        player.getStatHandler().updateStatSet();
        player.getRecipeBook().sendInitRecipesPacket(player);
        this.sendScoreboard(serverWorld.getScoreboard(), player);
        this.server.forcePlayerSampleUpdate();
        TranslatableText text = player.getGameProfile().getName().equalsIgnoreCase(string) ? new TranslatableText("multiplayer.player.joined", player.getDisplayName()) : new TranslatableText("multiplayer.player.joined.renamed", player.getDisplayName(), string);
        this.sendToAll(text.formatted(Formatting.YELLOW));
        serverPlayNetworkHandler.requestTeleport(player.x, player.y, player.z, player.yaw, player.pitch);
        this.players.add(player);
        this.playerMap.put(player.getUuid(), player);
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));
        for (int i = 0; i < this.players.size(); ++i) {
            player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, this.players.get(i)));
        }
        serverWorld.method_18213(player);
        this.server.getBossBarManager().onPlayerConnect(player);
        this.sendWorldInfo(player, serverWorld);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            player.method_14255(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }
        for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
            serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getEntityId(), statusEffectInstance));
        }
        if (compoundTag != null && compoundTag.contains("RootVehicle", 10) && (entity2 = EntityType.loadEntityWithPassengers((compoundTag2 = compoundTag.getCompound("RootVehicle")).getCompound("Entity"), serverWorld, entity -> {
            if (!serverWorld.method_18768((Entity)entity)) {
                return null;
            }
            return entity;
        })) != null) {
            UUID uUID = compoundTag2.getUuid("Attach");
            if (entity2.getUuid().equals(uUID)) {
                player.startRiding(entity2, true);
            } else {
                for (Entity entity22 : entity2.getPassengersDeep()) {
                    if (!entity22.getUuid().equals(uUID)) continue;
                    player.startRiding(entity22, true);
                    break;
                }
            }
            if (!player.hasVehicle()) {
                LOGGER.warn("Couldn't reattach entity to player");
                serverWorld.removeEntity(entity2);
                for (Entity entity22 : entity2.getPassengersDeep()) {
                    serverWorld.removeEntity(entity22);
                }
            }
        }
        player.method_14235();
    }

    protected void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
        HashSet set = Sets.newHashSet();
        for (Team team : scoreboard.getTeams()) {
            player.networkHandler.sendPacket(new TeamS2CPacket(team, 0));
        }
        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(i);
            if (scoreboardObjective == null || set.contains(scoreboardObjective)) continue;
            List<Packet<?>> list = scoreboard.createChangePackets(scoreboardObjective);
            for (Packet<?> packet : list) {
                player.networkHandler.sendPacket(packet);
            }
            set.add(scoreboardObjective);
        }
    }

    public void setMainWorld(ServerWorld world) {
        this.saveHandler = world.getSaveHandler();
        world.getWorldBorder().addListener(new WorldBorderListener(){

            @Override
            public void onSizeChange(WorldBorder worldBorder, double d) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_SIZE));
            }

            @Override
            public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.LERP_SIZE));
            }

            @Override
            public void onCenterChanged(WorldBorder centerX, double centerZ, double d) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(centerX, WorldBorderS2CPacket.Type.SET_CENTER));
            }

            @Override
            public void onWarningTimeChanged(WorldBorder warningTime, int i) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(warningTime, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
            }

            @Override
            public void onWarningBlocksChanged(WorldBorder warningBlocks, int i) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(warningBlocks, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
            }

            @Override
            public void onDamagePerBlockChanged(WorldBorder damagePerBlock, double d) {
            }

            @Override
            public void onSafeZoneChanged(WorldBorder safeZoneRadius, double d) {
            }
        });
    }

    @Nullable
    public CompoundTag loadPlayerData(ServerPlayerEntity player) {
        CompoundTag compoundTag2;
        CompoundTag compoundTag = this.server.getWorld(DimensionType.OVERWORLD).getLevelProperties().getPlayerData();
        if (player.getName().getString().equals(this.server.getUserName()) && compoundTag != null) {
            compoundTag2 = compoundTag;
            player.fromTag(compoundTag2);
            LOGGER.debug("loading single player");
        } else {
            compoundTag2 = this.saveHandler.loadPlayerData(player);
        }
        return compoundTag2;
    }

    protected void savePlayerData(ServerPlayerEntity player) {
        PlayerAdvancementTracker playerAdvancementTracker;
        this.saveHandler.savePlayerData(player);
        ServerStatHandler serverStatHandler = this.statisticsMap.get(player.getUuid());
        if (serverStatHandler != null) {
            serverStatHandler.save();
        }
        if ((playerAdvancementTracker = this.advancementTrackers.get(player.getUuid())) != null) {
            playerAdvancementTracker.save();
        }
    }

    public void remove(ServerPlayerEntity player) {
        Entity entity;
        ServerWorld serverWorld = player.getServerWorld();
        player.incrementStat(Stats.LEAVE_GAME);
        this.savePlayerData(player);
        if (player.hasVehicle() && (entity = player.getRootVehicle()).hasPlayerRider()) {
            LOGGER.debug("Removing player mount");
            player.stopRiding();
            serverWorld.removeEntity(entity);
            for (Entity entity2 : entity.getPassengersDeep()) {
                serverWorld.removeEntity(entity2);
            }
            serverWorld.getChunk(player.chunkX, player.chunkZ).markDirty();
        }
        player.detach();
        serverWorld.removePlayer(player);
        player.getAdvancementTracker().clearCriterions();
        this.players.remove(player);
        this.server.getBossBarManager().onPlayerDisconnenct(player);
        UUID uUID = player.getUuid();
        ServerPlayerEntity serverPlayerEntity = this.playerMap.get(uUID);
        if (serverPlayerEntity == player) {
            this.playerMap.remove(uUID);
            this.statisticsMap.remove(uUID);
            this.advancementTrackers.remove(uUID);
        }
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, player));
    }

    @Nullable
    public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (this.bannedProfiles.contains(gameProfile)) {
            BannedPlayerEntry bannedPlayerEntry = (BannedPlayerEntry)this.bannedProfiles.get(gameProfile);
            TranslatableText text = new TranslatableText("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
            if (bannedPlayerEntry.getExpiryDate() != null) {
                text.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
            }
            return text;
        }
        if (!this.isWhitelisted(gameProfile)) {
            return new TranslatableText("multiplayer.disconnect.not_whitelisted", new Object[0]);
        }
        if (this.bannedIps.isBanned(socketAddress)) {
            BannedIpEntry bannedIpEntry = this.bannedIps.get(socketAddress);
            TranslatableText text = new TranslatableText("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
            if (bannedIpEntry.getExpiryDate() != null) {
                text.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
            }
            return text;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)) {
            return new TranslatableText("multiplayer.disconnect.server_full", new Object[0]);
        }
        return null;
    }

    public ServerPlayerEntity createPlayer(GameProfile profile) {
        UUID uUID = PlayerEntity.getUuidFromProfile(profile);
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverPlayerEntity = this.players.get(i);
            if (!serverPlayerEntity.getUuid().equals(uUID)) continue;
            list.add(serverPlayerEntity);
        }
        ServerPlayerEntity serverPlayerEntity2 = this.playerMap.get(profile.getId());
        if (serverPlayerEntity2 != null && !list.contains(serverPlayerEntity2)) {
            list.add(serverPlayerEntity2);
        }
        for (ServerPlayerEntity serverPlayerEntity3 : list) {
            serverPlayerEntity3.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.duplicate_login", new Object[0]));
        }
        ServerPlayerInteractionManager serverPlayerInteractionManager = this.server.isDemo() ? new DemoServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD)) : new ServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
        return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.OVERWORLD), profile, serverPlayerInteractionManager);
    }

    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, DimensionType dimension, boolean alive) {
        this.players.remove(player);
        player.getServerWorld().removePlayer(player);
        BlockPos blockPos = player.getSpawnPosition();
        boolean bl = player.isSpawnForced();
        player.dimension = dimension;
        ServerPlayerInteractionManager serverPlayerInteractionManager = this.server.isDemo() ? new DemoServerPlayerInteractionManager(this.server.getWorld(player.dimension)) : new ServerPlayerInteractionManager(this.server.getWorld(player.dimension));
        ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(this.server, this.server.getWorld(player.dimension), player.getGameProfile(), serverPlayerInteractionManager);
        serverPlayerEntity.networkHandler = player.networkHandler;
        serverPlayerEntity.copyFrom(player, alive);
        serverPlayerEntity.setEntityId(player.getEntityId());
        serverPlayerEntity.setMainArm(player.getMainArm());
        for (String string : player.getScoreboardTags()) {
            serverPlayerEntity.addScoreboardTag(string);
        }
        ServerWorld serverWorld = this.server.getWorld(player.dimension);
        this.setGameMode(serverPlayerEntity, player, serverWorld);
        if (blockPos != null) {
            Optional<Vec3d> optional = PlayerEntity.method_7288(this.server.getWorld(player.dimension), blockPos, bl);
            if (optional.isPresent()) {
                Vec3d vec3d = optional.get();
                serverPlayerEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, 0.0f, 0.0f);
                serverPlayerEntity.setPlayerSpawn(blockPos, bl);
            } else {
                serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0f));
            }
        }
        while (!serverWorld.doesNotCollide(serverPlayerEntity) && serverPlayerEntity.y < 256.0) {
            serverPlayerEntity.updatePosition(serverPlayerEntity.x, serverPlayerEntity.y + 1.0, serverPlayerEntity.z);
        }
        LevelProperties levelProperties = serverPlayerEntity.world.getLevelProperties();
        serverPlayerEntity.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayerEntity.dimension, levelProperties.getGeneratorType(), serverPlayerEntity.interactionManager.getGameMode()));
        BlockPos blockPos2 = serverWorld.getSpawnPos();
        serverPlayerEntity.networkHandler.requestTeleport(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
        serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos2));
        serverPlayerEntity.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
        serverPlayerEntity.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity.experienceProgress, serverPlayerEntity.totalExperience, serverPlayerEntity.experienceLevel));
        this.sendWorldInfo(serverPlayerEntity, serverWorld);
        this.sendCommandTree(serverPlayerEntity);
        serverWorld.onPlayerRespawned(serverPlayerEntity);
        this.players.add(serverPlayerEntity);
        this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
        serverPlayerEntity.method_14235();
        serverPlayerEntity.setHealth(serverPlayerEntity.getHealth());
        return serverPlayerEntity;
    }

    public void sendCommandTree(ServerPlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        int i = this.server.getPermissionLevel(gameProfile);
        this.sendCommandTree(player, i);
    }

    public void updatePlayerLatency() {
        if (++this.latencyUpdateTimer > 600) {
            this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players));
            this.latencyUpdateTimer = 0;
        }
    }

    public void sendToAll(Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.sendPacket(packet);
        }
    }

    public void sendToDimension(Packet<?> packet, DimensionType dimension) {
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverPlayerEntity = this.players.get(i);
            if (serverPlayerEntity.dimension != dimension) continue;
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }

    public void sendToTeam(PlayerEntity source, Text text) {
        AbstractTeam abstractTeam = source.getScoreboardTeam();
        if (abstractTeam == null) {
            return;
        }
        Collection<String> collection = abstractTeam.getPlayerList();
        for (String string : collection) {
            ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
            if (serverPlayerEntity == null || serverPlayerEntity == source) continue;
            serverPlayerEntity.sendMessage(text);
        }
    }

    public void sendToOtherTeams(PlayerEntity source, Text text) {
        AbstractTeam abstractTeam = source.getScoreboardTeam();
        if (abstractTeam == null) {
            this.sendToAll(text);
            return;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverPlayerEntity = this.players.get(i);
            if (serverPlayerEntity.getScoreboardTeam() == abstractTeam) continue;
            serverPlayerEntity.sendMessage(text);
        }
    }

    public String[] getPlayerNames() {
        String[] strings = new String[this.players.size()];
        for (int i = 0; i < this.players.size(); ++i) {
            strings[i] = this.players.get(i).getGameProfile().getName();
        }
        return strings;
    }

    public BannedPlayerList getUserBanList() {
        return this.bannedProfiles;
    }

    public BannedIpList getIpBanList() {
        return this.bannedIps;
    }

    public void addToOperators(GameProfile gameProfile) {
        this.ops.add(new OperatorEntry(gameProfile, this.server.getOpPermissionLevel(), this.ops.isOp(gameProfile)));
        ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
        if (serverPlayerEntity != null) {
            this.sendCommandTree(serverPlayerEntity);
        }
    }

    public void removeFromOperators(GameProfile gameProfile) {
        this.ops.remove(gameProfile);
        ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
        if (serverPlayerEntity != null) {
            this.sendCommandTree(serverPlayerEntity);
        }
    }

    private void sendCommandTree(ServerPlayerEntity player, int permissionLevel) {
        if (player.networkHandler != null) {
            byte b = permissionLevel <= 0 ? (byte)24 : (permissionLevel >= 4 ? (byte)28 : (byte)((byte)(24 + permissionLevel)));
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, b));
        }
        this.server.getCommandManager().sendCommandTree(player);
    }

    public boolean isWhitelisted(GameProfile gameProfile) {
        return !this.whitelistEnabled || this.ops.contains(gameProfile) || this.whitelist.contains(gameProfile);
    }

    public boolean isOperator(GameProfile gameProfile) {
        return this.ops.contains(gameProfile) || this.server.isOwner(gameProfile) && this.server.getWorld(DimensionType.OVERWORLD).getLevelProperties().areCommandsAllowed() || this.cheatsAllowed;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(String string) {
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (!serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string)) continue;
            return serverPlayerEntity;
        }
        return null;
    }

    public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double d, DimensionType dimension, Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            double g;
            double f;
            double e;
            ServerPlayerEntity serverPlayerEntity = this.players.get(i);
            if (serverPlayerEntity == player || serverPlayerEntity.dimension != dimension || !((e = x - serverPlayerEntity.x) * e + (f = y - serverPlayerEntity.y) * f + (g = z - serverPlayerEntity.z) * g < d * d)) continue;
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }

    public void saveAllPlayerData() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.savePlayerData(this.players.get(i));
        }
    }

    public Whitelist getWhitelist() {
        return this.whitelist;
    }

    public String[] getWhitelistedNames() {
        return this.whitelist.getNames();
    }

    public OperatorList getOpList() {
        return this.ops;
    }

    public String[] getOpNames() {
        return this.ops.getNames();
    }

    public void reloadWhitelist() {
    }

    public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {
        WorldBorder worldBorder = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
        player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
        player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
        BlockPos blockPos = world.getSpawnPos();
        player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos));
        if (world.isRaining()) {
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(1, 0.0f));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(7, world.getRainGradient(1.0f)));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(8, world.getThunderGradient(1.0f)));
        }
    }

    public void method_14594(ServerPlayerEntity player) {
        player.openContainer(player.playerContainer);
        player.method_14217();
        player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
    }

    public int getCurrentPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayerCount() {
        return this.maxPlayers;
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
    }

    public List<ServerPlayerEntity> getPlayersByIp(String string) {
        ArrayList list = Lists.newArrayList();
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (!serverPlayerEntity.getServerBrand().equals(string)) continue;
            list.add(serverPlayerEntity);
        }
        return list;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public CompoundTag getUserData() {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    private void setGameMode(ServerPlayerEntity player, ServerPlayerEntity oldPlayer, IWorld world) {
        if (oldPlayer != null) {
            player.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode());
        } else if (this.gameMode != null) {
            player.interactionManager.setGameMode(this.gameMode);
        }
        player.interactionManager.setGameModeIfNotPresent(world.getLevelProperties().getGameMode());
    }

    @Environment(value=EnvType.CLIENT)
    public void setCheatsAllowed(boolean cheatsAllowed) {
        this.cheatsAllowed = cheatsAllowed;
    }

    public void disconnectAllPlayers() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.server_shutdown", new Object[0]));
        }
    }

    public void broadcastChatMessage(Text text, boolean system) {
        this.server.sendMessage(text);
        MessageType messageType = system ? MessageType.SYSTEM : MessageType.CHAT;
        this.sendToAll(new ChatMessageS2CPacket(text, messageType));
    }

    public void sendToAll(Text text) {
        this.broadcastChatMessage(text, true);
    }

    public ServerStatHandler createStatHandler(PlayerEntity player) {
        ServerStatHandler serverStatHandler;
        UUID uUID = player.getUuid();
        ServerStatHandler serverStatHandler2 = serverStatHandler = uUID == null ? null : this.statisticsMap.get(uUID);
        if (serverStatHandler == null) {
            File file3;
            File file = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "stats");
            File file2 = new File(file, uUID + ".json");
            if (!file2.exists() && (file3 = new File(file, player.getName().getString() + ".json")).exists() && file3.isFile()) {
                file3.renameTo(file2);
            }
            serverStatHandler = new ServerStatHandler(this.server, file2);
            this.statisticsMap.put(uUID, serverStatHandler);
        }
        return serverStatHandler;
    }

    public PlayerAdvancementTracker getAdvancementTracker(ServerPlayerEntity player) {
        UUID uUID = player.getUuid();
        PlayerAdvancementTracker playerAdvancementTracker = this.advancementTrackers.get(uUID);
        if (playerAdvancementTracker == null) {
            File file = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "advancements");
            File file2 = new File(file, uUID + ".json");
            playerAdvancementTracker = new PlayerAdvancementTracker(this.server, file2, player);
            this.advancementTrackers.put(uUID, playerAdvancementTracker);
        }
        playerAdvancementTracker.setOwner(player);
        return playerAdvancementTracker;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        this.sendToAll(new ChunkLoadDistanceS2CPacket(viewDistance));
        for (ServerWorld serverWorld : this.server.getWorlds()) {
            if (serverWorld == null) continue;
            serverWorld.getChunkManager().applyViewDistance(viewDistance);
        }
    }

    public List<ServerPlayerEntity> getPlayerList() {
        return this.players;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(UUID uuid) {
        return this.playerMap.get(uuid);
    }

    public boolean canBypassPlayerLimit(GameProfile gameProfile) {
        return false;
    }

    public void onDataPacksReloaded() {
        for (PlayerAdvancementTracker playerAdvancementTracker : this.advancementTrackers.values()) {
            playerAdvancementTracker.reload();
        }
        this.sendToAll(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket = new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values());
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            serverPlayerEntity.networkHandler.sendPacket(synchronizeRecipesS2CPacket);
            serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
        }
    }

    public boolean areCheatsAllowed() {
        return this.cheatsAllowed;
    }
}

