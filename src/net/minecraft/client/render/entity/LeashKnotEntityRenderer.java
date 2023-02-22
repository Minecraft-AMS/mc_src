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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LeashKnotEntityRenderer
extends EntityRenderer<LeadKnotEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/lead_knot.png");
    private final LeashEntityModel<LeadKnotEntity> model = new LeashEntityModel();

    public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(LeadKnotEntity leadKnotEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.setAngles(leadKnotEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(SKIN));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(leadKnotEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(LeadKnotEntity leadKnotEntity) {
        return SKIN;
    }
}

