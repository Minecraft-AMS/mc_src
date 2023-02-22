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
public class BlazeEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart[] field_3328 = new ModelPart[12];
    private final ModelPart field_3329;

    public BlazeEntityModel() {
        for (int i = 0; i < this.field_3328.length; ++i) {
            this.field_3328[i] = new ModelPart(this, 0, 16);
            this.field_3328[i].addCuboid(0.0f, 0.0f, 0.0f, 2, 8, 2);
        }
        this.field_3329 = new ModelPart(this, 0, 0);
        this.field_3329.addCuboid(-4.0f, -4.0f, -4.0f, 8, 8, 8);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3329.render(scale);
        for (ModelPart modelPart : this.field_3328) {
            modelPart.render(scale);
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        int i;
        float f = age * (float)Math.PI * -0.1f;
        for (i = 0; i < 4; ++i) {
            this.field_3328[i].pivotY = -2.0f + MathHelper.cos(((float)(i * 2) + age) * 0.25f);
            this.field_3328[i].pivotX = MathHelper.cos(f) * 9.0f;
            this.field_3328[i].pivotZ = MathHelper.sin(f) * 9.0f;
            f += 1.5707964f;
        }
        f = 0.7853982f + age * (float)Math.PI * 0.03f;
        for (i = 4; i < 8; ++i) {
            this.field_3328[i].pivotY = 2.0f + MathHelper.cos(((float)(i * 2) + age) * 0.25f);
            this.field_3328[i].pivotX = MathHelper.cos(f) * 7.0f;
            this.field_3328[i].pivotZ = MathHelper.sin(f) * 7.0f;
            f += 1.5707964f;
        }
        f = 0.47123894f + age * (float)Math.PI * -0.05f;
        for (i = 8; i < 12; ++i) {
            this.field_3328[i].pivotY = 11.0f + MathHelper.cos(((float)i * 1.5f + age) * 0.5f);
            this.field_3328[i].pivotX = MathHelper.cos(f) * 5.0f;
            this.field_3328[i].pivotZ = MathHelper.sin(f) * 5.0f;
            f += 1.5707964f;
        }
        this.field_3329.yaw = headYaw * ((float)Math.PI / 180);
        this.field_3329.pitch = headPitch * ((float)Math.PI / 180);
    }
}

