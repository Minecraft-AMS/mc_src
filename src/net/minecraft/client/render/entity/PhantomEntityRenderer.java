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
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityRenderer
extends MobEntityRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/phantom.png");

    public PhantomEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PhantomEntityModel(context.getPart(EntityModelLayers.PHANTOM)), 0.75f);
        this.addFeature(new PhantomEyesFeatureRenderer<PhantomEntity>(this));
    }

    @Override
    public Identifier getTexture(PhantomEntity phantomEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(PhantomEntity phantomEntity, MatrixStack matrixStack, float f) {
        int i = phantomEntity.getPhantomSize();
        float g = 1.0f + 0.15f * (float)i;
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0f, 1.3125f, 0.1875f);
    }

    @Override
    protected void setupTransforms(PhantomEntity phantomEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(phantomEntity, matrixStack, f, g, h);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(phantomEntity.getPitch()));
    }
}

