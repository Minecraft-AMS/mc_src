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
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BipedEntityRenderer<T extends MobEntity, M extends BipedEntityModel<T>>
extends MobEntityRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/steve.png");

    public BipedEntityRenderer(EntityRenderDispatcher dispatcher, M model, float f) {
        this(dispatcher, model, f, 1.0f, 1.0f, 1.0f);
    }

    public BipedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M bipedEntityModel, float f, float g, float h, float i) {
        super(entityRenderDispatcher, bipedEntityModel, f);
        this.addFeature(new HeadFeatureRenderer(this, g, h, i));
        this.addFeature(new ElytraFeatureRenderer(this));
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(T mobEntity) {
        return TEXTURE;
    }
}

