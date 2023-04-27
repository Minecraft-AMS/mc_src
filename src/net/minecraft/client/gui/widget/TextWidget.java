/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class TextWidget
extends AbstractTextWidget {
    private float horizontalAlignment = 0.5f;

    public TextWidget(Text message, TextRenderer textRenderer) {
        this(0, 0, textRenderer.getWidth(message.asOrderedText()), textRenderer.fontHeight, message, textRenderer);
    }

    public TextWidget(int width, int height, Text message, TextRenderer textRenderer) {
        this(0, 0, width, height, message, textRenderer);
    }

    public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message, textRenderer);
        this.active = false;
    }

    @Override
    public TextWidget setTextColor(int textColor) {
        super.setTextColor(textColor);
        return this;
    }

    private TextWidget align(float horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public TextWidget alignLeft() {
        return this.align(0.0f);
    }

    public TextWidget alignCenter() {
        return this.align(0.5f);
    }

    public TextWidget alignRight() {
        return this.align(1.0f);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        Text text = this.getMessage();
        TextRenderer textRenderer = this.getTextRenderer();
        int i = this.getX() + Math.round(this.horizontalAlignment * (float)(this.getWidth() - textRenderer.getWidth(text)));
        int j = this.getY() + (this.getHeight() - textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(textRenderer, text, i, j, this.getTextColor());
    }

    @Override
    public /* synthetic */ AbstractTextWidget setTextColor(int textColor) {
        return this.setTextColor(textColor);
    }
}

