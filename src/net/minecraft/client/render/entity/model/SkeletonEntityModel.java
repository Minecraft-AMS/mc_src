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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityModel<T extends MobEntity>
extends BipedEntityModel<T> {
    public SkeletonEntityModel() {
        this(0.0f, false);
    }

    public SkeletonEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, 32);
        if (!bl) {
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addCuboid(-1.0f, -2.0f, -1.0f, 2, 12, 2, f);
            this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 40, 16);
            this.leftArm.mirror = true;
            this.leftArm.addCuboid(-1.0f, -2.0f, -1.0f, 2, 12, 2, f);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.rightLeg = new ModelPart(this, 0, 16);
            this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 12, 2, f);
            this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 12, 2, f);
            this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
        }
    }

    @Override
    public void animateModel(T mobEntity, float f, float g, float h) {
        this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack = ((LivingEntity)mobEntity).getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() == Items.BOW && ((MobEntity)mobEntity).isAttacking()) {
            if (((MobEntity)mobEntity).getMainArm() == Arm.RIGHT) {
                this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
        }
        super.animateModel(mobEntity, f, g, h);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(mobEntity, f, g, h, i, j, k);
        ItemStack itemStack = ((LivingEntity)mobEntity).getMainHandStack();
        if (((MobEntity)mobEntity).isAttacking() && (itemStack.isEmpty() || itemStack.getItem() != Items.BOW)) {
            float l = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
            float m = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * (float)Math.PI);
            this.rightArm.roll = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - l * 0.6f);
            this.leftArm.yaw = 0.1f - l * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.leftArm.pitch = -1.5707964f;
            this.rightArm.pitch -= l * 1.2f - m * 0.4f;
            this.leftArm.pitch -= l * 1.2f - m * 0.4f;
            this.rightArm.roll += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
            this.leftArm.roll -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
            this.rightArm.pitch += MathHelper.sin(h * 0.067f) * 0.05f;
            this.leftArm.pitch -= MathHelper.sin(h * 0.067f) * 0.05f;
        }
    }

    @Override
    public void setArmAngle(float f, Arm arm) {
        float g = arm == Arm.RIGHT ? 1.0f : -1.0f;
        ModelPart modelPart = this.getArm(arm);
        modelPart.pivotX += g;
        modelPart.applyTransform(f);
        modelPart.pivotX -= g;
    }
}

