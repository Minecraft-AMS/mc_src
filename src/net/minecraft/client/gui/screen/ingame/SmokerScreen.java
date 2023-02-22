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
import net.minecraft.client.gui.screen.recipebook.SmokerRecipeBookScreen;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SmokerScreen
extends AbstractFurnaceScreen<SmokerContainer> {
    private static final Identifier BG_TEX = new Identifier("textures/gui/container/smoker.png");

    public SmokerScreen(SmokerContainer smokerContainer, PlayerInventory playerInventory, Text text) {
        super(smokerContainer, new SmokerRecipeBookScreen(), playerInventory, text, BG_TEX);
    }
}

