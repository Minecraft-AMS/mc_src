/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@Environment(value=EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, SlimeEntityModel<T>> {
    private final EntityModel<T> model = new SlimeEntityModel(0);

    public SlimeOverlayFeatureRenderer(FeatureRendererContext<T, SlimeEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if (((Entity)livingEntity).isInvisible()) {
            return;
        }
        ((SlimeEntityModel)this.getContextModel()).copyStateTo(this.model);
        this.model.animateModel(livingEntity, f, g, h);
        this.model.setAngles(livingEntity, f, g, j, k, l);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(livingEntity)));
        this.model.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(livingEntity, 0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

