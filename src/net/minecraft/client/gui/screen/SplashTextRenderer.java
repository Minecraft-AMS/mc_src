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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class SplashTextRenderer {
    public static final SplashTextRenderer MERRY_X_MAS_ = new SplashTextRenderer("Merry X-mas!");
    public static final SplashTextRenderer HAPPY_NEW_YEAR_ = new SplashTextRenderer("Happy new year!");
    public static final SplashTextRenderer OOOOO_O_O_OOOOO__SPOOKY_ = new SplashTextRenderer("OOoooOOOoooo! Spooky!");
    private static final int field_44664 = 123;
    private static final int field_44665 = 69;
    private final String text;

    public SplashTextRenderer(String text) {
        this.text = text;
    }

    public void render(DrawContext drawContext, int i, TextRenderer textRenderer, int j) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate((float)i / 2.0f + 123.0f, 69.0f, 0.0f);
        drawContext.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0f));
        float f = 1.8f - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
        f = f * 100.0f / (float)(textRenderer.getWidth(this.text) + 32);
        drawContext.getMatrices().scale(f, f, f);
        drawContext.drawCenteredTextWithShadow(textRenderer, this.text, 0, -8, 0xFFFF00 | j);
        drawContext.getMatrices().pop();
    }
}

