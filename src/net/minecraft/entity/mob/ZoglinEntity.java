/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class ZoglinEntity
extends HostileEntity
implements Monster,
Hoglin {
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final int field_30514 = 40;
    private static final int field_30505 = 1;
    private static final float field_30506 = 0.6f;
    private static final int field_30507 = 6;
    private static final float field_30508 = 0.5f;
    private static final int field_30509 = 40;
    private static final int field_30510 = 15;
    private static final int field_30511 = 200;
    private static final float field_30512 = 0.3f;
    private static final float field_30513 = 0.4f;
    private int movementCooldownTicks;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super ZoglinEntity>>> USED_SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> USED_MEMORY_MODULES = ImmutableList.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    public ZoglinEntity(EntityType<? extends ZoglinEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.experiencePoints = 5;
    }

    protected Brain.Profile<ZoglinEntity> createBrainProfile() {
        return Brain.createProfile(USED_MEMORY_MODULES, USED_SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<ZoglinEntity> brain = this.createBrainProfile().deserialize(dynamic);
        ZoglinEntity.addCoreTasks(brain);
        ZoglinEntity.addIdleTasks(brain);
        ZoglinEntity.addFightTasks(brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreTasks(Brain<ZoglinEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<ZoglinEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask()));
    }

    private static void addIdleTasks(Brain<ZoglinEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<ZoglinEntity>>)ImmutableList.of(new UpdateAttackTargetTask<ZoglinEntity>(ZoglinEntity::getHoglinTarget), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), UniformIntProvider.create(30, 60)), new RandomTask(ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.4f), (Object)2), (Object)Pair.of((Object)new GoTowardsLookTarget(0.4f, 3), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)))));
    }

    private static void addFightTasks(Brain<ZoglinEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<ZoglinEntity>>)ImmutableList.of((Object)new RangedApproachTask(1.0f), new ConditionalTask<MobEntity>(ZoglinEntity::isAdult, new MeleeAttackTask(40)), new ConditionalTask<MobEntity>(ZoglinEntity::isBaby, new MeleeAttackTask(15)), new ForgetAttackTargetTask()), MemoryModuleType.ATTACK_TARGET);
    }

    private Optional<? extends LivingEntity> getHoglinTarget() {
        return this.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty()).findFirst(this::shouldAttack);
    }

    private boolean shouldAttack(LivingEntity entity) {
        EntityType<?> entityType = entity.getType();
        return entityType != EntityType.ZOGLIN && entityType != EntityType.CREEPER && Sensor.testAttackableTargetPredicate(this, entity);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (BABY.equals(data)) {
            this.calculateDimensions();
        }
    }

    public static DefaultAttributeContainer.Builder createZoglinAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6f).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!(target instanceof LivingEntity)) {
            return false;
        }
        this.movementCooldownTicks = 10;
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0f, this.getSoundPitch());
        return Hoglin.tryAttack(this, (LivingEntity)target);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return !this.isLeashed();
    }

    @Override
    protected void knockback(LivingEntity target) {
        if (!this.isBaby()) {
            Hoglin.knockback(this, target);
        }
    }

    @Override
    public double getMountedHeightOffset() {
        return (double)this.getHeight() - (this.isBaby() ? 0.2 : 0.15);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (this.world.isClient) {
            return false;
        }
        if (!bl || !(source.getAttacker() instanceof LivingEntity)) {
            return bl;
        }
        LivingEntity livingEntity = (LivingEntity)source.getAttacker();
        if (this.canTarget(livingEntity) && !LookTargetUtil.isNewTargetTooFar(this, livingEntity, 4.0)) {
            this.setAttackTarget(livingEntity);
        }
        return bl;
    }

    private void setAttackTarget(LivingEntity entity) {
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.brain.remember(MemoryModuleType.ATTACK_TARGET, entity, 200L);
    }

    public Brain<ZoglinEntity> getBrain() {
        return super.getBrain();
    }

    protected void tickBrain() {
        Activity activity = this.brain.getFirstPossibleNonCoreActivity().orElse(null);
        this.brain.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity activity2 = this.brain.getFirstPossibleNonCoreActivity().orElse(null);
        if (activity2 == Activity.FIGHT && activity != Activity.FIGHT) {
            this.playAngrySound();
        }
        this.setAttacking(this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("zoglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.tickBrain();
    }

    @Override
    public void setBaby(boolean baby) {
        this.getDataTracker().set(BABY, baby);
        if (!this.world.isClient && baby) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataTracker().get(BABY);
    }

    @Override
    public void tickMovement() {
        if (this.movementCooldownTicks > 0) {
            --this.movementCooldownTicks;
        }
        super.tickMovement();
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 4) {
            this.movementCooldownTicks = 10;
            this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0f, this.getSoundPitch());
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public int getMovementCooldownTicks() {
        return this.movementCooldownTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.world.isClient) {
            return null;
        }
        if (this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            return SoundEvents.ENTITY_ZOGLIN_ANGRY;
        }
        return SoundEvents.ENTITY_ZOGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_ZOGLIN_STEP, 0.15f, 1.0f);
    }

    protected void playAngrySound() {
        this.playSound(SoundEvents.ENTITY_ZOGLIN_ANGRY, 1.0f, this.getSoundPitch());
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.isBaby()) {
            nbt.putBoolean("IsBaby", true);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.getBoolean("IsBaby")) {
            this.setBaby(true);
        }
    }
}

