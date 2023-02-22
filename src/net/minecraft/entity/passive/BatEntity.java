/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BatEntity
extends AmbientEntity {
    private static final TrackedData<Byte> BAT_FLAGS = DataTracker.registerData(BatEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(4.0).includeTeammates();
    private BlockPos hangingPosition;

    public BatEntity(EntityType<? extends BatEntity> entityType, World world) {
        super((EntityType<? extends AmbientEntity>)entityType, world);
        this.setRoosting(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BAT_FLAGS, (byte)0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1f;
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.95f;
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        if (this.isRoosting() && this.random.nextInt(4) != 0) {
            return null;
        }
        return SoundEvents.ENTITY_BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BAT_DEATH;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
    }

    @Override
    protected void tickCramming() {
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
    }

    public boolean isRoosting() {
        return (this.dataTracker.get(BAT_FLAGS) & 1) != 0;
    }

    public void setRoosting(boolean bl) {
        byte b = this.dataTracker.get(BAT_FLAGS);
        if (bl) {
            this.dataTracker.set(BAT_FLAGS, (byte)(b | 1));
        } else {
            this.dataTracker.set(BAT_FLAGS, (byte)(b & 0xFFFFFFFE));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRoosting()) {
            this.setVelocity(Vec3d.ZERO);
            this.y = (double)MathHelper.floor(this.y) + 1.0 - (double)this.getHeight();
        } else {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        BlockPos blockPos = new BlockPos(this);
        BlockPos blockPos2 = blockPos.up();
        if (this.isRoosting()) {
            if (this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos)) {
                if (this.random.nextInt(200) == 0) {
                    this.headYaw = this.random.nextInt(360);
                }
                if (this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this) != null) {
                    this.setRoosting(false);
                    this.world.playLevelEvent(null, 1025, blockPos, 0);
                }
            } else {
                this.setRoosting(false);
                this.world.playLevelEvent(null, 1025, blockPos, 0);
            }
        } else {
            if (!(this.hangingPosition == null || this.world.isAir(this.hangingPosition) && this.hangingPosition.getY() >= 1)) {
                this.hangingPosition = null;
            }
            if (this.hangingPosition == null || this.random.nextInt(30) == 0 || this.hangingPosition.isWithinDistance(this.getPos(), 2.0)) {
                this.hangingPosition = new BlockPos(this.x + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.y + (double)this.random.nextInt(6) - 2.0, this.z + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }
            double d = (double)this.hangingPosition.getX() + 0.5 - this.x;
            double e = (double)this.hangingPosition.getY() + 0.1 - this.y;
            double f = (double)this.hangingPosition.getZ() + 0.5 - this.z;
            Vec3d vec3d = this.getVelocity();
            Vec3d vec3d2 = vec3d.add((Math.signum(d) * 0.5 - vec3d.x) * (double)0.1f, (Math.signum(e) * (double)0.7f - vec3d.y) * (double)0.1f, (Math.signum(f) * 0.5 - vec3d.z) * (double)0.1f);
            this.setVelocity(vec3d2);
            float g = (float)(MathHelper.atan2(vec3d2.z, vec3d2.x) * 57.2957763671875) - 90.0f;
            float h = MathHelper.wrapDegrees(g - this.yaw);
            this.forwardSpeed = 0.5f;
            this.yaw += h;
            if (this.random.nextInt(100) == 0 && this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos2)) {
                this.setRoosting(true);
            }
        }
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.world.isClient && this.isRoosting()) {
            this.setRoosting(false);
        }
        return super.damage(source, amount);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.dataTracker.set(BAT_FLAGS, tag.getByte("BatFlags"));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putByte("BatFlags", this.dataTracker.get(BAT_FLAGS));
    }

    public static boolean method_20661(EntityType<BatEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        if (blockPos.getY() >= iWorld.getSeaLevel()) {
            return false;
        }
        int i = iWorld.getLightLevel(blockPos);
        int j = 4;
        if (BatEntity.isTodayAroundHalloween()) {
            j = 7;
        } else if (random.nextBoolean()) {
            return false;
        }
        if (i > random.nextInt(j)) {
            return false;
        }
        return BatEntity.method_20636(entityType, iWorld, spawnType, blockPos, random);
    }

    private static boolean isTodayAroundHalloween() {
        LocalDate localDate = LocalDate.now();
        int i = localDate.get(ChronoField.DAY_OF_MONTH);
        int j = localDate.get(ChronoField.MONTH_OF_YEAR);
        return j == 10 && i >= 20 || j == 11 && i <= 3;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height / 2.0f;
    }
}
