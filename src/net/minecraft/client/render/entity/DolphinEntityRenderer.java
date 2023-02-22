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
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityRenderer
extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/dolphin.png");

    public DolphinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN)), 0.7f);
        this.addFeature(new DolphinHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(DolphinEntity dolphinEntity) {
        return TEXTURE;
    }
}

