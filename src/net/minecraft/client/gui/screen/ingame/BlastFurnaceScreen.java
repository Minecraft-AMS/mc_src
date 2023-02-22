/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BlastFurnaceScreen
extends AbstractFurnaceScreen<BlastFurnaceContainer> {
    private static final Identifier BG_TEX = new Identifier("textures/gui/container/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceContainer container, PlayerInventory inventory, Text title) {
        super(container, new BlastFurnaceRecipeBookScreen(), inventory, title, BG_TEX);
    }
}

