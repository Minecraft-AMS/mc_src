/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.item.Item;

@Environment(value=EnvType.CLIENT)
public class FurnaceRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isFurnaceFilteringCraftable();
    }

    @Override
    protected void setFilteringCraftable(boolean filteringCraftable) {
        this.recipeBook.setFurnaceFilteringCraftable(filteringCraftable);
    }

    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isFurnaceGuiOpen();
    }

    @Override
    protected void setGuiOpen(boolean opened) {
        this.recipeBook.setFurnaceGuiOpen(opened);
    }

    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.smeltable";
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

