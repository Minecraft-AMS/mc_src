/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.MobSpawnerLogic;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
implements BlockEntityRenderer<MobSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public MobSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    @Override
    public void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
        Entity entity = mobSpawnerLogic.getRenderedEntity(mobSpawnerBlockEntity.getWorld(), mobSpawnerBlockEntity.getWorld().getRandom(), mobSpawnerBlockEntity.getPos());
        if (entity != null) {
            float g = 0.53125f;
            float h = Math.max(entity.getWidth(), entity.getHeight());
            if ((double)h > 1.0) {
                g /= h;
            }
            matrixStack.translate(0.0f, 0.4f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)MathHelper.lerp((double)f, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0f));
            matrixStack.translate(0.0f, -0.2f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0f));
            matrixStack.scale(g, g, g);
            this.entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, f, matrixStack, vertexConsumerProvider, i);
        }
        matrixStack.pop();
    }
}

