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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(value=EnvType.CLIENT)
public class ArmorStandArmorEntityModel
extends BipedEntityModel<ArmorStandEntity> {
    public ArmorStandArmorEntityModel(float f) {
        this(f, 64, 32);
    }

    protected ArmorStandArmorEntityModel(float scale, int textureWidth, int textureHeight) {
        super(scale, 0.0f, textureWidth, textureHeight);
    }

    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getPitch();
        this.head.yaw = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getYaw();
        this.head.roll = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getRoll();
        this.head.setPivot(0.0f, 1.0f, 0.0f);
        this.torso.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.torso.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.torso.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.leftArm.pitch = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getPitch();
        this.leftArm.yaw = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getYaw();
        this.leftArm.roll = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getRoll();
        this.rightArm.pitch = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getPitch();
        this.rightArm.yaw = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getYaw();
        this.rightArm.roll = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getRoll();
        this.leftLeg.pitch = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getPitch();
        this.leftLeg.yaw = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getYaw();
        this.leftLeg.roll = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getRoll();
        this.leftLeg.setPivot(1.9f, 11.0f, 0.0f);
        this.rightLeg.pitch = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getPitch();
        this.rightLeg.yaw = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getYaw();
        this.rightLeg.roll = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getRoll();
        this.rightLeg.setPivot(-1.9f, 11.0f, 0.0f);
        this.helmet.copyPositionAndRotation(this.head);
    }
}

