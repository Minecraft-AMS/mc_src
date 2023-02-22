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
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity>
extends EntityRenderer<T> {
    private final ItemRenderer item;
    private final float scale;

    public FlyingItemEntityRenderer(EntityRenderDispatcher renderManager, ItemRenderer itemRenderer, float scale) {
        super(renderManager);
        this.item = itemRenderer;
        this.scale = scale;
    }

    public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        this(entityRenderDispatcher, itemRenderer, 1.0f);
    }

    @Override
    public void render(T entity, double x, double y, double z, float yaw, float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.item.renderItem(((FlyingItemEntity)entity).getStack(), ModelTransformation.Type.GROUND);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }

    @Override
    protected Identifier getTexture(Entity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

