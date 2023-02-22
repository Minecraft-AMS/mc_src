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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer
extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/squid.png");

    public SquidEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SquidEntityModel(), 0.7f);
    }

    @Override
    public Identifier getTexture(SquidEntity squidEntity) {
        return SKIN;
    }

    @Override
    protected void setupTransforms(SquidEntity squidEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = MathHelper.lerp(h, squidEntity.field_6905, squidEntity.field_6907);
        float j = MathHelper.lerp(h, squidEntity.field_6906, squidEntity.field_6903);
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
        matrixStack.translate(0.0, -1.2f, 0.0);
    }

    @Override
    protected float getAnimationProgress(SquidEntity squidEntity, float f) {
        return MathHelper.lerp(f, squidEntity.field_6900, squidEntity.field_6904);
    }
}

