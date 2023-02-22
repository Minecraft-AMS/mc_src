/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CheckboxWidget
extends AbstractPressableButtonWidget {
    private static final Identifier TEXTURE = new Identifier("textures/gui/checkbox.png");
    boolean checked;

    public CheckboxWidget(int x, int y, int width, int height, String message, boolean checked) {
        super(x, y, width, height, message);
        this.checked = checked;
    }

    @Override
    public void onPress() {
        this.checked = !this.checked;
    }

    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableDepthTest();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        CheckboxWidget.blit(this.x, this.y, 0.0f, this.checked ? 20.0f : 0.0f, 20, this.height, 32, 64);
        this.renderBg(minecraftClient, mouseX, mouseY);
        int i = 0xE0E0E0;
        this.drawString(textRenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xE0E0E0 | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }
}

