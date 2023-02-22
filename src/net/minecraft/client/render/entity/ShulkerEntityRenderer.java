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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ShulkerHeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderer
extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
    public static final Identifier SKIN = new Identifier("textures/" + TexturedRenderLayers.SHULKER_TEXTURE_ID.getTextureId().getPath() + ".png");
    public static final Identifier[] SKIN_COLOR = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.stream().map(spriteIdentifier -> new Identifier("textures/" + spriteIdentifier.getTextureId().getPath() + ".png")).toArray(Identifier[]::new);

    public ShulkerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ShulkerEntityModel(), 0.0f);
        this.addFeature(new ShulkerHeadFeatureRenderer(this));
    }

    @Override
    public Vec3d getPositionOffset(ShulkerEntity shulkerEntity, float f) {
        int i = shulkerEntity.method_7113();
        if (i > 0 && shulkerEntity.method_7117()) {
            BlockPos blockPos = shulkerEntity.getAttachedBlock();
            BlockPos blockPos2 = shulkerEntity.method_7120();
            double d = (double)((float)i - f) / 6.0;
            d *= d;
            double e = (double)(blockPos.getX() - blockPos2.getX()) * d;
            double g = (double)(blockPos.getY() - blockPos2.getY()) * d;
            double h = (double)(blockPos.getZ() - blockPos2.getZ()) * d;
            return new Vec3d(-e, -g, -h);
        }
        return super.getPositionOffset(shulkerEntity, f);
    }

    @Override
    public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
        if (super.shouldRender(shulkerEntity, frustum, d, e, f)) {
            return true;
        }
        if (shulkerEntity.method_7113() > 0 && shulkerEntity.method_7117()) {
            Vec3d vec3d = new Vec3d(shulkerEntity.getAttachedBlock());
            Vec3d vec3d2 = new Vec3d(shulkerEntity.method_7120());
            if (frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Identifier getTexture(ShulkerEntity shulkerEntity) {
        if (shulkerEntity.getColor() == null) {
            return SKIN;
        }
        return SKIN_COLOR[shulkerEntity.getColor().getId()];
    }

    @Override
    protected void setupTransforms(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(shulkerEntity, matrixStack, f, g, h);
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.multiply(shulkerEntity.getAttachedFace().getOpposite().getRotationQuaternion());
        matrixStack.translate(0.0, -0.5, 0.0);
    }

    @Override
    protected void scale(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f) {
        float g = 0.999f;
        matrixStack.scale(0.999f, 0.999f, 0.999f);
    }

    @Override
    public /* synthetic */ Vec3d getPositionOffset(Entity entity, float tickDelta) {
        return this.getPositionOffset((ShulkerEntity)entity, tickDelta);
    }
}

