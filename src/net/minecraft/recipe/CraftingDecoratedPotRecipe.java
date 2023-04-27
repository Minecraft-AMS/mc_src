/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CraftingDecoratedPotRecipe
extends SpecialCraftingRecipe {
    public CraftingDecoratedPotRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        if (!this.fits(craftingInventory.getWidth(), craftingInventory.getHeight())) {
            return false;
        }
        block3: for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            switch (i) {
                case 1: 
                case 3: 
                case 5: 
                case 7: {
                    if (itemStack.isIn(ItemTags.DECORATED_POT_INGREDIENTS)) continue block3;
                    return false;
                }
                default: {
                    if (itemStack.isOf(Items.AIR)) continue block3;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
        DecoratedPotBlockEntity.Sherds sherds = new DecoratedPotBlockEntity.Sherds(craftingInventory.getStack(1).getItem(), craftingInventory.getStack(3).getItem(), craftingInventory.getStack(5).getItem(), craftingInventory.getStack(7).getItem());
        return CraftingDecoratedPotRecipe.getPotStackWith(sherds);
    }

    public static ItemStack getPotStackWith(DecoratedPotBlockEntity.Sherds sherds) {
        ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
        NbtCompound nbtCompound = sherds.toNbt(new NbtCompound());
        BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.DECORATED_POT, nbtCompound);
        return itemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.CRAFTING_DECORATED_POT;
    }
}

