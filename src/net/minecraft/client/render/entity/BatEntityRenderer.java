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
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BatEntityRenderer
extends MobEntityRenderer<BatEntity, BatEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/bat.png");

    public BatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BatEntityModel(), 0.25f);
    }

    @Override
    public Identifier getTexture(BatEntity batEntity) {
        return SKIN;
    }

    @Override
    protected void scale(BatEntity batEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void setupTransforms(BatEntity batEntity, MatrixStack matrixStack, float f, float g, float h) {
        if (batEntity.isRoosting()) {
            matrixStack.translate(0.0, -0.1f, 0.0);
        } else {
            matrixStack.translate(0.0, MathHelper.cos(f * 0.3f) * 0.1f, 0.0);
        }
        super.setupTransforms(batEntity, matrixStack, f, g, h);
    }
}

