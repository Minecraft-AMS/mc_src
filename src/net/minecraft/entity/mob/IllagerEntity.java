/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public abstract class IllagerEntity
extends RaiderEntity {
    protected IllagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super((EntityType<? extends RaiderEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ILLAGER;
    }

    public State getState() {
        return State.CROSSED;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target instanceof MerchantEntity && target.isBaby()) {
            return false;
        }
        return super.canTarget(target);
    }

    public static final class State
    extends Enum<State> {
        public static final /* enum */ State CROSSED = new State();
        public static final /* enum */ State ATTACKING = new State();
        public static final /* enum */ State SPELLCASTING = new State();
        public static final /* enum */ State BOW_AND_ARROW = new State();
        public static final /* enum */ State CROSSBOW_HOLD = new State();
        public static final /* enum */ State CROSSBOW_CHARGE = new State();
        public static final /* enum */ State CELEBRATING = new State();
        public static final /* enum */ State NEUTRAL = new State();
        private static final /* synthetic */ State[] field_7209;

        public static State[] values() {
            return (State[])field_7209.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private static /* synthetic */ State[] method_36647() {
            return new State[]{CROSSED, ATTACKING, SPELLCASTING, BOW_AND_ARROW, CROSSBOW_HOLD, CROSSBOW_CHARGE, CELEBRATING, NEUTRAL};
        }

        static {
            field_7209 = State.method_36647();
        }
    }

    protected class LongDoorInteractGoal
    extends net.minecraft.entity.ai.goal.LongDoorInteractGoal {
        public LongDoorInteractGoal(RaiderEntity raider) {
            super(raider, false);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && IllagerEntity.this.hasActiveRaid();
        }
    }
}

