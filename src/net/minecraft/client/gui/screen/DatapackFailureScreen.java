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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DatapackFailureScreen
extends Screen {
    private MultilineText wrappedText = MultilineText.EMPTY;
    private final Runnable runServerInSafeMode;

    public DatapackFailureScreen(Runnable runServerInSafeMode) {
        super(new TranslatableText("datapackFailure.title"));
        this.runServerInSafeMode = runServerInSafeMode;
    }

    @Override
    protected void init() {
        super.init();
        this.wrappedText = MultilineText.create(this.textRenderer, (StringVisitable)this.getTitle(), this.width - 50);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableText("datapackFailure.safeMode"), button -> this.runServerInSafeMode.run()));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableText("gui.toTitle"), button -> this.client.setScreen(null)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.wrappedText.drawCenterWithShadow(matrices, this.width / 2, 70);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

