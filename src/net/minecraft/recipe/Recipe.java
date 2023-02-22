/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface Recipe<C extends Inventory> {
    public boolean matches(C var1, World var2);

    public ItemStack craft(C var1);

    @Environment(value=EnvType.CLIENT)
    public boolean fits(int var1, int var2);

    public ItemStack getOutput();

    default public DefaultedList<ItemStack> getRemainder(C inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getStack(i).getItem();
            if (!item.hasRecipeRemainder()) continue;
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }

    default public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.of();
    }

    default public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    default public String getGroup() {
        return "";
    }

    @Environment(value=EnvType.CLIENT)
    default public ItemStack createIcon() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    public Identifier getId();

    public RecipeSerializer<?> getSerializer();

    public RecipeType<?> getType();
}

