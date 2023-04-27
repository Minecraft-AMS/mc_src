/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.recipe;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface RecipeUnlocker {
    public void setLastRecipe(@Nullable Recipe<?> var1);

    @Nullable
    public Recipe<?> getLastRecipe();

    default public void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {
        Recipe<?> recipe = this.getLastRecipe();
        if (recipe != null) {
            player.unlockCraftedRecipe(recipe, ingredients);
            if (!recipe.isIgnoredInRecipeBook()) {
                player.unlockRecipes(Collections.singleton(recipe));
                this.setLastRecipe(null);
            }
        }
    }

    default public boolean shouldCraftRecipe(World world, ServerPlayerEntity player, Recipe<?> recipe) {
        if (recipe.isIgnoredInRecipeBook() || !world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) || player.getRecipeBook().contains(recipe)) {
            this.setLastRecipe(recipe);
            return true;
        }
        return false;
    }
}

