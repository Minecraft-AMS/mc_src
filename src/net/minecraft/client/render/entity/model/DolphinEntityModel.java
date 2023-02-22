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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_4656;
    private final ModelPart field_4658;
    private final ModelPart field_4657;
    private final ModelPart field_4655;

    public DolphinEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        float f = 18.0f;
        float g = -8.0f;
        this.field_4658 = new ModelPart(this, 22, 0);
        this.field_4658.addCuboid(-4.0f, -7.0f, 0.0f, 8, 7, 13);
        this.field_4658.setPivot(0.0f, 22.0f, -5.0f);
        ModelPart modelPart = new ModelPart(this, 51, 0);
        modelPart.addCuboid(-0.5f, 0.0f, 8.0f, 1, 4, 5);
        modelPart.pitch = 1.0471976f;
        this.field_4658.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 48, 20);
        modelPart2.mirror = true;
        modelPart2.addCuboid(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        modelPart2.setPivot(2.0f, -2.0f, 4.0f);
        modelPart2.pitch = 1.0471976f;
        modelPart2.roll = 2.0943952f;
        this.field_4658.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(this, 48, 20);
        modelPart3.addCuboid(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        modelPart3.setPivot(-2.0f, -2.0f, 4.0f);
        modelPart3.pitch = 1.0471976f;
        modelPart3.roll = -2.0943952f;
        this.field_4658.addChild(modelPart3);
        this.field_4657 = new ModelPart(this, 0, 19);
        this.field_4657.addCuboid(-2.0f, -2.5f, 0.0f, 4, 5, 11);
        this.field_4657.setPivot(0.0f, -2.5f, 11.0f);
        this.field_4657.pitch = -0.10471976f;
        this.field_4658.addChild(this.field_4657);
        this.field_4655 = new ModelPart(this, 19, 20);
        this.field_4655.addCuboid(-5.0f, -0.5f, 0.0f, 10, 1, 6);
        this.field_4655.setPivot(0.0f, 0.0f, 9.0f);
        this.field_4655.pitch = 0.0f;
        this.field_4657.addChild(this.field_4655);
        this.field_4656 = new ModelPart(this, 0, 0);
        this.field_4656.addCuboid(-4.0f, -3.0f, -3.0f, 8, 7, 6);
        this.field_4656.setPivot(0.0f, -4.0f, -3.0f);
        ModelPart modelPart4 = new ModelPart(this, 0, 13);
        modelPart4.addCuboid(-1.0f, 2.0f, -7.0f, 2, 2, 4);
        this.field_4656.addChild(modelPart4);
        this.field_4658.addChild(this.field_4656);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.field_4658.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.field_4658.pitch = headPitch * ((float)Math.PI / 180);
        this.field_4658.yaw = headYaw * ((float)Math.PI / 180);
        if (Entity.squaredHorizontalLength(((Entity)entity).getVelocity()) > 1.0E-7) {
            this.field_4658.pitch += -0.05f + -0.05f * MathHelper.cos(age * 0.3f);
            this.field_4657.pitch = -0.1f * MathHelper.cos(age * 0.3f);
            this.field_4655.pitch = -0.2f * MathHelper.cos(age * 0.3f);
        }
    }
}

