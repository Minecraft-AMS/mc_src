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
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class EndermanBlockFeatureRenderer
extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
    public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, EndermanEntity endermanEntity, float f, float g, float h, float j, float k, float l) {
        BlockState blockState = endermanEntity.getCarriedBlock();
        if (blockState == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0, 0.6875, -0.75);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(20.0f));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45.0f));
        matrixStack.translate(0.25, 0.1875, 0.25);
        float m = 0.5f;
        matrixStack.scale(-0.5f, -0.5f, 0.5f);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}

