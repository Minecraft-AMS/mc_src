/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment
extends Enchantment {
    public final Type protectionType;

    public ProtectionEnchantment(Enchantment.Rarity weight, Type protectionType, EquipmentSlot ... slotTypes) {
        super(weight, protectionType == Type.FALL ? EnchantmentTarget.ARMOR_FEET : EnchantmentTarget.ARMOR, slotTypes);
        this.protectionType = protectionType;
    }

    @Override
    public int getMinPower(int level) {
        return this.protectionType.getBasePower() + (level - 1) * this.protectionType.getPowerPerLevel();
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + this.protectionType.getPowerPerLevel();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        }
        if (this.protectionType == Type.ALL) {
            return level;
        }
        if (this.protectionType == Type.FIRE && source.isIn(DamageTypeTags.IS_FIRE)) {
            return level * 2;
        }
        if (this.protectionType == Type.FALL && source.isIn(DamageTypeTags.IS_FALL)) {
            return level * 3;
        }
        if (this.protectionType == Type.EXPLOSION && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            return level * 2;
        }
        if (this.protectionType == Type.PROJECTILE && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            return level * 2;
        }
        return 0;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        if (other instanceof ProtectionEnchantment) {
            ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)other;
            if (this.protectionType == protectionEnchantment.protectionType) {
                return false;
            }
            return this.protectionType == Type.FALL || protectionEnchantment.protectionType == Type.FALL;
        }
        return super.canAccept(other);
    }

    public static int transformFireDuration(LivingEntity entity, int duration) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, entity);
        if (i > 0) {
            duration -= MathHelper.floor((float)duration * ((float)i * 0.15f));
        }
        return duration;
    }

    public static double transformExplosionKnockback(LivingEntity entity, double velocity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, entity);
        if (i > 0) {
            velocity *= MathHelper.clamp(1.0 - (double)i * 0.15, 0.0, 1.0);
        }
        return velocity;
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type ALL = new Type(1, 11);
        public static final /* enum */ Type FIRE = new Type(10, 8);
        public static final /* enum */ Type FALL = new Type(5, 6);
        public static final /* enum */ Type EXPLOSION = new Type(5, 8);
        public static final /* enum */ Type PROJECTILE = new Type(3, 6);
        private final int basePower;
        private final int powerPerLevel;
        private static final /* synthetic */ Type[] field_9136;

        public static Type[] values() {
            return (Type[])field_9136.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(int basePower, int powerPerLevel) {
            this.basePower = basePower;
            this.powerPerLevel = powerPerLevel;
        }

        public int getBasePower() {
            return this.basePower;
        }

        public int getPowerPerLevel() {
            return this.powerPerLevel;
        }

        private static /* synthetic */ Type[] method_36689() {
            return new Type[]{ALL, FIRE, FALL, EXPLOSION, PROJECTILE};
        }

        static {
            field_9136 = Type.method_36689();
        }
    }
}

