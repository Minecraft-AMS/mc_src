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
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityRenderer
extends MobEntityRenderer<PolarBearEntity, PolarBearEntityModel<PolarBearEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/bear/polarbear.png");

    public PolarBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PolarBearEntityModel(), 0.9f);
    }

    @Override
    public Identifier getTexture(PolarBearEntity polarBearEntity) {
        return SKIN;
    }

    @Override
    protected void scale(PolarBearEntity polarBearEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(1.2f, 1.2f, 1.2f);
        super.scale(polarBearEntity, matrixStack, f);
    }
}

