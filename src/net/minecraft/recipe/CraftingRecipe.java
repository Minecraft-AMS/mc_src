/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface CraftingRecipe
extends Recipe<CraftingInventory> {
    @Override
    default public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}

