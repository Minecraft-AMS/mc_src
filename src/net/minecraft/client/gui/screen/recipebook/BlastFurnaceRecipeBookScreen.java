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
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class BlastFurnaceRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    private static final Text TOGGLE_BLASTABLE_RECIPES_TEXT = Text.translatable("gui.recipebook.toggleRecipes.blastable");

    @Override
    protected Text getToggleCraftableButtonText() {
        return TOGGLE_BLASTABLE_RECIPES_TEXT;
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

