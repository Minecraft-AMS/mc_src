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
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends EntityRenderer<BoatEntity> {
    private static final Identifier[] SKIN = new Identifier[]{new Identifier("textures/entity/boat/oak.png"), new Identifier("textures/entity/boat/spruce.png"), new Identifier("textures/entity/boat/birch.png"), new Identifier("textures/entity/boat/jungle.png"), new Identifier("textures/entity/boat/acacia.png"), new Identifier("textures/entity/boat/dark_oak.png")};
    protected final BoatEntityModel model = new BoatEntityModel();

    public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.8f;
    }

    @Override
    public void render(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        this.translateToBoat(d, e, f);
        this.rotateToBoat(boatEntity, g, h);
        this.bindEntityTexture(boatEntity);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(boatEntity));
        }
        this.model.render(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(boatEntity, d, e, f, g, h);
    }

    public void rotateToBoat(BoatEntity boat, float yaw, float tickDelta) {
        float h;
        GlStateManager.rotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        float f = (float)boat.getDamageWobbleTicks() - tickDelta;
        float g = boat.getDamageWobbleStrength() - tickDelta;
        if (g < 0.0f) {
            g = 0.0f;
        }
        if (f > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(f) * f * g / 10.0f * (float)boat.getDamageWobbleSide(), 1.0f, 0.0f, 0.0f);
        }
        if (!MathHelper.approximatelyEquals(h = boat.interpolateBubbleWobble(tickDelta), 0.0f)) {
            GlStateManager.rotatef(boat.interpolateBubbleWobble(tickDelta), 1.0f, 0.0f, 1.0f);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
    }

    public void translateToBoat(double x, double y, double z) {
        GlStateManager.translatef((float)x, (float)y + 0.375f, (float)z);
    }

    @Override
    protected Identifier getTexture(BoatEntity boatEntity) {
        return SKIN[boatEntity.getBoatType().ordinal()];
    }

    @Override
    public boolean hasSecondPass() {
        return true;
    }

    @Override
    public void renderSecondPass(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        this.translateToBoat(d, e, f);
        this.rotateToBoat(boatEntity, g, h);
        this.bindEntityTexture(boatEntity);
        this.model.renderPass(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
}

