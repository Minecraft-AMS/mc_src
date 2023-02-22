/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GuardianEntity
extends HostileEntity {
    private static final TrackedData<Boolean> SPIKES_RETRACTED = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BEAM_TARGET_ID = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected float spikesExtension;
    protected float prevSpikesExtension;
    protected float spikesExtensionRate;
    protected float tailAngle;
    protected float prevTailAngle;
    private LivingEntity cachedBeamTarget;
    private int beamTicks;
    private boolean flopping;
    protected WanderAroundGoal wanderGoal;

    public GuardianEntity(EntityType<? extends GuardianEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.experiencePoints = 10;
        this.moveControl = new GuardianMoveControl(this);
        this.prevSpikesExtension = this.spikesExtension = this.random.nextFloat();
    }

    @Override
    protected void initGoals() {
        GoToWalkTargetGoal goToWalkTargetGoal = new GoToWalkTargetGoal(this, 1.0);
        this.wanderGoal = new WanderAroundGoal(this, 1.0, 80);
        this.goalSelector.add(4, new FireBeamGoal(this));
        this.goalSelector.add(5, goToWalkTargetGoal);
        this.goalSelector.add(7, this.wanderGoal);
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0f, 0.01f));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.wanderGoal.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        goToWalkTargetGoal.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.targetSelector.add(1, new FollowTargetGoal<LivingEntity>(this, LivingEntity.class, 10, true, false, new GuardianTargetPredicate(this)));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPIKES_RETRACTED, false);
        this.dataTracker.startTracking(BEAM_TARGET_ID, 0);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    public boolean areSpikesRetracted() {
        return this.dataTracker.get(SPIKES_RETRACTED);
    }

    private void setSpikesRetracted(boolean retracted) {
        this.dataTracker.set(SPIKES_RETRACTED, retracted);
    }

    public int getWarmupTime() {
        return 80;
    }

    private void setBeamTarget(int progress) {
        this.dataTracker.set(BEAM_TARGET_ID, progress);
    }

    public boolean hasBeamTarget() {
        return this.dataTracker.get(BEAM_TARGET_ID) != 0;
    }

    @Nullable
    public LivingEntity getBeamTarget() {
        if (!this.hasBeamTarget()) {
            return null;
        }
        if (this.world.isClient) {
            if (this.cachedBeamTarget != null) {
                return this.cachedBeamTarget;
            }
            Entity entity = this.world.getEntityById(this.dataTracker.get(BEAM_TARGET_ID));
            if (entity instanceof LivingEntity) {
                this.cachedBeamTarget = (LivingEntity)entity;
                return this.cachedBeamTarget;
            }
            return null;
        }
        return this.getTarget();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (BEAM_TARGET_ID.equals(data)) {
            this.beamTicks = 0;
            this.cachedBeamTarget = null;
        }
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.5f;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, CollisionView world) {
        if (world.getFluidState(pos).matches(FluidTags.WATER)) {
            return 10.0f + world.getBrightness(pos) - 0.5f;
        }
        return super.getPathfindingFavor(pos, world);
    }

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            if (this.world.isClient) {
                Vec3d vec3d;
                this.prevSpikesExtension = this.spikesExtension;
                if (!this.isTouchingWater()) {
                    this.spikesExtensionRate = 2.0f;
                    vec3d = this.getVelocity();
                    if (vec3d.y > 0.0 && this.flopping && !this.isSilent()) {
                        this.world.playSound(this.x, this.y, this.z, this.getFlopSound(), this.getSoundCategory(), 1.0f, 1.0f, false);
                    }
                    this.flopping = vec3d.y < 0.0 && this.world.isTopSolid(new BlockPos(this).down(), this);
                } else {
                    this.spikesExtensionRate = this.areSpikesRetracted() ? (this.spikesExtensionRate < 0.5f ? 4.0f : (this.spikesExtensionRate += (0.5f - this.spikesExtensionRate) * 0.1f)) : (this.spikesExtensionRate += (0.125f - this.spikesExtensionRate) * 0.2f);
                }
                this.spikesExtension += this.spikesExtensionRate;
                this.prevTailAngle = this.tailAngle;
                this.tailAngle = !this.isInsideWaterOrBubbleColumn() ? this.random.nextFloat() : (this.areSpikesRetracted() ? (this.tailAngle += (0.0f - this.tailAngle) * 0.25f) : (this.tailAngle += (1.0f - this.tailAngle) * 0.06f));
                if (this.areSpikesRetracted() && this.isTouchingWater()) {
                    vec3d = this.getRotationVec(0.0f);
                    for (int i = 0; i < 2; ++i) {
                        this.world.addParticle(ParticleTypes.BUBBLE, this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth() - vec3d.x * 1.5, this.y + this.random.nextDouble() * (double)this.getHeight() - vec3d.y * 1.5, this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth() - vec3d.z * 1.5, 0.0, 0.0, 0.0);
                    }
                }
                if (this.hasBeamTarget()) {
                    LivingEntity livingEntity;
                    if (this.beamTicks < this.getWarmupTime()) {
                        ++this.beamTicks;
                    }
                    if ((livingEntity = this.getBeamTarget()) != null) {
                        this.getLookControl().lookAt(livingEntity, 90.0f, 90.0f);
                        this.getLookControl().tick();
                        double d = this.getBeamProgress(0.0f);
                        double e = livingEntity.x - this.x;
                        double f = livingEntity.y + (double)(livingEntity.getHeight() * 0.5f) - (this.y + (double)this.getStandingEyeHeight());
                        double g = livingEntity.z - this.z;
                        double h = Math.sqrt(e * e + f * f + g * g);
                        e /= h;
                        f /= h;
                        g /= h;
                        double j = this.random.nextDouble();
                        while (j < h) {
                            this.world.addParticle(ParticleTypes.BUBBLE, this.x + e * (j += 1.8 - d + this.random.nextDouble() * (1.7 - d)), this.y + f * j + (double)this.getStandingEyeHeight(), this.z + g * j, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            if (this.isInsideWaterOrBubbleColumn()) {
                this.setAir(300);
            } else if (this.onGround) {
                this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.4f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f));
                this.yaw = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.velocityDirty = true;
            }
            if (this.hasBeamTarget()) {
                this.yaw = this.headYaw;
            }
        }
        super.tickMovement();
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_GUARDIAN_FLOP;
    }

    @Environment(value=EnvType.CLIENT)
    public float getSpikesExtension(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevSpikesExtension, this.spikesExtension);
    }

    @Environment(value=EnvType.CLIENT)
    public float getTailAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevTailAngle, this.tailAngle);
    }

    public float getBeamProgress(float tickDelta) {
        return ((float)this.beamTicks + tickDelta) / (float)this.getWarmupTime();
    }

    @Override
    public boolean canSpawn(CollisionView world) {
        return world.intersectsEntities(this);
    }

    public static boolean method_20676(EntityType<? extends GuardianEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return !(random.nextInt(20) != 0 && iWorld.method_8626(blockPos) || iWorld.getDifficulty() == Difficulty.PEACEFUL || spawnType != SpawnType.SPAWNER && !iWorld.getFluidState(blockPos).matches(FluidTags.WATER));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.areSpikesRetracted() && !source.getMagic() && source.getSource() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)source.getSource();
            if (!source.isExplosive()) {
                livingEntity.damage(DamageSource.thorns(this), 2.0f);
            }
        }
        if (this.wanderGoal != null) {
            this.wanderGoal.ignoreChanceOnce();
        }
        return super.damage(source, amount);
    }

    @Override
    public int getLookPitchSpeed() {
        return 180;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (!this.areSpikesRetracted() && this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    static class GuardianMoveControl
    extends MoveControl {
        private final GuardianEntity guardian;

        public GuardianMoveControl(GuardianEntity guardian) {
            super(guardian);
            this.guardian = guardian;
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO || this.guardian.getNavigation().isIdle()) {
                this.guardian.setMovementSpeed(0.0f);
                this.guardian.setSpikesRetracted(false);
                return;
            }
            Vec3d vec3d = new Vec3d(this.targetX - this.guardian.x, this.targetY - this.guardian.y, this.targetZ - this.guardian.z);
            double d = vec3d.length();
            double e = vec3d.x / d;
            double f = vec3d.y / d;
            double g = vec3d.z / d;
            float h = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875) - 90.0f;
            this.guardian.field_6283 = this.guardian.yaw = this.changeAngle(this.guardian.yaw, h, 90.0f);
            float i = (float)(this.speed * this.guardian.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            float j = MathHelper.lerp(0.125f, this.guardian.getMovementSpeed(), i);
            this.guardian.setMovementSpeed(j);
            double k = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.5) * 0.05;
            double l = Math.cos(this.guardian.yaw * ((float)Math.PI / 180));
            double m = Math.sin(this.guardian.yaw * ((float)Math.PI / 180));
            double n = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.75) * 0.05;
            this.guardian.setVelocity(this.guardian.getVelocity().add(k * l, n * (m + l) * 0.25 + (double)j * f * 0.1, k * m));
            LookControl lookControl = this.guardian.getLookControl();
            double o = this.guardian.x + e * 2.0;
            double p = (double)this.guardian.getStandingEyeHeight() + this.guardian.y + f / d;
            double q = this.guardian.z + g * 2.0;
            double r = lookControl.getLookX();
            double s = lookControl.getLookY();
            double t = lookControl.getLookZ();
            if (!lookControl.isActive()) {
                r = o;
                s = p;
                t = q;
            }
            this.guardian.getLookControl().lookAt(MathHelper.lerp(0.125, r, o), MathHelper.lerp(0.125, s, p), MathHelper.lerp(0.125, t, q), 10.0f, 40.0f);
            this.guardian.setSpikesRetracted(true);
        }
    }

    static class FireBeamGoal
    extends Goal {
        private final GuardianEntity guardian;
        private int beamTicks;
        private final boolean elder;

        public FireBeamGoal(GuardianEntity guardian) {
            this.guardian = guardian;
            this.elder = guardian instanceof ElderGuardianEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.guardian.getTarget();
            return livingEntity != null && livingEntity.isAlive();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && (this.elder || this.guardian.squaredDistanceTo(this.guardian.getTarget()) > 9.0);
        }

        @Override
        public void start() {
            this.beamTicks = -10;
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().lookAt(this.guardian.getTarget(), 90.0f, 90.0f);
            this.guardian.velocityDirty = true;
        }

        @Override
        public void stop() {
            this.guardian.setBeamTarget(0);
            this.guardian.setTarget(null);
            this.guardian.wanderGoal.ignoreChanceOnce();
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.guardian.getTarget();
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().lookAt(livingEntity, 90.0f, 90.0f);
            if (!this.guardian.canSee(livingEntity)) {
                this.guardian.setTarget(null);
                return;
            }
            ++this.beamTicks;
            if (this.beamTicks == 0) {
                this.guardian.setBeamTarget(this.guardian.getTarget().getEntityId());
                this.guardian.world.sendEntityStatus(this.guardian, (byte)21);
            } else if (this.beamTicks >= this.guardian.getWarmupTime()) {
                float f = 1.0f;
                if (this.guardian.world.getDifficulty() == Difficulty.HARD) {
                    f += 2.0f;
                }
                if (this.elder) {
                    f += 2.0f;
                }
                livingEntity.damage(DamageSource.magic(this.guardian, this.guardian), f);
                livingEntity.damage(DamageSource.mob(this.guardian), (float)this.guardian.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
                this.guardian.setTarget(null);
            }
            super.tick();
        }
    }

    static class GuardianTargetPredicate
    implements Predicate<LivingEntity> {
        private final GuardianEntity owner;

        public GuardianTargetPredicate(GuardianEntity owner) {
            this.owner = owner;
        }

        @Override
        public boolean test(@Nullable LivingEntity livingEntity) {
            return (livingEntity instanceof PlayerEntity || livingEntity instanceof SquidEntity) && livingEntity.squaredDistanceTo(this.owner) > 9.0;
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object context) {
            return this.test((LivingEntity)context);
        }
    }
}
