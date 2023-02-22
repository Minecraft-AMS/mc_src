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
public class QuadrupedEntityModel<T extends Entity>
extends EntityModel<T> {
    protected ModelPart head = new ModelPart(this, 0, 0);
    protected ModelPart torso;
    protected ModelPart backRightLeg;
    protected ModelPart backLeftLeg;
    protected ModelPart frontRightLeg;
    protected ModelPart frontLeftLeg;
    protected float field_3540 = 8.0f;
    protected float field_3537 = 4.0f;

    public QuadrupedEntityModel(int i, float f) {
        this.head.addCuboid(-4.0f, -4.0f, -8.0f, 8, 8, 8, f);
        this.head.setPivot(0.0f, 18 - i, -6.0f);
        this.torso = new ModelPart(this, 28, 8);
        this.torso.addCuboid(-5.0f, -10.0f, -7.0f, 10, 16, 8, f);
        this.torso.setPivot(0.0f, 17 - i, 2.0f);
        this.backRightLeg = new ModelPart(this, 0, 16);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.backRightLeg.setPivot(-3.0f, 24 - i, 7.0f);
        this.backLeftLeg = new ModelPart(this, 0, 16);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.backLeftLeg.setPivot(3.0f, 24 - i, 7.0f);
        this.frontRightLeg = new ModelPart(this, 0, 16);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.frontRightLeg.setPivot(-3.0f, 24 - i, -5.0f);
        this.frontLeftLeg = new ModelPart(this, 0, 16);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.frontLeftLeg.setPivot(3.0f, 24 - i, -5.0f);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.child) {
            float f = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.field_3540 * scale, this.field_3537 * scale);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.torso.render(scale);
            this.backRightLeg.render(scale);
            this.backLeftLeg.render(scale);
            this.frontRightLeg.render(scale);
            this.frontLeftLeg.render(scale);
            GlStateManager.popMatrix();
        } else {
            this.head.render(scale);
            this.torso.render(scale);
            this.backRightLeg.render(scale);
            this.backLeftLeg.render(scale);
            this.frontRightLeg.render(scale);
            this.frontLeftLeg.render(scale);
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        this.backRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.backLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.frontRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.frontLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}

