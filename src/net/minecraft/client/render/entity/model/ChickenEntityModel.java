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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart field_3346;
    private final ModelPart field_3345;
    private final ModelPart field_3343;
    private final ModelPart field_3341;
    private final ModelPart field_3347;
    private final ModelPart field_3340;
    private final ModelPart field_3342;

    public ChickenEntityModel() {
        int i = 16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setPivot(0.0f, 15.0f, -4.0f);
        this.field_3340 = new ModelPart(this, 14, 0);
        this.field_3340.addCuboid(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.field_3340.setPivot(0.0f, 15.0f, -4.0f);
        this.field_3342 = new ModelPart(this, 14, 4);
        this.field_3342.addCuboid(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.field_3342.setPivot(0.0f, 15.0f, -4.0f);
        this.field_3346 = new ModelPart(this, 0, 9);
        this.field_3346.addCuboid(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.field_3346.setPivot(0.0f, 16.0f, 0.0f);
        this.field_3345 = new ModelPart(this, 26, 0);
        this.field_3345.addCuboid(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.field_3345.setPivot(-2.0f, 19.0f, 1.0f);
        this.field_3343 = new ModelPart(this, 26, 0);
        this.field_3343.addCuboid(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.field_3343.setPivot(1.0f, 19.0f, 1.0f);
        this.field_3341 = new ModelPart(this, 24, 13);
        this.field_3341.addCuboid(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.field_3341.setPivot(-4.0f, 13.0f, 0.0f);
        this.field_3347 = new ModelPart(this, 24, 13);
        this.field_3347.addCuboid(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.field_3347.setPivot(4.0f, 13.0f, 0.0f);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.child) {
            float f = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 5.0f * scale, 2.0f * scale);
            this.head.render(scale);
            this.field_3340.render(scale);
            this.field_3342.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.field_3346.render(scale);
            this.field_3345.render(scale);
            this.field_3343.render(scale);
            this.field_3341.render(scale);
            this.field_3347.render(scale);
            GlStateManager.popMatrix();
        } else {
            this.head.render(scale);
            this.field_3340.render(scale);
            this.field_3342.render(scale);
            this.field_3346.render(scale);
            this.field_3345.render(scale);
            this.field_3343.render(scale);
            this.field_3341.render(scale);
            this.field_3347.render(scale);
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.field_3340.pitch = this.head.pitch;
        this.field_3340.yaw = this.head.yaw;
        this.field_3342.pitch = this.head.pitch;
        this.field_3342.yaw = this.head.yaw;
        this.field_3346.pitch = 1.5707964f;
        this.field_3345.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.field_3343.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_3341.roll = age;
        this.field_3347.roll = -age;
    }
}

