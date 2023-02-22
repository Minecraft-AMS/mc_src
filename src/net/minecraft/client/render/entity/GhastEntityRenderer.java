/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GhastEntityRenderer
extends MobEntityRenderer<GhastEntity, GhastEntityModel<GhastEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/ghast/ghast.png");
    private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/ghast/ghast_shooting.png");

    public GhastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new GhastEntityModel(), 1.5f);
    }

    @Override
    protected Identifier getTexture(GhastEntity ghastEntity) {
        if (ghastEntity.isShooting()) {
            return ANGRY_SKIN;
        }
        return SKIN;
    }

    @Override
    protected void scale(GhastEntity ghastEntity, float f) {
        float g = 1.0f;
        float h = 4.5f;
        float i = 4.5f;
        GlStateManager.scalef(4.5f, 4.5f, 4.5f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}

