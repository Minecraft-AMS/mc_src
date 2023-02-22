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
    private static final Identifier SKIN = new Identifier("textures/entity/steve.png");

    public BipedEntityRenderer(EntityRenderDispatcher renderManager, M model, float f) {
        super(renderManager, model, f);
        this.addFeature(new HeadFeatureRenderer(this));
        this.addFeature(new ElytraFeatureRenderer(this));
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    @Override
    protected Identifier getTexture(T mobEntity) {
        return SKIN;
    }
}

