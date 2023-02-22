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
public class CreeperEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart field_3362;
    private final ModelPart field_3361;
    private final ModelPart field_3359;
    private final ModelPart field_3358;
    private final ModelPart field_3363;
    private final ModelPart field_3357;

    public CreeperEntityModel() {
        this(0.0f);
    }

    public CreeperEntityModel(float f) {
        int i = 6;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -8.0f, -4.0f, 8, 8, 8, f);
        this.head.setPivot(0.0f, 6.0f, 0.0f);
        this.field_3362 = new ModelPart(this, 32, 0);
        this.field_3362.addCuboid(-4.0f, -8.0f, -4.0f, 8, 8, 8, f + 0.5f);
        this.field_3362.setPivot(0.0f, 6.0f, 0.0f);
        this.field_3361 = new ModelPart(this, 16, 16);
        this.field_3361.addCuboid(-4.0f, 0.0f, -2.0f, 8, 12, 4, f);
        this.field_3361.setPivot(0.0f, 6.0f, 0.0f);
        this.field_3359 = new ModelPart(this, 0, 16);
        this.field_3359.addCuboid(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3359.setPivot(-2.0f, 18.0f, 4.0f);
        this.field_3358 = new ModelPart(this, 0, 16);
        this.field_3358.addCuboid(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3358.setPivot(2.0f, 18.0f, 4.0f);
        this.field_3363 = new ModelPart(this, 0, 16);
        this.field_3363.addCuboid(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3363.setPivot(-2.0f, 18.0f, -4.0f);
        this.field_3357 = new ModelPart(this, 0, 16);
        this.field_3357.addCuboid(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3357.setPivot(2.0f, 18.0f, -4.0f);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.render(scale);
        this.field_3361.render(scale);
        this.field_3359.render(scale);
        this.field_3358.render(scale);
        this.field_3363.render(scale);
        this.field_3357.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.field_3359.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.field_3358.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_3363.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_3357.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}

