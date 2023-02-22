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
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class MultiplayerWarningScreen
extends Screen {
    private final Screen parent;
    private static final Text HEADER = new TranslatableText("multiplayerWarning.header").formatted(Formatting.BOLD);
    private static final Text MESSAGE = new TranslatableText("multiplayerWarning.message");
    private static final Text CHECK_MESSAGE = new TranslatableText("multiplayerWarning.check");
    private static final Text PROCEED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);
    private CheckboxWidget checkbox;
    private MultilineText lines = MultilineText.EMPTY;

    public MultiplayerWarningScreen(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.lines = MultilineText.create(this.textRenderer, (StringVisitable)MESSAGE, this.width - 50);
        int i = (this.lines.count() + 1) * this.textRenderer.fontHeight * 2;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, ScreenTexts.PROCEED, buttonWidget -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipMultiplayerWarning = true;
                this.client.options.write();
            }
            this.client.openScreen(new MultiplayerScreen(this.parent));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
        this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, CHECK_MESSAGE, false);
        this.addButton(this.checkbox);
    }

    @Override
    public String getNarrationMessage() {
        return PROCEED_TEXT.getString();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        MultiplayerWarningScreen.drawTextWithShadow(matrices, this.textRenderer, HEADER, 25, 30, 0xFFFFFF);
        this.lines.drawWithShadow(matrices, 25, 70, this.textRenderer.fontHeight * 2, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
