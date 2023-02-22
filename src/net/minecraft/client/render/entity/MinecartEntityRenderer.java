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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity>
extends EntityRenderer<T> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/minecart.png");
    protected final EntityModel<T> model;
    private final BlockRenderManager blockRenderManager;

    public MinecartEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx);
        this.shadowRadius = 0.7f;
        this.model = new MinecartEntityModel(ctx.getPart(layer));
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(T abstractMinecartEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(abstractMinecartEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        long l = (long)((Entity)abstractMinecartEntity).getId() * 493286711L;
        l = l * l * 4392167121L + l * 98761L;
        float h = (((float)(l >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float j = (((float)(l >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float k = (((float)(l >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        matrixStack.translate(h, j, k);
        double d = MathHelper.lerp((double)g, ((AbstractMinecartEntity)abstractMinecartEntity).lastRenderX, ((Entity)abstractMinecartEntity).getX());
        double e = MathHelper.lerp((double)g, ((AbstractMinecartEntity)abstractMinecartEntity).lastRenderY, ((Entity)abstractMinecartEntity).getY());
        double m = MathHelper.lerp((double)g, ((AbstractMinecartEntity)abstractMinecartEntity).lastRenderZ, ((Entity)abstractMinecartEntity).getZ());
        double n = 0.3f;
        Vec3d vec3d = ((AbstractMinecartEntity)abstractMinecartEntity).snapPositionToRail(d, e, m);
        float o = MathHelper.lerp(g, ((AbstractMinecartEntity)abstractMinecartEntity).prevPitch, ((Entity)abstractMinecartEntity).getPitch());
        if (vec3d != null) {
            Vec3d vec3d2 = ((AbstractMinecartEntity)abstractMinecartEntity).snapPositionToRailWithOffset(d, e, m, 0.3f);
            Vec3d vec3d3 = ((AbstractMinecartEntity)abstractMinecartEntity).snapPositionToRailWithOffset(d, e, m, -0.3f);
            if (vec3d2 == null) {
                vec3d2 = vec3d;
            }
            if (vec3d3 == null) {
                vec3d3 = vec3d;
            }
            matrixStack.translate(vec3d.x - d, (vec3d2.y + vec3d3.y) / 2.0 - e, vec3d.z - m);
            Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
            if (vec3d4.length() != 0.0) {
                vec3d4 = vec3d4.normalize();
                f = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
                o = (float)(Math.atan(vec3d4.y) * 73.0);
            }
        }
        matrixStack.translate(0.0f, 0.375f, 0.0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-o));
        float p = (float)((AbstractMinecartEntity)abstractMinecartEntity).getDamageWobbleTicks() - g;
        float q = ((AbstractMinecartEntity)abstractMinecartEntity).getDamageWobbleStrength() - g;
        if (q < 0.0f) {
            q = 0.0f;
        }
        if (p > 0.0f) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(p) * p * q / 10.0f * (float)((AbstractMinecartEntity)abstractMinecartEntity).getDamageWobbleSide()));
        }
        int r = ((AbstractMinecartEntity)abstractMinecartEntity).getBlockOffset();
        BlockState blockState = ((AbstractMinecartEntity)abstractMinecartEntity).getContainedBlock();
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            matrixStack.push();
            float s = 0.75f;
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.translate(-0.5f, (float)(r - 8) / 16.0f, 0.5f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            this.renderBlock(abstractMinecartEntity, g, blockState, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
        }
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.setAngles(abstractMinecartEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(abstractMinecartEntity)));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(T abstractMinecartEntity) {
        return TEXTURE;
    }

    protected void renderBlock(T entity, float delta, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        this.blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
    }
}

