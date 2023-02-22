/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PowerEnchantment
extends Enchantment {
    public PowerEnchantment(Enchantment.Weight weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.BOW, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaximumPower(int level) {
        return this.getMinimumPower(level) + 15;
    }

    @Override
    public int getMaximumLevel() {
        return 5;
    }
}
