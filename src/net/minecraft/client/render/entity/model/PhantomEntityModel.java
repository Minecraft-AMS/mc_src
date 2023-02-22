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
public class PhantomEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart field_3477;
    private final ModelPart field_3476;
    private final ModelPart field_3474;
    private final ModelPart field_3472;
    private final ModelPart field_3478;
    private final ModelPart field_3471;
    private final ModelPart field_3473;

    public PhantomEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelPart(this, 0, 8);
        this.body.addCuboid(-3.0f, -2.0f, -8.0f, 5, 3, 9);
        this.field_3471 = new ModelPart(this, 3, 20);
        this.field_3471.addCuboid(-2.0f, 0.0f, 0.0f, 3, 2, 6);
        this.field_3471.setPivot(0.0f, -2.0f, 1.0f);
        this.body.addChild(this.field_3471);
        this.field_3473 = new ModelPart(this, 4, 29);
        this.field_3473.addCuboid(-1.0f, 0.0f, 0.0f, 1, 1, 6);
        this.field_3473.setPivot(0.0f, 0.5f, 6.0f);
        this.field_3471.addChild(this.field_3473);
        this.field_3477 = new ModelPart(this, 23, 12);
        this.field_3477.addCuboid(0.0f, 0.0f, 0.0f, 6, 2, 9);
        this.field_3477.setPivot(2.0f, -2.0f, -8.0f);
        this.field_3476 = new ModelPart(this, 16, 24);
        this.field_3476.addCuboid(0.0f, 0.0f, 0.0f, 13, 1, 9);
        this.field_3476.setPivot(6.0f, 0.0f, 0.0f);
        this.field_3477.addChild(this.field_3476);
        this.field_3474 = new ModelPart(this, 23, 12);
        this.field_3474.mirror = true;
        this.field_3474.addCuboid(-6.0f, 0.0f, 0.0f, 6, 2, 9);
        this.field_3474.setPivot(-3.0f, -2.0f, -8.0f);
        this.field_3472 = new ModelPart(this, 16, 24);
        this.field_3472.mirror = true;
        this.field_3472.addCuboid(-13.0f, 0.0f, 0.0f, 13, 1, 9);
        this.field_3472.setPivot(-6.0f, 0.0f, 0.0f);
        this.field_3474.addChild(this.field_3472);
        this.field_3477.roll = 0.1f;
        this.field_3476.roll = 0.1f;
        this.field_3474.roll = -0.1f;
        this.field_3472.roll = -0.1f;
        this.body.pitch = -0.1f;
        this.field_3478 = new ModelPart(this, 0, 0);
        this.field_3478.addCuboid(-4.0f, -2.0f, -5.0f, 7, 3, 5);
        this.field_3478.setPivot(0.0f, 1.0f, -7.0f);
        this.field_3478.pitch = 0.2f;
        this.body.addChild(this.field_3478);
        this.body.addChild(this.field_3477);
        this.body.addChild(this.field_3474);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.body.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        float f = ((float)(((Entity)entity).getEntityId() * 3) + age) * 0.13f;
        float g = 16.0f;
        this.field_3477.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.field_3476.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.field_3474.roll = -this.field_3477.roll;
        this.field_3472.roll = -this.field_3476.roll;
        this.field_3471.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.field_3473.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

