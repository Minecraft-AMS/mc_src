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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer
extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishEntityModelA<TropicalFishEntity> modelA = new TropicalFishEntityModelA(0.008f);
    private final TropicalFishEntityModelB<TropicalFishEntity> modelB = new TropicalFishEntityModelB(0.008f);

    public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntity tropicalFishEntity, float f, float g, float h, float j, float k, float l) {
        TintableCompositeModel entityModel = tropicalFishEntity.getShape() == 0 ? this.modelA : this.modelB;
        float[] fs = tropicalFishEntity.getPatternColorComponents();
        TropicalFishSomethingFeatureRenderer.render(this.getContextModel(), entityModel, tropicalFishEntity.getVarietyId(), matrixStack, vertexConsumerProvider, i, tropicalFishEntity, f, g, j, k, l, h, fs[0], fs[1], fs[2]);
    }
}

