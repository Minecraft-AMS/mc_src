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
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CaveSpiderEntityRenderer
extends SpiderEntityRenderer<CaveSpiderEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/spider/cave_spider.png");

    public CaveSpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 *= 0.7f;
    }

    @Override
    protected void scale(CaveSpiderEntity caveSpiderEntity, float f) {
        GlStateManager.scalef(0.7f, 0.7f, 0.7f);
    }

    @Override
    protected Identifier getTexture(CaveSpiderEntity caveSpiderEntity) {
        return SKIN;
    }
}

