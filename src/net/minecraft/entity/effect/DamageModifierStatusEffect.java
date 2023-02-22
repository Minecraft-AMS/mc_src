/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class DamageModifierStatusEffect
extends StatusEffect {
    protected final double modifier;

    protected DamageModifierStatusEffect(StatusEffectType type, int color, double modifier) {
        super(type, color);
        this.modifier = modifier;
    }

    @Override
    public double method_5563(int i, EntityAttributeModifier entityAttributeModifier) {
        return this.modifier * (double)(i + 1);
    }
}
