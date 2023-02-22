/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class ProjectileAttackGoal
extends Goal {
    private final MobEntity mob;
    private final RangedAttackMob owner;
    private LivingEntity target;
    private int field_6581 = -1;
    private final double mobSpeed;
    private int field_6579;
    private final int field_6578;
    private final int field_6577;
    private final float maxShootRange;
    private final float squaredMaxShootRange;

    public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
        this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange);
    }

    public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
        if (!(mob instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.owner = mob;
        this.mob = (MobEntity)((Object)mob);
        this.mobSpeed = mobSpeed;
        this.field_6578 = minIntervalTicks;
        this.field_6577 = maxIntervalTicks;
        this.maxShootRange = maxShootRange;
        this.squaredMaxShootRange = maxShootRange * maxShootRange;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart() || !this.mob.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        this.target = null;
        this.field_6579 = 0;
        this.field_6581 = -1;
    }

    @Override
    public void tick() {
        double d = this.mob.squaredDistanceTo(this.target.x, this.target.getBoundingBox().y1, this.target.z);
        boolean bl = this.mob.getVisibilityCache().canSee(this.target);
        this.field_6579 = bl ? ++this.field_6579 : 0;
        if (d > (double)this.squaredMaxShootRange || this.field_6579 < 5) {
            this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
        } else {
            this.mob.getNavigation().stop();
        }
        this.mob.getLookControl().lookAt(this.target, 30.0f, 30.0f);
        if (--this.field_6581 == 0) {
            float f;
            if (!bl) {
                return;
            }
            float g = f = MathHelper.sqrt(d) / this.maxShootRange;
            g = MathHelper.clamp(g, 0.1f, 1.0f);
            this.owner.attack(this.target, g);
            this.field_6581 = MathHelper.floor(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
        } else if (this.field_6581 < 0) {
            float f = MathHelper.sqrt(d) / this.maxShootRange;
            this.field_6581 = MathHelper.floor(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
        }
    }
}

