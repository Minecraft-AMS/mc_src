/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.Unpooled
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.featuretoggle.FeatureFlags;
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
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.Whitelist;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.PathUtil;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class PlayerManager {
    public static final File BANNED_PLAYERS_FILE = new File("banned-players.json");
    public static final File BANNED_IPS_FILE = new File("banned-ips.json");
    public static final File OPERATORS_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    public static final Text FILTERED_FULL_TEXT = Text.translatable("chat.filtered_full");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LATENCY_UPDATE_INTERVAL = 600;
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
    private final WorldSaveHandler saveHandler;
    private boolean whitelistEnabled;
    private final CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager;
    private final DynamicRegistryManager.Immutable syncedRegistryManager;
    protected final int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean cheatsAllowed;
    private static final boolean field_29791 = false;
    private int latencyUpdateTimer;

    public PlayerManager(MinecraftServer server, CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager, WorldSaveHandler saveHandler, int maxPlayers) {
        this.server = server;
        this.registryManager = registryManager;
        this.syncedRegistryManager = new DynamicRegistryManager.ImmutableImpl(SerializableRegistries.streamDynamicEntries(registryManager)).toImmutable();
        this.maxPlayers = maxPlayers;
        this.saveHandler = saveHandler;
    }

    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
        NbtCompound nbtCompound2;
        Entity entity;
        ServerWorld serverWorld2;
        GameProfile gameProfile = player.getGameProfile();
        UserCache userCache = this.server.getUserCache();
        Optional<GameProfile> optional = userCache.getByUuid(gameProfile.getId());
        String string = optional.map(GameProfile::getName).orElse(gameProfile.getName());
        userCache.add(gameProfile);
        NbtCompound nbtCompound = this.loadPlayerData(player);
        RegistryKey<World> registryKey = nbtCompound != null ? DimensionType.worldFromDimensionNbt(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbtCompound.get("Dimension"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElse(World.OVERWORLD) : World.OVERWORLD;
        ServerWorld serverWorld = this.server.getWorld(registryKey);
        if (serverWorld == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", registryKey);
            serverWorld2 = this.server.getOverworld();
        } else {
            serverWorld2 = serverWorld;
        }
        player.setServerWorld(serverWorld2);
        String string2 = "local";
        if (connection.getAddress() != null) {
            string2 = connection.getAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{player.getName().getString(), string2, player.getId(), player.getX(), player.getY(), player.getZ()});
        WorldProperties worldProperties = serverWorld2.getLevelProperties();
        player.setGameMode(nbtCompound);
        ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player);
        GameRules gameRules = serverWorld2.getGameRules();
        boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        serverPlayNetworkHandler.sendPacket(new GameJoinS2CPacket(player.getId(), worldProperties.isHardcore(), player.interactionManager.getGameMode(), player.interactionManager.getPreviousGameMode(), this.server.getWorldRegistryKeys(), this.syncedRegistryManager, serverWorld2.getDimensionKey(), serverWorld2.getRegistryKey(), BiomeAccess.hashSeed(serverWorld2.getSeed()), this.getMaxPlayerCount(), this.viewDistance, this.simulationDistance, bl2, !bl, serverWorld2.isDebugWorld(), serverWorld2.isFlat(), player.getLastDeathPos()));
        serverPlayNetworkHandler.sendPacket(new FeaturesS2CPacket(FeatureFlags.FEATURE_MANAGER.toId(serverWorld2.getEnabledFeatures())));
        serverPlayNetworkHandler.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
        serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
        serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));
        serverPlayNetworkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
        serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
        serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(this.registryManager)));
        this.sendCommandTree(player);
        player.getStatHandler().updateStatSet();
        player.getRecipeBook().sendInitRecipesPacket(player);
        this.sendScoreboard(serverWorld2.getScoreboard(), player);
        this.server.forcePlayerSampleUpdate();
        MutableText mutableText = player.getGameProfile().getName().equalsIgnoreCase(string) ? Text.translatable("multiplayer.player.joined", player.getDisplayName()) : Text.translatable("multiplayer.player.joined.renamed", player.getDisplayName(), string);
        this.broadcast(mutableText.formatted(Formatting.YELLOW), false);
        serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        ServerMetadata serverMetadata = this.server.getServerMetadata();
        if (serverMetadata != null) {
            player.sendServerMetadata(serverMetadata);
        }
        player.networkHandler.sendPacket(PlayerListS2CPacket.entryFromPlayer(this.players));
        this.players.add(player);
        this.playerMap.put(player.getUuid(), player);
        this.sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
        this.sendWorldInfo(player, serverWorld2);
        serverWorld2.onPlayerConnected(player);
        this.server.getBossBarManager().onPlayerConnect(player);
        this.server.getResourcePackProperties().ifPresent(properties -> player.sendResourcePackUrl(properties.url(), properties.hash(), properties.isRequired(), properties.prompt()));
        for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
            serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), statusEffectInstance));
        }
        if (nbtCompound != null && nbtCompound.contains("RootVehicle", 10) && (entity = EntityType.loadEntityWithPassengers((nbtCompound2 = nbtCompound.getCompound("RootVehicle")).getCompound("Entity"), serverWorld2, vehicle -> {
            if (!serverWorld2.tryLoadEntity((Entity)vehicle)) {
                return null;
            }
            return vehicle;
        })) != null) {
            UUID uUID = nbtCompound2.containsUuid("Attach") ? nbtCompound2.getUuid("Attach") : null;
            if (entity.getUuid().equals(uUID)) {
                player.startRiding(entity, true);
            } else {
                for (Entity entity2 : entity.getPassengersDeep()) {
                    if (!entity2.getUuid().equals(uUID)) continue;
                    player.startRiding(entity2, true);
                    break;
                }
            }
            if (!player.hasVehicle()) {
                LOGGER.warn("Couldn't reattach entity to player");
                entity.discard();
                for (Entity entity2 : entity.getPassengersDeep()) {
                    entity2.discard();
                }
            }
        }
        player.onSpawn();
    }

    protected void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
        HashSet set = Sets.newHashSet();
        for (Team team : scoreboard.getTeams()) {
            player.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
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
        world.getWorldBorder().addListener(new WorldBorderListener(){

            @Override
            public void onSizeChange(WorldBorder border, double size) {
                PlayerManager.this.sendToAll(new WorldBorderSizeChangedS2CPacket(border));
            }

            @Override
            public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
                PlayerManager.this.sendToAll(new WorldBorderInterpolateSizeS2CPacket(border));
            }

            @Override
            public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
                PlayerManager.this.sendToAll(new WorldBorderCenterChangedS2CPacket(border));
            }

            @Override
            public void onWarningTimeChanged(WorldBorder border, int warningTime) {
                PlayerManager.this.sendToAll(new WorldBorderWarningTimeChangedS2CPacket(border));
            }

            @Override
            public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
                PlayerManager.this.sendToAll(new WorldBorderWarningBlocksChangedS2CPacket(border));
            }

            @Override
            public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
            }

            @Override
            public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
            }
        });
    }

    @Nullable
    public NbtCompound loadPlayerData(ServerPlayerEntity player) {
        NbtCompound nbtCompound2;
        NbtCompound nbtCompound = this.server.getSaveProperties().getPlayerData();
        if (this.server.isHost(player.getGameProfile()) && nbtCompound != null) {
            nbtCompound2 = nbtCompound;
            player.readNbt(nbtCompound2);
            LOGGER.debug("loading single player");
        } else {
            nbtCompound2 = this.saveHandler.loadPlayerData(player);
        }
        return nbtCompound2;
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
        Entity entity2;
        ServerWorld serverWorld = player.getServerWorld();
        player.incrementStat(Stats.LEAVE_GAME);
        this.savePlayerData(player);
        if (player.hasVehicle() && (entity2 = player.getRootVehicle()).hasPlayerRider()) {
            LOGGER.debug("Removing player mount");
            player.stopRiding();
            entity2.streamPassengersAndSelf().forEach(entity -> entity.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
        player.detach();
        serverWorld.removePlayer(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        player.getAdvancementTracker().clearCriteria();
        this.players.remove(player);
        this.server.getBossBarManager().onPlayerDisconnect(player);
        UUID uUID = player.getUuid();
        ServerPlayerEntity serverPlayerEntity = this.playerMap.get(uUID);
        if (serverPlayerEntity == player) {
            this.playerMap.remove(uUID);
            this.statisticsMap.remove(uUID);
            this.advancementTrackers.remove(uUID);
        }
        this.sendToAll(new PlayerRemoveS2CPacket(List.of(player.getUuid())));
    }

    @Nullable
    public Text checkCanJoin(SocketAddress address, GameProfile profile) {
        if (this.bannedProfiles.contains(profile)) {
            BannedPlayerEntry bannedPlayerEntry = (BannedPlayerEntry)this.bannedProfiles.get(profile);
            MutableText mutableText = Text.translatable("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
            if (bannedPlayerEntry.getExpiryDate() != null) {
                mutableText.append(Text.translatable("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
            }
            return mutableText;
        }
        if (!this.isWhitelisted(profile)) {
            return Text.translatable("multiplayer.disconnect.not_whitelisted");
        }
        if (this.bannedIps.isBanned(address)) {
            BannedIpEntry bannedIpEntry = this.bannedIps.get(address);
            MutableText mutableText = Text.translatable("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
            if (bannedIpEntry.getExpiryDate() != null) {
                mutableText.append(Text.translatable("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
            }
            return mutableText;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(profile)) {
            return Text.translatable("multiplayer.disconnect.server_full");
        }
        return null;
    }

    public ServerPlayerEntity createPlayer(GameProfile profile) {
        UUID uUID = Uuids.getUuidFromProfile(profile);
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
            serverPlayerEntity3.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.duplicate_login"));
        }
        return new ServerPlayerEntity(this.server, this.server.getOverworld(), profile);
    }

    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean alive) {
        this.players.remove(player);
        player.getServerWorld().removePlayer(player, Entity.RemovalReason.DISCARDED);
        BlockPos blockPos = player.getSpawnPointPosition();
        float f = player.getSpawnAngle();
        boolean bl = player.isSpawnForced();
        ServerWorld serverWorld = this.server.getWorld(player.getSpawnPointDimension());
        Optional<Object> optional = serverWorld != null && blockPos != null ? PlayerEntity.findRespawnPosition(serverWorld, blockPos, f, bl, alive) : Optional.empty();
        ServerWorld serverWorld2 = serverWorld != null && optional.isPresent() ? serverWorld : this.server.getOverworld();
        ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(this.server, serverWorld2, player.getGameProfile());
        serverPlayerEntity.networkHandler = player.networkHandler;
        serverPlayerEntity.copyFrom(player, alive);
        serverPlayerEntity.setId(player.getId());
        serverPlayerEntity.setMainArm(player.getMainArm());
        for (String string : player.getCommandTags()) {
            serverPlayerEntity.addCommandTag(string);
        }
        boolean bl2 = false;
        if (optional.isPresent()) {
            float g;
            BlockState blockState = serverWorld2.getBlockState(blockPos);
            boolean bl3 = blockState.isOf(Blocks.RESPAWN_ANCHOR);
            Vec3d vec3d = (Vec3d)optional.get();
            if (blockState.isIn(BlockTags.BEDS) || bl3) {
                Vec3d vec3d2 = Vec3d.ofBottomCenter(blockPos).subtract(vec3d).normalize();
                g = (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d2.z, vec3d2.x) * 57.2957763671875 - 90.0);
            } else {
                g = f;
            }
            serverPlayerEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, g, 0.0f);
            serverPlayerEntity.setSpawnPoint(serverWorld2.getRegistryKey(), blockPos, f, bl, false);
            bl2 = !alive && bl3;
        } else if (blockPos != null) {
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0f));
        }
        while (!serverWorld2.isSpaceEmpty(serverPlayerEntity) && serverPlayerEntity.getY() < (double)serverWorld2.getTopY()) {
            serverPlayerEntity.setPosition(serverPlayerEntity.getX(), serverPlayerEntity.getY() + 1.0, serverPlayerEntity.getZ());
        }
        byte b = alive ? (byte)1 : 0;
        WorldProperties worldProperties = serverPlayerEntity.getWorld().getLevelProperties();
        serverPlayerEntity.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayerEntity.getWorld().getDimensionKey(), serverPlayerEntity.getWorld().getRegistryKey(), BiomeAccess.hashSeed(serverPlayerEntity.getServerWorld().getSeed()), serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.interactionManager.getPreviousGameMode(), serverPlayerEntity.getWorld().isDebugWorld(), serverPlayerEntity.getServerWorld().isFlat(), b, serverPlayerEntity.getLastDeathPos()));
        serverPlayerEntity.networkHandler.requestTeleport(serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
        serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(serverWorld2.getSpawnPos(), serverWorld2.getSpawnAngle()));
        serverPlayerEntity.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
        serverPlayerEntity.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity.experienceProgress, serverPlayerEntity.totalExperience, serverPlayerEntity.experienceLevel));
        this.sendWorldInfo(serverPlayerEntity, serverWorld2);
        this.sendCommandTree(serverPlayerEntity);
        serverWorld2.onPlayerRespawned(serverPlayerEntity);
        this.players.add(serverPlayerEntity);
        this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
        serverPlayerEntity.onSpawn();
        serverPlayerEntity.setHealth(serverPlayerEntity.getHealth());
        if (bl2) {
            serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, serverWorld2.getRandom().nextLong()));
        }
        return serverPlayerEntity;
    }

    public void sendCommandTree(ServerPlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        int i = this.server.getPermissionLevel(gameProfile);
        this.sendCommandTree(player, i);
    }

    public void updatePlayerLatency() {
        if (++this.latencyUpdateTimer > 600) {
            this.sendToAll(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LATENCY), this.players));
            this.latencyUpdateTimer = 0;
        }
    }

    public void sendToAll(Packet<?> packet) {
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }

    public void sendToDimension(Packet<?> packet, RegistryKey<World> dimension) {
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (serverPlayerEntity.getWorld().getRegistryKey() != dimension) continue;
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }

    public void sendToTeam(PlayerEntity source, Text message) {
        AbstractTeam abstractTeam = source.getScoreboardTeam();
        if (abstractTeam == null) {
            return;
        }
        Collection<String> collection = abstractTeam.getPlayerList();
        for (String string : collection) {
            ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
            if (serverPlayerEntity == null || serverPlayerEntity == source) continue;
            serverPlayerEntity.sendMessage(message);
        }
    }

    public void sendToOtherTeams(PlayerEntity source, Text message) {
        AbstractTeam abstractTeam = source.getScoreboardTeam();
        if (abstractTeam == null) {
            this.broadcast(message, false);
            return;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverPlayerEntity = this.players.get(i);
            if (serverPlayerEntity.getScoreboardTeam() == abstractTeam) continue;
            serverPlayerEntity.sendMessage(message);
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

    public void addToOperators(GameProfile profile) {
        this.ops.add(new OperatorEntry(profile, this.server.getOpPermissionLevel(), this.ops.canBypassPlayerLimit(profile)));
        ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
        if (serverPlayerEntity != null) {
            this.sendCommandTree(serverPlayerEntity);
        }
    }

    public void removeFromOperators(GameProfile profile) {
        this.ops.remove(profile);
        ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
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

    public boolean isWhitelisted(GameProfile profile) {
        return !this.whitelistEnabled || this.ops.contains(profile) || this.whitelist.contains(profile);
    }

    public boolean isOperator(GameProfile profile) {
        return this.ops.contains(profile) || this.server.isHost(profile) && this.server.getSaveProperties().areCommandsAllowed() || this.cheatsAllowed;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(String name) {
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (!serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(name)) continue;
            return serverPlayerEntity;
        }
        return null;
    }

    public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double distance, RegistryKey<World> worldKey, Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            double f;
            double e;
            double d;
            ServerPlayerEntity serverPlayerEntity = this.players.get(i);
            if (serverPlayerEntity == player || serverPlayerEntity.getWorld().getRegistryKey() != worldKey || !((d = x - serverPlayerEntity.getX()) * d + (e = y - serverPlayerEntity.getY()) * e + (f = z - serverPlayerEntity.getZ()) * f < distance * distance)) continue;
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
        WorldBorder worldBorder = this.server.getOverworld().getWorldBorder();
        player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(worldBorder));
        player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
        player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(world.getSpawnPos(), world.getSpawnAngle()));
        if (world.isRaining()) {
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0f));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, world.getRainGradient(1.0f)));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, world.getThunderGradient(1.0f)));
        }
    }

    public void sendPlayerStatus(ServerPlayerEntity player) {
        player.playerScreenHandler.syncState();
        player.markHealthDirty();
        player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
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

    public List<ServerPlayerEntity> getPlayersByIp(String ip) {
        ArrayList list = Lists.newArrayList();
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (!serverPlayerEntity.getIp().equals(ip)) continue;
            list.add(serverPlayerEntity);
        }
        return list;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getSimulationDistance() {
        return this.simulationDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Nullable
    public NbtCompound getUserData() {
        return null;
    }

    public void setCheatsAllowed(boolean cheatsAllowed) {
        this.cheatsAllowed = cheatsAllowed;
    }

    public void disconnectAllPlayers() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void broadcast(Text message, boolean overlay) {
        this.broadcast(message, (ServerPlayerEntity player) -> message, overlay);
    }

    public void broadcast(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, boolean overlay) {
        this.server.sendMessage(message);
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            Text text = playerMessageFactory.apply(serverPlayerEntity);
            if (text == null) continue;
            serverPlayerEntity.sendMessageToClient(text, overlay);
        }
    }

    public void broadcast(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {
        this.broadcast(message, source::shouldFilterText, source.getPlayer(), params);
    }

    public void broadcast(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        this.broadcast(message, sender::shouldFilterMessagesSentTo, sender, params);
    }

    private void broadcast(SignedMessage message, Predicate<ServerPlayerEntity> shouldSendFiltered, @Nullable ServerPlayerEntity sender, MessageType.Parameters params) {
        boolean bl = this.verify(message);
        this.server.logChatMessage(message.getContent(), params, bl ? null : "Not Secure");
        SentMessage sentMessage = SentMessage.of(message);
        boolean bl2 = false;
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            boolean bl3 = shouldSendFiltered.test(serverPlayerEntity);
            serverPlayerEntity.sendChatMessage(sentMessage, bl3, params);
            bl2 |= bl3 && message.isFullyFiltered();
        }
        if (bl2 && sender != null) {
            sender.sendMessage(FILTERED_FULL_TEXT);
        }
    }

    private boolean verify(SignedMessage message) {
        return message.hasSignature() && !message.isExpiredOnServer(Instant.now());
    }

    public ServerStatHandler createStatHandler(PlayerEntity player) {
        UUID uUID = player.getUuid();
        ServerStatHandler serverStatHandler = this.statisticsMap.get(uUID);
        if (serverStatHandler == null) {
            File file3;
            Path path;
            File file = this.server.getSavePath(WorldSavePath.STATS).toFile();
            File file2 = new File(file, uUID + ".json");
            if (!file2.exists() && PathUtil.isNormal(path = (file3 = new File(file, player.getName().getString() + ".json")).toPath()) && PathUtil.isAllowedName(path) && path.startsWith(file.getPath()) && file3.isFile()) {
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
            Path path = this.server.getSavePath(WorldSavePath.ADVANCEMENTS).resolve(uUID + ".json");
            playerAdvancementTracker = new PlayerAdvancementTracker(this.server.getDataFixer(), this, this.server.getAdvancementLoader(), path, player);
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

    public void setSimulationDistance(int simulationDistance) {
        this.simulationDistance = simulationDistance;
        this.sendToAll(new SimulationDistanceS2CPacket(simulationDistance));
        for (ServerWorld serverWorld : this.server.getWorlds()) {
            if (serverWorld == null) continue;
            serverWorld.getChunkManager().applySimulationDistance(simulationDistance);
        }
    }

    public List<ServerPlayerEntity> getPlayerList() {
        return this.players;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(UUID uuid) {
        return this.playerMap.get(uuid);
    }

    public boolean canBypassPlayerLimit(GameProfile profile) {
        return false;
    }

    public void onDataPacksReloaded() {
        for (PlayerAdvancementTracker playerAdvancementTracker : this.advancementTrackers.values()) {
            playerAdvancementTracker.reload(this.server.getAdvancementLoader());
        }
        this.sendToAll(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(this.registryManager)));
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

