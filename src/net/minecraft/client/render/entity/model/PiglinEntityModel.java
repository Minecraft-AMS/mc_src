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
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity>
extends PlayerEntityModel<T> {
    public final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelTransform bodyRotation;
    private final ModelTransform headRotation;
    private final ModelTransform leftArmRotation;
    private final ModelTransform rightArmRotation;

    public PiglinEntityModel(ModelPart modelPart) {
        super(modelPart, false);
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
        this.bodyRotation = this.body.getTransform();
        this.headRotation = this.head.getTransform();
        this.leftArmRotation = this.leftArm.getTransform();
        this.rightArmRotation = this.rightArm.getTransform();
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = PlayerEntityModel.getTexturedModelData(dilation, false);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.NONE);
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, dilation).uv(31, 1).cuboid(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, dilation).uv(2, 4).cuboid(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, dilation).uv(2, 0).cuboid(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, dilation), ModelTransform.NONE);
        modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(51, 6).cuboid(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, dilation), ModelTransform.of(4.5f, -6.0f, 0.0f, 0.0f, 0.0f, -0.5235988f));
        modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(39, 6).cuboid(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, dilation), ModelTransform.of(-4.5f, -6.0f, 0.0f, 0.0f, 0.0f, 0.5235988f));
        modelPartData.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
        return modelData;
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        this.body.setTransform(this.bodyRotation);
        this.head.setTransform(this.headRotation);
        this.leftArm.setTransform(this.leftArmRotation);
        this.rightArm.setTransform(this.rightArmRotation);
        super.setAngles(mobEntity, f, g, h, i, j);
        float k = 0.5235988f;
        float l = h * 0.1f + f * 0.5f;
        float m = 0.08f + g * 0.4f;
        this.leftEar.roll = -0.5235988f - MathHelper.cos(l * 1.2f) * m;
        this.rightEar.roll = 0.5235988f + MathHelper.cos(l) * m;
        if (mobEntity instanceof AbstractPiglinEntity) {
            AbstractPiglinEntity abstractPiglinEntity = (AbstractPiglinEntity)mobEntity;
            PiglinActivity piglinActivity = abstractPiglinEntity.getActivity();
            if (piglinActivity == PiglinActivity.DANCING) {
                float n = h / 60.0f;
                this.rightEar.roll = 0.5235988f + (float)Math.PI / 180 * MathHelper.sin(n * 30.0f) * 10.0f;
                this.leftEar.roll = -0.5235988f - (float)Math.PI / 180 * MathHelper.cos(n * 30.0f) * 10.0f;
                this.head.pivotX = MathHelper.sin(n * 10.0f);
                this.head.pivotY = MathHelper.sin(n * 40.0f) + 0.4f;
                this.rightArm.roll = (float)Math.PI / 180 * (70.0f + MathHelper.cos(n * 40.0f) * 10.0f);
                this.leftArm.roll = this.rightArm.roll * -1.0f;
                this.rightArm.pivotY = MathHelper.sin(n * 40.0f) * 0.5f + 1.5f;
                this.leftArm.pivotY = MathHelper.sin(n * 40.0f) * 0.5f + 1.5f;
                this.body.pivotY = MathHelper.sin(n * 40.0f) * 0.35f;
            } else if (piglinActivity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON && this.handSwingProgress == 0.0f) {
                this.rotateMainArm(mobEntity);
            } else if (piglinActivity == PiglinActivity.CROSSBOW_HOLD) {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, !((MobEntity)mobEntity).isLeftHanded());
            } else if (piglinActivity == PiglinActivity.CROSSBOW_CHARGE) {
                CrossbowPosing.charge(this.rightArm, this.leftArm, mobEntity, !((MobEntity)mobEntity).isLeftHanded());
            } else if (piglinActivity == PiglinActivity.ADMIRING_ITEM) {
                this.head.pitch = 0.5f;
                this.head.yaw = 0.0f;
                if (((MobEntity)mobEntity).isLeftHanded()) {
                    this.rightArm.yaw = -0.5f;
                    this.rightArm.pitch = -0.9f;
                } else {
                    this.leftArm.yaw = 0.5f;
                    this.leftArm.pitch = -0.9f;
                }
            }
        } else if (((Entity)mobEntity).getType() == EntityType.ZOMBIFIED_PIGLIN) {
            CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, ((MobEntity)mobEntity).isAttacking(), this.handSwingProgress, h);
        }
        this.leftPants.copyTransform(this.leftLeg);
        this.rightPants.copyTransform(this.rightLeg);
        this.leftSleeve.copyTransform(this.leftArm);
        this.rightSleeve.copyTransform(this.rightArm);
        this.jacket.copyTransform(this.body);
        this.hat.copyTransform(this.head);
    }

    @Override
    protected void animateArms(T mobEntity, float f) {
        if (this.handSwingProgress > 0.0f && mobEntity instanceof PiglinEntity && ((PiglinEntity)mobEntity).getActivity() == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON) {
            CrossbowPosing.meleeAttack(this.rightArm, this.leftArm, mobEntity, this.handSwingProgress, f);
            return;
        }
        super.animateArms(mobEntity, f);
    }

    private void rotateMainArm(T entity) {
        if (((MobEntity)entity).isLeftHanded()) {
            this.leftArm.pitch = -1.8f;
        } else {
            this.rightArm.pitch = -1.8f;
        }
    }
}

