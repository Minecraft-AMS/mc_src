/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
    public static final Identifier[] DESTROY_STAGE_TEXTURES = new Identifier[]{new Identifier("textures/" + ModelLoader.DESTROY_STAGE_0.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_1.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_2.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_3.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_4.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_5.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_6.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_7.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_8.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_9.getPath() + ".png")};
    protected BlockEntityRenderDispatcher renderManager;

    public void render(T entity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage) {
        HitResult hitResult = this.renderManager.crosshairTarget;
        if (entity instanceof Nameable && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK && ((BlockEntity)entity).getPos().equals(((BlockHitResult)hitResult).getBlockPos())) {
            this.disableLightmap(true);
            this.renderName(entity, ((Nameable)entity).getDisplayName().asFormattedString(), xOffset, yOffset, zOffset, 12);
            this.disableLightmap(false);
        }
    }

    protected void disableLightmap(boolean bl) {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        if (bl) {
            GlStateManager.disableTexture();
        } else {
            GlStateManager.enableTexture();
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    protected void bindTexture(Identifier identifier) {
        TextureManager textureManager = this.renderManager.textureManager;
        if (textureManager != null) {
            textureManager.bindTexture(identifier);
        }
    }

    protected World getWorld() {
        return this.renderManager.world;
    }

    public void setRenderManager(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.renderManager = blockEntityRenderDispatcher;
    }

    public TextRenderer getFontRenderer() {
        return this.renderManager.getTextRenderer();
    }

    public boolean method_3563(T blockEntity) {
        return false;
    }

    protected void renderName(T blockEntity, String string, double d, double e, double f, int i) {
        Camera camera = this.renderManager.camera;
        double g = ((BlockEntity)blockEntity).getSquaredDistance(camera.getPos().x, camera.getPos().y, camera.getPos().z);
        if (g > (double)(i * i)) {
            return;
        }
        float h = camera.getYaw();
        float j = camera.getPitch();
        GameRenderer.renderFloatingText(this.getFontRenderer(), string, (float)d + 0.5f, (float)e + 1.5f, (float)f + 0.5f, 0, h, j, false);
    }
}
