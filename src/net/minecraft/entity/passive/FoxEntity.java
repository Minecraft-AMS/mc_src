/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.jetbrains.annotations.Nullable;

public class FoxEntity
extends AnimalEntity {
    private static final TrackedData<Integer> TYPE = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> FOX_FLAGS = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Optional<UUID>> OTHER_TRUSTED = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive();
    private static final Predicate<Entity> JUST_ATTACKED_SOMETHING_FILTER = entity -> {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.getAttacking() != null && livingEntity.getLastAttackTime() < livingEntity.age + 600;
        }
        return false;
    };
    private static final Predicate<Entity> CHICKEN_AND_RABBIT_FILTER = entity -> entity instanceof ChickenEntity || entity instanceof RabbitEntity;
    private static final Predicate<Entity> NOTICEABLE_PLAYER_FILTER = entity -> !entity.isSneaking() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test((Entity)entity);
    private Goal followChickenAndRabbitGoal;
    private Goal followBabyTurtleGoal;
    private Goal followFishGoal;
    private float headRollProgress;
    private float lastHeadRollProgress;
    private float extraRollingHeight;
    private float lastExtraRollingHeight;
    private int eatingTime;

    public FoxEntity(EntityType<? extends FoxEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.lookControl = new FoxLookControl();
        this.moveControl = new FoxMoveControl();
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0f);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(OTHER_TRUSTED, Optional.empty());
        this.dataTracker.startTracking(TYPE, 0);
        this.dataTracker.startTracking(FOX_FLAGS, (byte)0);
    }

    @Override
    protected void initGoals() {
        this.followChickenAndRabbitGoal = new FollowTargetGoal<AnimalEntity>(this, AnimalEntity.class, 10, false, false, livingEntity -> livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity);
        this.followBabyTurtleGoal = new FollowTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
        this.followFishGoal = new FollowTargetGoal<FishEntity>(this, FishEntity.class, 20, false, false, livingEntity -> livingEntity instanceof SchoolingFishEntity);
        this.goalSelector.add(0, new FoxSwimGoal());
        this.goalSelector.add(1, new StopWanderingGoal());
        this.goalSelector.add(2, new EscapeWhenNotAggresiveGoal(2.2));
        this.goalSelector.add(3, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 1.6, 1.4, livingEntity -> NOTICEABLE_PLAYER_FILTER.test((Entity)livingEntity) && !this.canTrust(livingEntity.getUuid()) && !this.isAggressive()));
        this.goalSelector.add(3, new FleeEntityGoal<WolfEntity>(this, WolfEntity.class, 8.0f, 1.6, 1.4, livingEntity -> !((WolfEntity)livingEntity).isTamed() && !this.isAggressive()));
        this.goalSelector.add(4, new MoveToHuntGoal());
        this.goalSelector.add(5, new JumpChasingGoal());
        this.goalSelector.add(5, new MateGoal(1.0));
        this.goalSelector.add(5, new AvoidDaylightGoal(1.25));
        this.goalSelector.add(6, new AttackGoal((double)1.2f, true));
        this.goalSelector.add(6, new DelayedCalmDownGoal());
        this.goalSelector.add(7, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(8, new GoToVillageGoal(32, 200));
        this.goalSelector.add(9, new EatSweetBerriesGoal((double)1.2f, 12, 2));
        this.goalSelector.add(9, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(10, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(10, new PickupItemGoal());
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
        this.goalSelector.add(12, new SitDownAndLookAroundGoal());
        this.targetSelector.add(3, new DefendFriendGoal(LivingEntity.class, false, false, livingEntity -> JUST_ATTACKED_SOMETHING_FILTER.test((Entity)livingEntity) && !this.canTrust(livingEntity.getUuid())));
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.ENTITY_FOX_EAT;
    }

    @Override
    public void tickMovement() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            LivingEntity livingEntity;
            ++this.eatingTime;
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (this.canEat(itemStack)) {
                if (this.eatingTime > 600) {
                    ItemStack itemStack2 = itemStack.finishUsing(this.world, this);
                    if (!itemStack2.isEmpty()) {
                        this.equipStack(EquipmentSlot.MAINHAND, itemStack2);
                    }
                    this.eatingTime = 0;
                } else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1f) {
                    this.playSound(this.getEatSound(itemStack), 1.0f, 1.0f);
                    this.world.sendEntityStatus(this, (byte)45);
                }
            }
            if ((livingEntity = this.getTarget()) == null || !livingEntity.isAlive()) {
                this.setCrouching(false);
                this.setRollingHead(false);
            }
        }
        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0f;
            this.forwardSpeed = 0.0f;
            this.field_6267 = 0.0f;
        }
        super.tickMovement();
        if (this.isAggressive() && this.random.nextFloat() < 0.05f) {
            this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0f, 1.0f);
        }
    }

    @Override
    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f;
    }

    private boolean canEat(ItemStack stack) {
        return stack.getItem().isFood() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        if (this.random.nextFloat() < 0.2f) {
            float f = this.random.nextFloat();
            ItemStack itemStack = f < 0.05f ? new ItemStack(Items.EMERALD) : (f < 0.2f ? new ItemStack(Items.EGG) : (f < 0.4f ? (this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE)) : (f < 0.6f ? new ItemStack(Items.WHEAT) : (f < 0.8f ? new ItemStack(Items.LEATHER) : new ItemStack(Items.FEATHER)))));
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 45) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0).rotateX(-this.pitch * ((float)Math.PI / 180)).rotateY(-this.yaw * ((float)Math.PI / 180));
                    this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack), this.x + this.getRotationVector().x / 2.0, this.y, this.z + this.getRotationVector().z / 2.0, vec3d.x, vec3d.y + 0.05, vec3d.z);
                }
            }
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
        this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    @Override
    public FoxEntity createChild(PassiveEntity passiveEntity) {
        FoxEntity foxEntity = EntityType.FOX.create(this.world);
        foxEntity.setType(this.random.nextBoolean() ? this.getFoxType() : ((FoxEntity)passiveEntity).getFoxType());
        return foxEntity;
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        Biome biome = world.getBiome(new BlockPos(this));
        Type type = Type.fromBiome(biome);
        boolean bl = false;
        if (entityData instanceof FoxData) {
            type = ((FoxData)entityData).type;
            if (((FoxData)entityData).uses >= 2) {
                bl = true;
            } else {
                ++((FoxData)entityData).uses;
            }
        } else {
            entityData = new FoxData(type);
            ++((FoxData)entityData).uses;
        }
        this.setType(type);
        if (bl) {
            this.setBreedingAge(-24000);
        }
        this.addTypeSpecificGoals();
        this.initEquipment(difficulty);
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    private void addTypeSpecificGoals() {
        if (this.getFoxType() == Type.RED) {
            this.targetSelector.add(4, this.followChickenAndRabbitGoal);
            this.targetSelector.add(4, this.followBabyTurtleGoal);
            this.targetSelector.add(6, this.followFishGoal);
        } else {
            this.targetSelector.add(4, this.followFishGoal);
            this.targetSelector.add(6, this.followChickenAndRabbitGoal);
            this.targetSelector.add(6, this.followBabyTurtleGoal);
        }
    }

    @Override
    protected void eat(PlayerEntity player, ItemStack stack) {
        if (this.isBreedingItem(stack)) {
            this.playSound(this.getEatSound(stack), 1.0f, 1.0f);
        }
        super.eat(player, stack);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        if (this.isBaby()) {
            return dimensions.height * 0.85f;
        }
        return 0.4f;
    }

    public Type getFoxType() {
        return Type.fromId(this.dataTracker.get(TYPE));
    }

    private void setType(Type type) {
        this.dataTracker.set(TYPE, type.getId());
    }

    private List<UUID> getTrustedUuids() {
        ArrayList list = Lists.newArrayList();
        list.add(this.dataTracker.get(OWNER).orElse(null));
        list.add(this.dataTracker.get(OTHER_TRUSTED).orElse(null));
        return list;
    }

    private void addTrustedUuid(@Nullable UUID uuid) {
        if (this.dataTracker.get(OWNER).isPresent()) {
            this.dataTracker.set(OTHER_TRUSTED, Optional.ofNullable(uuid));
        } else {
            this.dataTracker.set(OWNER, Optional.ofNullable(uuid));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        List<UUID> list = this.getTrustedUuids();
        ListTag listTag = new ListTag();
        for (UUID uUID : list) {
            if (uUID == null) continue;
            listTag.add(NbtHelper.fromUuid(uUID));
        }
        tag.put("TrustedUUIDs", listTag);
        tag.putBoolean("Sleeping", this.isSleeping());
        tag.putString("Type", this.getFoxType().getKey());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putBoolean("Crouching", this.isCrouching());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        ListTag listTag = tag.getList("TrustedUUIDs", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            this.addTrustedUuid(NbtHelper.toUuid(listTag.getCompound(i)));
        }
        this.setSleeping(tag.getBoolean("Sleeping"));
        this.setType(Type.byName(tag.getString("Type")));
        this.setSitting(tag.getBoolean("Sitting"));
        this.setCrouching(tag.getBoolean("Crouching"));
        this.addTypeSpecificGoals();
    }

    public boolean isSitting() {
        return this.getFoxFlag(1);
    }

    public void setSitting(boolean sitting) {
        this.setFoxFlag(1, sitting);
    }

    public boolean isWalking() {
        return this.getFoxFlag(64);
    }

    private void setWalking(boolean walking) {
        this.setFoxFlag(64, walking);
    }

    private boolean isAggressive() {
        return this.getFoxFlag(128);
    }

    private void setAggressive(boolean aggressive) {
        this.setFoxFlag(128, aggressive);
    }

    @Override
    public boolean isSleeping() {
        return this.getFoxFlag(32);
    }

    private void setSleeping(boolean sleeping) {
        this.setFoxFlag(32, sleeping);
    }

    private void setFoxFlag(int mask, boolean value) {
        if (value) {
            this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) | mask));
        } else {
            this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) & ~mask));
        }
    }

    private boolean getFoxFlag(int bitmask) {
        return (this.dataTracker.get(FOX_FLAGS) & bitmask) != 0;
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        if (!this.getEquippedStack(equipmentSlot).isEmpty()) {
            return false;
        }
        return equipmentSlot == EquipmentSlot.MAINHAND && super.canPickUp(stack);
    }

    @Override
    protected boolean canPickupItem(ItemStack stack) {
        Item item = stack.getItem();
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        return itemStack.isEmpty() || this.eatingTime > 0 && item.isFood() && !itemStack.getItem().isFood();
    }

    private void spit(ItemStack itemStack) {
        if (itemStack.isEmpty() || this.world.isClient) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(this.world, this.x + this.getRotationVector().x, this.y + 1.0, this.z + this.getRotationVector().z, itemStack);
        itemEntity.setPickupDelay(40);
        itemEntity.setThrower(this.getUuid());
        this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0f, 1.0f);
        this.world.spawnEntity(itemEntity);
    }

    private void dropItem(ItemStack itemStack) {
        ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, itemStack);
        this.world.spawnEntity(itemEntity);
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.canPickupItem(itemStack)) {
            int i = itemStack.getCount();
            if (i > 1) {
                this.dropItem(itemStack.split(i - 1));
            }
            this.spit(this.getEquippedStack(EquipmentSlot.MAINHAND));
            this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(item, itemStack.getCount());
            item.remove();
            this.eatingTime = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.canMoveVoluntarily()) {
            boolean bl = this.isTouchingWater();
            if (bl || this.getTarget() != null || this.world.isThundering()) {
                this.stopSleeping();
            }
            if (bl || this.isSleeping()) {
                this.setSitting(false);
            }
            if (this.isWalking() && this.world.random.nextFloat() < 0.2f) {
                BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
                BlockState blockState = this.world.getBlockState(blockPos);
                this.world.playLevelEvent(2001, blockPos, Block.getRawIdFromState(blockState));
            }
        }
        this.lastHeadRollProgress = this.headRollProgress;
        this.headRollProgress = this.isRollingHead() ? (this.headRollProgress += (1.0f - this.headRollProgress) * 0.4f) : (this.headRollProgress += (0.0f - this.headRollProgress) * 0.4f);
        this.lastExtraRollingHeight = this.extraRollingHeight;
        if (this.isCrouching()) {
            this.extraRollingHeight += 0.2f;
            if (this.extraRollingHeight > 3.0f) {
                this.extraRollingHeight = 3.0f;
            }
        } else {
            this.extraRollingHeight = 0.0f;
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.SWEET_BERRIES;
    }

    @Override
    protected void onPlayerSpawnedChild(PlayerEntity player, PassiveEntity child) {
        ((FoxEntity)child).addTrustedUuid(player.getUuid());
    }

    public boolean isChasing() {
        return this.getFoxFlag(16);
    }

    public void setChasing(boolean bl) {
        this.setFoxFlag(16, bl);
    }

    public boolean isFullyCrouched() {
        return this.extraRollingHeight == 3.0f;
    }

    public void setCrouching(boolean bl) {
        this.setFoxFlag(4, bl);
    }

    public boolean isCrouching() {
        return this.getFoxFlag(4);
    }

    public void setRollingHead(boolean bl) {
        this.setFoxFlag(8, bl);
    }

    public boolean isRollingHead() {
        return this.getFoxFlag(8);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadRoll(float f) {
        return MathHelper.lerp(f, this.lastHeadRollProgress, this.headRollProgress) * 0.11f * (float)Math.PI;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBodyRotationHeightOffset(float f) {
        return MathHelper.lerp(f, this.lastExtraRollingHeight, this.extraRollingHeight);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.isAggressive() && target == null) {
            this.setAggressive(false);
        }
        super.setTarget(target);
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
        BlockState blockState;
        int i = MathHelper.ceil((fallDistance - 5.0f) * damageMultiplier);
        if (i <= 0) {
            return;
        }
        this.damage(DamageSource.FALL, i);
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengersDeep()) {
                entity.damage(DamageSource.FALL, i);
            }
        }
        if (!(blockState = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z))).isAir() && !this.isSilent()) {
            BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
            this.world.playSound(null, this.x, this.y, this.z, blockSoundGroup.getStepSound(), this.getSoundCategory(), blockSoundGroup.getVolume() * 0.5f, blockSoundGroup.getPitch() * 0.75f);
        }
    }

    private void stopSleeping() {
        this.setSleeping(false);
    }

    private void stopActions() {
        this.setRollingHead(false);
        this.setCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setAggressive(false);
        this.setWalking(false);
    }

    private boolean wantsToPickupItem() {
        return !this.isSleeping() && !this.isSitting() && !this.isWalking();
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent == SoundEvents.ENTITY_FOX_SCREECH) {
            this.playSound(soundEvent, 2.0f, this.getSoundPitch());
        } else {
            super.playAmbientSound();
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        List<Entity> list;
        if (this.isSleeping()) {
            return SoundEvents.ENTITY_FOX_SLEEP;
        }
        if (!this.world.isDay() && this.random.nextFloat() < 0.1f && (list = this.world.getEntities(PlayerEntity.class, this.getBoundingBox().expand(16.0, 16.0, 16.0), EntityPredicates.EXCEPT_SPECTATOR)).isEmpty()) {
            return SoundEvents.ENTITY_FOX_SCREECH;
        }
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }

    private boolean canTrust(UUID uUID) {
        return this.getTrustedUuids().contains(uUID);
    }

    @Override
    protected void drop(DamageSource source) {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty()) {
            this.dropStack(itemStack);
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        super.drop(source);
    }

    public static boolean canJumpChase(FoxEntity foxEntity, LivingEntity livingEntity) {
        double d = livingEntity.z - foxEntity.z;
        double e = livingEntity.x - foxEntity.x;
        double f = d / e;
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            double g = f == 0.0 ? 0.0 : d * (double)((float)j / 6.0f);
            double h = f == 0.0 ? e * (double)((float)j / 6.0f) : g / f;
            for (int k = 1; k < 4; ++k) {
                if (foxEntity.world.getBlockState(new BlockPos(foxEntity.x + h, foxEntity.y + (double)k, foxEntity.z + g)).getMaterial().isReplaceable()) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity mate) {
        return this.createChild(mate);
    }

    class LookAtEntityGoal
    extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
        public LookAtEntityGoal(MobEntity fox, Class<? extends LivingEntity> targetType, float range) {
            super(fox, targetType, range);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
        }
    }

    class FollowParentGoal
    extends net.minecraft.entity.ai.goal.FollowParentGoal {
        private final FoxEntity fox;

        public FollowParentGoal(FoxEntity fox, double speed) {
            super(fox, speed);
            this.fox = fox;
        }

        @Override
        public boolean canStart() {
            return !this.fox.isAggressive() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.fox.isAggressive() && super.shouldContinue();
        }

        @Override
        public void start() {
            this.fox.stopActions();
            super.start();
        }
    }

    public class FoxLookControl
    extends LookControl {
        public FoxLookControl() {
            super(FoxEntity.this);
        }

        @Override
        public void tick() {
            if (!FoxEntity.this.isSleeping()) {
                super.tick();
            }
        }

        @Override
        protected boolean method_20433() {
            return !FoxEntity.this.isChasing() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isRollingHead() & !FoxEntity.this.isWalking();
        }
    }

    public class JumpChasingGoal
    extends DiveJumpingGoal {
        @Override
        public boolean canStart() {
            if (!FoxEntity.this.isFullyCrouched()) {
                return false;
            }
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            if (livingEntity.getMovementDirection() != livingEntity.getHorizontalFacing()) {
                return false;
            }
            boolean bl = FoxEntity.canJumpChase(FoxEntity.this, livingEntity);
            if (!bl) {
                FoxEntity.this.getNavigation().findPathTo(livingEntity, 0);
                FoxEntity.this.setCrouching(false);
                FoxEntity.this.setRollingHead(false);
            }
            return bl;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            double d = FoxEntity.this.getVelocity().y;
            return !(d * d < (double)0.05f && Math.abs(FoxEntity.this.pitch) < 15.0f && FoxEntity.this.onGround || FoxEntity.this.isWalking());
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public void start() {
            FoxEntity.this.setJumping(true);
            FoxEntity.this.setChasing(true);
            FoxEntity.this.setRollingHead(false);
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0f, 30.0f);
            Vec3d vec3d = new Vec3d(livingEntity.x - FoxEntity.this.x, livingEntity.y - FoxEntity.this.y, livingEntity.z - FoxEntity.this.z).normalize();
            FoxEntity.this.setVelocity(FoxEntity.this.getVelocity().add(vec3d.x * 0.8, 0.9, vec3d.z * 0.8));
            FoxEntity.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.extraRollingHeight = 0.0f;
            FoxEntity.this.lastExtraRollingHeight = 0.0f;
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setChasing(false);
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            if (livingEntity != null) {
                FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0f, 30.0f);
            }
            if (!FoxEntity.this.isWalking()) {
                Vec3d vec3d = FoxEntity.this.getVelocity();
                if (vec3d.y * vec3d.y < (double)0.03f && FoxEntity.this.pitch != 0.0f) {
                    FoxEntity.this.pitch = this.updatePitch(FoxEntity.this.pitch, 0.0f, 0.2f);
                } else {
                    double d = Math.sqrt(Entity.squaredHorizontalLength(vec3d));
                    double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 57.2957763671875;
                    FoxEntity.this.pitch = (float)e;
                }
            }
            if (livingEntity != null && FoxEntity.this.distanceTo(livingEntity) <= 2.0f) {
                FoxEntity.this.tryAttack(livingEntity);
            } else if (FoxEntity.this.pitch > 0.0f && FoxEntity.this.onGround && (float)FoxEntity.this.getVelocity().y != 0.0f && FoxEntity.this.world.getBlockState(new BlockPos(FoxEntity.this)).getBlock() == Blocks.SNOW) {
                FoxEntity.this.pitch = 60.0f;
                FoxEntity.this.setTarget(null);
                FoxEntity.this.setWalking(true);
            }
        }
    }

    class FoxSwimGoal
    extends SwimGoal {
        public FoxSwimGoal() {
            super(FoxEntity.this);
        }

        @Override
        public void start() {
            super.start();
            FoxEntity.this.stopActions();
        }

        @Override
        public boolean canStart() {
            return FoxEntity.this.isTouchingWater() && FoxEntity.this.getWaterHeight() > 0.25 || FoxEntity.this.isInLava();
        }
    }

    class GoToVillageGoal
    extends net.minecraft.entity.ai.goal.GoToVillageGoal {
        public GoToVillageGoal(int unused, int searchRange) {
            super(FoxEntity.this, searchRange);
        }

        @Override
        public void start() {
            FoxEntity.this.stopActions();
            super.start();
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.canGoToVillage();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.canGoToVillage();
        }

        private boolean canGoToVillage() {
            return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
        }
    }

    class EscapeWhenNotAggresiveGoal
    extends EscapeDangerGoal {
        public EscapeWhenNotAggresiveGoal(double speed) {
            super(FoxEntity.this, speed);
        }

        @Override
        public boolean canStart() {
            return !FoxEntity.this.isAggressive() && super.canStart();
        }
    }

    class StopWanderingGoal
    extends Goal {
        int timer;

        public StopWanderingGoal() {
            this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return FoxEntity.this.isWalking();
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart() && this.timer > 0;
        }

        @Override
        public void start() {
            this.timer = 40;
        }

        @Override
        public void stop() {
            FoxEntity.this.setWalking(false);
        }

        @Override
        public void tick() {
            --this.timer;
        }
    }

    public static class FoxData
    implements EntityData {
        public final Type type;
        public int uses;

        public FoxData(Type type) {
            this.type = type;
        }
    }

    public class EatSweetBerriesGoal
    extends MoveToTargetPosGoal {
        protected int timer;

        public EatSweetBerriesGoal(double speed, int rannge, int maxYDifference) {
            super(FoxEntity.this, speed, rannge, maxYDifference);
        }

        @Override
        public double getDesiredSquaredDistanceToTarget() {
            return 2.0;
        }

        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 100 == 0;
        }

        @Override
        protected boolean isTargetPos(CollisionView world, BlockPos pos) {
            BlockState blockState = world.getBlockState(pos);
            return blockState.getBlock() == Blocks.SWEET_BERRY_BUSH && blockState.get(SweetBerryBushBlock.AGE) >= 2;
        }

        @Override
        public void tick() {
            if (this.hasReached()) {
                if (this.timer >= 40) {
                    this.eatSweetBerry();
                } else {
                    ++this.timer;
                }
            } else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05f) {
                FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0f, 1.0f);
            }
            super.tick();
        }

        protected void eatSweetBerry() {
            if (!FoxEntity.this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return;
            }
            BlockState blockState = FoxEntity.this.world.getBlockState(this.targetPos);
            if (blockState.getBlock() != Blocks.SWEET_BERRY_BUSH) {
                return;
            }
            int i = blockState.get(SweetBerryBushBlock.AGE);
            blockState.with(SweetBerryBushBlock.AGE, 1);
            int j = 1 + FoxEntity.this.world.random.nextInt(2) + (i == 3 ? 1 : 0);
            ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (itemStack.isEmpty()) {
                FoxEntity.this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                --j;
            }
            if (j > 0) {
                Block.dropStack(FoxEntity.this.world, this.targetPos, new ItemStack(Items.SWEET_BERRIES, j));
            }
            FoxEntity.this.playSound(SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, 1.0f, 1.0f);
            FoxEntity.this.world.setBlockState(this.targetPos, (BlockState)blockState.with(SweetBerryBushBlock.AGE, 1), 2);
        }

        @Override
        public boolean canStart() {
            return !FoxEntity.this.isSleeping() && super.canStart();
        }

        @Override
        public void start() {
            this.timer = 0;
            FoxEntity.this.setSitting(false);
            super.start();
        }
    }

    class SitDownAndLookAroundGoal
    extends CalmDownGoal {
        private double lookX;
        private double lookZ;
        private int timer;
        private int counter;

        public SitDownAndLookAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return FoxEntity.this.getAttacker() == null && FoxEntity.this.getRandom().nextFloat() < 0.02f && !FoxEntity.this.isSleeping() && FoxEntity.this.getTarget() == null && FoxEntity.this.getNavigation().isIdle() && !this.canCalmDown() && !FoxEntity.this.isChasing() && !FoxEntity.this.isCrouching();
        }

        @Override
        public boolean shouldContinue() {
            return this.counter > 0;
        }

        @Override
        public void start() {
            this.chooseNewAngle();
            this.counter = 2 + FoxEntity.this.getRandom().nextInt(3);
            FoxEntity.this.setSitting(true);
            FoxEntity.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            FoxEntity.this.setSitting(false);
        }

        @Override
        public void tick() {
            --this.timer;
            if (this.timer <= 0) {
                --this.counter;
                this.chooseNewAngle();
            }
            FoxEntity.this.getLookControl().lookAt(FoxEntity.this.x + this.lookX, FoxEntity.this.y + (double)FoxEntity.this.getStandingEyeHeight(), FoxEntity.this.z + this.lookZ, FoxEntity.this.method_5986(), FoxEntity.this.getLookPitchSpeed());
        }

        private void chooseNewAngle() {
            double d = Math.PI * 2 * FoxEntity.this.getRandom().nextDouble();
            this.lookX = Math.cos(d);
            this.lookZ = Math.sin(d);
            this.timer = 80 + FoxEntity.this.getRandom().nextInt(20);
        }
    }

    class DelayedCalmDownGoal
    extends CalmDownGoal {
        private int timer;

        public DelayedCalmDownGoal() {
            this.timer = FoxEntity.this.random.nextInt(140);
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (FoxEntity.this.sidewaysSpeed != 0.0f || FoxEntity.this.upwardSpeed != 0.0f || FoxEntity.this.forwardSpeed != 0.0f) {
                return false;
            }
            return this.canNotCalmDown() || FoxEntity.this.isSleeping();
        }

        @Override
        public boolean shouldContinue() {
            return this.canNotCalmDown();
        }

        private boolean canNotCalmDown() {
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            return FoxEntity.this.world.isDay() && this.isAtFavoredLocation() && !this.canCalmDown();
        }

        @Override
        public void stop() {
            this.timer = FoxEntity.this.random.nextInt(140);
            FoxEntity.this.stopActions();
        }

        @Override
        public void start() {
            FoxEntity.this.setSitting(false);
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setJumping(false);
            FoxEntity.this.setSleeping(true);
            FoxEntity.this.getNavigation().stop();
            FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.x, FoxEntity.this.y, FoxEntity.this.z, 0.0);
        }
    }

    abstract class CalmDownGoal
    extends Goal {
        private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;

        private CalmDownGoal() {
            this.WORRIABLE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(12.0).includeHidden().setPredicate(new WorriableEntityFilter());
        }

        protected boolean isAtFavoredLocation() {
            BlockPos blockPos = new BlockPos(FoxEntity.this);
            return !FoxEntity.this.world.isSkyVisible(blockPos) && FoxEntity.this.getPathfindingFavor(blockPos) >= 0.0f;
        }

        protected boolean canCalmDown() {
            return !FoxEntity.this.world.getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, FoxEntity.this, FoxEntity.this.getBoundingBox().expand(12.0, 6.0, 12.0)).isEmpty();
        }
    }

    public class WorriableEntityFilter
    implements Predicate<LivingEntity> {
        @Override
        public boolean test(LivingEntity livingEntity) {
            if (livingEntity instanceof FoxEntity) {
                return false;
            }
            if (livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity || livingEntity instanceof HostileEntity) {
                return true;
            }
            if (livingEntity instanceof TameableEntity) {
                return !((TameableEntity)livingEntity).isTamed();
            }
            if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
                return false;
            }
            if (FoxEntity.this.canTrust(livingEntity.getUuid())) {
                return false;
            }
            return !livingEntity.isSleeping() && !livingEntity.isSneaking();
        }

        @Override
        public /* synthetic */ boolean test(Object entity) {
            return this.test((LivingEntity)entity);
        }
    }

    class AvoidDaylightGoal
    extends EscapeSunlightGoal {
        private int timer;

        public AvoidDaylightGoal(double speed) {
            super(FoxEntity.this, speed);
            this.timer = 100;
        }

        @Override
        public boolean canStart() {
            if (FoxEntity.this.isSleeping() || this.mob.getTarget() != null) {
                return false;
            }
            if (FoxEntity.this.world.isThundering()) {
                return true;
            }
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            this.timer = 100;
            BlockPos blockPos = new BlockPos(this.mob);
            return FoxEntity.this.world.isDay() && FoxEntity.this.world.isSkyVisible(blockPos) && !((ServerWorld)FoxEntity.this.world).isNearOccupiedPointOfInterest(blockPos) && this.method_18250();
        }

        @Override
        public void start() {
            FoxEntity.this.stopActions();
            super.start();
        }
    }

    class DefendFriendGoal
    extends FollowTargetGoal<LivingEntity> {
        @Nullable
        private LivingEntity offender;
        private LivingEntity friend;
        private int lastAttackedTime;

        public DefendFriendGoal(Class<LivingEntity> targetEntityClass, boolean checkVisibility, @Nullable boolean checkCanNavigate, Predicate<LivingEntity> targetPredicate) {
            super(FoxEntity.this, targetEntityClass, 10, checkVisibility, checkCanNavigate, targetPredicate);
        }

        @Override
        public boolean canStart() {
            if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
                return false;
            }
            for (UUID uUID : FoxEntity.this.getTrustedUuids()) {
                LivingEntity livingEntity;
                Entity entity;
                if (uUID == null || !(FoxEntity.this.world instanceof ServerWorld) || !((entity = ((ServerWorld)FoxEntity.this.world).getEntity(uUID)) instanceof LivingEntity)) continue;
                this.friend = livingEntity = (LivingEntity)entity;
                this.offender = livingEntity.getAttacker();
                int i = livingEntity.getLastAttackedTime();
                return i != this.lastAttackedTime && this.canTrack(this.offender, this.targetPredicate);
            }
            return false;
        }

        @Override
        public void start() {
            FoxEntity.this.setTarget(this.offender);
            this.targetEntity = this.offender;
            if (this.friend != null) {
                this.lastAttackedTime = this.friend.getLastAttackedTime();
            }
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0f, 1.0f);
            FoxEntity.this.setAggressive(true);
            FoxEntity.this.stopSleeping();
            super.start();
        }
    }

    class MateGoal
    extends AnimalMateGoal {
        public MateGoal(double chance) {
            super(FoxEntity.this, chance);
        }

        @Override
        public void start() {
            ((FoxEntity)this.animal).stopActions();
            ((FoxEntity)this.mate).stopActions();
            super.start();
        }

        @Override
        protected void breed() {
            FoxEntity foxEntity = (FoxEntity)this.animal.createChild(this.mate);
            if (foxEntity == null) {
                return;
            }
            ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
            ServerPlayerEntity serverPlayerEntity2 = this.mate.getLovingPlayer();
            ServerPlayerEntity serverPlayerEntity3 = serverPlayerEntity;
            if (serverPlayerEntity != null) {
                foxEntity.addTrustedUuid(serverPlayerEntity.getUuid());
            } else {
                serverPlayerEntity3 = serverPlayerEntity2;
            }
            if (serverPlayerEntity2 != null && serverPlayerEntity != serverPlayerEntity2) {
                foxEntity.addTrustedUuid(serverPlayerEntity2.getUuid());
            }
            if (serverPlayerEntity3 != null) {
                serverPlayerEntity3.incrementStat(Stats.ANIMALS_BRED);
                Criterions.BRED_ANIMALS.trigger(serverPlayerEntity3, this.animal, this.mate, foxEntity);
            }
            int i = 6000;
            this.animal.setBreedingAge(6000);
            this.mate.setBreedingAge(6000);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();
            foxEntity.setBreedingAge(-24000);
            foxEntity.refreshPositionAndAngles(this.animal.x, this.animal.y, this.animal.z, 0.0f, 0.0f);
            this.world.spawnEntity(foxEntity);
            this.world.sendEntityStatus(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, this.animal.getRandom().nextInt(7) + 1));
            }
        }
    }

    class AttackGoal
    extends MeleeAttackGoal {
        public AttackGoal(double speed, boolean bl) {
            super(FoxEntity.this, speed, bl);
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.ticksUntilAttack <= 0) {
                this.ticksUntilAttack = 20;
                this.mob.tryAttack(target);
                FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0f, 1.0f);
            }
        }

        @Override
        public void start() {
            FoxEntity.this.setRollingHead(false);
            super.start();
        }

        @Override
        public boolean canStart() {
            return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isWalking() && super.canStart();
        }
    }

    class MoveToHuntGoal
    extends Goal {
        public MoveToHuntGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (FoxEntity.this.isSleeping()) {
                return false;
            }
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            return livingEntity != null && livingEntity.isAlive() && CHICKEN_AND_RABBIT_FILTER.test(livingEntity) && FoxEntity.this.squaredDistanceTo(livingEntity) > 36.0 && !FoxEntity.this.isCrouching() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.jumping;
        }

        @Override
        public void start() {
            FoxEntity.this.setSitting(false);
            FoxEntity.this.setWalking(false);
        }

        @Override
        public void stop() {
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            if (livingEntity != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity)) {
                FoxEntity.this.setRollingHead(true);
                FoxEntity.this.setCrouching(true);
                FoxEntity.this.getNavigation().stop();
                FoxEntity.this.getLookControl().lookAt(livingEntity, FoxEntity.this.method_5986(), FoxEntity.this.getLookPitchSpeed());
            } else {
                FoxEntity.this.setRollingHead(false);
                FoxEntity.this.setCrouching(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            FoxEntity.this.getLookControl().lookAt(livingEntity, FoxEntity.this.method_5986(), FoxEntity.this.getLookPitchSpeed());
            if (FoxEntity.this.squaredDistanceTo(livingEntity) <= 36.0) {
                FoxEntity.this.setRollingHead(true);
                FoxEntity.this.setCrouching(true);
                FoxEntity.this.getNavigation().stop();
            } else {
                FoxEntity.this.getNavigation().startMovingTo(livingEntity, 1.5);
            }
        }
    }

    class FoxMoveControl
    extends MoveControl {
        public FoxMoveControl() {
            super(FoxEntity.this);
        }

        @Override
        public void tick() {
            if (FoxEntity.this.wantsToPickupItem()) {
                super.tick();
            }
        }
    }

    class PickupItemGoal
    extends Goal {
        public PickupItemGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            }
            if (FoxEntity.this.getTarget() != null || FoxEntity.this.getAttacker() != null) {
                return false;
            }
            if (!FoxEntity.this.wantsToPickupItem()) {
                return false;
            }
            if (FoxEntity.this.getRandom().nextInt(10) != 0) {
                return false;
            }
            List<ItemEntity> list = FoxEntity.this.world.getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            return !list.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public void tick() {
            List<ItemEntity> list = FoxEntity.this.world.getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (itemStack.isEmpty() && !list.isEmpty()) {
                FoxEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = FoxEntity.this.world.getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            if (!list.isEmpty()) {
                FoxEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            }
        }
    }

    public static enum Type {
        RED(0, "red", Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS),
        SNOW(1, "snow", Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);

        private static final Type[] TYPES;
        private static final Map<String, Type> NAME_TYPE_MAP;
        private final int id;
        private final String key;
        private final List<Biome> biomes;

        private Type(int id, String key, Biome ... biomes) {
            this.id = id;
            this.key = key;
            this.biomes = Arrays.asList(biomes);
        }

        public String getKey() {
            return this.key;
        }

        public List<Biome> getBiomes() {
            return this.biomes;
        }

        public int getId() {
            return this.id;
        }

        public static Type byName(String name) {
            return NAME_TYPE_MAP.getOrDefault(name, RED);
        }

        public static Type fromId(int id) {
            if (id < 0 || id > TYPES.length) {
                id = 0;
            }
            return TYPES[id];
        }

        public static Type fromBiome(Biome biome) {
            return SNOW.getBiomes().contains(biome) ? SNOW : RED;
        }

        static {
            TYPES = (Type[])Arrays.stream(Type.values()).sorted(Comparator.comparingInt(Type::getId)).toArray(Type[]::new);
            NAME_TYPE_MAP = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getKey, type -> type));
        }
    }
}
