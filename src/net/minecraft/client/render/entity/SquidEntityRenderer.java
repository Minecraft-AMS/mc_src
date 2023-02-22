/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.LivingEntity;
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
    protected Identifier getTexture(SquidEntity squidEntity) {
        return SKIN;
    }

    @Override
    protected void setupTransforms(SquidEntity squidEntity, float f, float g, float h) {
        float i = MathHelper.lerp(h, squidEntity.field_6905, squidEntity.field_6907);
        float j = MathHelper.lerp(h, squidEntity.field_6906, squidEntity.field_6903);
        GlStateManager.translatef(0.0f, 0.5f, 0.0f);
        GlStateManager.rotatef(180.0f - g, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(i, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(j, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.2f, 0.0f);
    }

    @Override
    protected float getAnimationProgress(SquidEntity squidEntity, float f) {
        return MathHelper.lerp(f, squidEntity.field_6900, squidEntity.field_6904);
    }

    @Override
    protected /* synthetic */ float getAnimationProgress(LivingEntity entity, float tickDelta) {
        return this.getAnimationProgress((SquidEntity)entity, tickDelta);
    }
}

