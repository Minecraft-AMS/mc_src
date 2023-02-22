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
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class VexEntityRenderer
extends MobEntityRenderer<VexEntity, VexEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vex.png");
    private static final Identifier CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");

    public VexEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VexEntityModel(context.getPart(EntityModelLayers.VEX)), 0.3f);
        this.addFeature(new HeldItemFeatureRenderer<VexEntity, VexEntityModel>(this, context.getHeldItemRenderer()));
    }

    @Override
    protected int getBlockLight(VexEntity vexEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(VexEntity vexEntity) {
        if (vexEntity.isCharging()) {
            return CHARGING_TEXTURE;
        }
        return TEXTURE;
    }
}

