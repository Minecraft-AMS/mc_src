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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class EnderCrystalEntityRenderer
extends EntityRenderer<EnderCrystalEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer field_21736 = RenderLayer.getEntityCutoutNoCull(SKIN);
    private static final float field_21002 = (float)Math.sin(0.7853981633974483);
    private final ModelPart field_21003;
    private final ModelPart field_21004;
    private final ModelPart bottom;

    public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowSize = 0.5f;
        this.field_21004 = new ModelPart(64, 32, 0, 0);
        this.field_21004.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.field_21003 = new ModelPart(64, 32, 32, 0);
        this.field_21003.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.bottom = new ModelPart(64, 32, 0, 16);
        this.bottom.addCuboid(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f);
    }

    @Override
    public void render(EnderCrystalEntity enderCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = EnderCrystalEntityRenderer.method_23155(enderCrystalEntity, g);
        float j = ((float)enderCrystalEntity.field_7034 + g) * 3.0f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(field_21736);
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0, -0.5, 0.0);
        int k = OverlayTexture.DEFAULT_UV;
        if (enderCrystalEntity.getShowBottom()) {
            this.bottom.render(matrixStack, vertexConsumer, i, k);
        }
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
        matrixStack.translate(0.0, 1.5f + h / 2.0f, 0.0);
        matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        this.field_21004.render(matrixStack, vertexConsumer, i, k);
        float l = 0.875f;
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
        this.field_21004.render(matrixStack, vertexConsumer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
        this.field_21003.render(matrixStack, vertexConsumer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        BlockPos blockPos = enderCrystalEntity.getBeamTarget();
        if (blockPos != null) {
            float m = (float)blockPos.getX() + 0.5f;
            float n = (float)blockPos.getY() + 0.5f;
            float o = (float)blockPos.getZ() + 0.5f;
            float p = (float)((double)m - enderCrystalEntity.getX());
            float q = (float)((double)n - enderCrystalEntity.getY());
            float r = (float)((double)o - enderCrystalEntity.getZ());
            matrixStack.translate(p, q, r);
            EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, enderCrystalEntity.field_7034, matrixStack, vertexConsumerProvider, i);
        }
        super.render(enderCrystalEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public static float method_23155(EnderCrystalEntity enderCrystalEntity, float f) {
        float g = (float)enderCrystalEntity.field_7034 + f;
        float h = MathHelper.sin(g * 0.2f) / 2.0f + 0.5f;
        h = (h * h + h) * 0.4f;
        return h - 1.4f;
    }

    @Override
    public Identifier getTexture(EnderCrystalEntity enderCrystalEntity) {
        return SKIN;
    }

    @Override
    public boolean shouldRender(EnderCrystalEntity enderCrystalEntity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender(enderCrystalEntity, frustum, d, e, f) || enderCrystalEntity.getBeamTarget() != null;
    }
}

