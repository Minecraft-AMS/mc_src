/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.PlayerContainer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.CollisionView;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEntity
extends LivingEntity {
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.changing(0.6f, 1.8f);
    private static final Map<EntityPose, EntityDimensions> POSE_DIMENSIONS = ImmutableMap.builder().put((Object)EntityPose.STANDING, (Object)STANDING_DIMENSIONS).put((Object)EntityPose.SLEEPING, (Object)SLEEPING_DIMENSIONS).put((Object)EntityPose.FALL_FLYING, (Object)EntityDimensions.changing(0.6f, 0.6f)).put((Object)EntityPose.SWIMMING, (Object)EntityDimensions.changing(0.6f, 0.6f)).put((Object)EntityPose.SPIN_ATTACK, (Object)EntityDimensions.changing(0.6f, 0.6f)).put((Object)EntityPose.SNEAKING, (Object)EntityDimensions.changing(0.6f, 1.5f)).put((Object)EntityPose.DYING, (Object)EntityDimensions.fixed(0.2f, 0.2f)).build();
    private static final TrackedData<Float> ABSORPTION_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> SCORE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Byte> PLAYER_MODEL_PARTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> MAIN_ARM = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<CompoundTag> LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    protected static final TrackedData<CompoundTag> RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    private long field_19428;
    public final PlayerInventory inventory = new PlayerInventory(this);
    protected EnderChestInventory enderChestInventory = new EnderChestInventory();
    public final PlayerContainer playerContainer;
    public Container container;
    protected HungerManager hungerManager = new HungerManager();
    protected int field_7489;
    public float field_7505;
    public float field_7483;
    public int experiencePickUpDelay;
    public double field_7524;
    public double field_7502;
    public double field_7522;
    public double field_7500;
    public double field_7521;
    public double field_7499;
    private int sleepTimer;
    protected boolean isSubmergedInWater;
    private BlockPos spawnPosition;
    private boolean spawnForced;
    public final PlayerAbilities abilities = new PlayerAbilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentTableSeed;
    protected final float field_7509 = 0.02f;
    private int lastPlayedLevelUpSoundTime;
    private final GameProfile gameProfile;
    @Environment(value=EnvType.CLIENT)
    private boolean reducedDebugInfo;
    private ItemStack selectedItem = ItemStack.EMPTY;
    private final ItemCooldownManager itemCooldownManager = this.createCooldownManager();
    @Nullable
    public FishingBobberEntity fishHook;

    public PlayerEntity(World world, GameProfile profile) {
        super((EntityType<? extends LivingEntity>)EntityType.PLAYER, world);
        this.setUuid(PlayerEntity.getUuidFromProfile(profile));
        this.gameProfile = profile;
        this.playerContainer = new PlayerContainer(this.inventory, !world.isClient, this);
        this.container = this.playerContainer;
        BlockPos blockPos = world.getSpawnPos();
        this.refreshPositionAndAngles((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5, 0.0f, 0.0f);
        this.field_6215 = 180.0f;
    }

    public boolean method_21701(World world, BlockPos blockPos, GameMode gameMode) {
        if (!gameMode.shouldLimitWorldModification()) {
            return false;
        }
        if (gameMode == GameMode.SPECTATOR) {
            return true;
        }
        if (this.canModifyWorld()) {
            return false;
        }
        ItemStack itemStack = this.getMainHandStack();
        return itemStack.isEmpty() || !itemStack.canDestroy(world.getTagManager(), new CachedBlockPosition(world, blockPos, false));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1f);
        this.getAttributes().register(EntityAttributes.ATTACK_SPEED);
        this.getAttributes().register(EntityAttributes.LUCK);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ABSORPTION_AMOUNT, Float.valueOf(0.0f));
        this.dataTracker.startTracking(SCORE, 0);
        this.dataTracker.startTracking(PLAYER_MODEL_PARTS, (byte)0);
        this.dataTracker.startTracking(MAIN_ARM, (byte)1);
        this.dataTracker.startTracking(LEFT_SHOULDER_ENTITY, new CompoundTag());
        this.dataTracker.startTracking(RIGHT_SHOULDER_ENTITY, new CompoundTag());
    }

    @Override
    public void tick() {
        this.noClip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.experiencePickUpDelay > 0) {
            --this.experiencePickUpDelay;
        }
        if (this.isSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }
            if (!this.world.isClient && this.world.isDay()) {
                this.wakeUp(false, true, true);
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }
        this.updateWaterSubmersionState();
        super.tick();
        if (!this.world.isClient && this.container != null && !this.container.canUse(this)) {
            this.closeContainer();
            this.container = this.playerContainer;
        }
        if (this.isOnFire() && this.abilities.invulnerable) {
            this.extinguish();
        }
        this.method_7313();
        if (!this.world.isClient) {
            this.hungerManager.update(this);
            this.incrementStat(Stats.PLAY_ONE_MINUTE);
            if (this.isAlive()) {
                this.incrementStat(Stats.TIME_SINCE_DEATH);
            }
            if (this.isSneaking()) {
                this.incrementStat(Stats.SNEAK_TIME);
            }
            if (!this.isSleeping()) {
                this.incrementStat(Stats.TIME_SINCE_REST);
            }
        }
        int i = 29999999;
        double d = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
        double e = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
        if (d != this.x || e != this.z) {
            this.updatePosition(d, this.y, e);
        }
        ++this.lastAttackedTicks;
        ItemStack itemStack = this.getMainHandStack();
        if (!ItemStack.areEqualIgnoreDamage(this.selectedItem, itemStack)) {
            if (!ItemStack.areItemsEqual(this.selectedItem, itemStack)) {
                this.resetLastAttackedTicks();
            }
            this.selectedItem = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
        }
        this.updateTurtleHelmet();
        this.itemCooldownManager.update();
        this.updateSize();
    }

    protected boolean updateWaterSubmersionState() {
        this.isSubmergedInWater = this.isSubmergedIn(FluidTags.WATER, true);
        return this.isSubmergedInWater;
    }

    private void updateTurtleHelmet() {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack.getItem() == Items.TURTLE_HELMET && !this.isInFluid(FluidTags.WATER)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, true));
        }
    }

    protected ItemCooldownManager createCooldownManager() {
        return new ItemCooldownManager();
    }

    private void method_7313() {
        this.field_7524 = this.field_7500;
        this.field_7502 = this.field_7521;
        this.field_7522 = this.field_7499;
        double d = this.x - this.field_7500;
        double e = this.y - this.field_7521;
        double f = this.z - this.field_7499;
        double g = 10.0;
        if (d > 10.0) {
            this.field_7524 = this.field_7500 = this.x;
        }
        if (f > 10.0) {
            this.field_7522 = this.field_7499 = this.z;
        }
        if (e > 10.0) {
            this.field_7502 = this.field_7521 = this.y;
        }
        if (d < -10.0) {
            this.field_7524 = this.field_7500 = this.x;
        }
        if (f < -10.0) {
            this.field_7522 = this.field_7499 = this.z;
        }
        if (e < -10.0) {
            this.field_7502 = this.field_7521 = this.y;
        }
        this.field_7500 += d * 0.25;
        this.field_7499 += f * 0.25;
        this.field_7521 += e * 0.25;
    }

    protected void updateSize() {
        if (!this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
            return;
        }
        EntityPose entityPose = this.isFallFlying() ? EntityPose.FALL_FLYING : (this.isSleeping() ? EntityPose.SLEEPING : (this.isSwimming() ? EntityPose.SWIMMING : (this.isUsingRiptide() ? EntityPose.SPIN_ATTACK : (this.isSneaking() && !this.abilities.flying ? EntityPose.SNEAKING : EntityPose.STANDING))));
        EntityPose entityPose2 = this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(entityPose) ? entityPose : (this.wouldPoseNotCollide(EntityPose.SNEAKING) ? EntityPose.SNEAKING : EntityPose.SWIMMING);
        this.setPose(entityPose2);
    }

    @Override
    public int getMaxNetherPortalTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDefaultNetherPortalCooldown() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        this.world.playSound(this, this.x, this.y, this.z, sound, this.getSoundCategory(), volume, pitch);
    }

    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected int getBurningDuration() {
        return 20;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 9) {
            this.method_6040();
        } else if (status == 23) {
            this.reducedDebugInfo = false;
        } else if (status == 22) {
            this.reducedDebugInfo = true;
        } else if (status == 43) {
            this.spawnParticles(ParticleTypes.CLOUD);
        } else {
            super.handleStatus(status);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void spawnParticles(ParticleEffect parameters) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(parameters, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 1.0 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), d, e, f);
        }
    }

    protected void closeContainer() {
        this.container = this.playerContainer;
    }

    @Override
    public void tickRiding() {
        if (!this.world.isClient && this.isSneaking() && this.hasVehicle()) {
            this.stopRiding();
            this.setSneaking(false);
            return;
        }
        double d = this.x;
        double e = this.y;
        double f = this.z;
        float g = this.yaw;
        float h = this.pitch;
        super.tickRiding();
        this.field_7505 = this.field_7483;
        this.field_7483 = 0.0f;
        this.method_7260(this.x - d, this.y - e, this.z - f);
        if (this.getVehicle() instanceof PigEntity) {
            this.pitch = h;
            this.yaw = g;
            this.field_6283 = ((PigEntity)this.getVehicle()).field_6283;
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void afterSpawn() {
        this.setPose(EntityPose.STANDING);
        super.afterSpawn();
        this.setHealth(this.getMaximumHealth());
        this.deathTime = 0;
    }

    @Override
    protected void tickNewAi() {
        super.tickNewAi();
        this.tickHandSwing();
        this.headYaw = this.yaw;
    }

    @Override
    public void tickMovement() {
        if (this.field_7489 > 0) {
            --this.field_7489;
        }
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaximumHealth() && this.age % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }
        this.inventory.updateItems();
        this.field_7505 = this.field_7483;
        super.tickMovement();
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (!this.world.isClient) {
            entityAttributeInstance.setBaseValue(this.abilities.getWalkSpeed());
        }
        this.field_6281 = 0.02f;
        if (this.isSprinting()) {
            this.field_6281 = (float)((double)this.field_6281 + 0.005999999865889549);
        }
        this.setMovementSpeed((float)entityAttributeInstance.getValue());
        float f = !this.onGround || this.getHealth() <= 0.0f || this.isSwimming() ? 0.0f : Math.min(0.1f, MathHelper.sqrt(PlayerEntity.squaredHorizontalLength(this.getVelocity())));
        this.field_7483 += (f - this.field_7483) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            Box box = this.hasVehicle() && !this.getVehicle().removed ? this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0) : this.getBoundingBox().expand(1.0, 0.5, 1.0);
            List<Entity> list = this.world.getEntities(this, box);
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (entity.removed) continue;
                this.collideWithEntity(entity);
            }
        }
        this.updateShoulderEntity(this.getShoulderEntityLeft());
        this.updateShoulderEntity(this.getShoulderEntityRight());
        if (!this.world.isClient && (this.fallDistance > 0.5f || this.isTouchingWater() || this.hasVehicle()) || this.abilities.flying || this.isSleeping()) {
            this.dropShoulderEntities();
        }
    }

    private void updateShoulderEntity(@Nullable CompoundTag entityTag) {
        if (entityTag != null && !entityTag.contains("Silent") || !entityTag.getBoolean("Silent")) {
            String string = entityTag.getString("id");
            EntityType.get(string).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> ParrotEntity.playMobSound(this.world, this));
        }
    }

    private void collideWithEntity(Entity entity) {
        entity.onPlayerCollision(this);
    }

    public int getScore() {
        return this.dataTracker.get(SCORE);
    }

    public void setScore(int score) {
        this.dataTracker.set(SCORE, score);
    }

    public void addScore(int score) {
        int i = this.getScore();
        this.dataTracker.set(SCORE, i + score);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.updatePosition(this.x, this.y, this.z);
        if (!this.isSpectator()) {
            this.drop(source);
        }
        if (source != null) {
            this.setVelocity(-MathHelper.cos((this.field_6271 + this.yaw) * ((float)Math.PI / 180)) * 0.1f, 0.1f, -MathHelper.sin((this.field_6271 + this.yaw) * ((float)Math.PI / 180)) * 0.1f);
        } else {
            this.setVelocity(0.0, 0.1, 0.0);
        }
        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.vanishCursedItems();
            this.inventory.dropAll();
        }
    }

    protected void vanishCursedItems() {
        for (int i = 0; i < this.inventory.getInvSize(); ++i) {
            ItemStack itemStack = this.inventory.getInvStack(i);
            if (itemStack.isEmpty() || !EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            this.inventory.removeInvStack(i);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (source == DamageSource.ON_FIRE) {
            return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
        }
        if (source == DamageSource.DROWN) {
            return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
        }
        if (source == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH;
        }
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Nullable
    public ItemEntity dropSelectedItem(boolean dropEntireStack) {
        return this.dropItem(this.inventory.takeInvStack(this.inventory.selectedSlot, dropEntireStack && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1), false, true);
    }

    @Nullable
    public ItemEntity dropItem(ItemStack stack, boolean bl) {
        return this.dropItem(stack, false, bl);
    }

    @Nullable
    public ItemEntity dropItem(ItemStack stack, boolean bl, boolean bl2) {
        if (stack.isEmpty()) {
            return null;
        }
        double d = this.y - (double)0.3f + (double)this.getStandingEyeHeight();
        ItemEntity itemEntity = new ItemEntity(this.world, this.x, d, this.z, stack);
        itemEntity.setPickupDelay(40);
        if (bl2) {
            itemEntity.setThrower(this.getUuid());
        }
        if (bl) {
            float f = this.random.nextFloat() * 0.5f;
            float g = this.random.nextFloat() * ((float)Math.PI * 2);
            this.setVelocity(-MathHelper.sin(g) * f, 0.2f, MathHelper.cos(g) * f);
        } else {
            float f = 0.3f;
            float g = MathHelper.sin(this.pitch * ((float)Math.PI / 180));
            float h = MathHelper.cos(this.pitch * ((float)Math.PI / 180));
            float i = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
            float j = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
            float k = this.random.nextFloat() * ((float)Math.PI * 2);
            float l = 0.02f * this.random.nextFloat();
            itemEntity.setVelocity((double)(-i * h * 0.3f) + Math.cos(k) * (double)l, -g * 0.3f + 0.1f + (this.random.nextFloat() - this.random.nextFloat()) * 0.1f, (double)(j * h * 0.3f) + Math.sin(k) * (double)l);
        }
        return itemEntity;
    }

    public float getBlockBreakingSpeed(BlockState block) {
        float f = this.inventory.getBlockBreakingSpeed(block);
        if (f > 1.0f) {
            int i = EnchantmentHelper.getEfficiency(this);
            ItemStack itemStack = this.getMainHandStack();
            if (i > 0 && !itemStack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }
        if (StatusEffectUtil.hasHaste(this)) {
            f *= 1.0f + (float)(StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2f;
        }
        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float g;
            switch (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    g = 0.3f;
                    break;
                }
                case 1: {
                    g = 0.09f;
                    break;
                }
                case 2: {
                    g = 0.0027f;
                    break;
                }
                default: {
                    g = 8.1E-4f;
                }
            }
            f *= g;
        }
        if (this.isInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= 5.0f;
        }
        if (!this.onGround) {
            f /= 5.0f;
        }
        return f;
    }

    public boolean isUsingEffectiveTool(BlockState block) {
        return block.getMaterial().canBreakByHand() || this.inventory.isUsingEffectiveTool(block);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setUuid(PlayerEntity.getUuidFromProfile(this.gameProfile));
        ListTag listTag = tag.getList("Inventory", 10);
        this.inventory.deserialize(listTag);
        this.inventory.selectedSlot = tag.getInt("SelectedItemSlot");
        this.sleepTimer = tag.getShort("SleepTimer");
        this.experienceProgress = tag.getFloat("XpP");
        this.experienceLevel = tag.getInt("XpLevel");
        this.totalExperience = tag.getInt("XpTotal");
        this.enchantmentTableSeed = tag.getInt("XpSeed");
        if (this.enchantmentTableSeed == 0) {
            this.enchantmentTableSeed = this.random.nextInt();
        }
        this.setScore(tag.getInt("Score"));
        if (tag.contains("SpawnX", 99) && tag.contains("SpawnY", 99) && tag.contains("SpawnZ", 99)) {
            this.spawnPosition = new BlockPos(tag.getInt("SpawnX"), tag.getInt("SpawnY"), tag.getInt("SpawnZ"));
            this.spawnForced = tag.getBoolean("SpawnForced");
        }
        this.hungerManager.deserialize(tag);
        this.abilities.deserialize(tag);
        if (tag.contains("EnderItems", 9)) {
            this.enderChestInventory.readTags(tag.getList("EnderItems", 10));
        }
        if (tag.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(tag.getCompound("ShoulderEntityLeft"));
        }
        if (tag.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(tag.getCompound("ShoulderEntityRight"));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        tag.put("Inventory", this.inventory.serialize(new ListTag()));
        tag.putInt("SelectedItemSlot", this.inventory.selectedSlot);
        tag.putShort("SleepTimer", (short)this.sleepTimer);
        tag.putFloat("XpP", this.experienceProgress);
        tag.putInt("XpLevel", this.experienceLevel);
        tag.putInt("XpTotal", this.totalExperience);
        tag.putInt("XpSeed", this.enchantmentTableSeed);
        tag.putInt("Score", this.getScore());
        if (this.spawnPosition != null) {
            tag.putInt("SpawnX", this.spawnPosition.getX());
            tag.putInt("SpawnY", this.spawnPosition.getY());
            tag.putInt("SpawnZ", this.spawnPosition.getZ());
            tag.putBoolean("SpawnForced", this.spawnForced);
        }
        this.hungerManager.serialize(tag);
        this.abilities.serialize(tag);
        tag.put("EnderItems", this.enderChestInventory.getTags());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            tag.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }
        if (!this.getShoulderEntityRight().isEmpty()) {
            tag.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.abilities.invulnerable && !source.isOutOfWorld()) {
            return false;
        }
        this.despawnCounter = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        this.dropShoulderEntities();
        if (source.isScaledWithDifficulty()) {
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                amount = 0.0f;
            }
            if (this.world.getDifficulty() == Difficulty.EASY) {
                amount = Math.min(amount / 2.0f + 1.0f, amount);
            }
            if (this.world.getDifficulty() == Difficulty.HARD) {
                amount = amount * 3.0f / 2.0f;
            }
        }
        if (amount == 0.0f) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    protected void takeShieldHit(LivingEntity attacker) {
        super.takeShieldHit(attacker);
        if (attacker.getMainHandStack().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    public boolean shouldDamagePlayer(PlayerEntity player) {
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        AbstractTeam abstractTeam2 = player.getScoreboardTeam();
        if (abstractTeam == null) {
            return true;
        }
        if (!abstractTeam.isEqual(abstractTeam2)) {
            return true;
        }
        return abstractTeam.isFriendlyFireAllowed();
    }

    @Override
    protected void damageArmor(float amount) {
        this.inventory.damageArmor(amount);
    }

    @Override
    protected void damageShield(float amount) {
        if (amount >= 3.0f && this.activeItemStack.getItem() == Items.SHIELD) {
            int i = 1 + MathHelper.floor(amount);
            Hand hand = this.getActiveHand();
            this.activeItemStack.damage(i, this, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            if (this.activeItemStack.isEmpty()) {
                if (hand == Hand.MAIN_HAND) {
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.activeItemStack = ItemStack.EMPTY;
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f);
            }
        }
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        amount = this.applyArmorToDamage(source, amount);
        float f = amount = this.applyEnchantmentsToDamage(source, amount);
        amount = Math.max(amount - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - amount));
        float g = f - amount;
        if (g > 0.0f && g < 3.4028235E37f) {
            this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0f));
        }
        if (amount == 0.0f) {
            return;
        }
        this.addExhaustion(source.getExhaustion());
        float h = this.getHealth();
        this.setHealth(this.getHealth() - amount);
        this.getDamageTracker().onDamage(source, h, amount);
        if (amount < 3.4028235E37f) {
            this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(amount * 10.0f));
        }
    }

    public void openEditSignScreen(SignBlockEntity signBlockEntity) {
    }

    public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
    }

    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
    }

    public void openStructureBlockScreen(StructureBlockBlockEntity structureBlockBlockEntity) {
    }

    public void openJigsawScreen(JigsawBlockEntity jigsawBlockEntity) {
    }

    public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
    }

    public OptionalInt openContainer(@Nullable NameableContainerFactory nameableContainerFactory) {
        return OptionalInt.empty();
    }

    public void sendTradeOffers(int syncId, TraderOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
    }

    public void openEditBookScreen(ItemStack book, Hand hand) {
    }

    public ActionResult interact(Entity entity, Hand hand) {
        ItemStack itemStack2;
        if (this.isSpectator()) {
            if (entity instanceof NameableContainerFactory) {
                this.openContainer((NameableContainerFactory)((Object)entity));
            }
            return ActionResult.PASS;
        }
        ItemStack itemStack = this.getStackInHand(hand);
        ItemStack itemStack3 = itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
        if (entity.interact(this, hand)) {
            if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getCount() < itemStack2.getCount()) {
                itemStack.setCount(itemStack2.getCount());
            }
            return ActionResult.SUCCESS;
        }
        if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
            if (this.abilities.creativeMode) {
                itemStack = itemStack2;
            }
            if (itemStack.useOnEntity(this, (LivingEntity)entity, hand)) {
                if (itemStack.isEmpty() && !this.abilities.creativeMode) {
                    this.setStackInHand(hand, ItemStack.EMPTY);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public double getHeightOffset() {
        return -0.35;
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        this.ridingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    public void attack(Entity target) {
        if (!target.isAttackable()) {
            return;
        }
        if (target.handleAttack(this)) {
            return;
        }
        float f = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
        float g = target instanceof LivingEntity ? EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup()) : EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
        float h = this.getAttackCooldownProgress(0.5f);
        g *= h;
        this.resetLastAttackedTicks();
        if ((f *= 0.2f + h * h * 0.8f) > 0.0f || g > 0.0f) {
            ItemStack itemStack;
            boolean bl = h > 0.9f;
            boolean bl2 = false;
            int i = 0;
            i += EnchantmentHelper.getKnockback(this);
            if (this.isSprinting() && bl) {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0f, 1.0f);
                ++i;
                bl2 = true;
            }
            boolean bl3 = bl && this.fallDistance > 0.0f && !this.onGround && !this.isClimbing() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && !this.hasVehicle() && target instanceof LivingEntity;
            boolean bl4 = bl3 = bl3 && !this.isSprinting();
            if (bl3) {
                f *= 1.5f;
            }
            f += g;
            boolean bl42 = false;
            double d = this.horizontalSpeed - this.prevHorizontalSpeed;
            if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed() && (itemStack = this.getStackInHand(Hand.MAIN_HAND)).getItem() instanceof SwordItem) {
                bl42 = true;
            }
            float j = 0.0f;
            boolean bl5 = false;
            int k = EnchantmentHelper.getFireAspect(this);
            if (target instanceof LivingEntity) {
                j = ((LivingEntity)target).getHealth();
                if (k > 0 && !target.isOnFire()) {
                    bl5 = true;
                    target.setOnFireFor(1);
                }
            }
            Vec3d vec3d = target.getVelocity();
            boolean bl6 = target.damage(DamageSource.player(this), f);
            if (bl6) {
                if (i > 0) {
                    if (target instanceof LivingEntity) {
                        ((LivingEntity)target).takeKnockback(this, (float)i * 0.5f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                    } else {
                        target.addVelocity(-MathHelper.sin(this.yaw * ((float)Math.PI / 180)) * (float)i * 0.5f, 0.1, MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * (float)i * 0.5f);
                    }
                    this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
                    this.setSprinting(false);
                }
                if (bl42) {
                    float l = 1.0f + EnchantmentHelper.getSweepingMultiplier(this) * f;
                    List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                    for (LivingEntity livingEntity : list) {
                        if (livingEntity == this || livingEntity == target || this.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(this.squaredDistanceTo(livingEntity) < 9.0)) continue;
                        livingEntity.takeKnockback(this, 0.4f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                        livingEntity.damage(DamageSource.player(this), l);
                    }
                    this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0f, 1.0f);
                    this.method_7263();
                }
                if (target instanceof ServerPlayerEntity && target.velocityModified) {
                    ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.velocityModified = false;
                    target.setVelocity(vec3d);
                }
                if (bl3) {
                    this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0f, 1.0f);
                    this.addCritParticles(target);
                }
                if (!bl3 && !bl42) {
                    if (bl) {
                        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 1.0f);
                    } else {
                        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0f, 1.0f);
                    }
                }
                if (g > 0.0f) {
                    this.addEnchantedHitParticles(target);
                }
                this.onAttacking(target);
                if (target instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged((LivingEntity)target, this);
                }
                EnchantmentHelper.onTargetDamaged(this, target);
                ItemStack itemStack2 = this.getMainHandStack();
                Entity entity = target;
                if (target instanceof EnderDragonPart) {
                    entity = ((EnderDragonPart)target).owner;
                }
                if (!this.world.isClient && !itemStack2.isEmpty() && entity instanceof LivingEntity) {
                    itemStack2.postHit((LivingEntity)entity, this);
                    if (itemStack2.isEmpty()) {
                        this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (target instanceof LivingEntity) {
                    float m = j - ((LivingEntity)target).getHealth();
                    this.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0f));
                    if (k > 0) {
                        target.setOnFireFor(k * 4);
                    }
                    if (this.world instanceof ServerWorld && m > 2.0f) {
                        int n = (int)((double)m * 0.5);
                        ((ServerWorld)this.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.x, target.y + (double)(target.getHeight() * 0.5f), target.z, n, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                this.addExhaustion(0.1f);
            } else {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0f, 1.0f);
                if (bl5) {
                    target.extinguish();
                }
            }
        }
    }

    @Override
    protected void attackLivingEntity(LivingEntity target) {
        this.attack(target);
    }

    public void disableShield(boolean sprinting) {
        float f = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
        if (sprinting) {
            f += 0.75f;
        }
        if (this.random.nextFloat() < f) {
            this.getItemCooldownManager().set(Items.SHIELD, 100);
            this.clearActiveItem();
            this.world.sendEntityStatus(this, (byte)30);
        }
    }

    public void addCritParticles(Entity target) {
    }

    public void addEnchantedHitParticles(Entity target) {
    }

    public void method_7263() {
        double d = -MathHelper.sin(this.yaw * ((float)Math.PI / 180));
        double e = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.x + d, this.y + (double)this.getHeight() * 0.5, this.z + e, 0, d, 0.0, e, 0.0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void requestRespawn() {
    }

    @Override
    public void remove() {
        super.remove();
        this.playerContainer.close(this);
        if (this.container != null) {
            this.container.close(this);
        }
    }

    public boolean isMainPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Either<SleepFailureReason, Unit> trySleep(BlockPos pos) {
        Direction direction = this.world.getBlockState(pos).get(HorizontalFacingBlock.FACING);
        if (!this.world.isClient) {
            if (this.isSleeping() || !this.isAlive()) {
                return Either.left((Object)((Object)SleepFailureReason.OTHER_PROBLEM));
            }
            if (!this.world.dimension.hasVisibleSky()) {
                return Either.left((Object)((Object)SleepFailureReason.NOT_POSSIBLE_HERE));
            }
            if (this.world.isDay()) {
                return Either.left((Object)((Object)SleepFailureReason.NOT_POSSIBLE_NOW));
            }
            if (!this.isWithinSleepingRange(pos, direction)) {
                return Either.left((Object)((Object)SleepFailureReason.TOO_FAR_AWAY));
            }
            if (this.isBedObstructed(pos, direction)) {
                return Either.left((Object)((Object)SleepFailureReason.OBSTRUCTED));
            }
            if (!this.isCreative()) {
                double d = 8.0;
                double e = 5.0;
                List<HostileEntity> list = this.world.getEntities(HostileEntity.class, new Box((double)pos.getX() - 8.0, (double)pos.getY() - 5.0, (double)pos.getZ() - 8.0, (double)pos.getX() + 8.0, (double)pos.getY() + 5.0, (double)pos.getZ() + 8.0), hostileEntity -> hostileEntity.isAngryAt(this));
                if (!list.isEmpty()) {
                    return Either.left((Object)((Object)SleepFailureReason.NOT_SAFE));
                }
            }
        }
        this.sleep(pos);
        this.sleepTimer = 0;
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).updatePlayersSleeping();
        }
        return Either.right((Object)((Object)Unit.INSTANCE));
    }

    @Override
    public void sleep(BlockPos pos) {
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        super.sleep(pos);
    }

    private boolean isWithinSleepingRange(BlockPos sleepPos, Direction direction) {
        if (Math.abs(this.x - (double)sleepPos.getX()) <= 3.0 && Math.abs(this.y - (double)sleepPos.getY()) <= 2.0 && Math.abs(this.z - (double)sleepPos.getZ()) <= 3.0) {
            return true;
        }
        BlockPos blockPos = sleepPos.offset(direction.getOpposite());
        return Math.abs(this.x - (double)blockPos.getX()) <= 3.0 && Math.abs(this.y - (double)blockPos.getY()) <= 2.0 && Math.abs(this.z - (double)blockPos.getZ()) <= 3.0;
    }

    private boolean isBedObstructed(BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.up();
        return !this.doesNotSuffocate(blockPos) || !this.doesNotSuffocate(blockPos.offset(direction.getOpposite()));
    }

    public void wakeUp(boolean bl, boolean bl2, boolean setSpawn) {
        Optional<BlockPos> optional = this.getSleepingPosition();
        super.wakeUp();
        if (this.world instanceof ServerWorld && bl2) {
            ((ServerWorld)this.world).updatePlayersSleeping();
        }
        int n = this.sleepTimer = bl ? 0 : 100;
        if (setSpawn) {
            optional.ifPresent(blockPos -> this.setPlayerSpawn((BlockPos)blockPos, false));
        }
    }

    @Override
    public void wakeUp() {
        this.wakeUp(true, true, false);
    }

    public static Optional<Vec3d> method_7288(CollisionView collisionView, BlockPos blockPos, boolean bl) {
        Block block = collisionView.getBlockState(blockPos).getBlock();
        if (!(block instanceof BedBlock)) {
            if (!bl) {
                return Optional.empty();
            }
            boolean bl2 = block.canMobSpawnInside();
            boolean bl3 = collisionView.getBlockState(blockPos.up()).getBlock().canMobSpawnInside();
            if (bl2 && bl3) {
                return Optional.of(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.1, (double)blockPos.getZ() + 0.5));
            }
            return Optional.empty();
        }
        return BedBlock.findWakeUpPosition(EntityType.PLAYER, collisionView, blockPos, 0);
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepTimer >= 100;
    }

    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public void addChatMessage(Text message, boolean bl) {
    }

    public BlockPos getSpawnPosition() {
        return this.spawnPosition;
    }

    public boolean isSpawnForced() {
        return this.spawnForced;
    }

    public void setPlayerSpawn(BlockPos pos, boolean bl) {
        if (pos != null) {
            this.spawnPosition = pos;
            this.spawnForced = bl;
        } else {
            this.spawnPosition = null;
            this.spawnForced = false;
        }
    }

    public void incrementStat(Identifier stat) {
        this.incrementStat(Stats.CUSTOM.getOrCreateStat(stat));
    }

    public void increaseStat(Identifier stat, int amount) {
        this.increaseStat(Stats.CUSTOM.getOrCreateStat(stat), amount);
    }

    public void incrementStat(Stat<?> stat) {
        this.increaseStat(stat, 1);
    }

    public void increaseStat(Stat<?> stat, int amount) {
    }

    public void resetStat(Stat<?> stat) {
    }

    public int unlockRecipes(Collection<Recipe<?>> recipes) {
        return 0;
    }

    public void unlockRecipes(Identifier[] ids) {
    }

    public int lockRecipes(Collection<Recipe<?>> recipes) {
        return 0;
    }

    @Override
    public void jump() {
        super.jump();
        this.incrementStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.addExhaustion(0.2f);
        } else {
            this.addExhaustion(0.05f);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        double g;
        double d = this.x;
        double e = this.y;
        double f = this.z;
        if (this.isSwimming() && !this.hasVehicle()) {
            double h;
            g = this.getRotationVector().y;
            double d2 = h = g < -0.2 ? 0.085 : 0.06;
            if (g <= 0.0 || this.jumping || !this.world.getBlockState(new BlockPos(this.x, this.y + 1.0 - 0.1, this.z)).getFluidState().isEmpty()) {
                Vec3d vec3d = this.getVelocity();
                this.setVelocity(vec3d.add(0.0, (g - vec3d.y) * h, 0.0));
            }
        }
        if (this.abilities.flying && !this.hasVehicle()) {
            g = this.getVelocity().y;
            float i = this.field_6281;
            this.field_6281 = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
            super.travel(movementInput);
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, g * 0.6, vec3d2.z);
            this.field_6281 = i;
            this.fallDistance = 0.0f;
            this.setFlag(7, false);
        } else {
            super.travel(movementInput);
        }
        this.method_7282(this.x - d, this.y - e, this.z - f);
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean doesNotSuffocate(BlockPos pos) {
        return !this.world.getBlockState(pos).canSuffocate(this.world, pos);
    }

    @Override
    public float getMovementSpeed() {
        return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
    }

    public void method_7282(double d, double e, double f) {
        if (this.hasVehicle()) {
            return;
        }
        if (this.isSwimming()) {
            int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.SWIM_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isSubmergedIn(FluidTags.WATER, true)) {
            int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isTouchingWater()) {
            int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isClimbing()) {
            if (e > 0.0) {
                this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(e * 100.0));
            }
        } else if (this.onGround) {
            int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0f);
            if (i > 0) {
                if (this.isSprinting()) {
                    this.increaseStat(Stats.SPRINT_ONE_CM, i);
                    this.addExhaustion(0.1f * (float)i * 0.01f);
                } else if (this.isSneaking()) {
                    this.increaseStat(Stats.CROUCH_ONE_CM, i);
                    this.addExhaustion(0.0f * (float)i * 0.01f);
                } else {
                    this.increaseStat(Stats.WALK_ONE_CM, i);
                    this.addExhaustion(0.0f * (float)i * 0.01f);
                }
            }
        } else if (this.isFallFlying()) {
            int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f);
            this.increaseStat(Stats.AVIATE_ONE_CM, i);
        } else {
            int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0f);
            if (i > 25) {
                this.increaseStat(Stats.FLY_ONE_CM, i);
            }
        }
    }

    private void method_7260(double d, double e, double f) {
        int i;
        if (this.hasVehicle() && (i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f)) > 0) {
            if (this.getVehicle() instanceof AbstractMinecartEntity) {
                this.increaseStat(Stats.MINECART_ONE_CM, i);
            } else if (this.getVehicle() instanceof BoatEntity) {
                this.increaseStat(Stats.BOAT_ONE_CM, i);
            } else if (this.getVehicle() instanceof PigEntity) {
                this.increaseStat(Stats.PIG_ONE_CM, i);
            } else if (this.getVehicle() instanceof HorseBaseEntity) {
                this.increaseStat(Stats.HORSE_ONE_CM, i);
            }
        }
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
        if (this.abilities.allowFlying) {
            return;
        }
        if (fallDistance >= 2.0f) {
            this.increaseStat(Stats.FALL_ONE_CM, (int)Math.round((double)fallDistance * 100.0));
        }
        super.handleFallDamage(fallDistance, damageMultiplier);
    }

    @Override
    protected void onSwimmingStart() {
        if (!this.isSpectator()) {
            super.onSwimmingStart();
        }
    }

    @Override
    protected SoundEvent getFallSound(int distance) {
        if (distance > 4) {
            return SoundEvents.ENTITY_PLAYER_BIG_FALL;
        }
        return SoundEvents.ENTITY_PLAYER_SMALL_FALL;
    }

    @Override
    public void onKilledOther(LivingEntity other) {
        this.incrementStat(Stats.KILLED.getOrCreateStat(other.getType()));
    }

    @Override
    public void slowMovement(BlockState state, Vec3d multiplier) {
        if (!this.abilities.flying) {
            super.slowMovement(state, multiplier);
        }
    }

    public void addExperience(int experience) {
        this.addScore(experience);
        this.experienceProgress += (float)experience / (float)this.getNextLevelExperience();
        this.totalExperience = MathHelper.clamp(this.totalExperience + experience, 0, Integer.MAX_VALUE);
        while (this.experienceProgress < 0.0f) {
            float f = this.experienceProgress * (float)this.getNextLevelExperience();
            if (this.experienceLevel > 0) {
                this.addExperienceLevels(-1);
                this.experienceProgress = 1.0f + f / (float)this.getNextLevelExperience();
                continue;
            }
            this.addExperienceLevels(-1);
            this.experienceProgress = 0.0f;
        }
        while (this.experienceProgress >= 1.0f) {
            this.experienceProgress = (this.experienceProgress - 1.0f) * (float)this.getNextLevelExperience();
            this.addExperienceLevels(1);
            this.experienceProgress /= (float)this.getNextLevelExperience();
        }
    }

    public int getEnchantmentTableSeed() {
        return this.enchantmentTableSeed;
    }

    public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
        this.experienceLevel -= experienceLevels;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        this.enchantmentTableSeed = this.random.nextInt();
    }

    public void addExperienceLevels(int levels) {
        this.experienceLevel += levels;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        if (levels > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.age - 100.0f) {
            float f = this.experienceLevel > 30 ? 1.0f : (float)this.experienceLevel / 30.0f;
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75f, 1.0f);
            this.lastPlayedLevelUpSoundTime = this.age;
        }
    }

    public int getNextLevelExperience() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        }
        if (this.experienceLevel >= 15) {
            return 37 + (this.experienceLevel - 15) * 5;
        }
        return 7 + this.experienceLevel * 2;
    }

    public void addExhaustion(float exhaustion) {
        if (this.abilities.invulnerable) {
            return;
        }
        if (!this.world.isClient) {
            this.hungerManager.addExhaustion(exhaustion);
        }
    }

    public HungerManager getHungerManager() {
        return this.hungerManager;
    }

    public boolean canConsume(boolean ignoreHunger) {
        return !this.abilities.invulnerable && (ignoreHunger || this.hungerManager.isNotFull());
    }

    public boolean canFoodHeal() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaximumHealth();
    }

    public boolean canModifyWorld() {
        return this.abilities.allowModifyWorld;
    }

    public boolean canPlaceOn(BlockPos pos, Direction facing, ItemStack stack) {
        if (this.abilities.allowModifyWorld) {
            return true;
        }
        BlockPos blockPos = pos.offset(facing.getOpposite());
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos, false);
        return stack.canPlaceOn(this.world.getTagManager(), cachedBlockPosition);
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || this.isSpectator()) {
            return 0;
        }
        int i = this.experienceLevel * 7;
        if (i > 100) {
            return 100;
        }
        return i;
    }

    @Override
    protected boolean shouldAlwaysDropXp() {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return true;
    }

    @Override
    protected boolean canClimb() {
        return !this.abilities.flying;
    }

    public void sendAbilitiesUpdate() {
    }

    public void setGameMode(GameMode gameMode) {
    }

    @Override
    public Text getName() {
        return new LiteralText(this.gameProfile.getName());
    }

    public EnderChestInventory getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.inventory.getMainHandStack();
        }
        if (slot == EquipmentSlot.OFFHAND) {
            return this.inventory.offHand.get(0);
        }
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get(slot.getEntitySlotId());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            this.onEquipStack(stack);
            this.inventory.main.set(this.inventory.selectedSlot, stack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            this.onEquipStack(stack);
            this.inventory.offHand.set(0, stack);
        } else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipStack(stack);
            this.inventory.armor.set(slot.getEntitySlotId(), stack);
        }
    }

    public boolean giveItemStack(ItemStack stack) {
        this.onEquipStack(stack);
        return this.inventory.insertStack(stack);
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return Lists.newArrayList((Object[])new ItemStack[]{this.getMainHandStack(), this.getOffHandStack()});
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    public boolean addShoulderEntity(CompoundTag tag) {
        if (this.hasVehicle() || !this.onGround || this.isTouchingWater()) {
            return false;
        }
        if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(tag);
            this.field_19428 = this.world.getTime();
            return true;
        }
        if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(tag);
            this.field_19428 = this.world.getTime();
            return true;
        }
        return false;
    }

    protected void dropShoulderEntities() {
        if (this.field_19428 + 20L < this.world.getTime()) {
            this.method_7296(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.method_7296(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }

    private void method_7296(CompoundTag compoundTag) {
        if (!this.world.isClient && !compoundTag.isEmpty()) {
            EntityType.getEntityFromTag(compoundTag, this.world).ifPresent(entity -> {
                if (entity instanceof TameableEntity) {
                    ((TameableEntity)entity).setOwnerUuid(this.uuid);
                }
                entity.updatePosition(this.x, this.y + (double)0.7f, this.z);
                ((ServerWorld)this.world).method_18768((Entity)entity);
            });
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isInvisibleTo(PlayerEntity player) {
        if (!this.isInvisible()) {
            return false;
        }
        if (player.isSpectator()) {
            return false;
        }
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        return abstractTeam == null || player == null || player.getScoreboardTeam() != abstractTeam || !abstractTeam.shouldShowFriendlyInvisibles();
    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean canFly() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public Text getDisplayName() {
        Text text = Team.modifyText(this.getScoreboardTeam(), this.getName());
        return this.addTellClickEvent(text);
    }

    public Text getNameAndUuid() {
        return new LiteralText("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
    }

    private Text addTellClickEvent(Text component) {
        String string = this.getGameProfile().getName();
        return component.styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " ")).setHoverEvent(this.getHoverEvent()).setInsertion(string));
    }

    @Override
    public String getEntityName() {
        return this.getGameProfile().getName();
    }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        switch (pose) {
            case SWIMMING: 
            case FALL_FLYING: 
            case SPIN_ATTACK: {
                return 0.4f;
            }
            case SNEAKING: {
                return 1.27f;
            }
        }
        return 1.62f;
    }

    @Override
    public void setAbsorptionAmount(float amount) {
        if (amount < 0.0f) {
            amount = 0.0f;
        }
        this.getDataTracker().set(ABSORPTION_AMOUNT, Float.valueOf(amount));
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getDataTracker().get(ABSORPTION_AMOUNT).floatValue();
    }

    public static UUID getUuidFromProfile(GameProfile profile) {
        UUID uUID = profile.getId();
        if (uUID == null) {
            uUID = PlayerEntity.getOfflinePlayerUuid(profile.getName());
        }
        return uUID;
    }

    public static UUID getOfflinePlayerUuid(String nickname) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return (this.getDataTracker().get(PLAYER_MODEL_PARTS) & modelPart.getBitFlag()) == modelPart.getBitFlag();
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (slot >= 0 && slot < this.inventory.main.size()) {
            this.inventory.setInvStack(slot, item);
            return true;
        }
        EquipmentSlot equipmentSlot = slot == 100 + EquipmentSlot.HEAD.getEntitySlotId() ? EquipmentSlot.HEAD : (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId() ? EquipmentSlot.CHEST : (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId() ? EquipmentSlot.LEGS : (slot == 100 + EquipmentSlot.FEET.getEntitySlotId() ? EquipmentSlot.FEET : null)));
        if (slot == 98) {
            this.equipStack(EquipmentSlot.MAINHAND, item);
            return true;
        }
        if (slot == 99) {
            this.equipStack(EquipmentSlot.OFFHAND, item);
            return true;
        }
        if (equipmentSlot != null) {
            if (!item.isEmpty() && (item.getItem() instanceof ArmorItem || item.getItem() instanceof ElytraItem ? MobEntity.getPreferredEquipmentSlot(item) != equipmentSlot : equipmentSlot != EquipmentSlot.HEAD)) {
                return false;
            }
            this.inventory.setInvStack(equipmentSlot.getEntitySlotId() + this.inventory.main.size(), item);
            return true;
        }
        int i = slot - 200;
        if (i >= 0 && i < this.enderChestInventory.getInvSize()) {
            this.enderChestInventory.setInvStack(i, item);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean getReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    @Override
    public Arm getMainArm() {
        return this.dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
    }

    public void setMainArm(Arm arm) {
        this.dataTracker.set(MAIN_ARM, (byte)(arm != Arm.LEFT ? 1 : 0));
    }

    public CompoundTag getShoulderEntityLeft() {
        return this.dataTracker.get(LEFT_SHOULDER_ENTITY);
    }

    protected void setShoulderEntityLeft(CompoundTag entityTag) {
        this.dataTracker.set(LEFT_SHOULDER_ENTITY, entityTag);
    }

    public CompoundTag getShoulderEntityRight() {
        return this.dataTracker.get(RIGHT_SHOULDER_ENTITY);
    }

    protected void setShoulderEntityRight(CompoundTag entityTag) {
        this.dataTracker.set(RIGHT_SHOULDER_ENTITY, entityTag);
    }

    public float getAttackCooldownProgressPerTick() {
        return (float)(1.0 / this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() * 20.0);
    }

    public float getAttackCooldownProgress(float baseTime) {
        return MathHelper.clamp(((float)this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0f, 1.0f);
    }

    public void resetLastAttackedTicks() {
        this.lastAttackedTicks = 0;
    }

    public ItemCooldownManager getItemCooldownManager() {
        return this.itemCooldownManager;
    }

    public float getLuck() {
        return (float)this.getAttributeInstance(EntityAttributes.LUCK).getValue();
    }

    public boolean isCreativeLevelTwoOp() {
        return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(equipmentSlot).isEmpty();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return POSE_DIMENSIONS.getOrDefault((Object)pose, STANDING_DIMENSIONS);
    }

    @Override
    public ItemStack getArrowType(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof RangedWeaponItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> predicate = ((RangedWeaponItem)itemStack.getItem()).getHeldProjectiles();
        ItemStack itemStack2 = RangedWeaponItem.getHeldProjectile(this, predicate);
        if (!itemStack2.isEmpty()) {
            return itemStack2;
        }
        predicate = ((RangedWeaponItem)itemStack.getItem()).getProjectiles();
        for (int i = 0; i < this.inventory.getInvSize(); ++i) {
            ItemStack itemStack3 = this.inventory.getInvStack(i);
            if (!predicate.test(itemStack3)) continue;
            return itemStack3;
        }
        return this.abilities.creativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack eatFood(World world, ItemStack stack) {
        this.getHungerManager().eat(stack.getItem(), stack);
        this.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
        if (this instanceof ServerPlayerEntity) {
            Criterions.CONSUME_ITEM.trigger((ServerPlayerEntity)this, stack);
        }
        return super.eatFood(world, stack);
    }

    public static enum SleepFailureReason {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(new TranslatableText("block.minecraft.bed.no_sleep", new Object[0])),
        TOO_FAR_AWAY(new TranslatableText("block.minecraft.bed.too_far_away", new Object[0])),
        OBSTRUCTED(new TranslatableText("block.minecraft.bed.obstructed", new Object[0])),
        OTHER_PROBLEM,
        NOT_SAFE(new TranslatableText("block.minecraft.bed.not_safe", new Object[0]));

        @Nullable
        private final Text text;

        private SleepFailureReason() {
            this.text = null;
        }

        private SleepFailureReason(Text text) {
            this.text = text;
        }

        @Nullable
        public Text toText() {
            return this.text;
        }
    }
}
