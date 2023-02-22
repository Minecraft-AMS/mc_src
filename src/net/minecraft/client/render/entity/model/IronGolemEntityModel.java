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
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityModel<T extends IronGolemEntity>
extends EntityModel<T> {
    private final ModelPart field_3415;
    private final ModelPart field_3413;
    public final ModelPart field_3414;
    private final ModelPart field_3412;
    private final ModelPart field_3411;
    private final ModelPart field_3416;

    public IronGolemEntityModel() {
        this(0.0f);
    }

    public IronGolemEntityModel(float f) {
        this(f, -7.0f);
    }

    public IronGolemEntityModel(float f, float g) {
        int i = 128;
        int j = 128;
        this.field_3415 = new ModelPart(this).setTextureSize(128, 128);
        this.field_3415.setPivot(0.0f, 0.0f + g, -2.0f);
        this.field_3415.setTextureOffset(0, 0).addCuboid(-4.0f, -12.0f, -5.5f, 8, 10, 8, f);
        this.field_3415.setTextureOffset(24, 0).addCuboid(-1.0f, -5.0f, -7.5f, 2, 4, 2, f);
        this.field_3413 = new ModelPart(this).setTextureSize(128, 128);
        this.field_3413.setPivot(0.0f, 0.0f + g, 0.0f);
        this.field_3413.setTextureOffset(0, 40).addCuboid(-9.0f, -2.0f, -6.0f, 18, 12, 11, f);
        this.field_3413.setTextureOffset(0, 70).addCuboid(-4.5f, 10.0f, -3.0f, 9, 5, 6, f + 0.5f);
        this.field_3414 = new ModelPart(this).setTextureSize(128, 128);
        this.field_3414.setPivot(0.0f, -7.0f, 0.0f);
        this.field_3414.setTextureOffset(60, 21).addCuboid(-13.0f, -2.5f, -3.0f, 4, 30, 6, f);
        this.field_3412 = new ModelPart(this).setTextureSize(128, 128);
        this.field_3412.setPivot(0.0f, -7.0f, 0.0f);
        this.field_3412.setTextureOffset(60, 58).addCuboid(9.0f, -2.5f, -3.0f, 4, 30, 6, f);
        this.field_3411 = new ModelPart(this, 0, 22).setTextureSize(128, 128);
        this.field_3411.setPivot(-4.0f, 18.0f + g, 0.0f);
        this.field_3411.setTextureOffset(37, 0).addCuboid(-3.5f, -3.0f, -3.0f, 6, 16, 5, f);
        this.field_3416 = new ModelPart(this, 0, 22).setTextureSize(128, 128);
        this.field_3416.mirror = true;
        this.field_3416.setTextureOffset(60, 0).setPivot(5.0f, 18.0f + g, 0.0f);
        this.field_3416.addCuboid(-3.5f, -3.0f, -3.0f, 6, 16, 5, f);
    }

    @Override
    public void render(T ironGolemEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(ironGolemEntity, f, g, h, i, j, k);
        this.field_3415.render(k);
        this.field_3413.render(k);
        this.field_3411.render(k);
        this.field_3416.render(k);
        this.field_3414.render(k);
        this.field_3412.render(k);
    }

    @Override
    public void setAngles(T ironGolemEntity, float f, float g, float h, float i, float j, float k) {
        this.field_3415.yaw = i * ((float)Math.PI / 180);
        this.field_3415.pitch = j * ((float)Math.PI / 180);
        this.field_3411.pitch = -1.5f * this.method_2810(f, 13.0f) * g;
        this.field_3416.pitch = 1.5f * this.method_2810(f, 13.0f) * g;
        this.field_3411.yaw = 0.0f;
        this.field_3416.yaw = 0.0f;
    }

    @Override
    public void animateModel(T ironGolemEntity, float f, float g, float h) {
        int i = ((IronGolemEntity)ironGolemEntity).method_6501();
        if (i > 0) {
            this.field_3414.pitch = -2.0f + 1.5f * this.method_2810((float)i - h, 10.0f);
            this.field_3412.pitch = -2.0f + 1.5f * this.method_2810((float)i - h, 10.0f);
        } else {
            int j = ((IronGolemEntity)ironGolemEntity).method_6502();
            if (j > 0) {
                this.field_3414.pitch = -0.8f + 0.025f * this.method_2810(j, 70.0f);
                this.field_3412.pitch = 0.0f;
            } else {
                this.field_3414.pitch = (-0.2f + 1.5f * this.method_2810(f, 13.0f)) * g;
                this.field_3412.pitch = (-0.2f - 1.5f * this.method_2810(f, 13.0f)) * g;
            }
        }
    }

    private float method_2810(float f, float g) {
        return (Math.abs(f % g - g * 0.5f) - g * 0.25f) / (g * 0.25f);
    }

    public ModelPart method_2809() {
        return this.field_3414;
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles((T)((IronGolemEntity)entity), limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.render((T)((IronGolemEntity)entity), limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }
}

