/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerItemCooldownManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerPlayerEntity
extends PlayerEntity
implements ContainerListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private String clientLanguage = "en_US";
    public ServerPlayNetworkHandler networkHandler;
    public final MinecraftServer server;
    public final ServerPlayerInteractionManager interactionManager;
    private final List<Integer> removedEntities = Lists.newLinkedList();
    private final PlayerAdvancementTracker advancementTracker;
    private final ServerStatHandler statHandler;
    private float field_13963 = Float.MIN_VALUE;
    private int field_13983 = Integer.MIN_VALUE;
    private int field_13968 = Integer.MIN_VALUE;
    private int field_13982 = Integer.MIN_VALUE;
    private int field_13965 = Integer.MIN_VALUE;
    private int field_13980 = Integer.MIN_VALUE;
    private float field_13997 = -1.0E8f;
    private int field_13979 = -99999999;
    private boolean field_13972 = true;
    private int field_13978 = -99999999;
    private int field_13998 = 60;
    private ChatVisibility clientChatVisibility;
    private boolean field_13971 = true;
    private long lastActionTime = Util.getMeasuringTimeMs();
    private Entity cameraEntity;
    private boolean inTeleportationState;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook;
    private Vec3d field_13992;
    private int field_13973;
    private boolean field_13964;
    @Nullable
    private Vec3d enteredNetherPos;
    private ChunkSectionPos cameraPosition = ChunkSectionPos.from(0, 0, 0);
    private int containerSyncId;
    public boolean field_13991;
    public int pingMilliseconds;
    public boolean notInAnyWorld;

    public ServerPlayerEntity(MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager) {
        super(serverWorld, gameProfile);
        serverPlayerInteractionManager.player = this;
        this.interactionManager = serverPlayerInteractionManager;
        this.server = minecraftServer;
        this.recipeBook = new ServerRecipeBook(minecraftServer.getRecipeManager());
        this.statHandler = minecraftServer.getPlayerManager().createStatHandler(this);
        this.advancementTracker = minecraftServer.getPlayerManager().getAdvancementTracker(this);
        this.stepHeight = 1.0f;
        this.method_14245(serverWorld);
    }

    private void method_14245(ServerWorld serverWorld) {
        BlockPos blockPos = serverWorld.getSpawnPos();
        if (serverWorld.dimension.hasSkyLight() && serverWorld.getLevelProperties().getGameMode() != GameMode.ADVENTURE) {
            long l;
            long m;
            int i = Math.max(0, this.server.getSpawnRadius(serverWorld));
            int j = MathHelper.floor(serverWorld.getWorldBorder().getDistanceInsideBorder(blockPos.getX(), blockPos.getZ()));
            if (j < i) {
                i = j;
            }
            if (j <= 1) {
                i = 1;
            }
            int k = (m = (l = (long)(i * 2 + 1)) * l) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)m;
            int n = this.method_14244(k);
            int o = new Random().nextInt(k);
            for (int p = 0; p < k; ++p) {
                int q = (o + n * p) % k;
                int r = q % (i * 2 + 1);
                int s = q / (i * 2 + 1);
                BlockPos blockPos2 = serverWorld.getDimension().getTopSpawningBlockPosition(blockPos.getX() + r - i, blockPos.getZ() + s - i, false);
                if (blockPos2 == null) continue;
                this.refreshPositionAndAngles(blockPos2, 0.0f, 0.0f);
                if (!serverWorld.doesNotCollide(this)) {
                    continue;
                }
                break;
            }
        } else {
            this.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
            while (!serverWorld.doesNotCollide(this) && this.y < 255.0) {
                this.updatePosition(this.x, this.y + 1.0, this.z);
            }
        }
    }

    private int method_14244(int i) {
        return i <= 16 ? i - 1 : 17;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("playerGameType", 99)) {
            if (this.getServer().shouldForceGameMode()) {
                this.interactionManager.setGameMode(this.getServer().getDefaultGameMode());
            } else {
                this.interactionManager.setGameMode(GameMode.byId(tag.getInt("playerGameType")));
            }
        }
        if (tag.contains("enteredNetherPosition", 10)) {
            CompoundTag compoundTag = tag.getCompound("enteredNetherPosition");
            this.enteredNetherPos = new Vec3d(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"));
        }
        this.seenCredits = tag.getBoolean("seenCredits");
        if (tag.contains("recipeBook", 10)) {
            this.recipeBook.fromTag(tag.getCompound("recipeBook"));
        }
        if (this.isSleeping()) {
            this.wakeUp();
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("playerGameType", this.interactionManager.getGameMode().getId());
        tag.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPos != null) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putDouble("x", this.enteredNetherPos.x);
            compoundTag.putDouble("y", this.enteredNetherPos.y);
            compoundTag.putDouble("z", this.enteredNetherPos.z);
            tag.put("enteredNetherPosition", compoundTag);
        }
        Entity entity = this.getRootVehicle();
        Entity entity2 = this.getVehicle();
        if (entity2 != null && entity != this && entity.hasPlayerRider()) {
            CompoundTag compoundTag2 = new CompoundTag();
            CompoundTag compoundTag3 = new CompoundTag();
            entity.saveToTag(compoundTag3);
            compoundTag2.putUuid("Attach", entity2.getUuid());
            compoundTag2.put("Entity", compoundTag3);
            tag.put("RootVehicle", compoundTag2);
        }
        tag.put("recipeBook", this.recipeBook.toTag());
    }

    public void setExperiencePoints(int i) {
        float f = this.getNextLevelExperience();
        float g = (f - 1.0f) / f;
        this.experienceProgress = MathHelper.clamp((float)i / f, 0.0f, g);
        this.field_13978 = -1;
    }

    public void setExperienceLevel(int level) {
        this.experienceLevel = level;
        this.field_13978 = -1;
    }

    @Override
    public void addExperienceLevels(int levels) {
        super.addExperienceLevels(levels);
        this.field_13978 = -1;
    }

    @Override
    public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
        super.applyEnchantmentCosts(enchantedItem, experienceLevels);
        this.field_13978 = -1;
    }

    public void method_14235() {
        this.container.addListener(this);
    }

    @Override
    public void method_6000() {
        super.method_6000();
        this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTER_COMBAT));
    }

    @Override
    public void method_6044() {
        super.method_6044();
        this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.END_COMBAT));
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        Criterions.ENTER_BLOCK.trigger(this, state);
    }

    @Override
    protected ItemCooldownManager createCooldownManager() {
        return new ServerItemCooldownManager(this);
    }

    @Override
    public void tick() {
        this.interactionManager.update();
        --this.field_13998;
        if (this.timeUntilRegen > 0) {
            --this.timeUntilRegen;
        }
        this.container.sendContentUpdates();
        if (!this.world.isClient && !this.container.canUse(this)) {
            this.closeContainer();
            this.container = this.playerContainer;
        }
        while (!this.removedEntities.isEmpty()) {
            int i = Math.min(this.removedEntities.size(), Integer.MAX_VALUE);
            int[] is = new int[i];
            Iterator<Integer> iterator = this.removedEntities.iterator();
            int j = 0;
            while (iterator.hasNext() && j < i) {
                is[j++] = iterator.next();
                iterator.remove();
            }
            this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(is));
        }
        Entity entity = this.getCameraEntity();
        if (entity != this) {
            if (entity.isAlive()) {
                this.updatePositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
                this.getServerWorld().getChunkManager().updateCameraPosition(this);
                if (this.isSneaking()) {
                    this.setCameraEntity(this);
                }
            } else {
                this.setCameraEntity(this);
            }
        }
        Criterions.TICK.trigger(this);
        if (this.field_13992 != null) {
            Criterions.LEVITATION.trigger(this, this.field_13992, this.age - this.field_13973);
        }
        this.advancementTracker.sendUpdate(this);
    }

    public void method_14226() {
        try {
            if (!this.isSpectator() || this.world.isBlockLoaded(new BlockPos(this))) {
                super.tick();
            }
            for (int i = 0; i < this.inventory.getInvSize(); ++i) {
                Packet<?> packet;
                ItemStack itemStack = this.inventory.getInvStack(i);
                if (!itemStack.getItem().isNetworkSynced() || (packet = ((NetworkSyncedItem)itemStack.getItem()).createSyncPacket(itemStack, this.world, this)) == null) continue;
                this.networkHandler.sendPacket(packet);
            }
            if (this.getHealth() != this.field_13997 || this.field_13979 != this.hungerManager.getFoodLevel() || this.hungerManager.getSaturationLevel() == 0.0f != this.field_13972) {
                this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
                this.field_13997 = this.getHealth();
                this.field_13979 = this.hungerManager.getFoodLevel();
                boolean bl = this.field_13972 = this.hungerManager.getSaturationLevel() == 0.0f;
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.field_13963) {
                this.field_13963 = this.getHealth() + this.getAbsorptionAmount();
                this.method_14212(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.field_13963));
            }
            if (this.hungerManager.getFoodLevel() != this.field_13983) {
                this.field_13983 = this.hungerManager.getFoodLevel();
                this.method_14212(ScoreboardCriterion.FOOD, MathHelper.ceil(this.field_13983));
            }
            if (this.getAir() != this.field_13968) {
                this.field_13968 = this.getAir();
                this.method_14212(ScoreboardCriterion.AIR, MathHelper.ceil(this.field_13968));
            }
            if (this.getArmor() != this.field_13982) {
                this.field_13982 = this.getArmor();
                this.method_14212(ScoreboardCriterion.ARMOR, MathHelper.ceil(this.field_13982));
            }
            if (this.totalExperience != this.field_13980) {
                this.field_13980 = this.totalExperience;
                this.method_14212(ScoreboardCriterion.XP, MathHelper.ceil(this.field_13980));
            }
            if (this.experienceLevel != this.field_13965) {
                this.field_13965 = this.experienceLevel;
                this.method_14212(ScoreboardCriterion.LEVEL, MathHelper.ceil(this.field_13965));
            }
            if (this.totalExperience != this.field_13978) {
                this.field_13978 = this.totalExperience;
                this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.age % 20 == 0) {
                Criterions.LOCATION.trigger(this);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking player");
            CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    private void method_14212(ScoreboardCriterion scoreboardCriterion, int i) {
        this.getScoreboard().forEachScore(scoreboardCriterion, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.setScore(i));
    }

    @Override
    public void onDeath(DamageSource source) {
        boolean bl = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
        if (bl) {
            Text text = this.getDamageTracker().getDeathMessage();
            this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, text), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> {
                if (!future.isSuccess()) {
                    int i = 256;
                    String string = text.asTruncatedString(256);
                    TranslatableText text2 = new TranslatableText("death.attack.message_too_long", new LiteralText(string).formatted(Formatting.YELLOW));
                    Text text3 = new TranslatableText("death.attack.even_more_magic", this.getDisplayName()).styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
                    this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, text3));
                }
            }));
            AbstractTeam abstractTeam = this.getScoreboardTeam();
            if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
                this.server.getPlayerManager().sendToAll(text);
            } else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerManager().sendToTeam(this, text);
            } else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerManager().sendToOtherTeams(this, text);
            }
        } else {
            this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED));
        }
        this.dropShoulderEntities();
        if (!this.isSpectator()) {
            this.drop(source);
        }
        this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
        LivingEntity livingEntity = this.method_6124();
        if (livingEntity != null) {
            this.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
            livingEntity.updateKilledAdvancementCriterion(this, this.field_6232, source);
            if (!this.world.isClient && livingEntity instanceof WitherEntity) {
                boolean bl2 = false;
                if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
                    BlockState blockState = Blocks.WITHER_ROSE.getDefaultState();
                    if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
                        this.world.setBlockState(blockPos, blockState, 3);
                        bl2 = true;
                    }
                }
                if (!bl2) {
                    ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
                    this.world.spawnEntity(itemEntity);
                }
            }
        }
        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
        this.getDamageTracker().update();
    }

    @Override
    public void updateKilledAdvancementCriterion(Entity killer, int score, DamageSource damageSource) {
        if (killer == this) {
            return;
        }
        super.updateKilledAdvancementCriterion(killer, score, damageSource);
        this.addScore(score);
        String string = this.getEntityName();
        String string2 = killer.getEntityName();
        this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
        if (killer instanceof PlayerEntity) {
            this.incrementStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
        } else {
            this.incrementStat(Stats.MOB_KILLS);
        }
        this.method_14227(string, string2, ScoreboardCriterion.TEAM_KILLS);
        this.method_14227(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
        Criterions.PLAYER_KILLED_ENTITY.trigger(this, killer, damageSource);
    }

    private void method_14227(String string, String string2, ScoreboardCriterion[] scoreboardCriterions) {
        int i;
        Team team = this.getScoreboard().getPlayerTeam(string2);
        if (team != null && (i = team.getColor().getColorIndex()) >= 0 && i < scoreboardCriterions.length) {
            this.getScoreboard().forEachScore(scoreboardCriterions[i], string, ScoreboardPlayerScore::incrementScore);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        boolean bl2 = bl = this.server.isDedicated() && this.method_14230() && "fall".equals(source.name);
        if (!bl && this.field_13998 > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (source instanceof EntityDamageSource) {
            ProjectileEntity projectileEntity;
            Entity entity2;
            Entity entity = source.getAttacker();
            if (entity instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity)) {
                return false;
            }
            if (entity instanceof ProjectileEntity && (entity2 = (projectileEntity = (ProjectileEntity)entity).getOwner()) instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity2)) {
                return false;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean shouldDamagePlayer(PlayerEntity player) {
        if (!this.method_14230()) {
            return false;
        }
        return super.shouldDamagePlayer(player);
    }

    private boolean method_14230() {
        return this.server.isPvpEnabled();
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType newDimension) {
        this.inTeleportationState = true;
        DimensionType dimensionType = this.dimension;
        if (dimensionType == DimensionType.THE_END && newDimension == DimensionType.OVERWORLD) {
            this.detach();
            this.getServerWorld().removePlayer(this);
            if (!this.notInAnyWorld) {
                this.notInAnyWorld = true;
                this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, this.seenCredits ? 0.0f : 1.0f));
                this.seenCredits = true;
            }
            return this;
        }
        ServerWorld serverWorld = this.server.getWorld(dimensionType);
        this.dimension = newDimension;
        ServerWorld serverWorld2 = this.server.getWorld(newDimension);
        LevelProperties levelProperties = this.world.getLevelProperties();
        this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(newDimension, levelProperties.getGeneratorType(), this.interactionManager.getGameMode()));
        this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
        PlayerManager playerManager = this.server.getPlayerManager();
        playerManager.sendCommandTree(this);
        serverWorld.removePlayer(this);
        this.removed = false;
        double d = this.x;
        double e = this.y;
        double f = this.z;
        float g = this.pitch;
        float h = this.yaw;
        double i = 8.0;
        float j = h;
        serverWorld.getProfiler().push("moving");
        if (dimensionType == DimensionType.OVERWORLD && newDimension == DimensionType.THE_NETHER) {
            this.enteredNetherPos = new Vec3d(this.x, this.y, this.z);
            d /= 8.0;
            f /= 8.0;
        } else if (dimensionType == DimensionType.THE_NETHER && newDimension == DimensionType.OVERWORLD) {
            d *= 8.0;
            f *= 8.0;
        } else if (dimensionType == DimensionType.OVERWORLD && newDimension == DimensionType.THE_END) {
            BlockPos blockPos = serverWorld2.getForcedSpawnPoint();
            d = blockPos.getX();
            e = blockPos.getY();
            f = blockPos.getZ();
            h = 90.0f;
            g = 0.0f;
        }
        this.refreshPositionAndAngles(d, e, f, h, g);
        serverWorld.getProfiler().pop();
        serverWorld.getProfiler().push("placing");
        double k = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundWest() + 16.0);
        double l = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundNorth() + 16.0);
        double m = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
        double n = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
        d = MathHelper.clamp(d, k, m);
        f = MathHelper.clamp(f, l, n);
        this.refreshPositionAndAngles(d, e, f, h, g);
        if (newDimension == DimensionType.THE_END) {
            int o = MathHelper.floor(this.x);
            int p = MathHelper.floor(this.y) - 1;
            int q = MathHelper.floor(this.z);
            boolean r = true;
            boolean s = false;
            for (int t = -2; t <= 2; ++t) {
                for (int u = -2; u <= 2; ++u) {
                    for (int v = -1; v < 3; ++v) {
                        int w = o + u * 1 + t * 0;
                        int x = p + v;
                        int y = q + u * 0 - t * 1;
                        boolean bl = v < 0;
                        serverWorld2.setBlockState(new BlockPos(w, x, y), bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                    }
                }
            }
            this.refreshPositionAndAngles(o, p, q, h, 0.0f);
            this.setVelocity(Vec3d.ZERO);
        } else if (!serverWorld2.getPortalForcer().usePortal(this, j)) {
            serverWorld2.getPortalForcer().createPortal(this);
            serverWorld2.getPortalForcer().usePortal(this, j);
        }
        serverWorld.getProfiler().pop();
        this.setWorld(serverWorld2);
        serverWorld2.method_18211(this);
        this.method_18783(serverWorld);
        this.networkHandler.requestTeleport(this.x, this.y, this.z, h, g);
        this.interactionManager.setWorld(serverWorld2);
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        playerManager.sendWorldInfo(this, serverWorld2);
        playerManager.method_14594(this);
        for (StatusEffectInstance statusEffectInstance : this.getStatusEffects()) {
            this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), statusEffectInstance));
        }
        this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
        this.field_13978 = -1;
        this.field_13997 = -1.0f;
        this.field_13979 = -1;
        return this;
    }

    private void method_18783(ServerWorld serverWorld) {
        DimensionType dimensionType = serverWorld.dimension.getType();
        DimensionType dimensionType2 = this.world.dimension.getType();
        Criterions.CHANGED_DIMENSION.trigger(this, dimensionType, dimensionType2);
        if (dimensionType == DimensionType.THE_NETHER && dimensionType2 == DimensionType.OVERWORLD && this.enteredNetherPos != null) {
            Criterions.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
        }
        if (dimensionType2 != DimensionType.THE_NETHER) {
            this.enteredNetherPos = null;
        }
    }

    @Override
    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        if (spectator.isSpectator()) {
            return this.getCameraEntity() == this;
        }
        if (this.isSpectator()) {
            return false;
        }
        return super.canBeSpectated(spectator);
    }

    private void sendBlockEntityUpdate(BlockEntity blockEntity) {
        BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket;
        if (blockEntity != null && (blockEntityUpdateS2CPacket = blockEntity.toUpdatePacket()) != null) {
            this.networkHandler.sendPacket(blockEntityUpdateS2CPacket);
        }
    }

    @Override
    public void sendPickup(Entity item, int count) {
        super.sendPickup(item, count);
        this.container.sendContentUpdates();
    }

    @Override
    public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
        return super.trySleep(pos).ifRight(unit -> {
            this.incrementStat(Stats.SLEEP_IN_BED);
            Criterions.SLEPT_IN_BED.trigger(this);
        });
    }

    @Override
    public void wakeUp(boolean bl, boolean bl2, boolean setSpawn) {
        if (this.isSleeping()) {
            this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
        }
        super.wakeUp(bl, bl2, setSpawn);
        if (this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        Entity entity2 = this.getVehicle();
        if (!super.startRiding(entity, force)) {
            return false;
        }
        Entity entity3 = this.getVehicle();
        if (entity3 != entity2 && this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
        }
        return true;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        Entity entity2 = this.getVehicle();
        if (entity2 != entity && this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    protected void applyFrostWalker(BlockPos pos) {
        if (!this.isSpectator()) {
            super.applyFrostWalker(pos);
        }
    }

    public void method_14207(double d, boolean bl) {
        BlockPos blockPos2;
        BlockState blockState2;
        Block block;
        int k;
        int j;
        int i = MathHelper.floor(this.x);
        BlockPos blockPos = new BlockPos(i, j = MathHelper.floor(this.y - (double)0.2f), k = MathHelper.floor(this.z));
        if (!this.world.isBlockLoaded(blockPos)) {
            return;
        }
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.isAir() && ((block = (blockState2 = this.world.getBlockState(blockPos2 = blockPos.down())).getBlock()).matches(BlockTags.FENCES) || block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock)) {
            blockPos = blockPos2;
            blockState = blockState2;
        }
        super.fall(d, bl, blockState, blockPos);
    }

    @Override
    public void openEditSignScreen(SignBlockEntity signBlockEntity) {
        signBlockEntity.setEditor(this);
        this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(signBlockEntity.getPos()));
    }

    private void incrementContainerSyncId() {
        this.containerSyncId = this.containerSyncId % 100 + 1;
    }

    @Override
    public OptionalInt openContainer(@Nullable NameableContainerFactory nameableContainerFactory) {
        if (nameableContainerFactory == null) {
            return OptionalInt.empty();
        }
        if (this.container != this.playerContainer) {
            this.closeContainer();
        }
        this.incrementContainerSyncId();
        Container container = nameableContainerFactory.createMenu(this.containerSyncId, this.inventory, this);
        if (container == null) {
            if (this.isSpectator()) {
                this.addChatMessage(new TranslatableText("container.spectatorCantOpen", new Object[0]).formatted(Formatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.networkHandler.sendPacket(new OpenContainerS2CPacket(container.syncId, container.getType(), nameableContainerFactory.getDisplayName()));
        container.addListener(this);
        this.container = container;
        return OptionalInt.of(this.containerSyncId);
    }

    @Override
    public void sendTradeOffers(int syncId, TraderOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
        this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
    }

    @Override
    public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
        if (this.container != this.playerContainer) {
            this.closeContainer();
        }
        this.incrementContainerSyncId();
        this.networkHandler.sendPacket(new OpenHorseContainerS2CPacket(this.containerSyncId, inventory.getInvSize(), horseBaseEntity.getEntityId()));
        this.container = new HorseContainer(this.containerSyncId, this.inventory, inventory, horseBaseEntity);
        this.container.addListener(this);
    }

    @Override
    public void openEditBookScreen(ItemStack book, Hand hand) {
        Item item = book.getItem();
        if (item == Items.WRITTEN_BOOK) {
            if (WrittenBookItem.resolve(book, this.getCommandSource(), this)) {
                this.container.sendContentUpdates();
            }
            this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
        }
    }

    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
        commandBlockBlockEntity.setNeedsUpdatePacket(true);
        this.sendBlockEntityUpdate(commandBlockBlockEntity);
    }

    @Override
    public void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack) {
        if (container.getSlot(slotId) instanceof CraftingResultSlot) {
            return;
        }
        if (container == this.playerContainer) {
            Criterions.INVENTORY_CHANGED.trigger(this, this.inventory);
        }
        if (this.field_13991) {
            return;
        }
        this.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(container.syncId, slotId, itemStack));
    }

    public void openContainer(Container container) {
        this.onContainerRegistered(container, container.getStacks());
    }

    @Override
    public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
        this.networkHandler.sendPacket(new InventoryS2CPacket(container.syncId, defaultedList));
        this.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }

    @Override
    public void onContainerPropertyUpdate(Container container, int propertyId, int i) {
        this.networkHandler.sendPacket(new ContainerPropertyUpdateS2CPacket(container.syncId, propertyId, i));
    }

    @Override
    public void closeContainer() {
        this.networkHandler.sendPacket(new CloseContainerS2CPacket(this.container.syncId));
        this.method_14247();
    }

    public void method_14241() {
        if (this.field_13991) {
            return;
        }
        this.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }

    public void method_14247() {
        this.container.close(this);
        this.container = this.playerContainer;
    }

    public void method_14218(float f, float g, boolean bl, boolean bl2) {
        if (this.hasVehicle()) {
            if (f >= -1.0f && f <= 1.0f) {
                this.sidewaysSpeed = f;
            }
            if (g >= -1.0f && g <= 1.0f) {
                this.forwardSpeed = g;
            }
            this.jumping = bl;
            this.setSneaking(bl2);
        }
    }

    @Override
    public void increaseStat(Stat<?> stat, int amount) {
        this.statHandler.increaseStat(this, stat, amount);
        this.getScoreboard().forEachScore(stat, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.incrementScore(amount));
    }

    @Override
    public void resetStat(Stat<?> stat) {
        this.statHandler.setStat(this, stat, 0);
        this.getScoreboard().forEachScore(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
    }

    @Override
    public int unlockRecipes(Collection<Recipe<?>> recipes) {
        return this.recipeBook.unlockRecipes(recipes, this);
    }

    @Override
    public void unlockRecipes(Identifier[] ids) {
        ArrayList list = Lists.newArrayList();
        for (Identifier identifier : ids) {
            this.server.getRecipeManager().get(identifier).ifPresent(list::add);
        }
        this.unlockRecipes(list);
    }

    @Override
    public int lockRecipes(Collection<Recipe<?>> recipes) {
        return this.recipeBook.lockRecipes(recipes, this);
    }

    @Override
    public void addExperience(int experience) {
        super.addExperience(experience);
        this.field_13978 = -1;
    }

    public void method_14231() {
        this.field_13964 = true;
        this.removeAllPassengers();
        if (this.isSleeping()) {
            this.wakeUp(true, false, false);
        }
    }

    public boolean method_14239() {
        return this.field_13964;
    }

    public void method_14217() {
        this.field_13997 = -1.0E8f;
    }

    @Override
    public void addChatMessage(Text message, boolean bl) {
        this.networkHandler.sendPacket(new ChatMessageS2CPacket(message, bl ? MessageType.GAME_INFO : MessageType.CHAT));
    }

    @Override
    protected void method_6040() {
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, 9));
            super.method_6040();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        super.lookAt(anchorPoint, target);
        this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, target.x, target.y, target.z));
    }

    public void method_14222(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
        Vec3d vec3d = entityAnchor2.positionAt(entity);
        super.lookAt(entityAnchor, vec3d);
        this.networkHandler.sendPacket(new LookAtS2CPacket(entityAnchor, entity, entityAnchor2));
    }

    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
        if (alive) {
            this.inventory.clone(oldPlayer.inventory);
            this.setHealth(oldPlayer.getHealth());
            this.hungerManager = oldPlayer.hungerManager;
            this.experienceLevel = oldPlayer.experienceLevel;
            this.totalExperience = oldPlayer.totalExperience;
            this.experienceProgress = oldPlayer.experienceProgress;
            this.setScore(oldPlayer.getScore());
            this.lastNetherPortalPosition = oldPlayer.lastNetherPortalPosition;
            this.lastNetherPortalDirectionVector = oldPlayer.lastNetherPortalDirectionVector;
            this.lastNetherPortalDirection = oldPlayer.lastNetherPortalDirection;
        } else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
            this.inventory.clone(oldPlayer.inventory);
            this.experienceLevel = oldPlayer.experienceLevel;
            this.totalExperience = oldPlayer.totalExperience;
            this.experienceProgress = oldPlayer.experienceProgress;
            this.setScore(oldPlayer.getScore());
        }
        this.enchantmentTableSeed = oldPlayer.enchantmentTableSeed;
        this.enderChestInventory = oldPlayer.enderChestInventory;
        this.getDataTracker().set(PLAYER_MODEL_PARTS, oldPlayer.getDataTracker().get(PLAYER_MODEL_PARTS));
        this.field_13978 = -1;
        this.field_13997 = -1.0f;
        this.field_13979 = -1;
        this.recipeBook.copyFrom(oldPlayer.recipeBook);
        this.removedEntities.addAll(oldPlayer.removedEntities);
        this.seenCredits = oldPlayer.seenCredits;
        this.enteredNetherPos = oldPlayer.enteredNetherPos;
        this.setShoulderEntityLeft(oldPlayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(oldPlayer.getShoulderEntityRight());
    }

    @Override
    protected void method_6020(StatusEffectInstance statusEffectInstance) {
        super.method_6020(statusEffectInstance);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), statusEffectInstance));
        if (statusEffectInstance.getEffectType() == StatusEffects.LEVITATION) {
            this.field_13973 = this.age;
            this.field_13992 = new Vec3d(this.x, this.y, this.z);
        }
        Criterions.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
        super.method_6009(statusEffectInstance, bl);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), statusEffectInstance));
        Criterions.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    protected void method_6129(StatusEffectInstance statusEffectInstance) {
        super.method_6129(statusEffectInstance);
        this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getEntityId(), statusEffectInstance.getEffectType()));
        if (statusEffectInstance.getEffectType() == StatusEffects.LEVITATION) {
            this.field_13992 = null;
        }
        Criterions.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    public void requestTeleport(double destX, double destY, double destZ) {
        this.networkHandler.requestTeleport(destX, destY, destZ, this.yaw, this.pitch);
    }

    @Override
    public void addCritParticles(Entity target) {
        this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, 4));
    }

    @Override
    public void addEnchantedHitParticles(Entity target) {
        this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, 5));
    }

    @Override
    public void sendAbilitiesUpdate() {
        if (this.networkHandler == null) {
            return;
        }
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        this.updatePotionVisibility();
    }

    public ServerWorld getServerWorld() {
        return (ServerWorld)this.world;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.interactionManager.setGameMode(gameMode);
        this.networkHandler.sendPacket(new GameStateChangeS2CPacket(3, gameMode.getId()));
        if (gameMode == GameMode.SPECTATOR) {
            this.dropShoulderEntities();
            this.stopRiding();
        } else {
            this.setCameraEntity(this);
        }
        this.sendAbilitiesUpdate();
        this.method_6008();
    }

    @Override
    public boolean isSpectator() {
        return this.interactionManager.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return this.interactionManager.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public void sendMessage(Text message) {
        this.sendChatMessage(message, MessageType.SYSTEM);
    }

    public void sendChatMessage(Text text, MessageType messageType) {
        this.networkHandler.sendPacket(new ChatMessageS2CPacket(text, messageType), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> {
            if (!(future.isSuccess() || messageType != MessageType.GAME_INFO && messageType != MessageType.SYSTEM)) {
                int i = 256;
                String string = text.asTruncatedString(256);
                Text text2 = new LiteralText(string).formatted(Formatting.YELLOW);
                this.networkHandler.sendPacket(new ChatMessageS2CPacket(new TranslatableText("multiplayer.message_not_delivered", text2).formatted(Formatting.RED), MessageType.SYSTEM));
            }
        }));
    }

    public String getServerBrand() {
        String string = this.networkHandler.connection.getAddress().toString();
        string = string.substring(string.indexOf("/") + 1);
        string = string.substring(0, string.indexOf(":"));
        return string;
    }

    public void setClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket) {
        this.clientLanguage = clientSettingsC2SPacket.getLanguage();
        this.clientChatVisibility = clientSettingsC2SPacket.getChatVisibility();
        this.field_13971 = clientSettingsC2SPacket.hasChatColors();
        this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
        this.getDataTracker().set(MAIN_ARM, (byte)(clientSettingsC2SPacket.getMainArm() != Arm.LEFT ? 1 : 0));
    }

    public ChatVisibility getClientChatVisibility() {
        return this.clientChatVisibility;
    }

    public void method_14255(String string, String string2) {
        this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(string, string2));
    }

    @Override
    protected int getPermissionLevel() {
        return this.server.getPermissionLevel(this.getGameProfile());
    }

    public void updateLastActionTime() {
        this.lastActionTime = Util.getMeasuringTimeMs();
    }

    public ServerStatHandler getStatHandler() {
        return this.statHandler;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void onStoppedTracking(Entity entity) {
        if (entity instanceof PlayerEntity) {
            this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(entity.getEntityId()));
        } else {
            this.removedEntities.add(entity.getEntityId());
        }
    }

    public void onStartedTracking(Entity entity) {
        this.removedEntities.remove((Object)entity.getEntityId());
    }

    @Override
    protected void updatePotionVisibility() {
        if (this.isSpectator()) {
            this.clearPotionSwirls();
            this.setInvisible(true);
        } else {
            super.updatePotionVisibility();
        }
    }

    public Entity getCameraEntity() {
        return this.cameraEntity == null ? this : this.cameraEntity;
    }

    public void setCameraEntity(Entity entity) {
        Entity entity2 = this.getCameraEntity();
        Entity entity3 = this.cameraEntity = entity == null ? this : entity;
        if (entity2 != this.cameraEntity) {
            this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
            this.requestTeleport(this.cameraEntity.x, this.cameraEntity.y, this.cameraEntity.z);
        }
    }

    @Override
    protected void tickNetherPortalCooldown() {
        if (this.netherPortalCooldown > 0 && !this.inTeleportationState) {
            --this.netherPortalCooldown;
        }
    }

    @Override
    public void attack(Entity target) {
        if (this.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            this.setCameraEntity(target);
        } else {
            super.attack(target);
        }
    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Text method_14206() {
        return null;
    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.resetLastAttackedTicks();
    }

    public boolean isInTeleportationState() {
        return this.inTeleportationState;
    }

    public void onTeleportationDone() {
        this.inTeleportationState = false;
    }

    public void method_14243() {
        this.setFlag(7, true);
    }

    public void method_14229() {
        this.setFlag(7, true);
        this.setFlag(7, false);
    }

    public PlayerAdvancementTracker getAdvancementTracker() {
        return this.advancementTracker;
    }

    public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
        this.setCameraEntity(this);
        this.stopRiding();
        if (targetWorld == this.world) {
            this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
        } else {
            ServerWorld serverWorld = this.getServerWorld();
            this.dimension = targetWorld.dimension.getType();
            LevelProperties levelProperties = targetWorld.getLevelProperties();
            this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(this.dimension, levelProperties.getGeneratorType(), this.interactionManager.getGameMode()));
            this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
            this.server.getPlayerManager().sendCommandTree(this);
            serverWorld.removePlayer(this);
            this.removed = false;
            this.refreshPositionAndAngles(x, y, z, yaw, pitch);
            this.setWorld(targetWorld);
            targetWorld.method_18207(this);
            this.method_18783(serverWorld);
            this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
            this.interactionManager.setWorld(targetWorld);
            this.server.getPlayerManager().sendWorldInfo(this, targetWorld);
            this.server.getPlayerManager().method_14594(this);
        }
    }

    public void sendInitialChunkPackets(ChunkPos chunkPos, Packet<?> packet, Packet<?> packet2) {
        this.networkHandler.sendPacket(packet2);
        this.networkHandler.sendPacket(packet);
    }

    public void sendUnloadChunkPacket(ChunkPos chunkPos) {
        this.networkHandler.sendPacket(new UnloadChunkS2CPacket(chunkPos.x, chunkPos.z));
    }

    public ChunkSectionPos getCameraPosition() {
        return this.cameraPosition;
    }

    public void setCameraPosition(ChunkSectionPos cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    @Override
    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
        this.networkHandler.sendPacket(new PlaySoundS2CPacket(event, category, this.x, this.y, this.z, volume, pitch));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new PlayerSpawnS2CPacket(this);
    }

    @Override
    public ItemEntity dropItem(ItemStack stack, boolean bl, boolean bl2) {
        ItemEntity itemEntity = super.dropItem(stack, bl, bl2);
        if (itemEntity == null) {
            return null;
        }
        this.world.spawnEntity(itemEntity);
        ItemStack itemStack = itemEntity.getStack();
        if (bl2) {
            if (!itemStack.isEmpty()) {
                this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
            }
            this.incrementStat(Stats.DROP);
        }
        return itemEntity;
    }
}

