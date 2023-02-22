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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BatEntityModel
extends EntityModel<BatEntity> {
    private final ModelPart field_3321;
    private final ModelPart field_3323;
    private final ModelPart field_3322;
    private final ModelPart field_3320;
    private final ModelPart field_3319;
    private final ModelPart field_3324;

    public BatEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3321 = new ModelPart(this, 0, 0);
        this.field_3321.addCuboid(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        ModelPart modelPart = new ModelPart(this, 24, 0);
        modelPart.addCuboid(-4.0f, -6.0f, -2.0f, 3, 4, 1);
        this.field_3321.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 24, 0);
        modelPart2.mirror = true;
        modelPart2.addCuboid(1.0f, -6.0f, -2.0f, 3, 4, 1);
        this.field_3321.addChild(modelPart2);
        this.field_3323 = new ModelPart(this, 0, 16);
        this.field_3323.addCuboid(-3.0f, 4.0f, -3.0f, 6, 12, 6);
        this.field_3323.setTextureOffset(0, 34).addCuboid(-5.0f, 16.0f, 0.0f, 10, 6, 1);
        this.field_3322 = new ModelPart(this, 42, 0);
        this.field_3322.addCuboid(-12.0f, 1.0f, 1.5f, 10, 16, 1);
        this.field_3319 = new ModelPart(this, 24, 16);
        this.field_3319.setPivot(-12.0f, 1.0f, 1.5f);
        this.field_3319.addCuboid(-8.0f, 1.0f, 0.0f, 8, 12, 1);
        this.field_3320 = new ModelPart(this, 42, 0);
        this.field_3320.mirror = true;
        this.field_3320.addCuboid(2.0f, 1.0f, 1.5f, 10, 16, 1);
        this.field_3324 = new ModelPart(this, 24, 16);
        this.field_3324.mirror = true;
        this.field_3324.setPivot(12.0f, 1.0f, 1.5f);
        this.field_3324.addCuboid(0.0f, 1.0f, 0.0f, 8, 12, 1);
        this.field_3323.addChild(this.field_3322);
        this.field_3323.addChild(this.field_3320);
        this.field_3322.addChild(this.field_3319);
        this.field_3320.addChild(this.field_3324);
    }

    @Override
    public void render(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(batEntity, f, g, h, i, j, k);
        this.field_3321.render(k);
        this.field_3323.render(k);
    }

    @Override
    public void setAngles(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
        if (batEntity.isRoosting()) {
            this.field_3321.pitch = j * ((float)Math.PI / 180);
            this.field_3321.yaw = (float)Math.PI - i * ((float)Math.PI / 180);
            this.field_3321.roll = (float)Math.PI;
            this.field_3321.setPivot(0.0f, -2.0f, 0.0f);
            this.field_3322.setPivot(-3.0f, 0.0f, 3.0f);
            this.field_3320.setPivot(3.0f, 0.0f, 3.0f);
            this.field_3323.pitch = (float)Math.PI;
            this.field_3322.pitch = -0.15707964f;
            this.field_3322.yaw = -1.2566371f;
            this.field_3319.yaw = -1.7278761f;
            this.field_3320.pitch = this.field_3322.pitch;
            this.field_3320.yaw = -this.field_3322.yaw;
            this.field_3324.yaw = -this.field_3319.yaw;
        } else {
            this.field_3321.pitch = j * ((float)Math.PI / 180);
            this.field_3321.yaw = i * ((float)Math.PI / 180);
            this.field_3321.roll = 0.0f;
            this.field_3321.setPivot(0.0f, 0.0f, 0.0f);
            this.field_3322.setPivot(0.0f, 0.0f, 0.0f);
            this.field_3320.setPivot(0.0f, 0.0f, 0.0f);
            this.field_3323.pitch = 0.7853982f + MathHelper.cos(h * 0.1f) * 0.15f;
            this.field_3323.yaw = 0.0f;
            this.field_3322.yaw = MathHelper.cos(h * 1.3f) * (float)Math.PI * 0.25f;
            this.field_3320.yaw = -this.field_3322.yaw;
            this.field_3319.yaw = this.field_3322.yaw * 0.5f;
            this.field_3324.yaw = -this.field_3322.yaw * 0.5f;
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles((BatEntity)entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.render((BatEntity)entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }
}

