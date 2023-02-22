/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundGoal
extends Goal {
    protected final MobEntityWithAi mob;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;

    public WanderAroundGoal(MobEntityWithAi mob, double speed) {
        this(mob, speed, 120);
    }

    public WanderAroundGoal(MobEntityWithAi mob, double speed, int chance) {
        this.mob = mob;
        this.speed = speed;
        this.chance = chance;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        Vec3d vec3d;
        if (this.mob.hasPassengers()) {
            return false;
        }
        if (!this.ignoringChance) {
            if (this.mob.getDespawnCounter() >= 100) {
                return false;
            }
            if (this.mob.getRandom().nextInt(this.chance) != 0) {
                return false;
            }
        }
        if ((vec3d = this.getWanderTarget()) == null) {
            return false;
        }
        this.targetX = vec3d.x;
        this.targetY = vec3d.y;
        this.targetZ = vec3d.z;
        this.ignoringChance = false;
        return true;
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return TargetFinder.findTarget(this.mob, 10, 7);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    public void ignoreChanceOnce() {
        this.ignoringChance = true;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
}
