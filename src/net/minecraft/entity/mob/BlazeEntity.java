/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlazeEntity
extends HostileEntity {
    private float field_7214 = 0.5f;
    private int field_7215;
    private static final TrackedData<Byte> BLAZE_FLAGS = DataTracker.registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);

    public BlazeEntity(EntityType<? extends BlazeEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0f);
        this.experiencePoints = 10;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new ShootFireballGoal(this));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 1.0));
        this.goalSelector.add(7, new WanderAroundFarGoal((MobEntityWithAi)this, 1.0, 0.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23f);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BLAZE_FLAGS, (byte)0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getLightmapCoordinates() {
        return 0xF000F0;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    public void tickMovement() {
        if (!this.onGround && this.getVelocity().y < 0.0) {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
        if (this.world.isClient) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.world.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), this.y + this.random.nextDouble() * (double)this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), 0.0, 0.0, 0.0);
            }
        }
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        LivingEntity livingEntity;
        if (this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        --this.field_7215;
        if (this.field_7215 <= 0) {
            this.field_7215 = 100;
            this.field_7214 = 0.5f + (float)this.random.nextGaussian() * 3.0f;
        }
        if ((livingEntity = this.getTarget()) != null && livingEntity.y + (double)livingEntity.getStandingEyeHeight() > this.y + (double)this.getStandingEyeHeight() + (double)this.field_7214 && this.canTarget(livingEntity)) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(this.getVelocity().add(0.0, ((double)0.3f - vec3d.y) * (double)0.3f, 0.0));
            this.velocityDirty = true;
        }
        super.mobTick();
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
    }

    @Override
    public boolean isOnFire() {
        return this.isFireActive();
    }

    private boolean isFireActive() {
        return (this.dataTracker.get(BLAZE_FLAGS) & 1) != 0;
    }

    private void setFireActive(boolean bl) {
        byte b = this.dataTracker.get(BLAZE_FLAGS);
        b = bl ? (byte)(b | 1) : (byte)(b & 0xFFFFFFFE);
        this.dataTracker.set(BLAZE_FLAGS, b);
    }

    static class ShootFireballGoal
    extends Goal {
        private final BlazeEntity blaze;
        private int field_7218;
        private int field_7217;
        private int field_19420;

        public ShootFireballGoal(BlazeEntity blaze) {
            this.blaze = blaze;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.blaze.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.blaze.canTarget(livingEntity);
        }

        @Override
        public void start() {
            this.field_7218 = 0;
        }

        @Override
        public void stop() {
            this.blaze.setFireActive(false);
            this.field_19420 = 0;
        }

        @Override
        public void tick() {
            --this.field_7217;
            LivingEntity livingEntity = this.blaze.getTarget();
            if (livingEntity == null) {
                return;
            }
            boolean bl = this.blaze.getVisibilityCache().canSee(livingEntity);
            this.field_19420 = bl ? 0 : ++this.field_19420;
            double d = this.blaze.squaredDistanceTo(livingEntity);
            if (d < 4.0) {
                if (!bl) {
                    return;
                }
                if (this.field_7217 <= 0) {
                    this.field_7217 = 20;
                    this.blaze.tryAttack(livingEntity);
                }
                this.blaze.getMoveControl().moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
            } else if (d < this.method_6995() * this.method_6995() && bl) {
                double e = livingEntity.x - this.blaze.x;
                double f = livingEntity.getBoundingBox().y1 + (double)(livingEntity.getHeight() / 2.0f) - (this.blaze.y + (double)(this.blaze.getHeight() / 2.0f));
                double g = livingEntity.z - this.blaze.z;
                if (this.field_7217 <= 0) {
                    ++this.field_7218;
                    if (this.field_7218 == 1) {
                        this.field_7217 = 60;
                        this.blaze.setFireActive(true);
                    } else if (this.field_7218 <= 4) {
                        this.field_7217 = 6;
                    } else {
                        this.field_7217 = 100;
                        this.field_7218 = 0;
                        this.blaze.setFireActive(false);
                    }
                    if (this.field_7218 > 1) {
                        float h = MathHelper.sqrt(MathHelper.sqrt(d)) * 0.5f;
                        this.blaze.world.playLevelEvent(null, 1018, new BlockPos(this.blaze), 0);
                        for (int i = 0; i < 1; ++i) {
                            SmallFireballEntity smallFireballEntity = new SmallFireballEntity(this.blaze.world, this.blaze, e + this.blaze.getRandom().nextGaussian() * (double)h, f, g + this.blaze.getRandom().nextGaussian() * (double)h);
                            smallFireballEntity.y = this.blaze.y + (double)(this.blaze.getHeight() / 2.0f) + 0.5;
                            this.blaze.world.spawnEntity(smallFireballEntity);
                        }
                    }
                }
                this.blaze.getLookControl().lookAt(livingEntity, 10.0f, 10.0f);
            } else if (this.field_19420 < 5) {
                this.blaze.getMoveControl().moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
            }
            super.tick();
        }

        private double method_6995() {
            return this.blaze.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).getValue();
        }
    }
}

