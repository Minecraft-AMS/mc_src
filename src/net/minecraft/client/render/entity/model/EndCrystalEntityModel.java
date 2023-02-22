/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart cube;
    private final ModelPart glass = new ModelPart(this, "glass");
    private final ModelPart base;

    public EndCrystalEntityModel(float f, boolean bl) {
        this.glass.setTextureOffset(0, 0).addCuboid(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        this.cube = new ModelPart(this, "cube");
        this.cube.setTextureOffset(32, 0).addCuboid(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        if (bl) {
            this.base = new ModelPart(this, "base");
            this.base.setTextureOffset(0, 16).addCuboid(-6.0f, 0.0f, -6.0f, 12, 4, 12);
        } else {
            this.base = null;
        }
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        GlStateManager.translatef(0.0f, -0.5f, 0.0f);
        if (this.base != null) {
            this.base.render(scale);
        }
        GlStateManager.rotatef(limbDistance, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.8f + age, 0.0f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        this.glass.render(scale);
        float f = 0.875f;
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(limbDistance, 0.0f, 1.0f, 0.0f);
        this.glass.render(scale);
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(limbDistance, 0.0f, 1.0f, 0.0f);
        this.cube.render(scale);
        GlStateManager.popMatrix();
    }
}

