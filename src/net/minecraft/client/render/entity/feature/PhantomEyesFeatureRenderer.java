/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PhantomEyesFeatureRenderer<T extends Entity>
extends FeatureRenderer<T, PhantomEntityModel<T>> {
    private static final Identifier SKIN = new Identifier("textures/entity/phantom_eyes.png");

    public PhantomEyesFeatureRenderer(FeatureRendererContext<T, PhantomEntityModel<T>> context) {
        super(context);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k, float l) {
        this.bindTexture(SKIN);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!((Entity)entity).isInvisible());
        int m = 61680;
        int n = 61680;
        boolean o = false;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        ((PhantomEntityModel)this.getContextModel()).render(entity, f, g, i, j, k, l);
        gameRenderer.setFogBlack(false);
        this.applyLightmapCoordinates(entity);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

