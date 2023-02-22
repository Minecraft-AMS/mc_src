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
public class SilverfishEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart[] field_3560;
    private final ModelPart[] field_3557;
    private final float[] field_3561 = new float[7];
    private static final int[][] field_3558 = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] field_3559 = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public SilverfishEntityModel() {
        this.field_3560 = new ModelPart[7];
        float f = -3.5f;
        for (int i = 0; i < this.field_3560.length; ++i) {
            this.field_3560[i] = new ModelPart(this, field_3559[i][0], field_3559[i][1]);
            this.field_3560[i].addCuboid((float)field_3558[i][0] * -0.5f, 0.0f, (float)field_3558[i][2] * -0.5f, field_3558[i][0], field_3558[i][1], field_3558[i][2]);
            this.field_3560[i].setPivot(0.0f, 24 - field_3558[i][1], f);
            this.field_3561[i] = f;
            if (i >= this.field_3560.length - 1) continue;
            f += (float)(field_3558[i][2] + field_3558[i + 1][2]) * 0.5f;
        }
        this.field_3557 = new ModelPart[3];
        this.field_3557[0] = new ModelPart(this, 20, 0);
        this.field_3557[0].addCuboid(-5.0f, 0.0f, (float)field_3558[2][2] * -0.5f, 10, 8, field_3558[2][2]);
        this.field_3557[0].setPivot(0.0f, 16.0f, this.field_3561[2]);
        this.field_3557[1] = new ModelPart(this, 20, 11);
        this.field_3557[1].addCuboid(-3.0f, 0.0f, (float)field_3558[4][2] * -0.5f, 6, 4, field_3558[4][2]);
        this.field_3557[1].setPivot(0.0f, 20.0f, this.field_3561[4]);
        this.field_3557[2] = new ModelPart(this, 20, 18);
        this.field_3557[2].addCuboid(-3.0f, 0.0f, (float)field_3558[4][2] * -0.5f, 6, 5, field_3558[1][2]);
        this.field_3557[2].setPivot(0.0f, 19.0f, this.field_3561[1]);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        for (ModelPart modelPart : this.field_3560) {
            modelPart.render(scale);
        }
        for (ModelPart modelPart : this.field_3557) {
            modelPart.render(scale);
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        for (int i = 0; i < this.field_3560.length; ++i) {
            this.field_3560[i].yaw = MathHelper.cos(age * 0.9f + (float)i * 0.15f * (float)Math.PI) * (float)Math.PI * 0.05f * (float)(1 + Math.abs(i - 2));
            this.field_3560[i].pivotX = MathHelper.sin(age * 0.9f + (float)i * 0.15f * (float)Math.PI) * (float)Math.PI * 0.2f * (float)Math.abs(i - 2);
        }
        this.field_3557[0].yaw = this.field_3560[2].yaw;
        this.field_3557[1].yaw = this.field_3560[4].yaw;
        this.field_3557[1].pivotX = this.field_3560[4].pivotX;
        this.field_3557[2].yaw = this.field_3560[1].yaw;
        this.field_3557[2].pivotX = this.field_3560[1].pivotX;
    }
}

