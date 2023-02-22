/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;

public abstract class MoveToTargetPosGoal
extends Goal {
    protected final MobEntityWithAi mob;
    public final double speed;
    protected int cooldown;
    protected int tryingTime;
    private int safeWaitingTime;
    protected BlockPos targetPos = BlockPos.ORIGIN;
    private boolean reached;
    private final int range;
    private final int maxYDifference;
    protected int lowestY;

    public MoveToTargetPosGoal(MobEntityWithAi mob, double speed, int range) {
        this(mob, speed, range, 1);
    }

    public MoveToTargetPosGoal(MobEntityWithAi mob, double speed, int range, int maxYDifference) {
        this.mob = mob;
        this.speed = speed;
        this.range = range;
        this.lowestY = 0;
        this.maxYDifference = maxYDifference;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = this.getInterval(this.mob);
        return this.findTargetPos();
    }

    protected int getInterval(MobEntityWithAi mob) {
        return 200 + mob.getRandom().nextInt(200);
    }

    @Override
    public boolean shouldContinue() {
        return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.mob.world, this.targetPos);
    }

    @Override
    public void start() {
        this.startMovingToTarget();
        this.tryingTime = 0;
        this.safeWaitingTime = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
    }

    protected void startMovingToTarget() {
        this.mob.getNavigation().startMovingTo((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, this.speed);
    }

    public double getDesiredSquaredDistanceToTarget() {
        return 1.0;
    }

    @Override
    public void tick() {
        if (!this.targetPos.up().isWithinDistance(this.mob.getPos(), this.getDesiredSquaredDistanceToTarget())) {
            this.reached = false;
            ++this.tryingTime;
            if (this.shouldResetPath()) {
                this.mob.getNavigation().startMovingTo((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, this.speed);
            }
        } else {
            this.reached = true;
            --this.tryingTime;
        }
    }

    public boolean shouldResetPath() {
        return this.tryingTime % 40 == 0;
    }

    protected boolean hasReached() {
        return this.reached;
    }

    protected boolean findTargetPos() {
        int i = this.range;
        int j = this.maxYDifference;
        BlockPos blockPos = new BlockPos(this.mob);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int k = this.lowestY;
        while (k <= j) {
            for (int l = 0; l < i; ++l) {
                int m = 0;
                while (m <= l) {
                    int n;
                    int n2 = n = m < l && m > -l ? l : 0;
                    while (n <= l) {
                        mutable.set(blockPos).setOffset(m, k - 1, n);
                        if (this.mob.isInWalkTargetRange(mutable) && this.isTargetPos(this.mob.world, mutable)) {
                            this.targetPos = mutable;
                            return true;
                        }
                        n = n > 0 ? -n : 1 - n;
                    }
                    m = m > 0 ? -m : 1 - m;
                }
            }
            k = k > 0 ? -k : 1 - k;
        }
        return false;
    }

    protected abstract boolean isTargetPos(CollisionView var1, BlockPos var2);
}

