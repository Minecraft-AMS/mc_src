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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>>
extends BipedEntityRenderer<T, M> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie/zombie.png");

    protected ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M zombieEntityModel, M zombieEntityModel2, M zombieEntityModel3) {
        super(entityRenderDispatcher, zombieEntityModel, 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, zombieEntityModel2, zombieEntityModel3));
    }

    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return SKIN;
    }

    @Override
    protected void setupTransforms(T zombieEntity, MatrixStack matrixStack, float f, float g, float h) {
        if (((ZombieEntity)zombieEntity).isConvertingInWater()) {
            g += (float)(Math.cos((double)((ZombieEntity)zombieEntity).age * 3.25) * Math.PI * 0.25);
        }
        super.setupTransforms(zombieEntity, matrixStack, f, g, h);
    }
}

