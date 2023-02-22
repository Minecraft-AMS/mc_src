/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DisconnectedRealmsScreen
extends RealmsScreen {
    private final Text title;
    private final Text reason;
    private MultilineText lines = MultilineText.EMPTY;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen parent, Text title, Text reason) {
        this.parent = parent;
        this.title = title;
        this.reason = reason;
    }

    @Override
    public void init() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setConnectedToRealms(false);
        minecraftClient.getResourcePackProvider().clear();
        Realms.narrateNow(this.title.getString() + ": " + this.reason.getString());
        this.lines = MultilineText.create(this.textRenderer, (StringVisitable)this.reason, this.width - 50);
        this.textHeight = this.lines.count() * this.textRenderer.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.textRenderer.fontHeight, 200, 20, ScreenTexts.BACK, buttonWidget -> minecraftClient.openScreen(this.parent)));
    }

    @Override
    public void onClose() {
        MinecraftClient.getInstance().openScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        DisconnectedRealmsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - this.textRenderer.fontHeight * 2, 0xAAAAAA);
        this.lines.drawCenterWithShadow(matrices, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
