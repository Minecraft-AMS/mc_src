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
public class MinecartEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart[] field_3432 = new ModelPart[7];

    public MinecartEntityModel() {
        this.field_3432[0] = new ModelPart(this, 0, 10);
        this.field_3432[1] = new ModelPart(this, 0, 0);
        this.field_3432[2] = new ModelPart(this, 0, 0);
        this.field_3432[3] = new ModelPart(this, 0, 0);
        this.field_3432[4] = new ModelPart(this, 0, 0);
        this.field_3432[5] = new ModelPart(this, 44, 10);
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        this.field_3432[0].addCuboid(-10.0f, -8.0f, -1.0f, 20, 16, 2, 0.0f);
        this.field_3432[0].setPivot(0.0f, 4.0f, 0.0f);
        this.field_3432[5].addCuboid(-9.0f, -7.0f, -1.0f, 18, 14, 1, 0.0f);
        this.field_3432[5].setPivot(0.0f, 4.0f, 0.0f);
        this.field_3432[1].addCuboid(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.field_3432[1].setPivot(-9.0f, 4.0f, 0.0f);
        this.field_3432[2].addCuboid(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.field_3432[2].setPivot(9.0f, 4.0f, 0.0f);
        this.field_3432[3].addCuboid(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.field_3432[3].setPivot(0.0f, 4.0f, -7.0f);
        this.field_3432[4].addCuboid(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.field_3432[4].setPivot(0.0f, 4.0f, 7.0f);
        this.field_3432[0].pitch = 1.5707964f;
        this.field_3432[1].yaw = 4.712389f;
        this.field_3432[2].yaw = 1.5707964f;
        this.field_3432[3].yaw = (float)Math.PI;
        this.field_3432[5].pitch = -1.5707964f;
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.field_3432[5].pivotY = 4.0f - age;
        for (int i = 0; i < 6; ++i) {
            this.field_3432[i].render(scale);
        }
    }
}

