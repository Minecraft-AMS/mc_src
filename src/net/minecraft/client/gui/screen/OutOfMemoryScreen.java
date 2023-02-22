/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

@Environment(value=EnvType.CLIENT)
public class OutOfMemoryScreen
extends Screen {
    public OutOfMemoryScreen() {
        super(new LiteralText("Out of memory!"));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.toTitle", new Object[0]), buttonWidget -> this.minecraft.openScreen(new TitleScreen())));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, I18n.translate("menu.quit", new Object[0]), buttonWidget -> this.minecraft.scheduleStop()));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
        this.drawString(this.font, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 0xA0A0A0);
        this.drawString(this.font, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 0xA0A0A0);
        this.drawString(this.font, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 0xA0A0A0);
        this.drawString(this.font, "memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 0xA0A0A0);
        this.drawString(this.font, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 0xA0A0A0);
        this.drawString(this.font, "We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 0xA0A0A0);
        this.drawString(this.font, "the main menu and back to playing, but this may not have worked.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 72, 0xA0A0A0);
        this.drawString(this.font, "Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 0xA0A0A0);
        super.render(mouseX, mouseY, delta);
    }
}
