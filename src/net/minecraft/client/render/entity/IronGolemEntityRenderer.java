/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/iron_golem/iron_golem.png");

    public IronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IronGolemEntityModel(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature(new IronGolemCrackFeatureRenderer(this));
        this.addFeature(new IronGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(IronGolemEntity ironGolemEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(IronGolemEntity ironGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(ironGolemEntity, matrixStack, f, g, h);
        if ((double)ironGolemEntity.limbAnimator.getSpeed() < 0.01) {
            return;
        }
        float i = 13.0f;
        float j = ironGolemEntity.limbAnimator.getPos(h) + 6.0f;
        float k = (Math.abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f;
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k));
    }
}

