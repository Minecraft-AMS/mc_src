/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel();

    public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(EvokerFangsEntity model, double x, double y, double d, float f, float g) {
        float h = model.getAnimationProgress(g);
        if (h == 0.0f) {
            return;
        }
        float i = 2.0f;
        if (h > 0.9f) {
            i = (float)((double)i * ((1.0 - (double)h) / (double)0.1f));
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableAlphaTest();
        this.bindEntityTexture(model);
        GlStateManager.translatef((float)x, (float)y, (float)d);
        GlStateManager.rotatef(90.0f - model.yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(-i, -i, i);
        float j = 0.03125f;
        GlStateManager.translatef(0.0f, -0.626f, 0.0f);
        this.model.render(model, h, 0.0f, 0.0f, model.yaw, model.pitch, 0.03125f);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        super.render(model, x, y, d, f, g);
    }

    @Override
    protected Identifier getTexture(EvokerFangsEntity evokerFangsEntity) {
        return SKIN;
    }
}

