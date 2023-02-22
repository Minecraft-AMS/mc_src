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
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitherEntityRenderer
extends MobEntityRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
    private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");

    public WitherEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitherEntityModel(0.0f), 1.0f);
        this.addFeature(new WitherArmorFeatureRenderer(this));
    }

    @Override
    protected int getBlockLight(WitherEntity witherEntity, float f) {
        return 15;
    }

    @Override
    public Identifier getTexture(WitherEntity witherEntity) {
        int i = witherEntity.getInvulnerableTimer();
        if (i <= 0 || i <= 80 && i / 5 % 2 == 1) {
            return SKIN;
        }
        return INVINCIBLE_SKIN;
    }

    @Override
    protected void scale(WitherEntity witherEntity, MatrixStack matrixStack, float f) {
        float g = 2.0f;
        int i = witherEntity.getInvulnerableTimer();
        if (i > 0) {
            g -= ((float)i - f) / 220.0f * 0.5f;
        }
        matrixStack.scale(g, g, g);
    }
}

