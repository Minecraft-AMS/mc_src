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
public class SmokerRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isSmokerFilteringCraftable();
    }

    @Override
    protected void setFilteringCraftable(boolean filteringCraftable) {
        this.recipeBook.setSmokerFilteringCraftable(filteringCraftable);
    }

    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isSmokerGuiOpen();
    }

    @Override
    protected void setGuiOpen(boolean opened) {
        this.recipeBook.setSmokerGuiOpen(opened);
    }

    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.smokable";
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

