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
public class TropicalFishEntityModelA<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3589;
    private final ModelPart field_3591;
    private final ModelPart field_3590;
    private final ModelPart field_3588;
    private final ModelPart field_3587;

    public TropicalFishEntityModelA() {
        this(0.0f);
    }

    public TropicalFishEntityModelA(float f) {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.field_3589 = new ModelPart(this, 0, 0);
        this.field_3589.addCuboid(-1.0f, -1.5f, -3.0f, 2, 3, 6, f);
        this.field_3589.setPivot(0.0f, 22.0f, 0.0f);
        this.field_3591 = new ModelPart(this, 22, -6);
        this.field_3591.addCuboid(0.0f, -1.5f, 0.0f, 0, 3, 6, f);
        this.field_3591.setPivot(0.0f, 22.0f, 3.0f);
        this.field_3590 = new ModelPart(this, 2, 16);
        this.field_3590.addCuboid(-2.0f, -1.0f, 0.0f, 2, 2, 0, f);
        this.field_3590.setPivot(-1.0f, 22.5f, 0.0f);
        this.field_3590.yaw = 0.7853982f;
        this.field_3588 = new ModelPart(this, 2, 12);
        this.field_3588.addCuboid(0.0f, -1.0f, 0.0f, 2, 2, 0, f);
        this.field_3588.setPivot(1.0f, 22.5f, 0.0f);
        this.field_3588.yaw = -0.7853982f;
        this.field_3587 = new ModelPart(this, 10, -5);
        this.field_3587.addCuboid(0.0f, -3.0f, 0.0f, 0, 3, 6, f);
        this.field_3587.setPivot(0.0f, 20.5f, -3.0f);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3589.render(scale);
        this.field_3591.render(scale);
        this.field_3590.render(scale);
        this.field_3588.render(scale);
        this.field_3587.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        float f = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.5f;
        }
        this.field_3591.yaw = -f * 0.45f * MathHelper.sin(0.6f * age);
    }
}

