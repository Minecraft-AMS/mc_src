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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DatapackFailureScreen
extends Screen {
    private MultilineText wrappedText = MultilineText.EMPTY;
    private final Runnable runServerInSafeMode;

    public DatapackFailureScreen(Runnable runServerInSafeMode) {
        super(Text.translatable("datapackFailure.title"));
        this.runServerInSafeMode = runServerInSafeMode;
    }

    @Override
    protected void init() {
        super.init();
        this.wrappedText = MultilineText.create(this.textRenderer, (StringVisitable)this.getTitle(), this.width - 50);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("datapackFailure.safeMode"), button -> this.runServerInSafeMode.run()).dimensions(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.TO_TITLE, button -> this.client.setScreen(null)).dimensions(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.wrappedText.drawCenterWithShadow(context, this.width / 2, 70);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

