/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class WardenEntity
extends HostileEntity
implements VibrationListener.Callback {
    private static final Logger field_38138 = LogUtils.getLogger();
    private static final int field_38139 = 16;
    private static final int field_38142 = 40;
    private static final int field_38860 = 200;
    private static final int field_38143 = 500;
    private static final float field_38144 = 0.3f;
    private static final float field_38145 = 1.0f;
    private static final float field_38146 = 1.5f;
    private static final int field_38147 = 30;
    private static final TrackedData<Integer> ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int field_38149 = 200;
    private static final int field_38150 = 260;
    private static final int field_38151 = 20;
    private static final int field_38152 = 120;
    private static final int field_38153 = 20;
    private static final int field_38155 = 35;
    private static final int field_38156 = 10;
    private static final int field_39117 = 20;
    private static final int field_38157 = 100;
    private static final int field_38158 = 20;
    private static final int field_38159 = 30;
    private static final float field_38160 = 4.5f;
    private static final float field_38161 = 0.7f;
    private static final int field_39305 = 30;
    private int tendrilPitch;
    private int lastTendrilPitch;
    private int heartbeatCooldown;
    private int lastHeartbeatCooldown;
    public AnimationState roaringAnimationState = new AnimationState();
    public AnimationState sniffingAnimationState = new AnimationState();
    public AnimationState emergingAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackingAnimationState = new AnimationState();
    public AnimationState chargingSonicBoomAnimationState = new AnimationState();
    private final EntityGameEventHandler<VibrationListener> gameEventHandler;
    private WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());

    public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.gameEventHandler = new EntityGameEventHandler<VibrationListener>(new VibrationListener(new EntityPositionSource(this, this.getStandingEyeHeight()), 16, this, null, 0.0f, 0));
        this.experiencePoints = 5;
        this.getNavigation().setCanSwim(true);
        this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0f);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0f);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0f);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.isInPose(EntityPose.EMERGING) ? 1 : 0);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (packet.getEntityData() == 1) {
            this.setPose(EntityPose.EMERGING);
        }
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return super.canSpawn(world) && world.isSpaceEmpty(this, this.getType().getDimensions().getBoxAt(this.getPos()));
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0f;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (this.isDiggingOrEmerging() && !damageSource.isOutOfWorld()) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    private boolean isDiggingOrEmerging() {
        return this.isInPose(EntityPose.DIGGING) || this.isInPose(EntityPose.EMERGING);
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean disablesShield() {
        return true;
    }

    @Override
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.55f;
    }

    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0);
    }

    @Override
    public boolean occludeVibrationSignals() {
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 4.0f;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isInPose(EntityPose.ROARING) || this.isDiggingOrEmerging()) {
            return null;
        }
        return this.getAngriness().getSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 10.0f, 1.0f);
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_WARDEN_ATTACK_IMPACT, 10.0f, this.getSoundPitch());
        SonicBoomTask.cooldown(this, 40);
        return super.tryAttack(target);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER, 0);
    }

    public int getAnger() {
        return this.dataTracker.get(ANGER);
    }

    private void updateAnger() {
        this.dataTracker.set(ANGER, this.getAngerAtTarget());
    }

    @Override
    public void tick() {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.gameEventHandler.getListener().tick(serverWorld);
            if (this.isPersistent() || this.cannotDespawn()) {
                WardenBrain.resetDigCooldown(this);
            }
        }
        super.tick();
        if (this.world.isClient()) {
            if (this.age % this.getHeartRate() == 0) {
                this.heartbeatCooldown = 10;
                if (!this.isSilent()) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, this.getSoundCategory(), 5.0f, this.getSoundPitch(), false);
                }
            }
            this.lastTendrilPitch = this.tendrilPitch;
            if (this.tendrilPitch > 0) {
                --this.tendrilPitch;
            }
            this.lastHeartbeatCooldown = this.heartbeatCooldown;
            if (this.heartbeatCooldown > 0) {
                --this.heartbeatCooldown;
            }
            switch (this.getPose()) {
                case EMERGING: {
                    this.addDigParticles(this.emergingAnimationState);
                    break;
                }
                case DIGGING: {
                    this.addDigParticles(this.diggingAnimationState);
                }
            }
        }
    }

    @Override
    protected void mobTick() {
        ServerWorld serverWorld = (ServerWorld)this.world;
        serverWorld.getProfiler().push("wardenBrain");
        this.getBrain().tick(serverWorld, this);
        this.world.getProfiler().pop();
        super.mobTick();
        if ((this.age + this.getId()) % 120 == 0) {
            WardenEntity.addDarknessToClosePlayers(serverWorld, this.getPos(), this, 20);
        }
        if (this.age % 20 == 0) {
            this.angerManager.tick(serverWorld, this::isValidTarget);
            this.updateAnger();
        }
        WardenBrain.updateActivities(this);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 4) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.age);
        } else if (status == 61) {
            this.tendrilPitch = 10;
        } else if (status == 62) {
            this.chargingSonicBoomAnimationState.start(this.age);
        } else {
            super.handleStatus(status);
        }
    }

    private int getHeartRate() {
        float f = (float)this.getAnger() / (float)Angriness.ANGRY.getThreshold();
        return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0f, 1.0f) * 30.0f);
    }

    public float getTendrilPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastTendrilPitch, this.tendrilPitch) / 10.0f;
    }

    public float getHeartPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastHeartbeatCooldown, this.heartbeatCooldown) / 10.0f;
    }

    private void addDigParticles(AnimationState animationState) {
        if ((float)animationState.getTimeRunning() < 4500.0f) {
            Random random = this.getRandom();
            BlockState blockState = this.getSteppingBlockState();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double)MathHelper.nextBetween(random, -0.7f, 0.7f);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7f, 0.7f);
                    this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            switch (this.getPose()) {
                case ROARING: {
                    this.roaringAnimationState.start(this.age);
                    break;
                }
                case SNIFFING: {
                    this.sniffingAnimationState.start(this.age);
                    break;
                }
                case EMERGING: {
                    this.emergingAnimationState.start(this.age);
                    break;
                }
                case DIGGING: {
                    this.diggingAnimationState.start(this.age);
                }
            }
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public boolean isImmuneToExplosion() {
        return this.isDiggingOrEmerging();
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return WardenBrain.create(this, dynamic);
    }

    public Brain<WardenEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            callback.accept(this.gameEventHandler, serverWorld);
        }
    }

    @Override
    public TagKey<GameEvent> getTag() {
        return GameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean triggersAvoidCriterion() {
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Contract(value="null->false")
    public boolean isValidTarget(@Nullable Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        LivingEntity livingEntity = (LivingEntity)entity;
        if (this.world != entity.world) return false;
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity)) return false;
        if (this.isTeammate(entity)) return false;
        if (livingEntity.getType() == EntityType.ARMOR_STAND) return false;
        if (livingEntity.getType() == EntityType.WARDEN) return false;
        if (livingEntity.isInvulnerable()) return false;
        if (livingEntity.isDead()) return false;
        if (!this.world.getWorldBorder().contains(livingEntity.getBoundingBox())) return false;
        return true;
    }

    public static void addDarknessToClosePlayers(ServerWorld world, Vec3d pos, @Nullable Entity entity, int range) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.DARKNESS, 260, 0, false, false);
        StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, pos, range, statusEffectInstance, 200);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        WardenAngerManager.createCodec(this::isValidTarget).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.angerManager).resultOrPartial(arg_0 -> ((Logger)field_38138).error(arg_0)).ifPresent(angerNbt -> nbt.put("anger", (NbtElement)angerNbt));
        VibrationListener.createCodec(this).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.gameEventHandler.getListener()).resultOrPartial(arg_0 -> ((Logger)field_38138).error(arg_0)).ifPresent(nbtElement -> nbt.put("listener", (NbtElement)nbtElement));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("anger")) {
            WardenAngerManager.createCodec(this::isValidTarget).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt.get("anger"))).resultOrPartial(arg_0 -> ((Logger)field_38138).error(arg_0)).ifPresent(angerManager -> {
                this.angerManager = angerManager;
            });
            this.updateAnger();
        }
        if (nbt.contains("listener", 10)) {
            VibrationListener.createCodec(this).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt.getCompound("listener"))).resultOrPartial(arg_0 -> ((Logger)field_38138).error(arg_0)).ifPresent(vibrationListener -> this.gameEventHandler.setListener((VibrationListener)vibrationListener, this.world));
        }
    }

    private void playListeningSound() {
        if (!this.isInPose(EntityPose.ROARING)) {
            this.playSound(this.getAngriness().getListeningSound(), 10.0f, this.getSoundPitch());
        }
    }

    public Angriness getAngriness() {
        return Angriness.getForAnger(this.getAngerAtTarget());
    }

    private int getAngerAtTarget() {
        return this.angerManager.getAngerFor(this.getTarget());
    }

    public void removeSuspect(Entity entity) {
        this.angerManager.removeSuspect(entity);
    }

    public void increaseAngerAt(@Nullable Entity entity) {
        this.increaseAngerAt(entity, 35, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening) {
        if (!this.isAiDisabled() && this.isValidTarget(entity)) {
            WardenBrain.resetDigCooldown(this);
            boolean bl = !(this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) instanceof PlayerEntity);
            int i = this.angerManager.increaseAngerAt(entity, amount);
            if (entity instanceof PlayerEntity && bl && Angriness.getForAnger(i).isAngry()) {
                this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            }
            if (listening) {
                this.playListeningSound();
            }
        }
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        if (this.getAngriness().isAngry()) {
            return this.angerManager.getPrimeSuspect();
        }
        return Optional.empty();
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.getBrain().remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        if (spawnReason == SpawnReason.TRIGGERED) {
            this.setPose(EntityPose.EMERGING);
            this.getBrain().remember(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, WardenBrain.EMERGE_DURATION);
            this.playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0f, 1.0f);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (!(this.world.isClient || this.isAiDisabled() || this.isDiggingOrEmerging())) {
            Entity entity = source.getAttacker();
            this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20, false);
            if (this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!(source instanceof ProjectileDamageSource) || this.isInRange(livingEntity, 5.0)) {
                    this.updateAttackTarget(livingEntity);
                }
            }
        }
        return bl;
    }

    public void updateAttackTarget(LivingEntity target) {
        this.getBrain().forget(MemoryModuleType.ROAR_TARGET);
        UpdateAttackTargetTask.updateAttackTarget(this, target);
        SonicBoomTask.cooldown(this, 200);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        EntityDimensions entityDimensions = super.getDimensions(pose);
        if (this.isDiggingOrEmerging()) {
            return EntityDimensions.fixed(entityDimensions.width, 1.0f);
        }
        return entityDimensions;
    }

    @Override
    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    @Override
    protected void pushAway(Entity entity) {
        if (!this.isAiDisabled() && !this.getBrain().hasMemoryModule(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().remember(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            WardenBrain.lookAtDisturbance(this, entity.getBlockPos());
        }
        super.pushAway(entity);
    }

    @Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
        LivingEntity livingEntity;
        if (this.isAiDisabled() || this.isDead() || this.getBrain().hasMemoryModule(MemoryModuleType.VIBRATION_COOLDOWN) || this.isDiggingOrEmerging() || !world.getWorldBorder().contains(pos) || this.isRemoved() || this.world != world) {
            return false;
        }
        Entity entity = emitter.sourceEntity();
        return !(entity instanceof LivingEntity) || this.isValidTarget(livingEntity = (LivingEntity)entity);
    }

    @Override
    public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance) {
        if (this.isDead()) {
            return;
        }
        this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
        world.sendEntityStatus(this, (byte)61);
        this.playSound(SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0f, this.getSoundPitch());
        BlockPos blockPos = pos;
        if (sourceEntity != null) {
            if (this.isInRange(sourceEntity, 30.0)) {
                if (this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
                    if (this.isValidTarget(sourceEntity)) {
                        blockPos = sourceEntity.getBlockPos();
                    }
                    this.increaseAngerAt(sourceEntity);
                } else {
                    this.increaseAngerAt(sourceEntity, 10, true);
                }
            }
            this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
        } else {
            this.increaseAngerAt(entity);
        }
        if (!this.getAngriness().isAngry()) {
            Optional<LivingEntity> optional = this.angerManager.getPrimeSuspect();
            if (sourceEntity != null || optional.isEmpty() || optional.get() == entity) {
                WardenBrain.lookAtDisturbance(this, blockPos);
            }
        }
    }

    @VisibleForTesting
    public WardenAngerManager getAngerManager() {
        return this.angerManager;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world){

            @Override
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LandPathNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                return new PathNodeNavigator(this.nodeMaker, range){

                    @Override
                    protected float getDistance(PathNode a, PathNode b) {
                        return a.getHorizontalDistance(b);
                    }
                };
            }
        };
    }
}

