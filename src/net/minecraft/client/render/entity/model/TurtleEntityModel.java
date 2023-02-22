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
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity>
extends QuadrupedEntityModel<T> {
    private final ModelPart field_3594;

    public TurtleEntityModel(float f) {
        super(12, f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 3, 0);
        this.head.addCuboid(-3.0f, -1.0f, -3.0f, 6, 5, 6, 0.0f);
        this.head.setPivot(0.0f, 19.0f, -10.0f);
        this.torso = new ModelPart(this);
        this.torso.setTextureOffset(7, 37).addCuboid(-9.5f, 3.0f, -10.0f, 19, 20, 6, 0.0f);
        this.torso.setTextureOffset(31, 1).addCuboid(-5.5f, 3.0f, -13.0f, 11, 18, 3, 0.0f);
        this.torso.setPivot(0.0f, 11.0f, -10.0f);
        this.field_3594 = new ModelPart(this);
        this.field_3594.setTextureOffset(70, 33).addCuboid(-4.5f, 3.0f, -14.0f, 9, 18, 1, 0.0f);
        this.field_3594.setPivot(0.0f, 11.0f, -10.0f);
        boolean i = true;
        this.backRightLeg = new ModelPart(this, 1, 23);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, 0.0f, 4, 1, 10, 0.0f);
        this.backRightLeg.setPivot(-3.5f, 22.0f, 11.0f);
        this.backLeftLeg = new ModelPart(this, 1, 12);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, 0.0f, 4, 1, 10, 0.0f);
        this.backLeftLeg.setPivot(3.5f, 22.0f, 11.0f);
        this.frontRightLeg = new ModelPart(this, 27, 30);
        this.frontRightLeg.addCuboid(-13.0f, 0.0f, -2.0f, 13, 1, 5, 0.0f);
        this.frontRightLeg.setPivot(-5.0f, 21.0f, -4.0f);
        this.frontLeftLeg = new ModelPart(this, 27, 24);
        this.frontLeftLeg.addCuboid(0.0f, 0.0f, -2.0f, 13, 1, 5, 0.0f);
        this.frontLeftLeg.setPivot(5.0f, 21.0f, -4.0f);
    }

    @Override
    public void render(T turtleEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(turtleEntity, f, g, h, i, j, k);
        if (this.child) {
            float l = 6.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.16666667f, 0.16666667f, 0.16666667f);
            GlStateManager.translatef(0.0f, 120.0f * k, 0.0f);
            this.head.render(k);
            this.torso.render(k);
            this.backRightLeg.render(k);
            this.backLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.frontLeftLeg.render(k);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            if (((TurtleEntity)turtleEntity).hasEgg()) {
                GlStateManager.translatef(0.0f, -0.08f, 0.0f);
            }
            this.head.render(k);
            this.torso.render(k);
            GlStateManager.pushMatrix();
            this.backRightLeg.render(k);
            this.backLeftLeg.render(k);
            GlStateManager.popMatrix();
            this.frontRightLeg.render(k);
            this.frontLeftLeg.render(k);
            if (((TurtleEntity)turtleEntity).hasEgg()) {
                this.field_3594.render(k);
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void setAngles(T turtleEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(turtleEntity, f, g, h, i, j, k);
        this.backRightLeg.pitch = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.backLeftLeg.pitch = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.frontRightLeg.roll = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.frontLeftLeg.roll = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.frontRightLeg.pitch = 0.0f;
        this.frontLeftLeg.pitch = 0.0f;
        this.frontRightLeg.yaw = 0.0f;
        this.frontLeftLeg.yaw = 0.0f;
        this.backRightLeg.yaw = 0.0f;
        this.backLeftLeg.yaw = 0.0f;
        this.field_3594.pitch = 1.5707964f;
        if (!((Entity)turtleEntity).isTouchingWater() && ((TurtleEntity)turtleEntity).onGround) {
            float l = ((TurtleEntity)turtleEntity).isDiggingSand() ? 4.0f : 1.0f;
            float m = ((TurtleEntity)turtleEntity).isDiggingSand() ? 2.0f : 1.0f;
            float n = 5.0f;
            this.frontRightLeg.yaw = MathHelper.cos(l * f * 5.0f + (float)Math.PI) * 8.0f * g * m;
            this.frontRightLeg.roll = 0.0f;
            this.frontLeftLeg.yaw = MathHelper.cos(l * f * 5.0f) * 8.0f * g * m;
            this.frontLeftLeg.roll = 0.0f;
            this.backRightLeg.yaw = MathHelper.cos(f * 5.0f + (float)Math.PI) * 3.0f * g;
            this.backRightLeg.pitch = 0.0f;
            this.backLeftLeg.yaw = MathHelper.cos(f * 5.0f) * 3.0f * g;
            this.backLeftLeg.pitch = 0.0f;
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles((T)((TurtleEntity)entity), limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.render((T)((TurtleEntity)entity), limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }
}

