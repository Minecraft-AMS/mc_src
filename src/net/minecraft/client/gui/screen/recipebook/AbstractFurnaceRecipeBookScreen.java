/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen
extends RecipeBookWidget {
    private Iterator<Item> field_3153;
    private Set<Item> field_3149;
    private Slot outputSlot;
    private Item field_3152;
    private float field_3151;

    @Override
    protected boolean toggleFilteringCraftable() {
        boolean bl = !this.isFilteringCraftable();
        this.setFilteringCraftable(bl);
        return bl;
    }

    protected abstract boolean isFilteringCraftable();

    protected abstract void setFilteringCraftable(boolean var1);

    @Override
    public boolean isOpen() {
        return this.isGuiOpen();
    }

    protected abstract boolean isGuiOpen();

    @Override
    protected void setOpen(boolean opened) {
        this.setGuiOpen(opened);
        if (!opened) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }

    protected abstract void setGuiOpen(boolean var1);

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
    }

    @Override
    protected String getCraftableButtonText() {
        return I18n.translate(this.toggleCraftableButton.isToggled() ? this.getToggleCraftableButtonText() : "gui.recipebook.toggleRecipes.all", new Object[0]);
    }

    protected abstract String getToggleCraftableButtonText();

    @Override
    public void slotClicked(@Nullable Slot slot) {
        super.slotClicked(slot);
        if (slot != null && slot.id < this.craftingContainer.getCraftingSlotCount()) {
            this.outputSlot = null;
        }
    }

    @Override
    public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
        ItemStack itemStack = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), slots.get((int)2).xPosition, slots.get((int)2).yPosition);
        DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
        this.outputSlot = slots.get(1);
        if (this.field_3149 == null) {
            this.field_3149 = this.getAllowedFuels();
        }
        this.field_3153 = this.field_3149.iterator();
        this.field_3152 = null;
        Iterator iterator = defaultedList.iterator();
        for (int i = 0; i < 2; ++i) {
            if (!iterator.hasNext()) {
                return;
            }
            Ingredient ingredient = (Ingredient)iterator.next();
            if (ingredient.isEmpty()) continue;
            Slot slot = slots.get(i);
            this.ghostSlots.addSlot(ingredient, slot.xPosition, slot.yPosition);
        }
    }

    protected abstract Set<Item> getAllowedFuels();

    @Override
    public void drawGhostSlots(int left, int top, boolean isBig, float lastFrameDuration) {
        super.drawGhostSlots(left, top, isBig, lastFrameDuration);
        if (this.outputSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.field_3151 += lastFrameDuration;
        }
        DiffuseLighting.enableForItems();
        GlStateManager.disableLighting();
        int i = this.outputSlot.xPosition + left;
        int j = this.outputSlot.yPosition + top;
        DrawableHelper.fill(i, j, i + 16, j + 16, 0x30FF0000);
        this.client.getItemRenderer().renderGuiItem(this.client.player, this.method_2658().getStackForRender(), i, j);
        GlStateManager.depthFunc(516);
        DrawableHelper.fill(i, j, i + 16, j + 16, 0x30FFFFFF);
        GlStateManager.depthFunc(515);
        GlStateManager.enableLighting();
        DiffuseLighting.disable();
    }

    private Item method_2658() {
        if (this.field_3152 == null || this.field_3151 > 30.0f) {
            this.field_3151 = 0.0f;
            if (this.field_3153 == null || !this.field_3153.hasNext()) {
                if (this.field_3149 == null) {
                    this.field_3149 = this.getAllowedFuels();
                }
                this.field_3153 = this.field_3149.iterator();
            }
            this.field_3152 = this.field_3153.next();
        }
        return this.field_3152;
    }
}

