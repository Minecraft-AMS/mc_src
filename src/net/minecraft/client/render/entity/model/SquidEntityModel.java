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

@Environment(value=EnvType.CLIENT)
public class SquidEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3575;
    private final ModelPart[] field_3574 = new ModelPart[8];

    public SquidEntityModel() {
        int i = -16;
        this.field_3575 = new ModelPart(this, 0, 0);
        this.field_3575.addCuboid(-6.0f, -8.0f, -6.0f, 12, 16, 12);
        this.field_3575.pivotY += 8.0f;
        for (int j = 0; j < this.field_3574.length; ++j) {
            this.field_3574[j] = new ModelPart(this, 48, 0);
            double d = (double)j * Math.PI * 2.0 / (double)this.field_3574.length;
            float f = (float)Math.cos(d) * 5.0f;
            float g = (float)Math.sin(d) * 5.0f;
            this.field_3574[j].addCuboid(-1.0f, 0.0f, -1.0f, 2, 18, 2);
            this.field_3574[j].pivotX = f;
            this.field_3574[j].pivotZ = g;
            this.field_3574[j].pivotY = 15.0f;
            d = (double)j * Math.PI * -2.0 / (double)this.field_3574.length + 1.5707963267948966;
            this.field_3574[j].yaw = (float)d;
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        for (ModelPart modelPart : this.field_3574) {
            modelPart.pitch = age;
        }
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3575.render(scale);
        for (ModelPart modelPart : this.field_3574) {
            modelPart.render(scale);
        }
    }
}

