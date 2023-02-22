/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class HorseBaseEntity
extends AnimalEntity
implements InventoryListener,
JumpingMount {
    private static final Predicate<LivingEntity> IS_BRED_HORSE = livingEntity -> livingEntity instanceof HorseBaseEntity && ((HorseBaseEntity)livingEntity).isBred();
    private static final TargetPredicate PARENT_HORSE_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().includeTeammates().includeHidden().setPredicate(IS_BRED_HORSE);
    protected static final EntityAttribute JUMP_STRENGTH = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0).setName("Jump Strength").setTracked(true);
    private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private int eatingGrassTicks;
    private int eatingTicks;
    private int angryTicks;
    public int field_6957;
    public int field_6958;
    protected boolean inAir;
    protected BasicInventory items;
    protected int temper;
    protected float jumpStrength;
    private boolean jumping;
    private float eatingGrassAnimationProgress;
    private float lastEatingGrassAnimationProgress;
    private float angryAnimationProgress;
    private float lastAngryAnimationProgress;
    private float eatingAnimationProgress;
    private float lastEatingAnimationProgress;
    protected boolean field_6964 = true;
    protected int soundTicks;

    protected HorseBaseEntity(EntityType<? extends HorseBaseEntity> type, World world) {
        super((EntityType<? extends AnimalEntity>)type, world);
        this.stepHeight = 1.0f;
        this.method_6721();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, HorseBaseEntity.class));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HORSE_FLAGS, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    protected boolean getHorseFlag(int bitmask) {
        return (this.dataTracker.get(HORSE_FLAGS) & bitmask) != 0;
    }

    protected void setHorseFlag(int bitmask, boolean flag) {
        byte b = this.dataTracker.get(HORSE_FLAGS);
        if (flag) {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b | bitmask));
        } else {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~bitmask));
        }
    }

    public boolean isTame() {
        return this.getHorseFlag(2);
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public boolean isInAir() {
        return this.inAir;
    }

    public void setTame(boolean tame) {
        this.setHorseFlag(2, tame);
    }

    public void setInAir(boolean inAir) {
        this.inAir = inAir;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return super.canBeLeashedBy(player) && this.getGroup() != EntityGroup.UNDEAD;
    }

    @Override
    protected void updateForLeashLength(float leashLength) {
        if (leashLength > 6.0f && this.isEatingGrass()) {
            this.setEatingGrass(false);
        }
    }

    public boolean isEatingGrass() {
        return this.getHorseFlag(16);
    }

    public boolean isAngry() {
        return this.getHorseFlag(32);
    }

    public boolean isBred() {
        return this.getHorseFlag(8);
    }

    public void setBred(boolean bl) {
        this.setHorseFlag(8, bl);
    }

    public void setSaddled(boolean bl) {
        this.setHorseFlag(4, bl);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int temper) {
        this.temper = temper;
    }

    public int addTemper(int difference) {
        int i = MathHelper.clamp(this.getTemper() + difference, 0, this.getMaxTemper());
        this.setTemper(i);
        return i;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity = source.getAttacker();
        if (this.hasPassengers() && entity != null && this.hasPassengerDeep(entity)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean isPushable() {
        return !this.hasPassengers();
    }

    private void playEatingAnimation() {
        this.setEating();
        if (!this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
        BlockState blockState;
        int i;
        if (fallDistance > 1.0f) {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4f, 1.0f);
        }
        if ((i = MathHelper.ceil((fallDistance * 0.5f - 3.0f) * damageMultiplier)) <= 0) {
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

    protected int getInventorySize() {
        return 2;
    }

    protected void method_6721() {
        BasicInventory basicInventory = this.items;
        this.items = new BasicInventory(this.getInventorySize());
        if (basicInventory != null) {
            basicInventory.removeListener(this);
            int i = Math.min(basicInventory.getInvSize(), this.items.getInvSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemStack = basicInventory.getInvStack(j);
                if (itemStack.isEmpty()) continue;
                this.items.setInvStack(j, itemStack.copy());
            }
        }
        this.items.addListener(this);
        this.updateSaddle();
    }

    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        this.setSaddled(!this.items.getInvStack(0).isEmpty() && this.canBeSaddled());
    }

    @Override
    public void onInvChange(Inventory inventory) {
        boolean bl = this.isSaddled();
        this.updateSaddle();
        if (this.age > 20 && !bl && this.isSaddled()) {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getJumpStrength() {
        return this.getAttributeInstance(JUMP_STRENGTH).getValue();
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.random.nextInt(3) == 0) {
            this.updateAnger();
        }
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.updateAnger();
        }
        return null;
    }

    public boolean canBeSaddled() {
        return true;
    }

    public boolean isSaddled() {
        return this.getHorseFlag(4);
    }

    @Nullable
    protected SoundEvent getAngrySound() {
        this.updateAnger();
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getMaterial().isLiquid()) {
            return;
        }
        BlockState blockState = this.world.getBlockState(pos.up());
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        if (blockState.getBlock() == Blocks.SNOW) {
            blockSoundGroup = blockState.getSoundGroup();
        }
        if (this.hasPassengers() && this.field_6964) {
            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                this.playWalkSound(blockSoundGroup);
            } else if (this.soundTicks <= 5) {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
            }
        } else if (blockSoundGroup == BlockSoundGroup.WOOD) {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
        } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
        }
    }

    protected void playWalkSound(BlockSoundGroup group) {
        this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, group.getVolume() * 0.15f, group.getPitch());
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(JUMP_STRENGTH);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(53.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.225f);
    }

    @Override
    public int getLimitPerChunk() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 400;
    }

    public void openInventory(PlayerEntity player) {
        if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTame()) {
            player.openHorseInventory(this, this.items);
        }
    }

    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        boolean bl = false;
        float f = 0.0f;
        int i = 0;
        int j = 0;
        Item item2 = item.getItem();
        if (item2 == Items.WHEAT) {
            f = 2.0f;
            i = 20;
            j = 3;
        } else if (item2 == Items.SUGAR) {
            f = 1.0f;
            i = 30;
            j = 3;
        } else if (item2 == Blocks.HAY_BLOCK.asItem()) {
            f = 20.0f;
            i = 180;
        } else if (item2 == Items.APPLE) {
            f = 3.0f;
            i = 60;
            j = 3;
        } else if (item2 == Items.GOLDEN_CARROT) {
            f = 4.0f;
            i = 60;
            j = 5;
            if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(player);
            }
        } else if (item2 == Items.GOLDEN_APPLE || item2 == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0f;
            i = 240;
            j = 10;
            if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(player);
            }
        }
        if (this.getHealth() < this.getMaximumHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(i);
            }
            bl = true;
        }
        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.world.isClient) {
                this.addTemper(j);
            }
        }
        if (bl) {
            this.playEatingAnimation();
        }
        return bl;
    }

    protected void putPlayerOnBack(PlayerEntity player) {
        this.setEatingGrass(false);
        this.setAngry(false);
        if (!this.world.isClient) {
            player.yaw = this.yaw;
            player.pitch = this.pitch;
            player.startRiding(this);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    private void method_6759() {
        this.field_6957 = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items == null) {
            return;
        }
        for (int i = 0; i < this.items.getInvSize(); ++i) {
            ItemStack itemStack = this.items.getInvStack(i);
            if (itemStack.isEmpty()) continue;
            this.dropStack(itemStack);
        }
    }

    @Override
    public void tickMovement() {
        if (this.random.nextInt(200) == 0) {
            this.method_6759();
        }
        super.tickMovement();
        if (this.world.isClient || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.eatsGrass()) {
            if (!this.isEatingGrass() && !this.hasPassengers() && this.random.nextInt(300) == 0 && this.world.getBlockState(new BlockPos(this).down()).getBlock() == Blocks.GRASS_BLOCK) {
                this.setEatingGrass(true);
            }
            if (this.isEatingGrass() && ++this.eatingGrassTicks > 50) {
                this.eatingGrassTicks = 0;
                this.setEatingGrass(false);
            }
        }
        this.walkToParent();
    }

    protected void walkToParent() {
        HorseBaseEntity livingEntity;
        if (this.isBred() && this.isBaby() && !this.isEatingGrass() && (livingEntity = this.world.method_21726(HorseBaseEntity.class, PARENT_HORSE_PREDICATE, this, this.x, this.y, this.z, this.getBoundingBox().expand(16.0))) != null && this.squaredDistanceTo(livingEntity) > 4.0) {
            this.navigation.findPathTo(livingEntity, 0);
        }
    }

    public boolean eatsGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
            this.eatingTicks = 0;
            this.setHorseFlag(64, false);
        }
        if ((this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) && this.angryTicks > 0 && ++this.angryTicks > 20) {
            this.angryTicks = 0;
            this.setAngry(false);
        }
        if (this.field_6957 > 0 && ++this.field_6957 > 8) {
            this.field_6957 = 0;
        }
        if (this.field_6958 > 0) {
            ++this.field_6958;
            if (this.field_6958 > 300) {
                this.field_6958 = 0;
            }
        }
        this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
        if (this.isEatingGrass()) {
            this.eatingGrassAnimationProgress += (1.0f - this.eatingGrassAnimationProgress) * 0.4f + 0.05f;
            if (this.eatingGrassAnimationProgress > 1.0f) {
                this.eatingGrassAnimationProgress = 1.0f;
            }
        } else {
            this.eatingGrassAnimationProgress += (0.0f - this.eatingGrassAnimationProgress) * 0.4f - 0.05f;
            if (this.eatingGrassAnimationProgress < 0.0f) {
                this.eatingGrassAnimationProgress = 0.0f;
            }
        }
        this.lastAngryAnimationProgress = this.angryAnimationProgress;
        if (this.isAngry()) {
            this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress = 0.0f;
            this.angryAnimationProgress += (1.0f - this.angryAnimationProgress) * 0.4f + 0.05f;
            if (this.angryAnimationProgress > 1.0f) {
                this.angryAnimationProgress = 1.0f;
            }
        } else {
            this.jumping = false;
            this.angryAnimationProgress += (0.8f * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress) * 0.6f - 0.05f;
            if (this.angryAnimationProgress < 0.0f) {
                this.angryAnimationProgress = 0.0f;
            }
        }
        this.lastEatingAnimationProgress = this.eatingAnimationProgress;
        if (this.getHorseFlag(64)) {
            this.eatingAnimationProgress += (1.0f - this.eatingAnimationProgress) * 0.7f + 0.05f;
            if (this.eatingAnimationProgress > 1.0f) {
                this.eatingAnimationProgress = 1.0f;
            }
        } else {
            this.eatingAnimationProgress += (0.0f - this.eatingAnimationProgress) * 0.7f - 0.05f;
            if (this.eatingAnimationProgress < 0.0f) {
                this.eatingAnimationProgress = 0.0f;
            }
        }
    }

    private void setEating() {
        if (!this.world.isClient) {
            this.eatingTicks = 1;
            this.setHorseFlag(64, true);
        }
    }

    public void setEatingGrass(boolean eatingGrass) {
        this.setHorseFlag(16, eatingGrass);
    }

    public void setAngry(boolean angry) {
        if (angry) {
            this.setEatingGrass(false);
        }
        this.setHorseFlag(32, angry);
    }

    private void updateAnger() {
        if (this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) {
            this.angryTicks = 1;
            this.setAngry(true);
        }
    }

    public void playAngrySound() {
        this.updateAnger();
        SoundEvent soundEvent = this.getAngrySound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public boolean bondWithPlayer(PlayerEntity player) {
        this.setOwnerUuid(player.getUuid());
        this.setTame(true);
        if (player instanceof ServerPlayerEntity) {
            Criterions.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
        }
        this.world.sendEntityStatus(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vec3d movementInput) {
        double e;
        double d;
        if (!this.isAlive()) {
            return;
        }
        if (!(this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled())) {
            this.field_6281 = 0.02f;
            super.travel(movementInput);
            return;
        }
        LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
        this.prevYaw = this.yaw = livingEntity.yaw;
        this.pitch = livingEntity.pitch * 0.5f;
        this.setRotation(this.yaw, this.pitch);
        this.headYaw = this.field_6283 = this.yaw;
        float f = livingEntity.sidewaysSpeed * 0.5f;
        float g = livingEntity.forwardSpeed;
        if (g <= 0.0f) {
            g *= 0.25f;
            this.soundTicks = 0;
        }
        if (this.onGround && this.jumpStrength == 0.0f && this.isAngry() && !this.jumping) {
            f = 0.0f;
            g = 0.0f;
        }
        if (this.jumpStrength > 0.0f && !this.isInAir() && this.onGround) {
            d = this.getJumpStrength() * (double)this.jumpStrength;
            e = this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? d + (double)((float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f) : d;
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, e, vec3d.z);
            this.setInAir(true);
            this.velocityDirty = true;
            if (g > 0.0f) {
                float h = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
                float i = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
                this.setVelocity(this.getVelocity().add(-0.4f * h * this.jumpStrength, 0.0, 0.4f * i * this.jumpStrength));
                this.playJumpSound();
            }
            this.jumpStrength = 0.0f;
        }
        this.field_6281 = this.getMovementSpeed() * 0.1f;
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setMovementSpeed((float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            super.travel(new Vec3d(f, movementInput.y, g));
        } else if (livingEntity instanceof PlayerEntity) {
            this.setVelocity(Vec3d.ZERO);
        }
        if (this.onGround) {
            this.jumpStrength = 0.0f;
            this.setInAir(false);
        }
        this.lastLimbDistance = this.limbDistance;
        d = this.x - this.prevX;
        e = this.z - this.prevZ;
        float j = MathHelper.sqrt(d * d + e * e) * 4.0f;
        if (j > 1.0f) {
            j = 1.0f;
        }
        this.limbDistance += (j - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("EatingHaystack", this.isEatingGrass());
        tag.putBoolean("Bred", this.isBred());
        tag.putInt("Temper", this.getTemper());
        tag.putBoolean("Tame", this.isTame());
        if (this.getOwnerUuid() != null) {
            tag.putString("OwnerUUID", this.getOwnerUuid().toString());
        }
        if (!this.items.getInvStack(0).isEmpty()) {
            tag.put("SaddleItem", this.items.getInvStack(0).toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        ItemStack itemStack;
        EntityAttributeInstance entityAttributeInstance;
        String string;
        super.readCustomDataFromTag(tag);
        this.setEatingGrass(tag.getBoolean("EatingHaystack"));
        this.setBred(tag.getBoolean("Bred"));
        this.setTemper(tag.getInt("Temper"));
        this.setTame(tag.getBoolean("Tame"));
        if (tag.contains("OwnerUUID", 8)) {
            string = tag.getString("OwnerUUID");
        } else {
            String string2 = tag.getString("Owner");
            string = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string2);
        }
        if (!string.isEmpty()) {
            this.setOwnerUuid(UUID.fromString(string));
        }
        if ((entityAttributeInstance = this.getAttributes().get("Speed")) != null) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
        }
        if (tag.contains("SaddleItem", 10) && (itemStack = ItemStack.fromTag(tag.getCompound("SaddleItem"))).getItem() == Items.SADDLE) {
            this.items.setInvStack(0, itemStack);
        }
        this.updateSaddle();
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    protected boolean canBreed() {
        return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaximumHealth() && this.isInLove();
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity mate) {
        return null;
    }

    protected void setChildAttributes(PassiveEntity mate, HorseBaseEntity child) {
        double d = this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue() + mate.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue() + (double)this.getChildHealthBonus();
        child.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(d / 3.0);
        double e = this.getAttributeInstance(JUMP_STRENGTH).getBaseValue() + mate.getAttributeInstance(JUMP_STRENGTH).getBaseValue() + this.getChildJumpStrengthBonus();
        child.getAttributeInstance(JUMP_STRENGTH).setBaseValue(e / 3.0);
        double f = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue() + mate.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue() + this.getChildMovementSpeedBonus();
        child.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(f / 3.0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getPrimaryPassenger() instanceof LivingEntity;
    }

    @Environment(value=EnvType.CLIENT)
    public float getEatingGrassAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getAngryAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastAngryAnimationProgress, this.angryAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getEatingAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setJumpStrength(int strength) {
        if (!this.isSaddled()) {
            return;
        }
        if (strength < 0) {
            strength = 0;
        } else {
            this.jumping = true;
            this.updateAnger();
        }
        this.jumpStrength = strength >= 90 ? 1.0f : 0.4f + 0.4f * (float)strength / 90.0f;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void startJumping(int height) {
        this.jumping = true;
        this.updateAnger();
    }

    @Override
    public void stopJumping() {
    }

    @Environment(value=EnvType.CLIENT)
    protected void spawnPlayerReactionParticles(boolean positive) {
        DefaultParticleType particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleEffect, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), d, e, f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 7) {
            this.spawnPlayerReactionParticles(true);
        } else if (status == 6) {
            this.spawnPlayerReactionParticles(false);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (passenger instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)passenger;
            this.field_6283 = mobEntity.field_6283;
        }
        if (this.lastAngryAnimationProgress > 0.0f) {
            float f = MathHelper.sin(this.field_6283 * ((float)Math.PI / 180));
            float g = MathHelper.cos(this.field_6283 * ((float)Math.PI / 180));
            float h = 0.7f * this.lastAngryAnimationProgress;
            float i = 0.15f * this.lastAngryAnimationProgress;
            passenger.updatePosition(this.x + (double)(h * f), this.y + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double)i, this.z - (double)(h * g));
            if (passenger instanceof LivingEntity) {
                ((LivingEntity)passenger).field_6283 = this.field_6283;
            }
        }
    }

    protected float getChildHealthBonus() {
        return 15.0f + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
    }

    protected double getChildJumpStrengthBonus() {
        return (double)0.4f + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
    }

    protected double getChildMovementSpeedBonus() {
        return ((double)0.45f + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.95f;
    }

    public boolean canEquip() {
        return false;
    }

    public boolean canEquip(ItemStack item) {
        return false;
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        int i = slot - 400;
        if (i >= 0 && i < 2 && i < this.items.getInvSize()) {
            if (i == 0 && item.getItem() != Items.SADDLE) {
                return false;
            }
            if (!(i != 1 || this.canEquip() && this.canEquip(item))) {
                return false;
            }
            this.items.setInvStack(i, item);
            this.updateSaddle();
            return true;
        }
        int j = slot - 500 + 2;
        if (j >= 2 && j < this.items.getInvSize()) {
            this.items.setInvStack(j, item);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        entityData = super.initialize(world, difficulty, spawnType, entityData, entityTag);
        if (this.random.nextInt(5) == 0) {
            this.setBreedingAge(-24000);
        }
        return entityData;
    }
}

