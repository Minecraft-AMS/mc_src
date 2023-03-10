/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class MultiplayerWarningScreen
extends WarningScreen {
    private static final Text HEADER = new TranslatableText("multiplayerWarning.header").formatted(Formatting.BOLD);
    private static final Text MESSAGE = new TranslatableText("multiplayerWarning.message");
    private static final Text CHECK_MESSAGE = new TranslatableText("multiplayerWarning.check");
    private static final Text NARRATED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);

    public MultiplayerWarningScreen(Screen parent) {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT, parent);
    }

    @Override
    protected void initButtons(int yOffset) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, ScreenTexts.PROCEED, buttonWidget -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipMultiplayerWarning = true;
                this.client.options.write();
            }
            this.client.setScreen(new MultiplayerScreen(this.parent));
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, 100 + yOffset, 150, 20, ScreenTexts.BACK, buttonWidget -> this.client.setScreen(this.parent)));
    }
}

