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
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity>
extends EntityModel<T>
implements ModelWithHead,
ModelWithHat {
    protected final ModelPart head;
    protected ModelPart headOverlay;
    protected final ModelPart hat;
    protected final ModelPart torso;
    protected final ModelPart robe;
    protected final ModelPart arms;
    protected final ModelPart rightLeg;
    protected final ModelPart leftLeg;
    protected final ModelPart nose;

    public VillagerResemblingModel(float scale) {
        this(scale, 64, 64);
    }

    public VillagerResemblingModel(float scale, int textureWidth, int textureHeight) {
        float f = 0.5f;
        this.head = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8, 10, 8, scale);
        this.headOverlay = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.headOverlay.setPivot(0.0f, 0.0f, 0.0f);
        this.headOverlay.setTextureOffset(32, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8, 10, 8, scale + 0.5f);
        this.head.addChild(this.headOverlay);
        this.hat = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.hat.setPivot(0.0f, 0.0f, 0.0f);
        this.hat.setTextureOffset(30, 47).addCuboid(-8.0f, -8.0f, -6.0f, 16, 16, 1, scale);
        this.hat.pitch = -1.5707964f;
        this.headOverlay.addChild(this.hat);
        this.nose = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.nose.setPivot(0.0f, -2.0f, 0.0f);
        this.nose.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2, 4, 2, scale);
        this.head.addChild(this.nose);
        this.torso = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.torso.setPivot(0.0f, 0.0f, 0.0f);
        this.torso.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8, 12, 6, scale);
        this.robe = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.robe.setPivot(0.0f, 0.0f, 0.0f);
        this.robe.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8, 18, 6, scale + 0.5f);
        this.torso.addChild(this.robe);
        this.arms = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.arms.setPivot(0.0f, 2.0f, 0.0f);
        this.arms.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4, 8, 4, scale);
        this.arms.setTextureOffset(44, 22).addCuboid(4.0f, -2.0f, -2.0f, 4, 8, 4, scale, true);
        this.arms.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8, 4, 4, scale);
        this.rightLeg = new ModelPart(this, 0, 22).setTextureSize(textureWidth, textureHeight);
        this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
        this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.leftLeg = new ModelPart(this, 0, 22).setTextureSize(textureWidth, textureHeight);
        this.leftLeg.mirror = true;
        this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
    }

    @Override
    public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.render(scale);
        this.torso.render(scale);
        this.rightLeg.render(scale);
        this.leftLeg.render(scale);
        this.arms.render(scale);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
        boolean bl = false;
        if (entity instanceof AbstractTraderEntity) {
            bl = ((AbstractTraderEntity)entity).getHeadRollingTimeLeft() > 0;
        }
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        if (bl) {
            this.head.roll = 0.3f * MathHelper.sin(0.45f * age);
            this.head.pitch = 0.4f;
        } else {
            this.head.roll = 0.0f;
        }
        this.arms.pivotY = 3.0f;
        this.arms.pivotZ = -1.0f;
        this.arms.pitch = -0.75f;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance * 0.5f;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance * 0.5f;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setHatVisible(boolean visible) {
        this.head.visible = visible;
        this.headOverlay.visible = visible;
        this.hat.visible = visible;
    }
}

