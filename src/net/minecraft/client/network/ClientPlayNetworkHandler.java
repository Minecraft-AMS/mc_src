/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.RidingMinecartSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockActionS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TagQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientPlayNetworkHandler
implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ClientConnection connection;
    private final GameProfile profile;
    private final Screen loginScreen;
    private MinecraftClient client;
    private ClientWorld world;
    private boolean field_3698;
    private final Map<UUID, PlayerListEntry> playerListEntries = Maps.newHashMap();
    private final ClientAdvancementManager advancementHandler;
    private final ClientCommandSource commandSource;
    private RegistryTagManager tagManager = new RegistryTagManager();
    private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
    private int chunkLoadDistance = 3;
    private final Random random = new Random();
    private CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher();
    private final RecipeManager recipeManager = new RecipeManager();
    private final UUID sessionId = UUID.randomUUID();

    public ClientPlayNetworkHandler(MinecraftClient client, Screen screen, ClientConnection connection, GameProfile profile) {
        this.client = client;
        this.loginScreen = screen;
        this.connection = connection;
        this.profile = profile;
        this.advancementHandler = new ClientAdvancementManager(client);
        this.commandSource = new ClientCommandSource(this, client);
    }

    public ClientCommandSource getCommandSource() {
        return this.commandSource;
    }

    public void clearWorld() {
        this.world = null;
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    @Override
    public void onGameJoin(GameJoinS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
        this.chunkLoadDistance = packet.getChunkLoadDistance();
        this.world = new ClientWorld(this, new LevelInfo(0L, packet.getGameMode(), false, packet.isHardcore(), packet.getGeneratorType()), packet.getDimension(), this.chunkLoadDistance, this.client.getProfiler(), this.client.worldRenderer);
        this.client.joinWorld(this.world);
        if (this.client.player == null) {
            this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook(this.world.getRecipeManager()));
            this.client.player.yaw = -180.0f;
            if (this.client.getServer() != null) {
                this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
            }
        }
        this.client.debugRenderer.method_20413();
        this.client.player.afterSpawn();
        int i = packet.getEntityId();
        this.world.addPlayer(i, this.client.player);
        this.client.player.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities(this.client.player);
        this.client.cameraEntity = this.client.player;
        this.client.player.dimension = packet.getDimension();
        this.client.openScreen(new DownloadingTerrainScreen());
        this.client.player.setEntityId(i);
        this.client.player.setReducedDebugInfo(packet.hasReducedDebugInfo());
        this.client.interactionManager.setGameMode(packet.getGameMode());
        this.client.options.onPlayerModelPartChange();
        this.connection.send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
        this.client.getGame().onStartGameSession();
    }

    @Override
    public void onEntitySpawn(EntitySpawnS2CPacket packet) {
        Entity entity2;
        Entity entity;
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        EntityType<?> entityType = packet.getEntityTypeId();
        if (entityType == EntityType.CHEST_MINECART) {
            entity = new ChestMinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.FURNACE_MINECART) {
            entity = new FurnaceMinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.TNT_MINECART) {
            entity = new TntMinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.SPAWNER_MINECART) {
            entity = new SpawnerMinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.HOPPER_MINECART) {
            entity = new HopperMinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.COMMAND_BLOCK_MINECART) {
            entity = new CommandBlockMinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.MINECART) {
            entity = new MinecartEntity(this.world, d, e, f);
        } else if (entityType == EntityType.FISHING_BOBBER) {
            entity2 = this.world.getEntityById(packet.getEntityData());
            entity = entity2 instanceof PlayerEntity ? new FishingBobberEntity(this.world, (PlayerEntity)entity2, d, e, f) : null;
        } else if (entityType == EntityType.ARROW) {
            entity = new ArrowEntity(this.world, d, e, f);
            entity2 = this.world.getEntityById(packet.getEntityData());
            if (entity2 != null) {
                ((ProjectileEntity)entity).setOwner(entity2);
            }
        } else if (entityType == EntityType.SPECTRAL_ARROW) {
            entity = new SpectralArrowEntity(this.world, d, e, f);
            entity2 = this.world.getEntityById(packet.getEntityData());
            if (entity2 != null) {
                ((ProjectileEntity)entity).setOwner(entity2);
            }
        } else if (entityType == EntityType.TRIDENT) {
            entity = new TridentEntity(this.world, d, e, f);
            entity2 = this.world.getEntityById(packet.getEntityData());
            if (entity2 != null) {
                ((ProjectileEntity)entity).setOwner(entity2);
            }
        } else {
            entity = entityType == EntityType.SNOWBALL ? new SnowballEntity(this.world, d, e, f) : (entityType == EntityType.LLAMA_SPIT ? new LlamaSpitEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz()) : (entityType == EntityType.ITEM_FRAME ? new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.byId(packet.getEntityData())) : (entityType == EntityType.LEASH_KNOT ? new LeadKnotEntity(this.world, new BlockPos(d, e, f)) : (entityType == EntityType.ENDER_PEARL ? new ThrownEnderpearlEntity(this.world, d, e, f) : (entityType == EntityType.EYE_OF_ENDER ? new EnderEyeEntity(this.world, d, e, f) : (entityType == EntityType.FIREWORK_ROCKET ? new FireworkEntity(this.world, d, e, f, ItemStack.EMPTY) : (entityType == EntityType.FIREBALL ? new FireballEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz()) : (entityType == EntityType.DRAGON_FIREBALL ? new DragonFireballEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz()) : (entityType == EntityType.SMALL_FIREBALL ? new SmallFireballEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz()) : (entityType == EntityType.WITHER_SKULL ? new WitherSkullEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz()) : (entityType == EntityType.SHULKER_BULLET ? new ShulkerBulletEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz()) : (entityType == EntityType.EGG ? new ThrownEggEntity(this.world, d, e, f) : (entityType == EntityType.EVOKER_FANGS ? new EvokerFangsEntity(this.world, d, e, f, 0.0f, 0, null) : (entityType == EntityType.POTION ? new ThrownPotionEntity(this.world, d, e, f) : (entityType == EntityType.EXPERIENCE_BOTTLE ? new ThrownExperienceBottleEntity(this.world, d, e, f) : (entityType == EntityType.BOAT ? new BoatEntity(this.world, d, e, f) : (entityType == EntityType.TNT ? new TntEntity(this.world, d, e, f, null) : (entityType == EntityType.ARMOR_STAND ? new ArmorStandEntity(this.world, d, e, f) : (entityType == EntityType.END_CRYSTAL ? new EnderCrystalEntity(this.world, d, e, f) : (entityType == EntityType.ITEM ? new ItemEntity(this.world, d, e, f) : (entityType == EntityType.FALLING_BLOCK ? new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(packet.getEntityData())) : (entityType == EntityType.AREA_EFFECT_CLOUD ? new AreaEffectCloudEntity(this.world, d, e, f) : null))))))))))))))))))))));
        }
        if (entity != null) {
            int i = packet.getId();
            entity.updateTrackedPosition(d, e, f);
            entity.pitch = (float)(packet.getPitch() * 360) / 256.0f;
            entity.yaw = (float)(packet.getYaw() * 360) / 256.0f;
            entity.setEntityId(i);
            entity.setUuid(packet.getUuid());
            this.world.addEntity(i, entity);
            if (entity instanceof AbstractMinecartEntity) {
                this.client.getSoundManager().play(new RidingMinecartSoundInstance((AbstractMinecartEntity)entity));
            }
        }
    }

    @Override
    public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        ExperienceOrbEntity entity = new ExperienceOrbEntity(this.world, d, e, f, packet.getExperience());
        entity.updateTrackedPosition(d, e, f);
        entity.yaw = 0.0f;
        entity.pitch = 0.0f;
        entity.setEntityId(packet.getId());
        this.world.addEntity(packet.getId(), entity);
    }

    @Override
    public void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        if (packet.getEntityTypeId() == 1) {
            LightningEntity lightningEntity = new LightningEntity(this.world, d, e, f, false);
            lightningEntity.updateTrackedPosition(d, e, f);
            lightningEntity.yaw = 0.0f;
            lightningEntity.pitch = 0.0f;
            lightningEntity.setEntityId(packet.getId());
            this.world.addLightning(lightningEntity);
        }
    }

    @Override
    public void onPaintingSpawn(PaintingSpawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        PaintingEntity paintingEntity = new PaintingEntity(this.world, packet.getPos(), packet.getFacing(), packet.getMotive());
        paintingEntity.setEntityId(packet.getId());
        paintingEntity.setUuid(packet.getPaintingUuid());
        this.world.addEntity(packet.getId(), paintingEntity);
    }

    @Override
    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity == null) {
            return;
        }
        entity.setVelocityClient((double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0);
    }

    @Override
    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.id());
        if (entity != null && packet.getTrackedValues() != null) {
            entity.getDataTracker().writeUpdatedEntries(packet.getTrackedValues());
        }
    }

    @Override
    public void onPlayerSpawn(PlayerSpawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        float g = (float)(packet.getYaw() * 360) / 256.0f;
        float h = (float)(packet.getPitch() * 360) / 256.0f;
        int i = packet.getId();
        OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(this.client.world, this.getPlayerListEntry(packet.getPlayerUuid()).getProfile());
        otherClientPlayerEntity.setEntityId(i);
        otherClientPlayerEntity.prevX = d;
        otherClientPlayerEntity.lastRenderX = d;
        otherClientPlayerEntity.prevY = e;
        otherClientPlayerEntity.lastRenderY = e;
        otherClientPlayerEntity.prevZ = f;
        otherClientPlayerEntity.lastRenderZ = f;
        otherClientPlayerEntity.updateTrackedPosition(d, e, f);
        otherClientPlayerEntity.updatePositionAndAngles(d, e, f, g, h);
        this.world.addPlayer(i, otherClientPlayerEntity);
        List<DataTracker.Entry<?>> list = packet.getTrackedValues();
        if (list != null) {
            otherClientPlayerEntity.getDataTracker().writeUpdatedEntries(list);
        }
    }

    @Override
    public void onEntityPosition(EntityPositionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity == null) {
            return;
        }
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        entity.updateTrackedPosition(d, e, f);
        if (!entity.isLogicalSideForUpdatingMovement()) {
            float g = (float)(packet.getYaw() * 360) / 256.0f;
            float h = (float)(packet.getPitch() * 360) / 256.0f;
            if (Math.abs(entity.x - d) >= 0.03125 || Math.abs(entity.y - e) >= 0.015625 || Math.abs(entity.z - f) >= 0.03125) {
                entity.updateTrackedPositionAndAngles(d, e, f, g, h, 3, true);
            } else {
                entity.updateTrackedPositionAndAngles(entity.x, entity.y, entity.z, g, h, 0, true);
            }
            entity.onGround = packet.isOnGround();
        }
    }

    @Override
    public void onHeldItemChange(HeldItemChangeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (PlayerInventory.isValidHotbarIndex(packet.getSlot())) {
            this.client.player.inventory.selectedSlot = packet.getSlot();
        }
    }

    @Override
    public void onEntityUpdate(EntityS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = packet.getEntity(this.world);
        if (entity == null) {
            return;
        }
        entity.trackedX += (long)packet.getDeltaXShort();
        entity.trackedY += (long)packet.getDeltaYShort();
        entity.trackedZ += (long)packet.getDeltaZShort();
        Vec3d vec3d = EntityS2CPacket.decodePacketCoordinates(entity.trackedX, entity.trackedY, entity.trackedZ);
        if (!entity.isLogicalSideForUpdatingMovement()) {
            float f = packet.hasRotation() ? (float)(packet.getYaw() * 360) / 256.0f : entity.yaw;
            float g = packet.hasRotation() ? (float)(packet.getPitch() * 360) / 256.0f : entity.pitch;
            entity.updateTrackedPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f, g, 3, false);
            entity.onGround = packet.isOnGround();
        }
    }

    @Override
    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = packet.getEntity(this.world);
        if (entity == null) {
            return;
        }
        float f = (float)(packet.getHeadYaw() * 360) / 256.0f;
        entity.updateTrackedHeadRotation(f, 3);
    }

    @Override
    public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        for (int i = 0; i < packet.getEntityIds().length; ++i) {
            int j = packet.getEntityIds()[i];
            this.world.removeEntity(j);
        }
    }

    @Override
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientPlayerEntity playerEntity = this.client.player;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        float g = packet.getYaw();
        float h = packet.getPitch();
        Vec3d vec3d = playerEntity.getVelocity();
        double i = vec3d.x;
        double j = vec3d.y;
        double k = vec3d.z;
        if (packet.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.X)) {
            playerEntity.lastRenderX += d;
            d += playerEntity.x;
        } else {
            playerEntity.lastRenderX = d;
            i = 0.0;
        }
        if (packet.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.Y)) {
            playerEntity.lastRenderY += e;
            e += playerEntity.y;
        } else {
            playerEntity.lastRenderY = e;
            j = 0.0;
        }
        if (packet.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.Z)) {
            playerEntity.lastRenderZ += f;
            f += playerEntity.z;
        } else {
            playerEntity.lastRenderZ = f;
            k = 0.0;
        }
        playerEntity.setVelocity(i, j, k);
        if (packet.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.X_ROT)) {
            h += playerEntity.pitch;
        }
        if (packet.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            g += playerEntity.yaw;
        }
        playerEntity.updatePositionAndAngles(d, e, f, g, h);
        this.connection.send(new TeleportConfirmC2SPacket(packet.getTeleportId()));
        this.connection.send(new PlayerMoveC2SPacket.Both(playerEntity.x, playerEntity.getBoundingBox().y1, playerEntity.z, playerEntity.yaw, playerEntity.pitch, false));
        if (!this.field_3698) {
            this.client.player.prevX = this.client.player.x;
            this.client.player.prevY = this.client.player.y;
            this.client.player.prevZ = this.client.player.z;
            this.field_3698 = true;
            this.client.openScreen(null);
        }
    }

    @Override
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : packet.getRecords()) {
            this.world.setBlockStateWithoutNeighborUpdates(chunkDeltaRecord.getBlockPos(), chunkDeltaRecord.getState());
        }
    }

    @Override
    public void onChunkData(ChunkDataS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        int i = packet.getX();
        int j = packet.getZ();
        WorldChunk worldChunk = this.world.getChunkManager().loadChunkFromPacket(this.world, i, j, packet.getReadBuffer(), packet.getHeightmaps(), packet.getVerticalStripBitmask(), packet.isFullChunk());
        if (worldChunk != null && packet.isFullChunk()) {
            this.world.addEntitiesToChunk(worldChunk);
        }
        for (int k = 0; k < 16; ++k) {
            this.world.scheduleBlockRenders(i, k, j);
        }
        for (CompoundTag compoundTag : packet.getBlockEntityTagList()) {
            BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
            BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
            if (blockEntity == null) continue;
            blockEntity.fromTag(compoundTag);
        }
    }

    @Override
    public void onUnloadChunk(UnloadChunkS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        int i = packet.getX();
        int j = packet.getZ();
        ClientChunkManager clientChunkManager = this.world.getChunkManager();
        clientChunkManager.unload(i, j);
        LightingProvider lightingProvider = clientChunkManager.getLightingProvider();
        for (int k = 0; k < 16; ++k) {
            this.world.scheduleBlockRenders(i, k, j);
            lightingProvider.updateSectionStatus(ChunkSectionPos.from(i, k, j), true);
        }
        lightingProvider.setLightEnabled(new ChunkPos(i, j), false);
    }

    @Override
    public void onBlockUpdate(BlockUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.world.setBlockStateWithoutNeighborUpdates(packet.getPos(), packet.getState());
    }

    @Override
    public void onDisconnect(DisconnectS2CPacket packet) {
        this.connection.disconnect(packet.getReason());
    }

    @Override
    public void onDisconnected(Text reason) {
        this.client.disconnect();
        if (this.loginScreen != null) {
            if (this.loginScreen instanceof RealmsScreenProxy) {
                this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.loginScreen).getScreen(), "disconnect.lost", reason).getProxy());
            } else {
                this.client.openScreen(new DisconnectedScreen(this.loginScreen, "disconnect.lost", reason));
            }
        } else {
            this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", reason));
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.connection.send(packet);
    }

    @Override
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getEntityId());
        LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(packet.getCollectorEntityId());
        if (livingEntity == null) {
            livingEntity = this.client.player;
        }
        if (entity != null) {
            if (entity instanceof ExperienceOrbEntity) {
                this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            if (entity instanceof ItemEntity) {
                ((ItemEntity)entity).getStack().setCount(packet.getStackAmount());
            }
            this.client.particleManager.addParticle(new ItemPickupParticle((World)this.world, entity, livingEntity, 0.5f));
            this.world.removeEntity(packet.getEntityId());
        }
    }

    @Override
    public void onChatMessage(ChatMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.inGameHud.addChatMessage(packet.getLocation(), packet.getMessage());
    }

    @Override
    public void onEntityAnimation(EntityAnimationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity == null) {
            return;
        }
        if (packet.getAnimationId() == 0) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.swingHand(Hand.MAIN_HAND);
        } else if (packet.getAnimationId() == 3) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.swingHand(Hand.OFF_HAND);
        } else if (packet.getAnimationId() == 1) {
            entity.animateDamage();
        } else if (packet.getAnimationId() == 2) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            playerEntity.wakeUp(false, false, false);
        } else if (packet.getAnimationId() == 4) {
            this.client.particleManager.addEmitter(entity, ParticleTypes.CRIT);
        } else if (packet.getAnimationId() == 5) {
            this.client.particleManager.addEmitter(entity, ParticleTypes.ENCHANTED_HIT);
        }
    }

    @Override
    public void onMobSpawn(MobSpawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        float g = (float)(packet.getYaw() * 360) / 256.0f;
        float h = (float)(packet.getPitch() * 360) / 256.0f;
        LivingEntity livingEntity = (LivingEntity)EntityType.createInstanceFromId(packet.getEntityTypeId(), this.client.world);
        if (livingEntity != null) {
            livingEntity.updateTrackedPosition(d, e, f);
            livingEntity.field_6283 = (float)(packet.getHeadYaw() * 360) / 256.0f;
            livingEntity.headYaw = (float)(packet.getHeadYaw() * 360) / 256.0f;
            if (livingEntity instanceof EnderDragonEntity) {
                EnderDragonPart[] enderDragonParts = ((EnderDragonEntity)livingEntity).method_5690();
                for (int i = 0; i < enderDragonParts.length; ++i) {
                    enderDragonParts[i].setEntityId(i + packet.getId());
                }
            }
            livingEntity.setEntityId(packet.getId());
            livingEntity.setUuid(packet.getUuid());
            livingEntity.updatePositionAndAngles(d, e, f, g, h);
            livingEntity.setVelocity((float)packet.getVelocityX() / 8000.0f, (float)packet.getVelocityY() / 8000.0f, (float)packet.getVelocityZ() / 8000.0f);
            this.world.addEntity(packet.getId(), livingEntity);
            List<DataTracker.Entry<?>> list = packet.getTrackedValues();
            if (list != null) {
                livingEntity.getDataTracker().writeUpdatedEntries(list);
            }
        } else {
            LOGGER.warn("Skipping Entity with id {}", (Object)packet.getEntityTypeId());
        }
    }

    @Override
    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.world.setTime(packet.getTime());
        this.client.world.setTimeOfDay(packet.getTimeOfDay());
    }

    @Override
    public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.player.setPlayerSpawn(packet.getPos(), true);
        this.client.world.getLevelProperties().setSpawnPos(packet.getPos());
    }

    @Override
    public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity == null) {
            LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        boolean bl = entity.hasPassengerDeep(this.client.player);
        entity.removeAllPassengers();
        for (int i : packet.getPassengerIds()) {
            Entity entity2 = this.world.getEntityById(i);
            if (entity2 == null) continue;
            entity2.startRiding(entity, true);
            if (entity2 != this.client.player || bl) continue;
            this.client.inGameHud.setOverlayMessage(I18n.translate("mount.onboard", this.client.options.keySneak.getLocalizedName()), false);
        }
    }

    @Override
    public void onEntityAttach(EntityAttachS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getAttachedEntityId());
        if (entity instanceof MobEntity) {
            ((MobEntity)entity).setHoldingEntityId(packet.getHoldingEntityId());
        }
    }

    private static ItemStack method_19691(PlayerEntity playerEntity) {
        for (Hand hand : Hand.values()) {
            ItemStack itemStack = playerEntity.getStackInHand(hand);
            if (itemStack.getItem() != Items.TOTEM_OF_UNDYING) continue;
            return itemStack;
        }
        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void onEntityStatus(EntityStatusS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = packet.getEntity(this.world);
        if (entity != null) {
            if (packet.getStatus() == 21) {
                this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
            } else if (packet.getStatus() == 35) {
                int i = 40;
                this.client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
                if (entity == this.client.player) {
                    this.client.gameRenderer.showFloatingItem(ClientPlayNetworkHandler.method_19691(this.client.player));
                }
            } else {
                entity.handleStatus(packet.getStatus());
            }
        }
    }

    @Override
    public void onHealthUpdate(HealthUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.player.updateHealth(packet.getHealth());
        this.client.player.getHungerManager().setFoodLevel(packet.getFood());
        this.client.player.getHungerManager().setSaturationLevelClient(packet.getSaturation());
    }

    @Override
    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.player.method_3145(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        DimensionType dimensionType = packet.getDimension();
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        int i = clientPlayerEntity.getEntityId();
        if (dimensionType != clientPlayerEntity.dimension) {
            this.field_3698 = false;
            Scoreboard scoreboard = this.world.getScoreboard();
            this.world = new ClientWorld(this, new LevelInfo(0L, packet.getGameMode(), false, this.client.world.getLevelProperties().isHardcore(), packet.getGeneratorType()), packet.getDimension(), this.chunkLoadDistance, this.client.getProfiler(), this.client.worldRenderer);
            this.world.setScoreboard(scoreboard);
            this.client.joinWorld(this.world);
            this.client.openScreen(new DownloadingTerrainScreen());
        }
        this.world.setDefaultSpawnClient();
        this.world.finishRemovingEntities();
        String string = clientPlayerEntity.getServerBrand();
        this.client.cameraEntity = null;
        ClientPlayerEntity clientPlayerEntity2 = this.client.interactionManager.createPlayer(this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook());
        clientPlayerEntity2.setEntityId(i);
        clientPlayerEntity2.dimension = dimensionType;
        this.client.player = clientPlayerEntity2;
        this.client.cameraEntity = clientPlayerEntity2;
        clientPlayerEntity2.getDataTracker().writeUpdatedEntries(clientPlayerEntity.getDataTracker().getAllEntries());
        clientPlayerEntity2.afterSpawn();
        clientPlayerEntity2.setServerBrand(string);
        this.world.addPlayer(i, clientPlayerEntity2);
        clientPlayerEntity2.yaw = -180.0f;
        clientPlayerEntity2.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities(clientPlayerEntity2);
        clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.getReducedDebugInfo());
        if (this.client.currentScreen instanceof DeathScreen) {
            this.client.openScreen(null);
        }
        this.client.interactionManager.setGameMode(packet.getGameMode());
    }

    @Override
    public void onExplosion(ExplosionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Explosion explosion = new Explosion(this.client.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks());
        explosion.affectWorld(true);
        this.client.player.setVelocity(this.client.player.getVelocity().add(packet.getPlayerVelocityX(), packet.getPlayerVelocityY(), packet.getPlayerVelocityZ()));
    }

    @Override
    public void onOpenHorseContainer(OpenHorseContainerS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getHorseId());
        if (entity instanceof HorseBaseEntity) {
            ClientPlayerEntity clientPlayerEntity = this.client.player;
            HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
            BasicInventory basicInventory = new BasicInventory(packet.getSlotCount());
            HorseContainer horseContainer = new HorseContainer(packet.getSyncId(), clientPlayerEntity.inventory, basicInventory, horseBaseEntity);
            clientPlayerEntity.container = horseContainer;
            this.client.openScreen(new HorseScreen(horseContainer, clientPlayerEntity.inventory, horseBaseEntity));
        }
    }

    @Override
    public void onOpenContainer(OpenContainerS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Screens.open(packet.getContainerType(), this.client, packet.getSyncId(), packet.getName());
    }

    @Override
    public void onContainerSlotUpdate(ContainerSlotUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientPlayerEntity playerEntity = this.client.player;
        ItemStack itemStack = packet.getItemStack();
        int i = packet.getSlot();
        this.client.getTutorialManager().onSlotUpdate(itemStack);
        if (packet.getSyncId() == -1) {
            if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
                playerEntity.inventory.setCursorStack(itemStack);
            }
        } else if (packet.getSyncId() == -2) {
            playerEntity.inventory.setInvStack(i, itemStack);
        } else {
            boolean bl = false;
            if (this.client.currentScreen instanceof CreativeInventoryScreen) {
                CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen)this.client.currentScreen;
                boolean bl2 = bl = creativeInventoryScreen.method_2469() != ItemGroup.INVENTORY.getIndex();
            }
            if (packet.getSyncId() == 0 && packet.getSlot() >= 36 && i < 45) {
                ItemStack itemStack2;
                if (!itemStack.isEmpty() && ((itemStack2 = playerEntity.playerContainer.getSlot(i).getStack()).isEmpty() || itemStack2.getCount() < itemStack.getCount())) {
                    itemStack.setCooldown(5);
                }
                playerEntity.playerContainer.setStackInSlot(i, itemStack);
            } else if (!(packet.getSyncId() != playerEntity.container.syncId || packet.getSyncId() == 0 && bl)) {
                playerEntity.container.setStackInSlot(i, itemStack);
            }
        }
    }

    @Override
    public void onGuiActionConfirm(ConfirmGuiActionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Container container = null;
        ClientPlayerEntity playerEntity = this.client.player;
        if (packet.getId() == 0) {
            container = playerEntity.playerContainer;
        } else if (packet.getId() == playerEntity.container.syncId) {
            container = playerEntity.container;
        }
        if (container != null && !packet.wasAccepted()) {
            this.sendPacket(new ConfirmGuiActionC2SPacket(packet.getId(), packet.getActionId(), true));
        }
    }

    @Override
    public void onInventory(InventoryS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientPlayerEntity playerEntity = this.client.player;
        if (packet.getGuiId() == 0) {
            playerEntity.playerContainer.updateSlotStacks(packet.getSlotStacks());
        } else if (packet.getGuiId() == playerEntity.container.syncId) {
            playerEntity.container.updateSlotStacks(packet.getSlotStacks());
        }
    }

    @Override
    public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        BlockEntity blockEntity = this.world.getBlockEntity(packet.getPos());
        if (!(blockEntity instanceof SignBlockEntity)) {
            blockEntity = new SignBlockEntity();
            blockEntity.setWorld(this.world);
            blockEntity.setPos(packet.getPos());
        }
        this.client.player.openEditSignScreen((SignBlockEntity)blockEntity);
    }

    @Override
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (this.client.world.isBlockLoaded(packet.getPos())) {
            boolean bl;
            BlockEntity blockEntity = this.client.world.getBlockEntity(packet.getPos());
            int i = packet.getBlockEntityType();
            boolean bl2 = bl = i == 2 && blockEntity instanceof CommandBlockBlockEntity;
            if (i == 1 && blockEntity instanceof MobSpawnerBlockEntity || bl || i == 3 && blockEntity instanceof BeaconBlockEntity || i == 4 && blockEntity instanceof SkullBlockEntity || i == 6 && blockEntity instanceof BannerBlockEntity || i == 7 && blockEntity instanceof StructureBlockBlockEntity || i == 8 && blockEntity instanceof EndGatewayBlockEntity || i == 9 && blockEntity instanceof SignBlockEntity || i == 11 && blockEntity instanceof BedBlockEntity || i == 5 && blockEntity instanceof ConduitBlockEntity || i == 12 && blockEntity instanceof JigsawBlockEntity || i == 13 && blockEntity instanceof CampfireBlockEntity) {
                blockEntity.fromTag(packet.getCompoundTag());
            }
            if (bl && this.client.currentScreen instanceof CommandBlockScreen) {
                ((CommandBlockScreen)this.client.currentScreen).method_2457();
            }
        }
    }

    @Override
    public void onContainerPropertyUpdate(ContainerPropertyUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientPlayerEntity playerEntity = this.client.player;
        if (playerEntity.container != null && playerEntity.container.syncId == packet.getSyncId()) {
            playerEntity.container.setProperty(packet.getPropertyId(), packet.getValue());
        }
    }

    @Override
    public void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity != null) {
            entity.equipStack(packet.getSlot(), packet.getStack());
        }
    }

    @Override
    public void onCloseContainer(CloseContainerS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.player.closeScreen();
    }

    @Override
    public void onBlockAction(BlockActionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.world.addBlockAction(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
    }

    @Override
    public void onBlockDestroyProgress(BlockBreakingProgressS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.world.setBlockBreakingInfo(packet.getEntityId(), packet.getPos(), packet.getProgress());
    }

    @Override
    public void onGameStateChange(GameStateChangeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientPlayerEntity playerEntity = this.client.player;
        int i = packet.getReason();
        float f = packet.getValue();
        int j = MathHelper.floor(f + 0.5f);
        if (i >= 0 && i < GameStateChangeS2CPacket.REASON_MESSAGES.length && GameStateChangeS2CPacket.REASON_MESSAGES[i] != null) {
            ((PlayerEntity)playerEntity).addChatMessage(new TranslatableText(GameStateChangeS2CPacket.REASON_MESSAGES[i], new Object[0]), false);
        }
        if (i == 1) {
            this.world.getLevelProperties().setRaining(true);
            this.world.setRainGradient(0.0f);
        } else if (i == 2) {
            this.world.getLevelProperties().setRaining(false);
            this.world.setRainGradient(1.0f);
        } else if (i == 3) {
            this.client.interactionManager.setGameMode(GameMode.byId(j));
        } else if (i == 4) {
            if (j == 0) {
                this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
                this.client.openScreen(new DownloadingTerrainScreen());
            } else if (j == 1) {
                this.client.openScreen(new CreditsScreen(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN))));
            }
        } else if (i == 5) {
            GameOptions gameOptions = this.client.options;
            if (f == 0.0f) {
                this.client.openScreen(new DemoScreen());
            } else if (f == 101.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.movement", gameOptions.keyForward.getLocalizedName(), gameOptions.keyLeft.getLocalizedName(), gameOptions.keyBack.getLocalizedName(), gameOptions.keyRight.getLocalizedName()));
            } else if (f == 102.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.jump", gameOptions.keyJump.getLocalizedName()));
            } else if (f == 103.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()));
            } else if (f == 104.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.day.6", gameOptions.keyScreenshot.getLocalizedName()));
            }
        } else if (i == 6) {
            this.world.playSound(playerEntity, playerEntity.x, playerEntity.y + (double)playerEntity.getStandingEyeHeight(), playerEntity.z, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18f, 0.45f);
        } else if (i == 7) {
            this.world.setRainGradient(f);
        } else if (i == 8) {
            this.world.setThunderGradient(f);
        } else if (i == 9) {
            this.world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        } else if (i == 10) {
            this.world.addParticle(ParticleTypes.ELDER_GUARDIAN, playerEntity.x, playerEntity.y, playerEntity.z, 0.0, 0.0, 0.0);
            this.world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }
    }

    @Override
    public void onMapUpdate(MapUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
        String string = FilledMapItem.getMapName(packet.getId());
        MapState mapState = this.client.world.getMapState(string);
        if (mapState == null) {
            MapState mapState2;
            mapState = new MapState(string);
            if (mapRenderer.getTexture(string) != null && (mapState2 = mapRenderer.getState(mapRenderer.getTexture(string))) != null) {
                mapState = mapState2;
            }
            this.client.world.putMapState(mapState);
        }
        packet.apply(mapState);
        mapRenderer.updateTexture(mapState);
    }

    @Override
    public void onWorldEvent(WorldEventS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (packet.isGlobal()) {
            this.client.world.playGlobalEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
        } else {
            this.client.world.playLevelEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
        }
    }

    @Override
    public void onAdvancements(AdvancementUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.advancementHandler.onAdvancements(packet);
    }

    @Override
    public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Identifier identifier = packet.getTabId();
        if (identifier == null) {
            this.advancementHandler.selectTab(null, false);
        } else {
            Advancement advancement = this.advancementHandler.getManager().get(identifier);
            this.advancementHandler.selectTab(advancement, false);
        }
    }

    @Override
    public void onCommandTree(CommandTreeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.commandDispatcher = new CommandDispatcher(packet.getCommandTree());
    }

    @Override
    public void onStopSound(StopSoundS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.getSoundManager().stopSounds(packet.getSoundId(), packet.getCategory());
    }

    @Override
    public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.commandSource.onCommandSuggestions(packet.getCompletionId(), packet.getSuggestions());
    }

    @Override
    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.recipeManager.method_20702(packet.getRecipes());
        SearchableContainer<RecipeResultCollection> searchableContainer = this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT);
        searchableContainer.clear();
        ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
        clientRecipeBook.reload();
        clientRecipeBook.getOrderedResults().forEach(searchableContainer::add);
        searchableContainer.reload();
    }

    @Override
    public void onLookAt(LookAtS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Vec3d vec3d = packet.getTargetPosition(this.world);
        if (vec3d != null) {
            this.client.player.lookAt(packet.getSelfAnchor(), vec3d);
        }
    }

    @Override
    public void onTagQuery(TagQueryResponseS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (!this.dataQueryHandler.handleQueryResponse(packet.getTransactionId(), packet.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", (Object)packet.getTransactionId());
        }
    }

    @Override
    public void onStatistics(StatisticsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        for (Map.Entry<Stat<?>, Integer> entry : packet.getStatMap().entrySet()) {
            Stat<?> stat = entry.getKey();
            int i = entry.getValue();
            this.client.player.getStatHandler().setStat(this.client.player, stat, i);
        }
        if (this.client.currentScreen instanceof StatsListener) {
            ((StatsListener)((Object)this.client.currentScreen)).onStatsReady();
        }
    }

    @Override
    public void onUnlockRecipes(UnlockRecipesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
        clientRecipeBook.setGuiOpen(packet.isGuiOpen());
        clientRecipeBook.setFilteringCraftable(packet.isFilteringCraftable());
        clientRecipeBook.setFurnaceGuiOpen(packet.isFurnaceGuiOpen());
        clientRecipeBook.setFurnaceFilteringCraftable(packet.isFurnaceFilteringCraftable());
        UnlockRecipesS2CPacket.Action action = packet.getAction();
        switch (action) {
            case REMOVE: {
                for (Identifier identifier : packet.getRecipeIdsToChange()) {
                    this.recipeManager.get(identifier).ifPresent(clientRecipeBook::remove);
                }
                break;
            }
            case INIT: {
                for (Identifier identifier : packet.getRecipeIdsToChange()) {
                    this.recipeManager.get(identifier).ifPresent(clientRecipeBook::add);
                }
                for (Identifier identifier : packet.getRecipeIdsToInit()) {
                    this.recipeManager.get(identifier).ifPresent(clientRecipeBook::display);
                }
                break;
            }
            case ADD: {
                for (Identifier identifier : packet.getRecipeIdsToChange()) {
                    this.recipeManager.get(identifier).ifPresent(recipe -> {
                        clientRecipeBook.add((Recipe<?>)recipe);
                        clientRecipeBook.display((Recipe<?>)recipe);
                        RecipeToast.show(this.client.getToastManager(), recipe);
                    });
                }
                break;
            }
        }
        clientRecipeBook.getOrderedResults().forEach(recipeResultCollection -> recipeResultCollection.initialize(clientRecipeBook));
        if (this.client.currentScreen instanceof RecipeBookProvider) {
            ((RecipeBookProvider)((Object)this.client.currentScreen)).refreshRecipeBook();
        }
    }

    @Override
    public void onEntityPotionEffect(EntityStatusEffectS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        StatusEffect statusEffect = StatusEffect.byRawId(packet.getEffectId());
        if (statusEffect == null) {
            return;
        }
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon());
        statusEffectInstance.setPermanent(packet.isPermanent());
        ((LivingEntity)entity).addStatusEffect(statusEffectInstance);
    }

    @Override
    public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.tagManager = packet.getTagManager();
        if (!this.connection.isLocal()) {
            BlockTags.setContainer(this.tagManager.blocks());
            ItemTags.setContainer(this.tagManager.items());
            FluidTags.setContainer(this.tagManager.fluids());
            EntityTypeTags.setContainer(this.tagManager.entityTypes());
        }
        this.client.getSearchableContainer(SearchManager.ITEM_TAG).reload();
    }

    @Override
    public void onCombatEvent(CombatEventS2CPacket packet) {
        Entity entity;
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (packet.type == CombatEventS2CPacket.Type.ENTITY_DIED && (entity = this.world.getEntityById(packet.entityId)) == this.client.player) {
            this.client.openScreen(new DeathScreen(packet.deathMessage, this.world.getLevelProperties().isHardcore()));
        }
    }

    @Override
    public void onDifficulty(DifficultyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.world.getLevelProperties().setDifficulty(packet.getDifficulty());
        this.client.world.getLevelProperties().setDifficultyLocked(packet.isDifficultyLocked());
    }

    @Override
    public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = packet.getEntity(this.world);
        if (entity != null) {
            this.client.setCameraEntity(entity);
        }
    }

    @Override
    public void onWorldBorder(WorldBorderS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        packet.apply(this.world.getWorldBorder());
    }

    @Override
    public void onTitle(TitleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        TitleS2CPacket.Action action = packet.getAction();
        String string = null;
        String string2 = null;
        String string3 = packet.getText() != null ? packet.getText().asFormattedString() : "";
        switch (action) {
            case TITLE: {
                string = string3;
                break;
            }
            case SUBTITLE: {
                string2 = string3;
                break;
            }
            case ACTIONBAR: {
                this.client.inGameHud.setOverlayMessage(string3, false);
                return;
            }
            case RESET: {
                this.client.inGameHud.setTitles("", "", -1, -1, -1);
                this.client.inGameHud.setDefaultTitleFade();
                return;
            }
        }
        this.client.inGameHud.setTitles(string, string2, packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
    }

    @Override
    public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
        this.client.inGameHud.getPlayerListWidget().setHeader(packet.getHeader().asFormattedString().isEmpty() ? null : packet.getHeader());
        this.client.inGameHud.getPlayerListWidget().setFooter(packet.getFooter().asFormattedString().isEmpty() ? null : packet.getFooter());
    }

    @Override
    public void onRemoveEntityEffect(RemoveEntityStatusEffectS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = packet.getEntity(this.world);
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).removeStatusEffectInternal(packet.getEffectType());
        }
    }

    @Override
    public void onPlayerList(PlayerListS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
            if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
                this.playerListEntries.remove(entry.getProfile().getId());
                continue;
            }
            PlayerListEntry playerListEntry = this.playerListEntries.get(entry.getProfile().getId());
            if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
                playerListEntry = new PlayerListEntry(entry);
                this.playerListEntries.put(playerListEntry.getProfile().getId(), playerListEntry);
            }
            if (playerListEntry == null) continue;
            switch (packet.getAction()) {
                case ADD_PLAYER: {
                    playerListEntry.setGameMode(entry.getGameMode());
                    playerListEntry.setLatency(entry.getLatency());
                    playerListEntry.setDisplayName(entry.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    playerListEntry.setGameMode(entry.getGameMode());
                    break;
                }
                case UPDATE_LATENCY: {
                    playerListEntry.setLatency(entry.getLatency());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    playerListEntry.setDisplayName(entry.getDisplayName());
                }
            }
        }
    }

    @Override
    public void onKeepAlive(KeepAliveS2CPacket packet) {
        this.sendPacket(new KeepAliveC2SPacket(packet.getId()));
    }

    @Override
    public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ClientPlayerEntity playerEntity = this.client.player;
        playerEntity.abilities.flying = packet.isFlying();
        playerEntity.abilities.creativeMode = packet.isCreativeMode();
        playerEntity.abilities.invulnerable = packet.isInvulnerable();
        playerEntity.abilities.allowFlying = packet.allowFlying();
        playerEntity.abilities.setFlySpeed(packet.getFlySpeed());
        playerEntity.abilities.setWalkSpeed(packet.getFovModifier());
    }

    @Override
    public void onPlaySound(PlaySoundS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.world.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
    }

    @Override
    public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            return;
        }
        this.client.world.playSoundFromEntity(this.client.player, entity, packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
    }

    @Override
    public void onPlaySoundId(PlaySoundIdS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.getSoundManager().play(new PositionedSoundInstance(packet.getSoundId(), packet.getCategory(), packet.getVolume(), packet.getPitch(), false, 0, SoundInstance.AttenuationType.LINEAR, (float)packet.getX(), (float)packet.getY(), (float)packet.getZ(), false));
    }

    @Override
    public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
        String string = packet.getURL();
        String string2 = packet.getSHA1();
        if (!this.validateResourcePackUrl(string)) {
            return;
        }
        if (string.startsWith("level://")) {
            try {
                String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
                File file = new File(this.client.runDirectory, "saves");
                File file2 = new File(file, string3);
                if (file2.isFile()) {
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
                    CompletableFuture<Void> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2);
                    this.method_2885(completableFuture);
                    return;
                }
            }
            catch (UnsupportedEncodingException string3) {
                // empty catch block
            }
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
            return;
        }
        ServerInfo serverInfo = this.client.getCurrentServerEntry();
        if (serverInfo != null && serverInfo.getResourcePack() == ServerInfo.ResourcePackState.ENABLED) {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
            this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
        } else if (serverInfo == null || serverInfo.getResourcePack() == ServerInfo.ResourcePackState.PROMPT) {
            this.client.execute(() -> this.client.openScreen(new ConfirmScreen(bl -> {
                this.client = MinecraftClient.getInstance();
                ServerInfo serverInfo = this.client.getCurrentServerEntry();
                if (bl) {
                    if (serverInfo != null) {
                        serverInfo.setResourcePackState(ServerInfo.ResourcePackState.ENABLED);
                    }
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
                    this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
                } else {
                    if (serverInfo != null) {
                        serverInfo.setResourcePackState(ServerInfo.ResourcePackState.DISABLED);
                    }
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
                }
                ServerList.updateServerListEntry(serverInfo);
                this.client.openScreen(null);
            }, new TranslatableText("multiplayer.texturePrompt.line1", new Object[0]), new TranslatableText("multiplayer.texturePrompt.line2", new Object[0]))));
        } else {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
        }
    }

    private boolean validateResourcePackUrl(String url) {
        try {
            URI uRI = new URI(url);
            String string = uRI.getScheme();
            boolean bl = "level".equals(string);
            if (!("http".equals(string) || "https".equals(string) || bl)) {
                throw new URISyntaxException(url, "Wrong protocol");
            }
            if (bl && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
            return false;
        }
        return true;
    }

    private void method_2885(CompletableFuture<?> completableFuture) {
        ((CompletableFuture)completableFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED))).exceptionally(throwable -> {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
            return null;
        });
    }

    private void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status packStatus) {
        this.connection.send(new ResourcePackStatusC2SPacket(packStatus));
    }

    @Override
    public void onBossBar(BossBarS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.client.inGameHud.getBossBarHud().handlePacket(packet);
    }

    @Override
    public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (packet.getCooldown() == 0) {
            this.client.player.getItemCooldownManager().remove(packet.getItem());
        } else {
            this.client.player.getItemCooldownManager().set(packet.getItem(), packet.getCooldown());
        }
    }

    @Override
    public void onVehicleMove(VehicleMoveS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.client.player.getRootVehicle();
        if (entity != this.client.player && entity.isLogicalSideForUpdatingMovement()) {
            entity.updatePositionAndAngles(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
            this.connection.send(new VehicleMoveC2SPacket(entity));
        }
    }

    @Override
    public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
        if (itemStack.getItem() == Items.WRITTEN_BOOK) {
            this.client.openScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCustomPayload(CustomPayloadS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Identifier identifier = packet.getChannel();
        PacketByteBuf packetByteBuf = null;
        try {
            packetByteBuf = packet.getData();
            if (CustomPayloadS2CPacket.BRAND.equals(identifier)) {
                this.client.player.setServerBrand(packetByteBuf.readString(Short.MAX_VALUE));
            } else if (CustomPayloadS2CPacket.DEBUG_PATH.equals(identifier)) {
                int i = packetByteBuf.readInt();
                float f = packetByteBuf.readFloat();
                Path path = Path.fromBuffer(packetByteBuf);
                this.client.debugRenderer.pathfindingDebugRenderer.addPath(i, path, f);
            } else if (CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier)) {
                long l = packetByteBuf.readVarLong();
                BlockPos blockPos = packetByteBuf.readBlockPos();
                ((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).method_3870(l, blockPos);
            } else if (CustomPayloadS2CPacket.DEBUG_CAVES.equals(identifier)) {
                BlockPos blockPos2 = packetByteBuf.readBlockPos();
                int j = packetByteBuf.readInt();
                ArrayList list = Lists.newArrayList();
                ArrayList list2 = Lists.newArrayList();
                for (int k = 0; k < j; ++k) {
                    list.add(packetByteBuf.readBlockPos());
                    list2.add(Float.valueOf(packetByteBuf.readFloat()));
                }
                this.client.debugRenderer.caveDebugRenderer.method_3704(blockPos2, list, list2);
            } else if (CustomPayloadS2CPacket.DEBUG_STRUCTURES.equals(identifier)) {
                DimensionType dimensionType = DimensionType.byRawId(packetByteBuf.readInt());
                BlockBox blockBox = new BlockBox(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
                int m = packetByteBuf.readInt();
                ArrayList list2 = Lists.newArrayList();
                ArrayList list3 = Lists.newArrayList();
                for (int n = 0; n < m; ++n) {
                    list2.add(new BlockBox(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()));
                    list3.add(packetByteBuf.readBoolean());
                }
                this.client.debugRenderer.structureDebugRenderer.method_3871(blockBox, list2, list3, dimensionType);
            } else if (CustomPayloadS2CPacket.DEBUG_WORLDGEN_ATTEMPT.equals(identifier)) {
                ((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer).method_3872(packetByteBuf.readBlockPos(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
            } else if (CustomPayloadS2CPacket.DEBUG_VILLAGE_SECTIONS.equals(identifier)) {
                int j;
                int i = packetByteBuf.readInt();
                for (j = 0; j < i; ++j) {
                    this.client.debugRenderer.villageDebugRenderer.method_19433(packetByteBuf.readChunkSectionPos());
                }
                j = packetByteBuf.readInt();
                for (int m = 0; m < j; ++m) {
                    this.client.debugRenderer.villageDebugRenderer.method_19435(packetByteBuf.readChunkSectionPos());
                }
            } else if (CustomPayloadS2CPacket.DEBUG_POI_ADDED.equals(identifier)) {
                BlockPos blockPos2 = packetByteBuf.readBlockPos();
                String string = packetByteBuf.readString();
                int m = packetByteBuf.readInt();
                VillageDebugRenderer.class_4233 lv = new VillageDebugRenderer.class_4233(blockPos2, string, m);
                this.client.debugRenderer.villageDebugRenderer.method_19701(lv);
            } else if (CustomPayloadS2CPacket.DEBUG_POI_REMOVED.equals(identifier)) {
                BlockPos blockPos2 = packetByteBuf.readBlockPos();
                this.client.debugRenderer.villageDebugRenderer.removePointOfInterest(blockPos2);
            } else if (CustomPayloadS2CPacket.DEBUG_POI_TICKET_COUNT.equals(identifier)) {
                BlockPos blockPos2 = packetByteBuf.readBlockPos();
                int j = packetByteBuf.readInt();
                this.client.debugRenderer.villageDebugRenderer.method_19702(blockPos2, j);
            } else if (CustomPayloadS2CPacket.DEBUG_GOAL_SELECTOR.equals(identifier)) {
                BlockPos blockPos2 = packetByteBuf.readBlockPos();
                int j = packetByteBuf.readInt();
                int m = packetByteBuf.readInt();
                ArrayList list2 = Lists.newArrayList();
                for (int k = 0; k < m; ++k) {
                    int n = packetByteBuf.readInt();
                    boolean bl = packetByteBuf.readBoolean();
                    String string2 = packetByteBuf.readString(255);
                    list2.add(new GoalSelectorDebugRenderer.class_4206(blockPos2, n, string2, bl));
                }
                this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(j, list2);
            } else if (CustomPayloadS2CPacket.DEBUG_RAIDS.equals(identifier)) {
                int i = packetByteBuf.readInt();
                ArrayList collection = Lists.newArrayList();
                for (int m = 0; m < i; ++m) {
                    collection.add(packetByteBuf.readBlockPos());
                }
                this.client.debugRenderer.raidCenterDebugRenderer.setRaidCenters(collection);
            } else if (CustomPayloadS2CPacket.DEBUG_BRAIN.equals(identifier)) {
                int u;
                int t;
                int s;
                int r;
                double d = packetByteBuf.readDouble();
                double e = packetByteBuf.readDouble();
                double g = packetByteBuf.readDouble();
                PositionImpl position = new PositionImpl(d, e, g);
                UUID uUID = packetByteBuf.readUuid();
                int o = packetByteBuf.readInt();
                String string3 = packetByteBuf.readString();
                String string4 = packetByteBuf.readString();
                int p = packetByteBuf.readInt();
                String string5 = packetByteBuf.readString();
                boolean bl2 = packetByteBuf.readBoolean();
                Path path2 = bl2 ? Path.fromBuffer(packetByteBuf) : null;
                boolean bl3 = packetByteBuf.readBoolean();
                VillageDebugRenderer.class_4232 lv2 = new VillageDebugRenderer.class_4232(uUID, o, string3, string4, p, position, string5, path2, bl3);
                int q = packetByteBuf.readInt();
                for (r = 0; r < q; ++r) {
                    String string6 = packetByteBuf.readString();
                    lv2.field_18927.add(string6);
                }
                r = packetByteBuf.readInt();
                for (s = 0; s < r; ++s) {
                    String string7 = packetByteBuf.readString();
                    lv2.field_18928.add(string7);
                }
                s = packetByteBuf.readInt();
                for (t = 0; t < s; ++t) {
                    String string8 = packetByteBuf.readString();
                    lv2.field_19374.add(string8);
                }
                t = packetByteBuf.readInt();
                for (u = 0; u < t; ++u) {
                    BlockPos blockPos3 = packetByteBuf.readBlockPos();
                    lv2.field_18930.add(blockPos3);
                }
                u = packetByteBuf.readInt();
                for (int v = 0; v < u; ++v) {
                    String string9 = packetByteBuf.readString();
                    lv2.field_19375.add(string9);
                }
                this.client.debugRenderer.villageDebugRenderer.addBrain(lv2);
            } else {
                LOGGER.warn("Unknown custom packed identifier: {}", (Object)identifier);
            }
        }
        finally {
            if (packetByteBuf != null) {
                packetByteBuf.release();
            }
        }
    }

    @Override
    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();
        String string = packet.getName();
        if (packet.getMode() == 0) {
            scoreboard.addObjective(string, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType());
        } else if (scoreboard.containsObjective(string)) {
            ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
            if (packet.getMode() == 1) {
                scoreboard.removeObjective(scoreboardObjective);
            } else if (packet.getMode() == 2) {
                scoreboardObjective.setRenderType(packet.getType());
                scoreboardObjective.setDisplayName(packet.getDisplayName());
            }
        }
    }

    @Override
    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();
        String string = packet.getObjectiveName();
        switch (packet.getUpdateMode()) {
            case CHANGE: {
                ScoreboardObjective scoreboardObjective = scoreboard.getObjective(string);
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(packet.getPlayerName(), scoreboardObjective);
                scoreboardPlayerScore.setScore(packet.getScore());
                break;
            }
            case REMOVE: {
                scoreboard.resetPlayerScore(packet.getPlayerName(), scoreboard.getNullableObjective(string));
            }
        }
    }

    @Override
    public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();
        String string = packet.getName();
        ScoreboardObjective scoreboardObjective = string == null ? null : scoreboard.getObjective(string);
        scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardObjective);
    }

    @Override
    public void onTeam(TeamS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();
        Team team = packet.getMode() == 0 ? scoreboard.addTeam(packet.getTeamName()) : scoreboard.getTeam(packet.getTeamName());
        if (packet.getMode() == 0 || packet.getMode() == 2) {
            AbstractTeam.CollisionRule collisionRule;
            team.setDisplayName(packet.getDisplayName());
            team.setColor(packet.getPlayerPrefix());
            team.setFriendlyFlagsBitwise(packet.getFlags());
            AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(packet.getNameTagVisibilityRule());
            if (visibilityRule != null) {
                team.setNameTagVisibilityRule(visibilityRule);
            }
            if ((collisionRule = AbstractTeam.CollisionRule.getRule(packet.getCollisionRule())) != null) {
                team.setCollisionRule(collisionRule);
            }
            team.setPrefix(packet.getPrefix());
            team.setSuffix(packet.getSuffix());
        }
        if (packet.getMode() == 0 || packet.getMode() == 3) {
            for (String string : packet.getPlayerList()) {
                scoreboard.addPlayerToTeam(string, team);
            }
        }
        if (packet.getMode() == 4) {
            for (String string : packet.getPlayerList()) {
                scoreboard.removePlayerFromTeam(string, team);
            }
        }
        if (packet.getMode() == 1) {
            scoreboard.removeTeam(team);
        }
    }

    @Override
    public void onParticle(ParticleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (packet.getCount() == 0) {
            double d = packet.getSpeed() * packet.getOffsetX();
            double e = packet.getSpeed() * packet.getOffsetY();
            double f = packet.getSpeed() * packet.getOffsetZ();
            try {
                this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX(), packet.getY(), packet.getZ(), d, e, f);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not spawn particle effect {}", (Object)packet.getParameters());
            }
        } else {
            for (int i = 0; i < packet.getCount(); ++i) {
                double g = this.random.nextGaussian() * (double)packet.getOffsetX();
                double h = this.random.nextGaussian() * (double)packet.getOffsetY();
                double j = this.random.nextGaussian() * (double)packet.getOffsetZ();
                double k = this.random.nextGaussian() * (double)packet.getSpeed();
                double l = this.random.nextGaussian() * (double)packet.getSpeed();
                double m = this.random.nextGaussian() * (double)packet.getSpeed();
                try {
                    this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX() + g, packet.getY() + h, packet.getZ() + j, k, l, m);
                    continue;
                }
                catch (Throwable throwable2) {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)packet.getParameters());
                    return;
                }
            }
        }
    }

    @Override
    public void onEntityAttributes(EntityAttributesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            return;
        }
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
        }
        AbstractEntityAttributeContainer abstractEntityAttributeContainer = ((LivingEntity)entity).getAttributes();
        for (EntityAttributesS2CPacket.Entry entry : packet.getEntries()) {
            EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getId());
            if (entityAttributeInstance == null) {
                entityAttributeInstance = abstractEntityAttributeContainer.register(new ClampedEntityAttribute(null, entry.getId(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }
            entityAttributeInstance.setBaseValue(entry.getBaseValue());
            entityAttributeInstance.clearModifiers();
            for (EntityAttributeModifier entityAttributeModifier : entry.getModifiers()) {
                entityAttributeInstance.addModifier(entityAttributeModifier);
            }
        }
    }

    @Override
    public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Container container = this.client.player.container;
        if (container.syncId != packet.getSyncId() || !container.isNotRestricted(this.client.player)) {
            return;
        }
        this.recipeManager.get(packet.getRecipeId()).ifPresent(recipe -> {
            if (this.client.currentScreen instanceof RecipeBookProvider) {
                RecipeBookWidget recipeBookWidget = ((RecipeBookProvider)((Object)this.client.currentScreen)).getRecipeBookWidget();
                recipeBookWidget.showGhostRecipe((Recipe<?>)recipe, container.slots);
            }
        });
    }

    @Override
    public void onLightUpdate(LightUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        int i = packet.getChunkX();
        int j = packet.getChunkZ();
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        int k = packet.getSkyLightMask();
        int l = packet.getFilledSkyLightMask();
        Iterator<byte[]> iterator = packet.getSkyLightUpdates().iterator();
        this.method_2870(i, j, lightingProvider, LightType.SKY, k, l, iterator);
        int m = packet.getBlockLightMask();
        int n = packet.getFilledBlockLightMask();
        Iterator<byte[]> iterator2 = packet.getBlockLightUpdates().iterator();
        this.method_2870(i, j, lightingProvider, LightType.BLOCK, m, n, iterator2);
    }

    @Override
    public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Container container = this.client.player.container;
        if (packet.getSyncId() == container.syncId && container instanceof MerchantContainer) {
            ((MerchantContainer)container).setOffers(new TraderOfferList(packet.getOffers().toTag()));
            ((MerchantContainer)container).setExperienceFromServer(packet.getExperience());
            ((MerchantContainer)container).setLevelProgress(packet.getLevelProgress());
            ((MerchantContainer)container).setCanLevel(packet.isLeveled());
            ((MerchantContainer)container).setRefreshTrades(packet.method_20722());
        }
    }

    @Override
    public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.chunkLoadDistance = packet.getDistance();
        this.world.getChunkManager().updateLoadDistance(packet.getDistance());
    }

    @Override
    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
    }

    @Override
    public void method_21707(PlayerActionResponseS2CPacket playerActionResponseS2CPacket) {
        NetworkThreadUtils.forceMainThread(playerActionResponseS2CPacket, this, this.client);
        this.client.interactionManager.method_21705(this.world, playerActionResponseS2CPacket.getBlockPos(), playerActionResponseS2CPacket.getBlockState(), playerActionResponseS2CPacket.getAction(), playerActionResponseS2CPacket.method_21711());
    }

    private void method_2870(int i, int j, LightingProvider lightingProvider, LightType lightType, int k, int l, Iterator<byte[]> iterator) {
        for (int m = 0; m < 18; ++m) {
            boolean bl2;
            int n = -1 + m;
            boolean bl = (k & 1 << m) != 0;
            boolean bl3 = bl2 = (l & 1 << m) != 0;
            if (!bl && !bl2) continue;
            lightingProvider.queueData(lightType, ChunkSectionPos.from(i, n, j), bl ? new ChunkNibbleArray((byte[])iterator.next().clone()) : new ChunkNibbleArray());
            this.world.scheduleBlockRenders(i, n, j);
        }
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }

    public Collection<PlayerListEntry> getPlayerList() {
        return this.playerListEntries.values();
    }

    @Nullable
    public PlayerListEntry getPlayerListEntry(UUID uuid) {
        return this.playerListEntries.get(uuid);
    }

    @Nullable
    public PlayerListEntry getPlayerListEntry(String profileName) {
        for (PlayerListEntry playerListEntry : this.playerListEntries.values()) {
            if (!playerListEntry.getProfile().getName().equals(profileName)) continue;
            return playerListEntry;
        }
        return null;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public ClientAdvancementManager getAdvancementHandler() {
        return this.advancementHandler;
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public ClientWorld getWorld() {
        return this.world;
    }

    public RegistryTagManager getTagManager() {
        return this.tagManager;
    }

    public DataQueryHandler getDataQueryHandler() {
        return this.dataQueryHandler;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }
}
