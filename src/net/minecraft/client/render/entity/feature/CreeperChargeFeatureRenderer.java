/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreeperChargeFeatureRenderer
extends FeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperEntity> model = new CreeperEntityModel(2.0f);

    public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> context) {
        super(context);
    }

    @Override
    public void render(CreeperEntity creeperEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!creeperEntity.isCharged()) {
            return;
        }
        boolean bl = creeperEntity.isInvisible();
        GlStateManager.depthMask(!bl);
        this.bindTexture(SKIN);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float m = (float)creeperEntity.age + h;
        GlStateManager.translatef(m * 0.01f, m * 0.01f, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float n = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        ((CreeperEntityModel)this.getContextModel()).copyStateTo(this.model);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        this.model.render(creeperEntity, f, g, i, j, k, l);
        gameRenderer.setFogBlack(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
