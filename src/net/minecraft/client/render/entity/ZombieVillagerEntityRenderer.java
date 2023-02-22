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
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityRenderer
extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
        super(entityRenderDispatcher, new ZombieVillagerEntityModel(), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new ZombieVillagerEntityModel(0.5f, true), new ZombieVillagerEntityModel(1.0f, true)));
        this.addFeature(new VillagerClothingFeatureRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>(this, reloadableResourceManager, "zombie_villager"));
    }

    @Override
    protected Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
        return SKIN;
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity zombieVillagerEntity, float f, float g, float h) {
        if (zombieVillagerEntity.isConverting()) {
            g += (float)(Math.cos((double)zombieVillagerEntity.age * 3.25) * Math.PI * 0.25);
        }
        super.setupTransforms(zombieVillagerEntity, f, g, h);
    }
}

