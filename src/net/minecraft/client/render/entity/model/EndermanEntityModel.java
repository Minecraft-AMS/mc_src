/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    public boolean carryingBlock;
    public boolean angry;

    public EndermanEntityModel(float f) {
        super(0.0f, -14.0f, 64, 32);
        float g = -14.0f;
        this.hat = new ModelPart(this, 0, 16);
        this.hat.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f - 0.5f);
        this.hat.setPivot(0.0f, -14.0f, 0.0f);
        this.body = new ModelPart(this, 32, 16);
        this.body.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.body.setPivot(0.0f, -14.0f, 0.0f);
        this.rightArm = new ModelPart(this, 56, 0);
        this.rightArm.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.rightArm.setPivot(-3.0f, -12.0f, 0.0f);
        this.leftArm = new ModelPart(this, 56, 0);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.leftArm.setPivot(5.0f, -12.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 56, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.rightLeg.setPivot(-2.0f, -2.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 56, 0);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.leftLeg.setPivot(2.0f, -2.0f, 0.0f);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        float m;
        super.setAngles(livingEntity, f, g, h, i, j);
        this.head.visible = true;
        float k = -14.0f;
        this.body.pitch = 0.0f;
        this.body.pivotY = -14.0f;
        this.body.pivotZ = -0.0f;
        this.rightLeg.pitch -= 0.0f;
        this.leftLeg.pitch -= 0.0f;
        this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
        this.leftArm.pitch = (float)((double)this.leftArm.pitch * 0.5);
        this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
        this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
        float l = 0.4f;
        if (this.rightArm.pitch > 0.4f) {
            this.rightArm.pitch = 0.4f;
        }
        if (this.leftArm.pitch > 0.4f) {
            this.leftArm.pitch = 0.4f;
        }
        if (this.rightArm.pitch < -0.4f) {
            this.rightArm.pitch = -0.4f;
        }
        if (this.leftArm.pitch < -0.4f) {
            this.leftArm.pitch = -0.4f;
        }
        if (this.rightLeg.pitch > 0.4f) {
            this.rightLeg.pitch = 0.4f;
        }
        if (this.leftLeg.pitch > 0.4f) {
            this.leftLeg.pitch = 0.4f;
        }
        if (this.rightLeg.pitch < -0.4f) {
            this.rightLeg.pitch = -0.4f;
        }
        if (this.leftLeg.pitch < -0.4f) {
            this.leftLeg.pitch = -0.4f;
        }
        if (this.carryingBlock) {
            this.rightArm.pitch = -0.5f;
            this.leftArm.pitch = -0.5f;
            this.rightArm.roll = 0.05f;
            this.leftArm.roll = -0.05f;
        }
        this.rightArm.pivotZ = 0.0f;
        this.leftArm.pivotZ = 0.0f;
        this.rightLeg.pivotZ = 0.0f;
        this.leftLeg.pivotZ = 0.0f;
        this.rightLeg.pivotY = -5.0f;
        this.leftLeg.pivotY = -5.0f;
        this.head.pivotZ = -0.0f;
        this.head.pivotY = -13.0f;
        this.hat.pivotX = this.head.pivotX;
        this.hat.pivotY = this.head.pivotY;
        this.hat.pivotZ = this.head.pivotZ;
        this.hat.pitch = this.head.pitch;
        this.hat.yaw = this.head.yaw;
        this.hat.roll = this.head.roll;
        if (this.angry) {
            m = 1.0f;
            this.head.pivotY -= 5.0f;
        }
        m = -14.0f;
        this.rightArm.setPivot(-5.0f, -12.0f, 0.0f);
        this.leftArm.setPivot(5.0f, -12.0f, 0.0f);
    }
}

