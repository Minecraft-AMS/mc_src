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
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity>
extends EntityModel<T> {
    private final ModelPart[] field_3613;
    private final ModelPart[] field_3612;

    public WitherEntityModel(float f) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3613 = new ModelPart[3];
        this.field_3613[0] = new ModelPart(this, 0, 16);
        this.field_3613[0].addCuboid(-10.0f, 3.9f, -0.5f, 20, 3, 3, f);
        this.field_3613[1] = new ModelPart(this).setTextureSize(this.textureWidth, this.textureHeight);
        this.field_3613[1].setPivot(-2.0f, 6.9f, -0.5f);
        this.field_3613[1].setTextureOffset(0, 22).addCuboid(0.0f, 0.0f, 0.0f, 3, 10, 3, f);
        this.field_3613[1].setTextureOffset(24, 22).addCuboid(-4.0f, 1.5f, 0.5f, 11, 2, 2, f);
        this.field_3613[1].setTextureOffset(24, 22).addCuboid(-4.0f, 4.0f, 0.5f, 11, 2, 2, f);
        this.field_3613[1].setTextureOffset(24, 22).addCuboid(-4.0f, 6.5f, 0.5f, 11, 2, 2, f);
        this.field_3613[2] = new ModelPart(this, 12, 22);
        this.field_3613[2].addCuboid(0.0f, 0.0f, 0.0f, 3, 6, 3, f);
        this.field_3612 = new ModelPart[3];
        this.field_3612[0] = new ModelPart(this, 0, 0);
        this.field_3612[0].addCuboid(-4.0f, -4.0f, -4.0f, 8, 8, 8, f);
        this.field_3612[1] = new ModelPart(this, 32, 0);
        this.field_3612[1].addCuboid(-4.0f, -4.0f, -4.0f, 6, 6, 6, f);
        this.field_3612[1].pivotX = -8.0f;
        this.field_3612[1].pivotY = 4.0f;
        this.field_3612[2] = new ModelPart(this, 32, 0);
        this.field_3612[2].addCuboid(-4.0f, -4.0f, -4.0f, 6, 6, 6, f);
        this.field_3612[2].pivotX = 10.0f;
        this.field_3612[2].pivotY = 4.0f;
    }

    @Override
    public void render(T witherEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(witherEntity, f, g, h, i, j, k);
        for (ModelPart modelPart : this.field_3612) {
            modelPart.render(k);
        }
        for (ModelPart modelPart : this.field_3613) {
            modelPart.render(k);
        }
    }

    @Override
    public void setAngles(T witherEntity, float f, float g, float h, float i, float j, float k) {
        float l = MathHelper.cos(h * 0.1f);
        this.field_3613[1].pitch = (0.065f + 0.05f * l) * (float)Math.PI;
        this.field_3613[2].setPivot(-2.0f, 6.9f + MathHelper.cos(this.field_3613[1].pitch) * 10.0f, -0.5f + MathHelper.sin(this.field_3613[1].pitch) * 10.0f);
        this.field_3613[2].pitch = (0.265f + 0.1f * l) * (float)Math.PI;
        this.field_3612[0].yaw = i * ((float)Math.PI / 180);
        this.field_3612[0].pitch = j * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T witherEntity, float f, float g, float h) {
        for (int i = 1; i < 3; ++i) {
            this.field_3612[i].yaw = (((WitherEntity)witherEntity).getHeadYaw(i - 1) - ((WitherEntity)witherEntity).field_6283) * ((float)Math.PI / 180);
            this.field_3612[i].pitch = ((WitherEntity)witherEntity).getHeadPitch(i - 1) * ((float)Math.PI / 180);
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles((T)((WitherEntity)entity), limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.render((T)((WitherEntity)entity), limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }
}

