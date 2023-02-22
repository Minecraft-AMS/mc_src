/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    private final ModelPart rightTorso;
    private final ModelPart leftTorso;
    private final ModelPart shoulderStick;
    private final ModelPart basePlate;

    public ArmorStandEntityModel() {
        this(0.0f);
    }

    public ArmorStandEntityModel(float f) {
        super(f, 64, 64);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.body = new ModelPart(this, 0, 26);
        this.body.addCuboid(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f, f);
        this.body.setPivot(0.0f, 0.0f, 0.0f);
        this.rightArm = new ModelPart(this, 24, 0);
        this.rightArm.addCuboid(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 8, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightTorso = new ModelPart(this, 16, 0);
        this.rightTorso.addCuboid(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.rightTorso.setPivot(0.0f, 0.0f, 0.0f);
        this.rightTorso.visible = true;
        this.leftTorso = new ModelPart(this, 48, 16);
        this.leftTorso.addCuboid(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.leftTorso.setPivot(0.0f, 0.0f, 0.0f);
        this.shoulderStick = new ModelPart(this, 0, 48);
        this.shoulderStick.addCuboid(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f, f);
        this.shoulderStick.setPivot(0.0f, 0.0f, 0.0f);
        this.basePlate = new ModelPart(this, 0, 32);
        this.basePlate.addCuboid(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f, f);
        this.basePlate.setPivot(0.0f, 12.0f, 0.0f);
        this.hat.visible = false;
    }

    @Override
    public void animateModel(ArmorStandEntity armorStandEntity, float f, float g, float h) {
        this.basePlate.pitch = 0.0f;
        this.basePlate.yaw = (float)Math.PI / 180 * -MathHelper.lerpAngleDegrees(h, armorStandEntity.prevYaw, armorStandEntity.yaw);
        this.basePlate.roll = 0.0f;
    }

    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
        super.setAngles(armorStandEntity, f, g, h, i, j);
        this.leftArm.visible = armorStandEntity.shouldShowArms();
        this.rightArm.visible = armorStandEntity.shouldShowArms();
        this.basePlate.visible = !armorStandEntity.shouldHideBasePlate();
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.rightTorso.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.rightTorso.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.rightTorso.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.leftTorso.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.leftTorso.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.leftTorso.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.shoulderStick.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.shoulderStick.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.shoulderStick.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), (Iterable)ImmutableList.of((Object)this.rightTorso, (Object)this.leftTorso, (Object)this.shoulderStick, (Object)this.basePlate));
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        ModelPart modelPart = this.getArm(arm);
        boolean bl = modelPart.visible;
        modelPart.visible = true;
        super.setArmAngle(arm, matrices);
        modelPart.visible = bl;
    }
}

