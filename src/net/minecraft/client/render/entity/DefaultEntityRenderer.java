/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultEntityRenderer
extends EntityRenderer<Entity> {
    public DefaultEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(Entity entity, double x, double y, double z, float yaw, float tickDelta) {
        GlStateManager.pushMatrix();
        DefaultEntityRenderer.renderBox(entity.getBoundingBox(), x - entity.lastRenderX, y - entity.lastRenderY, z - entity.lastRenderZ);
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }

    @Override
    @Nullable
    protected Identifier getTexture(Entity entity) {
        return null;
    }
}

