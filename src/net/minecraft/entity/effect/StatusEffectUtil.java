/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;

public final class StatusEffectUtil {
    public static String durationToString(StatusEffectInstance effect, float multiplier) {
        if (effect.isPermanent()) {
            return "**:**";
        }
        int i = MathHelper.floor((float)effect.getDuration() * multiplier);
        return StringHelper.formatTicks(i);
    }

    public static boolean hasHaste(LivingEntity entity) {
        return entity.hasStatusEffect(StatusEffects.HASTE) || entity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }

    public static int getHasteAmplifier(LivingEntity entity) {
        int i = 0;
        int j = 0;
        if (entity.hasStatusEffect(StatusEffects.HASTE)) {
            i = entity.getStatusEffect(StatusEffects.HASTE).getAmplifier();
        }
        if (entity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
            j = entity.getStatusEffect(StatusEffects.CONDUIT_POWER).getAmplifier();
        }
        return Math.max(i, j);
    }

    public static boolean hasWaterBreathing(LivingEntity entity) {
        return entity.hasStatusEffect(StatusEffects.WATER_BREATHING) || entity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }
}

