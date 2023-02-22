/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen
extends RecipeBookWidget {
    private Iterator<Item> fuelIterator;
    private Set<Item> fuels;
    private Slot outputSlot;
    private Item currentItem;
    private float frameTime;

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
    }

    @Override
    public void slotClicked(@Nullable Slot slot) {
        super.slotClicked(slot);
        if (slot != null && slot.id < this.craftingScreenHandler.getCraftingSlotCount()) {
            this.outputSlot = null;
        }
    }

    @Override
    public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
        ItemStack itemStack = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), slots.get((int)2).x, slots.get((int)2).y);
        DefaultedList<Ingredient> defaultedList = recipe.getIngredients();
        this.outputSlot = slots.get(1);
        if (this.fuels == null) {
            this.fuels = this.getAllowedFuels();
        }
        this.fuelIterator = this.fuels.iterator();
        this.currentItem = null;
        Iterator iterator = defaultedList.iterator();
        for (int i = 0; i < 2; ++i) {
            if (!iterator.hasNext()) {
                return;
            }
            Ingredient ingredient = (Ingredient)iterator.next();
            if (ingredient.isEmpty()) continue;
            Slot slot = slots.get(i);
            this.ghostSlots.addSlot(ingredient, slot.x, slot.y);
        }
    }

    protected abstract Set<Item> getAllowedFuels();

    @Override
    public void drawGhostSlots(MatrixStack matrices, int i, int j, boolean bl, float f) {
        super.drawGhostSlots(matrices, i, j, bl, f);
        if (this.outputSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.frameTime += f;
        }
        int k = this.outputSlot.x + i;
        int l = this.outputSlot.y + j;
        DrawableHelper.fill(matrices, k, l, k + 16, l + 16, 0x30FF0000);
        this.client.getItemRenderer().renderInGuiWithOverrides(this.client.player, this.getItem().getDefaultStack(), k, l);
        RenderSystem.depthFunc(516);
        DrawableHelper.fill(matrices, k, l, k + 16, l + 16, 0x30FFFFFF);
        RenderSystem.depthFunc(515);
    }

    private Item getItem() {
        if (this.currentItem == null || this.frameTime > 30.0f) {
            this.frameTime = 0.0f;
            if (this.fuelIterator == null || !this.fuelIterator.hasNext()) {
                if (this.fuels == null) {
                    this.fuels = this.getAllowedFuels();
                }
                this.fuelIterator = this.fuels.iterator();
            }
            this.currentItem = this.fuelIterator.next();
        }
        return this.currentItem;
    }
}

