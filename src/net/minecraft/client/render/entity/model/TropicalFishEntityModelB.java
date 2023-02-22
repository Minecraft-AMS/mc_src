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
public class TropicalFishEntityModelB<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3597;
    private final ModelPart field_3599;
    private final ModelPart field_3598;
    private final ModelPart field_3596;
    private final ModelPart field_3595;
    private final ModelPart field_3600;

    public TropicalFishEntityModelB() {
        this(0.0f);
    }

    public TropicalFishEntityModelB(float f) {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 19;
        this.field_3597 = new ModelPart(this, 0, 20);
        this.field_3597.addCuboid(-1.0f, -3.0f, -3.0f, 2, 6, 6, f);
        this.field_3597.setPivot(0.0f, 19.0f, 0.0f);
        this.field_3599 = new ModelPart(this, 21, 16);
        this.field_3599.addCuboid(0.0f, -3.0f, 0.0f, 0, 6, 5, f);
        this.field_3599.setPivot(0.0f, 19.0f, 3.0f);
        this.field_3598 = new ModelPart(this, 2, 16);
        this.field_3598.addCuboid(-2.0f, 0.0f, 0.0f, 2, 2, 0, f);
        this.field_3598.setPivot(-1.0f, 20.0f, 0.0f);
        this.field_3598.yaw = 0.7853982f;
        this.field_3596 = new ModelPart(this, 2, 12);
        this.field_3596.addCuboid(0.0f, 0.0f, 0.0f, 2, 2, 0, f);
        this.field_3596.setPivot(1.0f, 20.0f, 0.0f);
        this.field_3596.yaw = -0.7853982f;
        this.field_3595 = new ModelPart(this, 20, 11);
        this.field_3595.addCuboid(0.0f, -4.0f, 0.0f, 0, 4, 6, f);
        this.field_3595.setPivot(0.0f, 16.0f, -3.0f);
        this.field_3600 = new ModelPart(this, 20, 21);
        this.field_3600.addCuboid(0.0f, 0.0f, 0.0f, 0, 4, 6, f);
        this.field_3600.setPivot(0.0f, 22.0f, -3.0f);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3597.render(scale);
        this.field_3599.render(scale);
        this.field_3598.render(scale);
        this.field_3596.render(scale);
        this.field_3595.render(scale);
        this.field_3600.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        float f = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.5f;
        }
        this.field_3599.yaw = -f * 0.45f * MathHelper.sin(0.6f * age);
    }
}

