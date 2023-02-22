/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class OrderedTextTooltipComponent
implements TooltipComponent {
    private final OrderedText text;

    public OrderedTextTooltipComponent(OrderedText text) {
        this.text = text;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return textRenderer.getWidth(this.text);
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        textRenderer.draw(this.text, (float)x, (float)y, -1, true, matrix, (VertexConsumerProvider)vertexConsumers, false, 0, 0xF000F0);
    }
}

