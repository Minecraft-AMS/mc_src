/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;
import org.jetbrains.annotations.Nullable;

public class ToggleableActiveTargetGoal<T extends LivingEntity>
extends ActiveTargetGoal<T> {
    private boolean enabled = true;

    public ToggleableActiveTargetGoal(RaiderEntity actor, Class<T> targetEntityClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(actor, targetEntityClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean canStart() {
        return this.enabled && super.canStart();
    }
}

