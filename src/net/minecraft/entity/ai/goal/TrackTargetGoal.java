/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.scoreboard.AbstractTeam;
import org.jetbrains.annotations.Nullable;

public abstract class TrackTargetGoal
extends Goal {
    private static final int UNSET = 0;
    private static final int CAN_TRACK = 1;
    private static final int CANNOT_TRACK = 2;
    protected final MobEntity mob;
    protected final boolean checkVisibility;
    private final boolean checkCanNavigate;
    private int canNavigateFlag;
    private int checkCanNavigateCooldown;
    private int timeWithoutVisibility;
    @Nullable
    protected LivingEntity target;
    protected int maxTimeWithoutVisibility = 60;

    public TrackTargetGoal(MobEntity mob, boolean checkVisibility) {
        this(mob, checkVisibility, false);
    }

    public TrackTargetGoal(MobEntity mob, boolean checkVisibility, boolean checkNavigable) {
        this.mob = mob;
        this.checkVisibility = checkVisibility;
        this.checkCanNavigate = checkNavigable;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            livingEntity = this.target;
        }
        if (livingEntity == null) {
            return false;
        }
        if (!this.mob.canTarget(livingEntity)) {
            return false;
        }
        AbstractTeam abstractTeam = this.mob.getScoreboardTeam();
        AbstractTeam abstractTeam2 = livingEntity.getScoreboardTeam();
        if (abstractTeam != null && abstractTeam2 == abstractTeam) {
            return false;
        }
        double d = this.getFollowRange();
        if (this.mob.squaredDistanceTo(livingEntity) > d * d) {
            return false;
        }
        if (this.checkVisibility) {
            if (this.mob.getVisibilityCache().canSee(livingEntity)) {
                this.timeWithoutVisibility = 0;
            } else if (++this.timeWithoutVisibility > TrackTargetGoal.toGoalTicks(this.maxTimeWithoutVisibility)) {
                return false;
            }
        }
        this.mob.setTarget(livingEntity);
        return true;
    }

    protected double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    @Override
    public void start() {
        this.canNavigateFlag = 0;
        this.checkCanNavigateCooldown = 0;
        this.timeWithoutVisibility = 0;
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.target = null;
    }

    protected boolean canTrack(@Nullable LivingEntity target, TargetPredicate targetPredicate) {
        if (target == null) {
            return false;
        }
        if (!targetPredicate.test(this.mob, target)) {
            return false;
        }
        if (!this.mob.isInWalkTargetRange(target.getBlockPos())) {
            return false;
        }
        if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) {
                this.canNavigateFlag = 0;
            }
            if (this.canNavigateFlag == 0) {
                int n = this.canNavigateFlag = this.canNavigateToEntity(target) ? 1 : 2;
            }
            if (this.canNavigateFlag == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean canNavigateToEntity(LivingEntity entity) {
        int j;
        this.checkCanNavigateCooldown = TrackTargetGoal.toGoalTicks(10 + this.mob.getRandom().nextInt(5));
        Path path = this.mob.getNavigation().findPathTo(entity, 0);
        if (path == null) {
            return false;
        }
        PathNode pathNode = path.getEnd();
        if (pathNode == null) {
            return false;
        }
        int i = pathNode.x - entity.getBlockX();
        return (double)(i * i + (j = pathNode.z - entity.getBlockZ()) * j) <= 2.25;
    }

    public TrackTargetGoal setMaxTimeWithoutVisibility(int time) {
        this.maxTimeWithoutVisibility = time;
        return this;
    }
}

