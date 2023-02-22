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
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends DrownedEntity>
extends FeatureRenderer<T, DrownedEntityModel<T>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedEntityModel<T> model;

    public DrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        this.model = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_OUTER));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T drownedEntity, float f, float g, float h, float j, float k, float l) {
        DrownedOverlayFeatureRenderer.render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, drownedEntity, f, g, j, k, l, h, 1.0f, 1.0f, 1.0f);
    }
}

