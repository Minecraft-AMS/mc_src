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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityRenderer
extends MobEntityRenderer<ParrotEntity, ParrotEntityModel> {
    public static final Identifier[] TEXTURES = new Identifier[]{new Identifier("textures/entity/parrot/parrot_red_blue.png"), new Identifier("textures/entity/parrot/parrot_blue.png"), new Identifier("textures/entity/parrot/parrot_green.png"), new Identifier("textures/entity/parrot/parrot_yellow_blue.png"), new Identifier("textures/entity/parrot/parrot_grey.png")};

    public ParrotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3f);
    }

    @Override
    public Identifier getTexture(ParrotEntity parrotEntity) {
        return TEXTURES[parrotEntity.getVariant()];
    }

    @Override
    public float getAnimationProgress(ParrotEntity parrotEntity, float f) {
        float g = MathHelper.lerp(f, parrotEntity.prevFlapProgress, parrotEntity.flapProgress);
        float h = MathHelper.lerp(f, parrotEntity.prevMaxWingDeviation, parrotEntity.maxWingDeviation);
        return (MathHelper.sin(g) + 1.0f) * h;
    }
}

