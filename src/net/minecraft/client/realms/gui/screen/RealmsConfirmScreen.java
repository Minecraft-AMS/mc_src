/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer callback;
    private final Text title1;
    private final Text title2;

    public RealmsConfirmScreen(BooleanConsumer callback, Text title1, Text title2) {
        super(NarratorManager.EMPTY);
        this.callback = callback;
        this.title1 = title1;
        this.title2 = title2;
    }

    @Override
    public void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.YES, button -> this.callback.accept(true)).dimensions(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.NO, button -> this.callback.accept(false)).dimensions(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}

