/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class LookAtEntityGoal
extends Goal {
    protected final MobEntity mob;
    protected Entity target;
    protected final float range;
    private int lookTime;
    private final float chance;
    protected final Class<? extends LivingEntity> targetType;
    protected final TargetPredicate targetPredicate;

    public LookAtEntityGoal(MobEntity mob, Class<? extends LivingEntity> targetType, float range) {
        this(mob, targetType, range, 0.02f);
    }

    public LookAtEntityGoal(MobEntity mob, Class<? extends LivingEntity> targetType, float range, float chance) {
        this.mob = mob;
        this.targetType = targetType;
        this.range = range;
        this.chance = chance;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
        this.targetPredicate = targetType == PlayerEntity.class ? new TargetPredicate().setBaseMaxDistance(range).includeTeammates().includeInvulnerable().ignoreEntityTargetRules().setPredicate(livingEntity -> EntityPredicates.rides(mob).test((Entity)livingEntity)) : new TargetPredicate().setBaseMaxDistance(range).includeTeammates().includeInvulnerable().ignoreEntityTargetRules();
    }

    @Override
    public boolean canStart() {
        if (this.mob.getRandom().nextFloat() >= this.chance) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            this.target = this.mob.getTarget();
        }
        this.target = this.targetType == PlayerEntity.class ? this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.x, this.mob.y + (double)this.mob.getStandingEyeHeight(), this.mob.z) : this.mob.world.method_21727(this.targetType, this.targetPredicate, this.mob, this.mob.x, this.mob.y + (double)this.mob.getStandingEyeHeight(), this.mob.z, this.mob.getBoundingBox().expand(this.range, 3.0, this.range));
        return this.target != null;
    }

    @Override
    public boolean shouldContinue() {
        if (!this.target.isAlive()) {
            return false;
        }
        if (this.mob.squaredDistanceTo(this.target) > (double)(this.range * this.range)) {
            return false;
        }
        return this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = 40 + this.mob.getRandom().nextInt(40);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.target.x, this.target.y + (double)this.target.getStandingEyeHeight(), this.target.z);
        --this.lookTime;
    }
}

