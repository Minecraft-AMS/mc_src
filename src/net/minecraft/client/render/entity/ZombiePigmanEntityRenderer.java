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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombiePigmanEntityRenderer
extends BipedEntityRenderer<ZombiePigmanEntity, ZombieEntityModel<ZombiePigmanEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie_pigman.png");

    public ZombiePigmanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ZombieEntityModel(0.0f, false), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new ZombieEntityModel(0.5f, true), new ZombieEntityModel(1.0f, true)));
    }

    @Override
    public Identifier getTexture(ZombiePigmanEntity zombiePigmanEntity) {
        return SKIN;
    }
}

