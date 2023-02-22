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
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity>
extends AnimalModel<T>
implements ModelWithArms,
ModelWithHead {
    public ModelPart head;
    public ModelPart helmet;
    public ModelPart torso;
    public ModelPart rightArm;
    public ModelPart leftArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ArmPose leftArmPose = ArmPose.EMPTY;
    public ArmPose rightArmPose = ArmPose.EMPTY;
    public boolean isSneaking;
    public float field_3396;
    private float itemUsedTime;

    public BipedEntityModel(float scale) {
        this(RenderLayer::getEntityCutoutNoCull, scale, 0.0f, 64, 32);
    }

    protected BipedEntityModel(float f, float g, int i, int j) {
        this(RenderLayer::getEntityCutoutNoCull, f, g, i, j);
    }

    public BipedEntityModel(Function<Identifier, RenderLayer> function, float f, float g, int i, int j) {
        super(function, true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f);
        this.textureWidth = i;
        this.textureHeight = j;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f);
        this.head.setPivot(0.0f, 0.0f + g, 0.0f);
        this.helmet = new ModelPart(this, 32, 0);
        this.helmet.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f + 0.5f);
        this.helmet.setPivot(0.0f, 0.0f + g, 0.0f);
        this.torso = new ModelPart(this, 16, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.torso.setPivot(0.0f, 0.0f + g, 0.0f);
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.rightArm.setPivot(-5.0f, 2.0f + g, 0.0f);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftArm.setPivot(5.0f, 2.0f + g, 0.0f);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.rightLeg.setPivot(-1.9f, 12.0f + g, 0.0f);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftLeg.setPivot(1.9f, 12.0f + g, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of((Object)this.torso, (Object)this.rightArm, (Object)this.leftArm, (Object)this.rightLeg, (Object)this.leftLeg, (Object)this.helmet);
    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        this.field_3396 = ((LivingEntity)livingEntity).getLeaningPitch(h);
        this.itemUsedTime = ((LivingEntity)livingEntity).getItemUseTime();
        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        float n;
        float m;
        float l;
        boolean bl = ((LivingEntity)livingEntity).getRoll() > 4;
        boolean bl2 = ((LivingEntity)livingEntity).isInSwimmingPose();
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = bl ? -0.7853982f : (this.field_3396 > 0.0f ? (bl2 ? this.lerpAngle(this.head.pitch, -0.7853982f, this.field_3396) : this.lerpAngle(this.head.pitch, j * ((float)Math.PI / 180), this.field_3396)) : j * ((float)Math.PI / 180));
        this.torso.yaw = 0.0f;
        this.rightArm.pivotZ = 0.0f;
        this.rightArm.pivotX = -5.0f;
        this.leftArm.pivotZ = 0.0f;
        this.leftArm.pivotX = 5.0f;
        float k = 1.0f;
        if (bl) {
            k = (float)((Entity)livingEntity).getVelocity().lengthSquared();
            k /= 0.2f;
            k *= k * k;
        }
        if (k < 1.0f) {
            k = 1.0f;
        }
        this.rightArm.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f / k;
        this.leftArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f / k;
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g / k;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g / k;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.roll = 0.0f;
        this.leftLeg.roll = 0.0f;
        if (this.riding) {
            this.rightArm.pitch += -0.62831855f;
            this.leftArm.pitch += -0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        this.rightArm.yaw = 0.0f;
        this.rightArm.roll = 0.0f;
        switch (this.leftArmPose) {
            case EMPTY: {
                this.leftArm.yaw = 0.0f;
                break;
            }
            case BLOCK: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.9424779f;
                this.leftArm.yaw = 0.5235988f;
                break;
            }
            case ITEM: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
                this.leftArm.yaw = 0.0f;
            }
        }
        switch (this.rightArmPose) {
            case EMPTY: {
                this.rightArm.yaw = 0.0f;
                break;
            }
            case BLOCK: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.9424779f;
                this.rightArm.yaw = -0.5235988f;
                break;
            }
            case ITEM: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
                this.rightArm.yaw = 0.0f;
            }
        }
        if (this.leftArmPose == ArmPose.THROW_SPEAR && this.rightArmPose != ArmPose.BLOCK && this.rightArmPose != ArmPose.THROW_SPEAR && this.rightArmPose != ArmPose.BOW_AND_ARROW) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
            this.leftArm.yaw = 0.0f;
        }
        if (this.handSwingProgress > 0.0f) {
            Arm arm = this.getPreferredArm(livingEntity);
            ModelPart modelPart = this.getArm(arm);
            l = this.handSwingProgress;
            this.torso.yaw = MathHelper.sin(MathHelper.sqrt(l) * ((float)Math.PI * 2)) * 0.2f;
            if (arm == Arm.LEFT) {
                this.torso.yaw *= -1.0f;
            }
            this.rightArm.pivotZ = MathHelper.sin(this.torso.yaw) * 5.0f;
            this.rightArm.pivotX = -MathHelper.cos(this.torso.yaw) * 5.0f;
            this.leftArm.pivotZ = -MathHelper.sin(this.torso.yaw) * 5.0f;
            this.leftArm.pivotX = MathHelper.cos(this.torso.yaw) * 5.0f;
            this.rightArm.yaw += this.torso.yaw;
            this.leftArm.yaw += this.torso.yaw;
            this.leftArm.pitch += this.torso.yaw;
            l = 1.0f - this.handSwingProgress;
            l *= l;
            l *= l;
            l = 1.0f - l;
            m = MathHelper.sin(l * (float)Math.PI);
            n = MathHelper.sin(this.handSwingProgress * (float)Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
            modelPart.pitch = (float)((double)modelPart.pitch - ((double)m * 1.2 + (double)n));
            modelPart.yaw += this.torso.yaw * 2.0f;
            modelPart.roll += MathHelper.sin(this.handSwingProgress * (float)Math.PI) * -0.4f;
        }
        if (this.isSneaking) {
            this.torso.pitch = 0.5f;
            this.rightArm.pitch += 0.4f;
            this.leftArm.pitch += 0.4f;
            this.rightLeg.pivotZ = 4.0f;
            this.leftLeg.pivotZ = 4.0f;
            this.rightLeg.pivotY = 12.2f;
            this.leftLeg.pivotY = 12.2f;
            this.head.pivotY = 4.2f;
            this.torso.pivotY = 3.2f;
            this.leftArm.pivotY = 5.2f;
            this.rightArm.pivotY = 5.2f;
        } else {
            this.torso.pitch = 0.0f;
            this.rightLeg.pivotZ = 0.1f;
            this.leftLeg.pivotZ = 0.1f;
            this.rightLeg.pivotY = 12.0f;
            this.leftLeg.pivotY = 12.0f;
            this.head.pivotY = 0.0f;
            this.torso.pivotY = 0.0f;
            this.leftArm.pivotY = 2.0f;
            this.rightArm.pivotY = 2.0f;
        }
        this.rightArm.roll += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
        this.leftArm.roll -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
        this.rightArm.pitch += MathHelper.sin(h * 0.067f) * 0.05f;
        this.leftArm.pitch -= MathHelper.sin(h * 0.067f) * 0.05f;
        if (this.rightArmPose == ArmPose.BOW_AND_ARROW) {
            this.rightArm.yaw = -0.1f + this.head.yaw;
            this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
            this.rightArm.pitch = -1.5707964f + this.head.pitch;
            this.leftArm.pitch = -1.5707964f + this.head.pitch;
        } else if (this.leftArmPose == ArmPose.BOW_AND_ARROW && this.rightArmPose != ArmPose.THROW_SPEAR && this.rightArmPose != ArmPose.BLOCK) {
            this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
            this.leftArm.yaw = 0.1f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch;
            this.leftArm.pitch = -1.5707964f + this.head.pitch;
        }
        float o = CrossbowItem.getPullTime(((LivingEntity)livingEntity).getActiveItem());
        if (this.rightArmPose == ArmPose.CROSSBOW_CHARGE) {
            this.rightArm.yaw = -0.8f;
            this.rightArm.pitch = -0.97079635f;
            this.leftArm.pitch = -0.97079635f;
            float p = MathHelper.clamp(this.itemUsedTime, 0.0f, o);
            this.leftArm.yaw = MathHelper.lerp(p / o, 0.4f, 0.85f);
            this.leftArm.pitch = MathHelper.lerp(p / o, this.leftArm.pitch, -1.5707964f);
        } else if (this.leftArmPose == ArmPose.CROSSBOW_CHARGE) {
            this.leftArm.yaw = 0.8f;
            this.rightArm.pitch = -0.97079635f;
            this.leftArm.pitch = -0.97079635f;
            float p = MathHelper.clamp(this.itemUsedTime, 0.0f, o);
            this.rightArm.yaw = MathHelper.lerp(p / o, -0.4f, -0.85f);
            this.rightArm.pitch = MathHelper.lerp(p / o, this.rightArm.pitch, -1.5707964f);
        }
        if (this.rightArmPose == ArmPose.CROSSBOW_HOLD && this.handSwingProgress <= 0.0f) {
            this.rightArm.yaw = -0.3f + this.head.yaw;
            this.leftArm.yaw = 0.6f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch + 0.1f;
            this.leftArm.pitch = -1.5f + this.head.pitch;
        } else if (this.leftArmPose == ArmPose.CROSSBOW_HOLD) {
            this.rightArm.yaw = -0.6f + this.head.yaw;
            this.leftArm.yaw = 0.3f + this.head.yaw;
            this.rightArm.pitch = -1.5f + this.head.pitch;
            this.leftArm.pitch = -1.5707964f + this.head.pitch + 0.1f;
        }
        if (this.field_3396 > 0.0f) {
            float p = f % 26.0f;
            float f2 = l = this.handSwingProgress > 0.0f ? 0.0f : this.field_3396;
            if (p < 14.0f) {
                this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, 0.0f, this.field_3396);
                this.rightArm.pitch = MathHelper.lerp(l, this.rightArm.pitch, 0.0f);
                this.leftArm.yaw = this.lerpAngle(this.leftArm.yaw, (float)Math.PI, this.field_3396);
                this.rightArm.yaw = MathHelper.lerp(l, this.rightArm.yaw, (float)Math.PI);
                this.leftArm.roll = this.lerpAngle(this.leftArm.roll, (float)Math.PI + 1.8707964f * this.method_2807(p) / this.method_2807(14.0f), this.field_3396);
                this.rightArm.roll = MathHelper.lerp(l, this.rightArm.roll, (float)Math.PI - 1.8707964f * this.method_2807(p) / this.method_2807(14.0f));
            } else if (p >= 14.0f && p < 22.0f) {
                m = (p - 14.0f) / 8.0f;
                this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, 1.5707964f * m, this.field_3396);
                this.rightArm.pitch = MathHelper.lerp(l, this.rightArm.pitch, 1.5707964f * m);
                this.leftArm.yaw = this.lerpAngle(this.leftArm.yaw, (float)Math.PI, this.field_3396);
                this.rightArm.yaw = MathHelper.lerp(l, this.rightArm.yaw, (float)Math.PI);
                this.leftArm.roll = this.lerpAngle(this.leftArm.roll, 5.012389f - 1.8707964f * m, this.field_3396);
                this.rightArm.roll = MathHelper.lerp(l, this.rightArm.roll, 1.2707963f + 1.8707964f * m);
            } else if (p >= 22.0f && p < 26.0f) {
                m = (p - 22.0f) / 4.0f;
                this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, 1.5707964f - 1.5707964f * m, this.field_3396);
                this.rightArm.pitch = MathHelper.lerp(l, this.rightArm.pitch, 1.5707964f - 1.5707964f * m);
                this.leftArm.yaw = this.lerpAngle(this.leftArm.yaw, (float)Math.PI, this.field_3396);
                this.rightArm.yaw = MathHelper.lerp(l, this.rightArm.yaw, (float)Math.PI);
                this.leftArm.roll = this.lerpAngle(this.leftArm.roll, (float)Math.PI, this.field_3396);
                this.rightArm.roll = MathHelper.lerp(l, this.rightArm.roll, (float)Math.PI);
            }
            m = 0.3f;
            n = 0.33333334f;
            this.leftLeg.pitch = MathHelper.lerp(this.field_3396, this.leftLeg.pitch, 0.3f * MathHelper.cos(f * 0.33333334f + (float)Math.PI));
            this.rightLeg.pitch = MathHelper.lerp(this.field_3396, this.rightLeg.pitch, 0.3f * MathHelper.cos(f * 0.33333334f));
        }
        this.helmet.copyPositionAndRotation(this.head);
    }

    protected float lerpAngle(float from, float to, float position) {
        float f = (to - from) % ((float)Math.PI * 2);
        if (f < (float)(-Math.PI)) {
            f += (float)Math.PI * 2;
        }
        if (f >= (float)Math.PI) {
            f -= (float)Math.PI * 2;
        }
        return from + position * f;
    }

    private float method_2807(float f) {
        return -65.0f * f + f * f;
    }

    public void setAttributes(BipedEntityModel<T> bipedEntityModel) {
        super.copyStateTo(bipedEntityModel);
        bipedEntityModel.leftArmPose = this.leftArmPose;
        bipedEntityModel.rightArmPose = this.rightArmPose;
        bipedEntityModel.isSneaking = this.isSneaking;
    }

    public void setVisible(boolean visible) {
        this.head.visible = visible;
        this.helmet.visible = visible;
        this.torso.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrixStack) {
        this.getArm(arm).rotate(matrixStack);
    }

    protected ModelPart getArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    protected Arm getPreferredArm(T entity) {
        Arm arm = ((LivingEntity)entity).getMainArm();
        return ((LivingEntity)entity).preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ArmPose {
        EMPTY,
        ITEM,
        BLOCK,
        BOW_AND_ARROW,
        THROW_SPEAR,
        CROSSBOW_CHARGE,
        CROSSBOW_HOLD;

    }
}

