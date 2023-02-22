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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsClientOutdatedScreen
extends RealmsScreen {
    private static final Text INCOMPATIBLE_TITLE = Text.translatable("mco.client.incompatible.title");
    private static final Text[] INCOMPATIBLE_LINES_UNSTABLE = new Text[]{Text.translatable("mco.client.incompatible.msg.line1"), Text.translatable("mco.client.incompatible.msg.line2"), Text.translatable("mco.client.incompatible.msg.line3")};
    private static final Text[] INCOMPATIBLE_LINES = new Text[]{Text.translatable("mco.client.incompatible.msg.line1"), Text.translatable("mco.client.incompatible.msg.line2")};
    private final Screen parent;

    public RealmsClientOutdatedScreen(Screen parent) {
        super(INCOMPATIBLE_TITLE);
        this.parent = parent;
    }

    @Override
    public void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, RealmsClientOutdatedScreen.row(12), 200, 20, ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsClientOutdatedScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, RealmsClientOutdatedScreen.row(3), 0xFF0000);
        Text[] texts = this.getLines();
        for (int i = 0; i < texts.length; ++i) {
            RealmsClientOutdatedScreen.drawCenteredText(matrices, this.textRenderer, texts[i], this.width / 2, RealmsClientOutdatedScreen.row(5) + i * 12, 0xFFFFFF);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private Text[] getLines() {
        if (this.client.getGame().getVersion().isStable()) {
            return INCOMPATIBLE_LINES;
        }
        return INCOMPATIBLE_LINES_UNSTABLE;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335 || keyCode == 256) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

