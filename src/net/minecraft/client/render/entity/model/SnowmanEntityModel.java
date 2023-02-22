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
public class SnowmanEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart middleSnowball;
    private final ModelPart bottomSnowball;
    private final ModelPart topSnowball;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public SnowmanEntityModel() {
        float f = 4.0f;
        float g = 0.0f;
        this.topSnowball = new ModelPart(this, 0, 0).setTextureSize(64, 64);
        this.topSnowball.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, -0.5f);
        this.topSnowball.setPivot(0.0f, 4.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 0).setTextureSize(64, 64);
        this.leftArm.addCuboid(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, -0.5f);
        this.leftArm.setPivot(0.0f, 6.0f, 0.0f);
        this.rightArm = new ModelPart(this, 32, 0).setTextureSize(64, 64);
        this.rightArm.addCuboid(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, -0.5f);
        this.rightArm.setPivot(0.0f, 6.0f, 0.0f);
        this.middleSnowball = new ModelPart(this, 0, 16).setTextureSize(64, 64);
        this.middleSnowball.addCuboid(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, -0.5f);
        this.middleSnowball.setPivot(0.0f, 13.0f, 0.0f);
        this.bottomSnowball = new ModelPart(this, 0, 36).setTextureSize(64, 64);
        this.bottomSnowball.addCuboid(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, -0.5f);
        this.bottomSnowball.setPivot(0.0f, 24.0f, 0.0f);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        this.topSnowball.yaw = headYaw * ((float)Math.PI / 180);
        this.topSnowball.pitch = headPitch * ((float)Math.PI / 180);
        this.middleSnowball.yaw = headYaw * ((float)Math.PI / 180) * 0.25f;
        float f = MathHelper.sin(this.middleSnowball.yaw);
        float g = MathHelper.cos(this.middleSnowball.yaw);
        this.leftArm.roll = 1.0f;
        this.rightArm.roll = -1.0f;
        this.leftArm.yaw = 0.0f + this.middleSnowball.yaw;
        this.rightArm.yaw = (float)Math.PI + this.middleSnowball.yaw;
        this.leftArm.pivotX = g * 5.0f;
        this.leftArm.pivotZ = -f * 5.0f;
        this.rightArm.pivotX = -g * 5.0f;
        this.rightArm.pivotZ = f * 5.0f;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.middleSnowball, (Object)this.bottomSnowball, (Object)this.topSnowball, (Object)this.leftArm, (Object)this.rightArm);
    }

    public ModelPart method_2834() {
        return this.topSnowball;
    }
}

