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
    private static final Identifier RED_BLUE_TEXTURE = new Identifier("textures/entity/parrot/parrot_red_blue.png");
    private static final Identifier BLUE_TEXTURE = new Identifier("textures/entity/parrot/parrot_blue.png");
    private static final Identifier GREEN_TEXTURE = new Identifier("textures/entity/parrot/parrot_green.png");
    private static final Identifier YELLOW_TEXTURE = new Identifier("textures/entity/parrot/parrot_yellow_blue.png");
    private static final Identifier GREY_TEXTURE = new Identifier("textures/entity/parrot/parrot_grey.png");

    public ParrotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3f);
    }

    @Override
    public Identifier getTexture(ParrotEntity parrotEntity) {
        return ParrotEntityRenderer.getTexture(parrotEntity.getVariant());
    }

    public static Identifier getTexture(ParrotEntity.Variant variant) {
        return switch (variant) {
            default -> throw new IncompatibleClassChangeError();
            case ParrotEntity.Variant.RED_BLUE -> RED_BLUE_TEXTURE;
            case ParrotEntity.Variant.BLUE -> BLUE_TEXTURE;
            case ParrotEntity.Variant.GREEN -> GREEN_TEXTURE;
            case ParrotEntity.Variant.YELLOW_BLUE -> YELLOW_TEXTURE;
            case ParrotEntity.Variant.GRAY -> GREY_TEXTURE;
        };
    }

    @Override
    public float getAnimationProgress(ParrotEntity parrotEntity, float f) {
        float g = MathHelper.lerp(f, parrotEntity.prevFlapProgress, parrotEntity.flapProgress);
        float h = MathHelper.lerp(f, parrotEntity.prevMaxWingDeviation, parrotEntity.maxWingDeviation);
        return (MathHelper.sin(g) + 1.0f) * h;
    }
}

