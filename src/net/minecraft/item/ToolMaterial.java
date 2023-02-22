/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.recipe.Ingredient;

public interface ToolMaterial {
    public int getDurability();

    public float getMiningSpeed();

    public float getAttackDamage();

    public int getMiningLevel();

    public int getEnchantability();

    public Ingredient getRepairIngredient();
}

