/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class SilkTouchEnchantment
extends Enchantment {
    protected SilkTouchEnchantment(Enchantment.Weight weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.DIGGER, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return 15;
    }

    @Override
    public int getMaximumPower(int level) {
        return super.getMinimumPower(level) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }

    @Override
    public boolean differs(Enchantment other) {
        return super.differs(other) && other != Enchantments.FORTUNE;
    }
}

