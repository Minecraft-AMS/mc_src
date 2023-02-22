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
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEyesFeatureRenderer
extends FeatureRenderer<EnderDragonEntity, DragonEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_eyes.png");

    public EnderDragonEyesFeatureRenderer(FeatureRendererContext<EnderDragonEntity, DragonEntityModel> context) {
        super(context);
    }

    @Override
    public void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
        this.bindTexture(SKIN);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthFunc(514);
        int m = 61680;
        int n = 61680;
        boolean o = false;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        ((DragonEntityModel)this.getContextModel()).render(enderDragonEntity, f, g, i, j, k, l);
        gameRenderer.setFogBlack(false);
        this.applyLightmapCoordinates(enderDragonEntity);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.depthFunc(515);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

