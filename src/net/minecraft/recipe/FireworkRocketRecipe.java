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
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkRocketRecipe
extends SpecialCraftingRecipe {
    private static final Ingredient PAPER = Ingredient.ofItems(Items.PAPER);
    private static final Ingredient DURATION_MODIFIER = Ingredient.ofItems(Items.GUNPOWDER);
    private static final Ingredient FIREWORK_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        boolean bl = false;
        int i = 0;
        for (int j = 0; j < craftingInventory.getInvSize(); ++j) {
            ItemStack itemStack = craftingInventory.getInvStack(j);
            if (itemStack.isEmpty()) continue;
            if (PAPER.test(itemStack)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (!(DURATION_MODIFIER.test(itemStack) ? ++i > 3 : !FIREWORK_STAR.test(itemStack))) continue;
            return false;
        }
        return bl && i >= 1;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundTag compoundTag = itemStack.getOrCreateSubTag("Fireworks");
        ListTag listTag = new ListTag();
        int i = 0;
        for (int j = 0; j < craftingInventory.getInvSize(); ++j) {
            CompoundTag compoundTag2;
            ItemStack itemStack2 = craftingInventory.getInvStack(j);
            if (itemStack2.isEmpty()) continue;
            if (DURATION_MODIFIER.test(itemStack2)) {
                ++i;
                continue;
            }
            if (!FIREWORK_STAR.test(itemStack2) || (compoundTag2 = itemStack2.getSubTag("Explosion")) == null) continue;
            listTag.add(compoundTag2);
        }
        compoundTag.putByte("Flight", (byte)i);
        if (!listTag.isEmpty()) {
            compoundTag.put("Explosions", listTag);
        }
        return itemStack;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}

