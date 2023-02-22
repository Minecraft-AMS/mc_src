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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
    protected final EntityRenderDispatcher renderManager;
    protected float shadowSize;
    protected float shadowDarkness = 1.0f;

    protected EntityRenderer(EntityRenderDispatcher dispatcher) {
        this.renderManager = dispatcher;
    }

    public final int getLight(T entity, float tickDelta) {
        return LightmapTextureManager.pack(this.getBlockLight(entity, tickDelta), ((Entity)entity).world.getLightLevel(LightType.SKY, new BlockPos(((Entity)entity).getCameraPosVec(tickDelta))));
    }

    protected int getBlockLight(T entity, float tickDelta) {
        if (((Entity)entity).isOnFire()) {
            return 15;
        }
        return ((Entity)entity).world.getLightLevel(LightType.BLOCK, new BlockPos(((Entity)entity).getCameraPosVec(tickDelta)));
    }

    public boolean shouldRender(T entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        if (!((Entity)entity).shouldRender(cameraX, cameraY, cameraZ)) {
            return false;
        }
        if (((Entity)entity).ignoreCameraFrustum) {
            return true;
        }
        Box box = ((Entity)entity).getVisibilityBoundingBox().expand(0.5);
        if (box.isValid() || box.getAverageSideLength() == 0.0) {
            box = new Box(((Entity)entity).getX() - 2.0, ((Entity)entity).getY() - 2.0, ((Entity)entity).getZ() - 2.0, ((Entity)entity).getX() + 2.0, ((Entity)entity).getY() + 2.0, ((Entity)entity).getZ() + 2.0);
        }
        return visibleRegion.isVisible(box);
    }

    public Vec3d getPositionOffset(T entity, float tickDelta) {
        return Vec3d.ZERO;
    }

    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!this.hasLabel(entity)) {
            return;
        }
        this.renderLabelIfPresent(entity, ((Entity)entity).getDisplayName().asFormattedString(), matrices, vertexConsumers, light);
    }

    protected boolean hasLabel(T entity) {
        return ((Entity)entity).shouldRenderName() && ((Entity)entity).hasCustomName();
    }

    public abstract Identifier getTexture(T var1);

    public TextRenderer getFontRenderer() {
        return this.renderManager.getTextRenderer();
    }

    protected void renderLabelIfPresent(T entity, String string, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i) {
        double d = this.renderManager.getSquaredDistanceToCamera((Entity)entity);
        if (d > 4096.0) {
            return;
        }
        boolean bl = !((Entity)entity).isSneaky();
        float f = ((Entity)entity).getHeight() + 0.5f;
        int j = "deadmau5".equals(string) ? -10 : 0;
        matrices.push();
        matrices.translate(0.0, f, 0.0);
        matrices.multiply(this.renderManager.getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrices.peek().getModel();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int k = (int)(g * 255.0f) << 24;
        TextRenderer textRenderer = this.getFontRenderer();
        float h = -textRenderer.getStringWidth(string) / 2;
        textRenderer.draw(string, h, j, 0x20FFFFFF, false, matrix4f, vertexConsumerProvider, bl, k, i);
        if (bl) {
            textRenderer.draw(string, h, j, -1, false, matrix4f, vertexConsumerProvider, false, 0, i);
        }
        matrices.pop();
    }

    public EntityRenderDispatcher getRenderManager() {
        return this.renderManager;
    }
}

