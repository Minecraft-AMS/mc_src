/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class HorseBaseEntity
extends AnimalEntity
implements InventoryChangedListener,
JumpingMount,
Saddleable {
    public static final int field_30413 = 400;
    public static final int field_30414 = 499;
    public static final int field_30415 = 500;
    private static final Predicate<LivingEntity> IS_BRED_HORSE = entity -> entity instanceof HorseBaseEntity && ((HorseBaseEntity)entity).isBred();
    private static final TargetPredicate PARENT_HORSE_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreVisibility().setPredicate(IS_BRED_HORSE);
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final int TAMED_FLAG = 2;
    private static final int SADDLED_FLAG = 4;
    private static final int BRED_FLAG = 8;
    private static final int EATING_GRASS_FLAG = 16;
    private static final int ANGRY_FLAG = 32;
    private static final int EATING_FLAG = 64;
    public static final int field_30416 = 0;
    public static final int field_30417 = 1;
    public static final int field_30418 = 2;
    private int eatingGrassTicks;
    private int eatingTicks;
    private int angryTicks;
    public int tailWagTicks;
    public int field_6958;
    protected boolean inAir;
    protected SimpleInventory items;
    protected int temper;
    protected float jumpStrength;
    private boolean jumping;
    private float eatingGrassAnimationProgress;
    private float lastEatingGrassAnimationProgress;
    private float angryAnimationProgress;
    private float lastAngryAnimationProgress;
    private float eatingAnimationProgress;
    private float lastEatingAnimationProgress;
    protected boolean playExtraHorseSounds = true;
    protected int soundTicks;

    protected HorseBaseEntity(EntityType<? extends HorseBaseEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.stepHeight = 1.0f;
        this.onChestedStatusChanged();
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
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), false));
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

    public void setBred(boolean bred) {
        this.setHorseFlag(8, bred);
    }

    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    @Override
    public void saddle(@Nullable SoundCategory sound) {
        this.items.setStack(0, new ItemStack(Items.SADDLE));
        if (sound != null) {
            this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_HORSE_SADDLE, sound, 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.getHorseFlag(4);
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
    public boolean isPushable() {
        return !this.hasPassengers();
    }

    private void playEatingAnimation() {
        SoundEvent soundEvent;
        this.setEating();
        if (!this.isSilent() && (soundEvent = this.getEatSound()) != null) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        int i;
        if (fallDistance > 1.0f) {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4f, 1.0f);
        }
        if ((i = this.computeFallDamage(fallDistance, damageMultiplier)) <= 0) {
            return false;
        }
        this.damage(damageSource, i);
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengersDeep()) {
                entity.damage(damageSource, i);
            }
        }
        this.playBlockFallSound();
        return true;
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return MathHelper.ceil((fallDistance * 0.5f - 3.0f) * damageMultiplier);
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void onChestedStatusChanged() {
        SimpleInventory simpleInventory = this.items;
        this.items = new SimpleInventory(this.getInventorySize());
        if (simpleInventory != null) {
            simpleInventory.removeListener(this);
            int i = Math.min(simpleInventory.size(), this.items.size());
            for (int j = 0; j < i; ++j) {
                ItemStack itemStack = simpleInventory.getStack(j);
                if (itemStack.isEmpty()) continue;
                this.items.setStack(j, itemStack.copy());
            }
        }
        this.items.addListener(this);
        this.updateSaddle();
    }

    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        this.setHorseFlag(4, !this.items.getStack(0).isEmpty());
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        boolean bl = this.isSaddled();
        this.updateSaddle();
        if (this.age > 20 && !bl && this.isSaddled()) {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getJumpStrength() {
        return this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
    }

    @Nullable
    protected SoundEvent getEatSound() {
        return null;
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
        if (blockState.isOf(Blocks.SNOW)) {
            blockSoundGroup = blockState.getSoundGroup();
        }
        if (this.hasPassengers() && this.playExtraHorseSounds) {
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

    public static DefaultAttributeContainer.Builder createBaseHorseAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.HORSE_JUMP_STRENGTH).add(EntityAttributes.GENERIC_MAX_HEALTH, 53.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225f);
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

    public ActionResult interactHorse(PlayerEntity player, ItemStack stack) {
        boolean bl = this.receiveFood(player, stack);
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        if (this.world.isClient) {
            return ActionResult.CONSUME;
        }
        return bl ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        boolean bl = false;
        float f = 0.0f;
        int i = 0;
        int j = 0;
        if (item.isOf(Items.WHEAT)) {
            f = 2.0f;
            i = 20;
            j = 3;
        } else if (item.isOf(Items.SUGAR)) {
            f = 1.0f;
            i = 30;
            j = 3;
        } else if (item.isOf(Blocks.HAY_BLOCK.asItem())) {
            f = 20.0f;
            i = 180;
        } else if (item.isOf(Items.APPLE)) {
            f = 3.0f;
            i = 60;
            j = 3;
        } else if (item.isOf(Items.GOLDEN_CARROT)) {
            f = 4.0f;
            i = 60;
            j = 5;
            if (!this.world.isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(player);
            }
        } else if (item.isOf(Items.GOLDEN_APPLE) || item.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            f = 10.0f;
            i = 240;
            j = 10;
            if (!this.world.isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(player);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
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
            this.emitGameEvent(GameEvent.EAT, this.getCameraBlockPos());
        }
        return bl;
    }

    protected void putPlayerOnBack(PlayerEntity player) {
        this.setEatingGrass(false);
        this.setAngry(false);
        if (!this.world.isClient) {
            player.setYaw(this.getYaw());
            player.setPitch(this.getPitch());
            player.startRiding(this);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    private void wagTail() {
        this.tailWagTicks = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items == null) {
            return;
        }
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack = this.items.getStack(i);
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            this.dropStack(itemStack);
        }
    }

    @Override
    public void tickMovement() {
        if (this.random.nextInt(200) == 0) {
            this.wagTail();
        }
        super.tickMovement();
        if (this.world.isClient || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.eatsGrass()) {
            if (!this.isEatingGrass() && !this.hasPassengers() && this.random.nextInt(300) == 0 && this.world.getBlockState(this.getBlockPos().down()).isOf(Blocks.GRASS_BLOCK)) {
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
        if (this.isBred() && this.isBaby() && !this.isEatingGrass() && (livingEntity = this.world.getClosestEntity(HorseBaseEntity.class, PARENT_HORSE_PREDICATE, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().expand(16.0))) != null && this.squaredDistanceTo(livingEntity) > 4.0) {
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
        if (this.tailWagTicks > 0 && ++this.tailWagTicks > 8) {
            this.tailWagTicks = 0;
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
        if (!this.isAngry()) {
            this.updateAnger();
            SoundEvent soundEvent = this.getAngrySound();
            if (soundEvent != null) {
                this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    public boolean bondWithPlayer(PlayerEntity player) {
        this.setOwnerUuid(player.getUuid());
        this.setTame(true);
        if (player instanceof ServerPlayerEntity) {
            Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
        }
        this.world.sendEntityStatus(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (!this.isAlive()) {
            return;
        }
        if (!(this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled())) {
            this.airStrafingSpeed = 0.02f;
            super.travel(movementInput);
            return;
        }
        LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
        this.setYaw(livingEntity.getYaw());
        this.prevYaw = this.getYaw();
        this.setPitch(livingEntity.getPitch() * 0.5f);
        this.setRotation(this.getYaw(), this.getPitch());
        this.headYaw = this.bodyYaw = this.getYaw();
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
            double d = this.getJumpStrength() * (double)this.jumpStrength * (double)this.getJumpVelocityMultiplier();
            double e = d + this.getJumpBoostVelocityModifier();
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, e, vec3d.z);
            this.setInAir(true);
            this.velocityDirty = true;
            if (g > 0.0f) {
                float h = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180));
                float i = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180));
                this.setVelocity(this.getVelocity().add(-0.4f * h * this.jumpStrength, 0.0, 0.4f * i * this.jumpStrength));
            }
            this.jumpStrength = 0.0f;
        }
        this.airStrafingSpeed = this.getMovementSpeed() * 0.1f;
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setMovementSpeed((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            super.travel(new Vec3d(f, movementInput.y, g));
        } else if (livingEntity instanceof PlayerEntity) {
            this.setVelocity(Vec3d.ZERO);
        }
        if (this.onGround) {
            this.jumpStrength = 0.0f;
            this.setInAir(false);
        }
        this.updateLimbs(this, false);
        this.tryCheckBlockCollision();
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("EatingHaystack", this.isEatingGrass());
        nbt.putBoolean("Bred", this.isBred());
        nbt.putInt("Temper", this.getTemper());
        nbt.putBoolean("Tame", this.isTame());
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        if (!this.items.getStack(0).isEmpty()) {
            nbt.put("SaddleItem", this.items.getStack(0).writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        ItemStack itemStack;
        UUID uUID;
        super.readCustomDataFromNbt(nbt);
        this.setEatingGrass(nbt.getBoolean("EatingHaystack"));
        this.setBred(nbt.getBoolean("Bred"));
        this.setTemper(nbt.getInt("Temper"));
        this.setTame(nbt.getBoolean("Tame"));
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            this.setOwnerUuid(uUID);
        }
        if (nbt.contains("SaddleItem", 10) && (itemStack = ItemStack.fromNbt(nbt.getCompound("SaddleItem"))).isOf(Items.SADDLE)) {
            this.items.setStack(0, itemStack);
        }
        this.updateSaddle();
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    protected boolean canBreed() {
        return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    protected void setChildAttributes(PassiveEntity mate, HorseBaseEntity child) {
        double d = this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + mate.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + (double)this.getChildHealthBonus();
        child.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(d / 3.0);
        double e = this.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH) + mate.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH) + this.getChildJumpStrengthBonus();
        child.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(e / 3.0);
        double f = this.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + mate.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + this.getChildMovementSpeedBonus();
        child.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(f / 3.0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getPrimaryPassenger() instanceof LivingEntity;
    }

    public float getEatingGrassAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
    }

    public float getAngryAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastAngryAnimationProgress, this.angryAnimationProgress);
    }

    public float getEatingAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
    }

    @Override
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
        this.playJumpSound();
    }

    @Override
    public void stopJumping() {
    }

    protected void spawnPlayerReactionParticles(boolean positive) {
        DefaultParticleType particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
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
            this.bodyYaw = mobEntity.bodyYaw;
        }
        if (this.lastAngryAnimationProgress > 0.0f) {
            float f = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
            float g = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
            float h = 0.7f * this.lastAngryAnimationProgress;
            float i = 0.15f * this.lastAngryAnimationProgress;
            passenger.setPosition(this.getX() + (double)(h * f), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double)i, this.getZ() - (double)(h * g));
            if (passenger instanceof LivingEntity) {
                ((LivingEntity)passenger).bodyYaw = this.bodyYaw;
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

    public boolean hasArmorSlot() {
        return false;
    }

    public boolean hasArmorInSlot() {
        return !this.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
    }

    public boolean isHorseArmor(ItemStack item) {
        return false;
    }

    private StackReference createInventoryStackReference(final int slot, final Predicate<ItemStack> predicate) {
        return new StackReference(){

            @Override
            public ItemStack get() {
                return HorseBaseEntity.this.items.getStack(slot);
            }

            @Override
            public boolean set(ItemStack stack) {
                if (!predicate.test(stack)) {
                    return false;
                }
                HorseBaseEntity.this.items.setStack(slot, stack);
                HorseBaseEntity.this.updateSaddle();
                return true;
            }
        };
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        int j;
        int i = mappedIndex - 400;
        if (i >= 0 && i < 2 && i < this.items.size()) {
            if (i == 0) {
                return this.createInventoryStackReference(i, stack -> stack.isEmpty() || stack.isOf(Items.SADDLE));
            }
            if (i == 1) {
                if (!this.hasArmorSlot()) {
                    return StackReference.EMPTY;
                }
                return this.createInventoryStackReference(i, stack -> stack.isEmpty() || this.isHorseArmor((ItemStack)stack));
            }
        }
        if ((j = mappedIndex - 500 + 2) >= 2 && j < this.items.size()) {
            return StackReference.of(this.items, j);
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }

    @Nullable
    private Vec3d locateSafeDismountingPos(Vec3d offset, LivingEntity passenger) {
        double d = this.getX() + offset.x;
        double e = this.getBoundingBox().minY;
        double f = this.getZ() + offset.z;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        block0: for (EntityPose entityPose : passenger.getPoses()) {
            mutable.set(d, e, f);
            double g = this.getBoundingBox().maxY + 0.75;
            do {
                Vec3d vec3d;
                Box box;
                double h = this.world.getDismountHeight(mutable);
                if ((double)mutable.getY() + h > g) continue block0;
                if (Dismounting.canDismountInBlock(h) && Dismounting.canPlaceEntityAt(this.world, passenger, (box = passenger.getBoundingBox(entityPose)).offset(vec3d = new Vec3d(d, (double)mutable.getY() + h, f)))) {
                    passenger.setPose(entityPose);
                    return vec3d;
                }
                mutable.move(Direction.UP);
            } while ((double)mutable.getY() < g);
        }
        return null;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Vec3d vec3d = HorseBaseEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), this.getYaw() + (passenger.getMainArm() == Arm.RIGHT ? 90.0f : -90.0f));
        Vec3d vec3d2 = this.locateSafeDismountingPos(vec3d, passenger);
        if (vec3d2 != null) {
            return vec3d2;
        }
        Vec3d vec3d3 = HorseBaseEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), this.getYaw() + (passenger.getMainArm() == Arm.LEFT ? 90.0f : -90.0f));
        Vec3d vec3d4 = this.locateSafeDismountingPos(vec3d3, passenger);
        if (vec3d4 != null) {
            return vec3d4;
        }
        return this.getPos();
    }

    protected void initAttributes() {
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (entityData == null) {
            entityData = new PassiveEntity.PassiveData(0.2f);
        }
        this.initAttributes();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public boolean areInventoriesDifferent(Inventory inventory) {
        return this.items != inventory;
    }
}

