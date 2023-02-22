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
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StrayEntityRenderer
extends SkeletonEntityRenderer {
    private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray.png");

    public StrayEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.addFeature(new StrayOverlayFeatureRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>>(this));
    }

    @Override
    protected Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
        return SKIN;
    }
}
