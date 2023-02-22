/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PatrolEntity
extends HostileEntity {
    private BlockPos patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;

    protected PatrolEntity(EntityType<? extends PatrolEntity> type, World world) {
        super((EntityType<? extends HostileEntity>)type, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(4, new PatrolGoal<PatrolEntity>(this, 0.7, 0.595));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.patrolTarget != null) {
            tag.put("PatrolTarget", NbtHelper.fromBlockPos(this.patrolTarget));
        }
        tag.putBoolean("PatrolLeader", this.patrolLeader);
        tag.putBoolean("Patrolling", this.patrolling);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("PatrolTarget")) {
            this.patrolTarget = NbtHelper.toBlockPos(tag.getCompound("PatrolTarget"));
        }
        this.patrolLeader = tag.getBoolean("PatrolLeader");
        this.patrolling = tag.getBoolean("Patrolling");
    }

    @Override
    public double getHeightOffset() {
        return -0.45;
    }

    public boolean canLead() {
        return true;
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        if (spawnType != SpawnType.PATROL && spawnType != SpawnType.EVENT && spawnType != SpawnType.STRUCTURE && this.random.nextFloat() < 0.06f && this.canLead()) {
            this.patrolLeader = true;
        }
        if (this.isPatrolLeader()) {
            this.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
            this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
        }
        if (spawnType == SpawnType.PATROL) {
            this.patrolling = true;
        }
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    public static boolean method_20739(EntityType<? extends PatrolEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        if (iWorld.getLightLevel(LightType.BLOCK, blockPos) > 8) {
            return false;
        }
        return PatrolEntity.method_20681(entityType, iWorld, spawnType, blockPos, random);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.patrolling || distanceSquared > 16384.0;
    }

    public void setPatrolTarget(BlockPos targetPos) {
        this.patrolTarget = targetPos;
        this.patrolling = true;
    }

    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }

    public boolean hasPatrolTarget() {
        return this.patrolTarget != null;
    }

    public void setPatrolLeader(boolean patrolLeader) {
        this.patrolLeader = patrolLeader;
        this.patrolling = true;
    }

    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }

    public boolean hasNoRaid() {
        return true;
    }

    public void setRandomPatrolTarget() {
        this.patrolTarget = new BlockPos(this).add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }

    protected boolean isRaidCenterSet() {
        return this.patrolling;
    }

    public static class PatrolGoal<T extends PatrolEntity>
    extends Goal {
        private final T actor;
        private final double leaderSpeed;
        private final double fellowSpeed;

        public PatrolGoal(T actor, double leaderSpeed, double fellowSpeed) {
            this.actor = actor;
            this.leaderSpeed = leaderSpeed;
            this.fellowSpeed = fellowSpeed;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return ((PatrolEntity)this.actor).isRaidCenterSet() && ((MobEntity)this.actor).getTarget() == null && !((Entity)this.actor).hasPassengers() && ((PatrolEntity)this.actor).hasPatrolTarget();
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            boolean bl = ((PatrolEntity)this.actor).isPatrolLeader();
            EntityNavigation entityNavigation = ((MobEntity)this.actor).getNavigation();
            if (entityNavigation.isIdle()) {
                if (!bl || !((PatrolEntity)this.actor).getPatrolTarget().isWithinDistance(((Entity)this.actor).getPos(), 10.0)) {
                    Vec3d vec3d = new Vec3d(((PatrolEntity)this.actor).getPatrolTarget());
                    Vec3d vec3d2 = new Vec3d(((PatrolEntity)this.actor).x, ((PatrolEntity)this.actor).y, ((PatrolEntity)this.actor).z);
                    Vec3d vec3d3 = vec3d2.subtract(vec3d);
                    vec3d = vec3d3.rotateY(90.0f).multiply(0.4).add(vec3d);
                    Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
                    BlockPos blockPos = new BlockPos(vec3d4);
                    if (!entityNavigation.startMovingTo((blockPos = ((PatrolEntity)this.actor).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos)).getX(), blockPos.getY(), blockPos.getZ(), bl ? this.fellowSpeed : this.leaderSpeed)) {
                        this.wander();
                    } else if (bl) {
                        List<PatrolEntity> list = ((PatrolEntity)this.actor).world.getEntities(PatrolEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0), patrolEntity -> !patrolEntity.isPatrolLeader() && patrolEntity.hasNoRaid());
                        for (PatrolEntity patrolEntity2 : list) {
                            patrolEntity2.setPatrolTarget(blockPos);
                        }
                    }
                } else {
                    ((PatrolEntity)this.actor).setRandomPatrolTarget();
                }
            }
        }

        private void wander() {
            Random random = ((LivingEntity)this.actor).getRandom();
            BlockPos blockPos = ((PatrolEntity)this.actor).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos((Entity)this.actor).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
            ((MobEntity)this.actor).getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.leaderSpeed);
        }
    }
}

