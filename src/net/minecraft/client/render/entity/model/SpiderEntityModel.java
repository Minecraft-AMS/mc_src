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
public class SpiderEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3583;
    private final ModelPart field_3585;
    private final ModelPart field_3584;
    private final ModelPart field_3580;
    private final ModelPart field_3578;
    private final ModelPart field_3586;
    private final ModelPart field_3577;
    private final ModelPart field_3579;
    private final ModelPart field_3581;
    private final ModelPart field_3576;
    private final ModelPart field_3582;

    public SpiderEntityModel() {
        float f = 0.0f;
        int i = 15;
        this.field_3583 = new ModelPart(this, 32, 4);
        this.field_3583.addCuboid(-4.0f, -4.0f, -8.0f, 8, 8, 8, 0.0f);
        this.field_3583.setPivot(0.0f, 15.0f, -3.0f);
        this.field_3585 = new ModelPart(this, 0, 0);
        this.field_3585.addCuboid(-3.0f, -3.0f, -3.0f, 6, 6, 6, 0.0f);
        this.field_3585.setPivot(0.0f, 15.0f, 0.0f);
        this.field_3584 = new ModelPart(this, 0, 12);
        this.field_3584.addCuboid(-5.0f, -4.0f, -6.0f, 10, 8, 12, 0.0f);
        this.field_3584.setPivot(0.0f, 15.0f, 9.0f);
        this.field_3580 = new ModelPart(this, 18, 0);
        this.field_3580.addCuboid(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3580.setPivot(-4.0f, 15.0f, 2.0f);
        this.field_3578 = new ModelPart(this, 18, 0);
        this.field_3578.addCuboid(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3578.setPivot(4.0f, 15.0f, 2.0f);
        this.field_3586 = new ModelPart(this, 18, 0);
        this.field_3586.addCuboid(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3586.setPivot(-4.0f, 15.0f, 1.0f);
        this.field_3577 = new ModelPart(this, 18, 0);
        this.field_3577.addCuboid(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3577.setPivot(4.0f, 15.0f, 1.0f);
        this.field_3579 = new ModelPart(this, 18, 0);
        this.field_3579.addCuboid(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3579.setPivot(-4.0f, 15.0f, 0.0f);
        this.field_3581 = new ModelPart(this, 18, 0);
        this.field_3581.addCuboid(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3581.setPivot(4.0f, 15.0f, 0.0f);
        this.field_3576 = new ModelPart(this, 18, 0);
        this.field_3576.addCuboid(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3576.setPivot(-4.0f, 15.0f, -1.0f);
        this.field_3582 = new ModelPart(this, 18, 0);
        this.field_3582.addCuboid(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3582.setPivot(4.0f, 15.0f, -1.0f);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.field_3583.render(scale);
        this.field_3585.render(scale);
        this.field_3584.render(scale);
        this.field_3580.render(scale);
        this.field_3578.render(scale);
        this.field_3586.render(scale);
        this.field_3577.render(scale);
        this.field_3579.render(scale);
        this.field_3581.render(scale);
        this.field_3576.render(scale);
        this.field_3582.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.field_3583.yaw = headYaw * ((float)Math.PI / 180);
        this.field_3583.pitch = headPitch * ((float)Math.PI / 180);
        float f = 0.7853982f;
        this.field_3580.roll = -0.7853982f;
        this.field_3578.roll = 0.7853982f;
        this.field_3586.roll = -0.58119464f;
        this.field_3577.roll = 0.58119464f;
        this.field_3579.roll = -0.58119464f;
        this.field_3581.roll = 0.58119464f;
        this.field_3576.roll = -0.7853982f;
        this.field_3582.roll = 0.7853982f;
        float g = -0.0f;
        float h = 0.3926991f;
        this.field_3580.yaw = 0.7853982f;
        this.field_3578.yaw = -0.7853982f;
        this.field_3586.yaw = 0.3926991f;
        this.field_3577.yaw = -0.3926991f;
        this.field_3579.yaw = -0.3926991f;
        this.field_3581.yaw = 0.3926991f;
        this.field_3576.yaw = -0.7853982f;
        this.field_3582.yaw = 0.7853982f;
        float i = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 0.0f) * 0.4f) * limbDistance;
        float j = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * limbDistance;
        float k = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * limbDistance;
        float l = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 4.712389f) * 0.4f) * limbDistance;
        float m = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 0.0f) * 0.4f) * limbDistance;
        float n = Math.abs(MathHelper.sin(limbAngle * 0.6662f + (float)Math.PI) * 0.4f) * limbDistance;
        float o = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 1.5707964f) * 0.4f) * limbDistance;
        float p = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 4.712389f) * 0.4f) * limbDistance;
        this.field_3580.yaw += i;
        this.field_3578.yaw += -i;
        this.field_3586.yaw += j;
        this.field_3577.yaw += -j;
        this.field_3579.yaw += k;
        this.field_3581.yaw += -k;
        this.field_3576.yaw += l;
        this.field_3582.yaw += -l;
        this.field_3580.roll += m;
        this.field_3578.roll += -m;
        this.field_3586.roll += n;
        this.field_3577.roll += -n;
        this.field_3579.roll += o;
        this.field_3581.roll += -o;
        this.field_3576.roll += p;
        this.field_3582.roll += -p;
    }
}

