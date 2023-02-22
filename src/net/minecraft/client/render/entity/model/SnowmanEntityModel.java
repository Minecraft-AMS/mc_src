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
public class SnowmanEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3567;
    private final ModelPart field_3569;
    private final ModelPart field_3568;
    private final ModelPart field_3566;
    private final ModelPart field_3565;

    public SnowmanEntityModel() {
        float f = 4.0f;
        float g = 0.0f;
        this.field_3568 = new ModelPart(this, 0, 0).setTextureSize(64, 64);
        this.field_3568.addCuboid(-4.0f, -8.0f, -4.0f, 8, 8, 8, -0.5f);
        this.field_3568.setPivot(0.0f, 4.0f, 0.0f);
        this.field_3566 = new ModelPart(this, 32, 0).setTextureSize(64, 64);
        this.field_3566.addCuboid(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.field_3566.setPivot(0.0f, 6.0f, 0.0f);
        this.field_3565 = new ModelPart(this, 32, 0).setTextureSize(64, 64);
        this.field_3565.addCuboid(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.field_3565.setPivot(0.0f, 6.0f, 0.0f);
        this.field_3567 = new ModelPart(this, 0, 16).setTextureSize(64, 64);
        this.field_3567.addCuboid(-5.0f, -10.0f, -5.0f, 10, 10, 10, -0.5f);
        this.field_3567.setPivot(0.0f, 13.0f, 0.0f);
        this.field_3569 = new ModelPart(this, 0, 36).setTextureSize(64, 64);
        this.field_3569.addCuboid(-6.0f, -12.0f, -6.0f, 12, 12, 12, -0.5f);
        this.field_3569.setPivot(0.0f, 24.0f, 0.0f);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3568.yaw = headYaw * ((float)Math.PI / 180);
        this.field_3568.pitch = headPitch * ((float)Math.PI / 180);
        this.field_3567.yaw = headYaw * ((float)Math.PI / 180) * 0.25f;
        float f = MathHelper.sin(this.field_3567.yaw);
        float g = MathHelper.cos(this.field_3567.yaw);
        this.field_3566.roll = 1.0f;
        this.field_3565.roll = -1.0f;
        this.field_3566.yaw = 0.0f + this.field_3567.yaw;
        this.field_3565.yaw = (float)Math.PI + this.field_3567.yaw;
        this.field_3566.pivotX = g * 5.0f;
        this.field_3566.pivotZ = -f * 5.0f;
        this.field_3565.pivotX = -g * 5.0f;
        this.field_3565.pivotZ = f * 5.0f;
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3567.render(scale);
        this.field_3569.render(scale);
        this.field_3568.render(scale);
        this.field_3566.render(scale);
        this.field_3565.render(scale);
    }

    public ModelPart method_2834() {
        return this.field_3568;
    }
}

