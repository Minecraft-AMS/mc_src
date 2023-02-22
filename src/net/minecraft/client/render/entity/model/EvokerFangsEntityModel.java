/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3374 = new ModelPart(this, 0, 0);
    private final ModelPart field_3376;
    private final ModelPart field_3375;

    public EvokerFangsEntityModel() {
        this.field_3374.setPivot(-5.0f, 22.0f, -5.0f);
        this.field_3374.addCuboid(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f);
        this.field_3376 = new ModelPart(this, 40, 0);
        this.field_3376.setPivot(1.5f, 22.0f, -4.0f);
        this.field_3376.addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        this.field_3375 = new ModelPart(this, 40, 0);
        this.field_3375.setPivot(-1.5f, 22.0f, 4.0f);
        this.field_3375.addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
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
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.field_3374, (Object)this.field_3376, (Object)this.field_3375);
    }
}

