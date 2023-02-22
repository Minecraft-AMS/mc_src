/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class ThornsEnchantment
extends Enchantment {
    public ThornsEnchantment(Enchantment.Weight weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.ARMOR_CHEST, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return 10 + 20 * (level - 1);
    }

    @Override
    public int getMaximumPower(int level) {
        return super.getMinimumPower(level) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            return true;
        }
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
        Random random = user.getRandom();
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.THORNS, user);
        if (ThornsEnchantment.shouldDamageAttacker(level, random)) {
            if (attacker != null) {
                attacker.damage(DamageSource.thorns(user), ThornsEnchantment.getDamageAmount(level, random));
            }
            if (entry != null) {
                entry.getValue().damage(3, user, livingEntity -> livingEntity.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
            }
        } else if (entry != null) {
            entry.getValue().damage(1, user, livingEntity -> livingEntity.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
        }
    }

    public static boolean shouldDamageAttacker(int level, Random random) {
        if (level <= 0) {
            return false;
        }
        return random.nextFloat() < 0.15f * (float)level;
    }

    public static int getDamageAmount(int level, Random random) {
        if (level > 10) {
            return level - 10;
        }
        return 1 + random.nextInt(4);
    }
}

