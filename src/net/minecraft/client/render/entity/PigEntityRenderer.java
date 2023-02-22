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
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PigEntityRenderer
extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/pig/pig.png");

    public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PigEntityModel(), 0.7f);
        this.addFeature(new PigSaddleFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(PigEntity pigEntity) {
        return SKIN;
    }
}

