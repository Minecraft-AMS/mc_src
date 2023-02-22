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
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SilverfishEntityRenderer
extends MobEntityRenderer<SilverfishEntity, SilverfishEntityModel<SilverfishEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/silverfish.png");

    public SilverfishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SilverfishEntityModel(), 0.3f);
    }

    @Override
    protected float getLyingAngle(SilverfishEntity silverfishEntity) {
        return 180.0f;
    }

    @Override
    protected Identifier getTexture(SilverfishEntity silverfishEntity) {
        return SKIN;
    }

    @Override
    protected /* synthetic */ float getLyingAngle(LivingEntity entity) {
        return this.getLyingAngle((SilverfishEntity)entity);
    }

    @Override
    protected /* synthetic */ Identifier getTexture(Entity entity) {
        return this.getTexture((SilverfishEntity)entity);
    }
}
