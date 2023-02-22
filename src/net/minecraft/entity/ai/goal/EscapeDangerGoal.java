/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class EscapeDangerGoal
extends Goal {
    protected final MobEntityWithAi mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;

    public EscapeDangerGoal(MobEntityWithAi mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        BlockPos blockPos;
        if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
            return false;
        }
        if (this.mob.isOnFire() && (blockPos = this.locateClosestWater(this.mob.world, this.mob, 5, 4)) != null) {
            this.targetX = blockPos.getX();
            this.targetY = blockPos.getY();
            this.targetZ = blockPos.getZ();
            return true;
        }
        return this.findTarget();
    }

    protected boolean findTarget() {
        Vec3d vec3d = TargetFinder.findTarget(this.mob, 5, 4);
        if (vec3d == null) {
            return false;
        }
        this.targetX = vec3d.x;
        this.targetY = vec3d.y;
        this.targetZ = vec3d.z;
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Nullable
    protected BlockPos locateClosestWater(BlockView blockView, Entity entity, int rangeX, int rangeY) {
        BlockPos blockPos = new BlockPos(entity);
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        float f = rangeX * rangeX * rangeY * 2;
        BlockPos blockPos2 = null;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = i - rangeX; l <= i + rangeX; ++l) {
            for (int m = j - rangeY; m <= j + rangeY; ++m) {
                for (int n = k - rangeX; n <= k + rangeX; ++n) {
                    float g;
                    mutable.set(l, m, n);
                    if (!blockView.getFluidState(mutable).matches(FluidTags.WATER) || !((g = (float)((l - i) * (l - i) + (m - j) * (m - j) + (n - k) * (n - k))) < f)) continue;
                    f = g;
                    blockPos2 = new BlockPos(mutable);
                }
            }
        }
        return blockPos2;
    }
}
