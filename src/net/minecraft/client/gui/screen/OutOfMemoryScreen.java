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
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class OutOfMemoryScreen
extends Screen {
    private MultilineText message = MultilineText.EMPTY;

    public OutOfMemoryScreen() {
        super(Text.translatable("outOfMemory.title"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.TO_TITLE, button -> this.client.setScreen(new TitleScreen())).dimensions(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), button -> this.client.scheduleStop()).dimensions(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20).build());
        this.message = MultilineText.create(this.textRenderer, (StringVisitable)Text.translatable("outOfMemory.message"), 295);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
        this.message.drawWithShadow(context, this.width / 2 - 145, this.height / 4, 9, 0xA0A0A0);
        super.render(context, mouseX, mouseY, delta);
    }
}

