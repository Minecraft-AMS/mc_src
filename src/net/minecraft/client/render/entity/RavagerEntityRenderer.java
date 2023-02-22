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
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityRenderer
extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/ravager.png");

    public RavagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new RavagerEntityModel(), 1.1f);
    }

    @Override
    protected Identifier getTexture(RavagerEntity ravagerEntity) {
        return SKIN;
    }
}
