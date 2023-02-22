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
public class EvokerFangsEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3374 = new ModelPart(this, 0, 0);
    private final ModelPart field_3376;
    private final ModelPart field_3375;

    public EvokerFangsEntityModel() {
        this.field_3374.setPivot(-5.0f, 22.0f, -5.0f);
        this.field_3374.addCuboid(0.0f, 0.0f, 0.0f, 10, 12, 10);
        this.field_3376 = new ModelPart(this, 40, 0);
        this.field_3376.setPivot(1.5f, 22.0f, -4.0f);
        this.field_3376.addCuboid(0.0f, 0.0f, 0.0f, 4, 14, 8);
        this.field_3375 = new ModelPart(this, 40, 0);
        this.field_3375.setPivot(-1.5f, 22.0f, 4.0f);
        this.field_3375.addCuboid(0.0f, 0.0f, 0.0f, 4, 14, 8);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        float f = limbAngle * 2.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        f = 1.0f - f * f * f;
        this.field_3376.roll = (float)Math.PI - f * 0.35f * (float)Math.PI;
        this.field_3375.roll = (float)Math.PI + f * 0.35f * (float)Math.PI;
        this.field_3375.yaw = (float)Math.PI;
        float g = (limbAngle + MathHelper.sin(limbAngle * 2.7f)) * 0.6f * 12.0f;
        this.field_3375.pivotY = this.field_3376.pivotY = 24.0f - g;
        this.field_3374.pivotY = this.field_3376.pivotY;
        this.field_3374.render(scale);
        this.field_3376.render(scale);
        this.field_3375.render(scale);
    }
}

