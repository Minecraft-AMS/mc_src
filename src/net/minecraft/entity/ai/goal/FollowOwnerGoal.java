/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.CollisionView;

public class FollowOwnerGoal
extends Goal {
    protected final TameableEntity tameable;
    private LivingEntity owner;
    protected final CollisionView world;
    private final double field_6442;
    private final EntityNavigation navigation;
    private int field_6443;
    private final float maxDistance;
    private final float minDistance;
    private float field_6447;

    public FollowOwnerGoal(TameableEntity tameable, double speed, float minDistance, float maxDistance) {
        this.tameable = tameable;
        this.world = tameable.world;
        this.field_6442 = speed;
        this.navigation = tameable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(tameable.getNavigation() instanceof MobNavigation) && !(tameable.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).isSpectator()) {
            return false;
        }
        if (this.tameable.isSitting()) {
            return false;
        }
        if (this.tameable.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
            return false;
        }
        this.owner = livingEntity;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return !this.navigation.isIdle() && this.tameable.squaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance) && !this.tameable.isSitting();
    }

    @Override
    public void start() {
        this.field_6443 = 0;
        this.field_6447 = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.field_6447);
    }

    @Override
    public void tick() {
        this.tameable.getLookControl().lookAt(this.owner, 10.0f, this.tameable.getLookPitchSpeed());
        if (this.tameable.isSitting()) {
            return;
        }
        if (--this.field_6443 > 0) {
            return;
        }
        this.field_6443 = 10;
        if (this.navigation.startMovingTo(this.owner, this.field_6442)) {
            return;
        }
        if (this.tameable.isLeashed() || this.tameable.hasVehicle()) {
            return;
        }
        if (this.tameable.squaredDistanceTo(this.owner) < 144.0) {
            return;
        }
        int i = MathHelper.floor(this.owner.x) - 2;
        int j = MathHelper.floor(this.owner.z) - 2;
        int k = MathHelper.floor(this.owner.getBoundingBox().y1);
        for (int l = 0; l <= 4; ++l) {
            for (int m = 0; m <= 4; ++m) {
                if (l >= 1 && m >= 1 && l <= 3 && m <= 3 || !this.method_6263(new BlockPos(i + l, k - 1, j + m))) continue;
                this.tameable.refreshPositionAndAngles((float)(i + l) + 0.5f, k, (float)(j + m) + 0.5f, this.tameable.yaw, this.tameable.pitch);
                this.navigation.stop();
                return;
            }
        }
    }

    protected boolean method_6263(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        return blockState.allowsSpawning(this.world, pos, this.tameable.getType()) && this.world.isAir(pos.up()) && this.world.isAir(pos.up(2));
    }
}

