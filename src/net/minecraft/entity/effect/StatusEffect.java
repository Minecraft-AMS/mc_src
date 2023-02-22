/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class StatusEffect {
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();
    private final StatusEffectType type;
    private final int color;
    @Nullable
    private String translationKey;

    @Nullable
    public static StatusEffect byRawId(int rawId) {
        return (StatusEffect)Registry.STATUS_EFFECT.get(rawId);
    }

    public static int getRawId(StatusEffect type) {
        return Registry.STATUS_EFFECT.getRawId(type);
    }

    protected StatusEffect(StatusEffectType statusEffectType, int i) {
        this.type = statusEffectType;
        this.color = i;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (this == StatusEffects.REGENERATION) {
            if (entity.getHealth() < entity.getMaximumHealth()) {
                entity.heal(1.0f);
            }
        } else if (this == StatusEffects.POISON) {
            if (entity.getHealth() > 1.0f) {
                entity.damage(DamageSource.MAGIC, 1.0f);
            }
        } else if (this == StatusEffects.WITHER) {
            entity.damage(DamageSource.WITHER, 1.0f);
        } else if (this == StatusEffects.HUNGER && entity instanceof PlayerEntity) {
            ((PlayerEntity)entity).addExhaustion(0.005f * (float)(amplifier + 1));
        } else if (this == StatusEffects.SATURATION && entity instanceof PlayerEntity) {
            if (!entity.world.isClient) {
                ((PlayerEntity)entity).getHungerManager().add(amplifier + 1, 1.0f);
            }
        } else if (this == StatusEffects.INSTANT_HEALTH && !entity.isUndead() || this == StatusEffects.INSTANT_DAMAGE && entity.isUndead()) {
            entity.heal(Math.max(4 << amplifier, 0));
        } else if (this == StatusEffects.INSTANT_DAMAGE && !entity.isUndead() || this == StatusEffects.INSTANT_HEALTH && entity.isUndead()) {
            entity.damage(DamageSource.MAGIC, 6 << amplifier);
        }
    }

    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (this == StatusEffects.INSTANT_HEALTH && !target.isUndead() || this == StatusEffects.INSTANT_DAMAGE && target.isUndead()) {
            int i = (int)(proximity * (double)(4 << amplifier) + 0.5);
            target.heal(i);
        } else if (this == StatusEffects.INSTANT_DAMAGE && !target.isUndead() || this == StatusEffects.INSTANT_HEALTH && target.isUndead()) {
            int i = (int)(proximity * (double)(6 << amplifier) + 0.5);
            if (source == null) {
                target.damage(DamageSource.MAGIC, i);
            } else {
                target.damage(DamageSource.magic(source, attacker), i);
            }
        } else {
            this.applyUpdateEffect(target, amplifier);
        }
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if (this == StatusEffects.REGENERATION) {
            int i = 50 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        if (this == StatusEffects.POISON) {
            int i = 25 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        if (this == StatusEffects.WITHER) {
            int i = 40 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        return this == StatusEffects.HUNGER;
    }

    public boolean isInstant() {
        return false;
    }

    protected String method_5559() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("effect", Registry.STATUS_EFFECT.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.method_5559();
    }

    public Text method_5560() {
        return new TranslatableText(this.getTranslationKey(), new Object[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public StatusEffectType getType() {
        return this.type;
    }

    public int getColor() {
        return this.color;
    }

    public StatusEffect addAttributeModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
        EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.fromString(uuid), this::getTranslationKey, amount, operation);
        this.attributeModifiers.put(attribute, entityAttributeModifier);
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void method_5562(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
        }
    }

    public void method_5555(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + i, this.method_5563(i, entityAttributeModifier), entityAttributeModifier.getOperation()));
        }
    }

    public double method_5563(int i, EntityAttributeModifier entityAttributeModifier) {
        return entityAttributeModifier.getAmount() * (double)(i + 1);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_5573() {
        return this.type == StatusEffectType.BENEFICIAL;
    }
}

