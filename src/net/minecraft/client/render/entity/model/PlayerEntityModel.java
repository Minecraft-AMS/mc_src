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
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    private static final String EAR = "ear";
    private static final String CLOAK = "cloak";
    private static final String LEFT_SLEEVE = "left_sleeve";
    private static final String RIGHT_SLEEVE = "right_sleeve";
    private static final String LEFT_PANTS = "left_pants";
    private static final String RIGHT_PANTS = "right_pants";
    private final List<ModelPart> parts;
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final ModelPart cloak;
    private final ModelPart ear;
    private final boolean thinArms;

    public PlayerEntityModel(ModelPart root, boolean thinArms) {
        super(root, RenderLayer::getEntityTranslucent);
        this.thinArms = thinArms;
        this.ear = root.getChild(EAR);
        this.cloak = root.getChild(CLOAK);
        this.leftSleeve = root.getChild(LEFT_SLEEVE);
        this.rightSleeve = root.getChild(RIGHT_SLEEVE);
        this.leftPants = root.getChild(LEFT_PANTS);
        this.rightPants = root.getChild(RIGHT_PANTS);
        this.jacket = root.getChild("jacket");
        this.parts = (List)root.traverse().filter(part -> !part.isEmpty()).collect(ImmutableList.toImmutableList());
    }

    public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
        ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EAR, ModelPartBuilder.create().uv(24, 0).cuboid(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, dilation), ModelTransform.NONE);
        modelPartData.addChild(CLOAK, ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, dilation, 1.0f, 0.5f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        float f = 0.25f;
        if (slim) {
            modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(5.0f, 2.5f, 0.0f));
            modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(-5.0f, 2.5f, 0.0f));
            modelPartData.addChild(LEFT_SLEEVE, ModelPartBuilder.create().uv(48, 48).cuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.pivot(5.0f, 2.5f, 0.0f));
            modelPartData.addChild(RIGHT_SLEEVE, ModelPartBuilder.create().uv(40, 32).cuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.pivot(-5.0f, 2.5f, 0.0f));
        } else {
            modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(5.0f, 2.0f, 0.0f));
            modelPartData.addChild(LEFT_SLEEVE, ModelPartBuilder.create().uv(48, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.pivot(5.0f, 2.0f, 0.0f));
            modelPartData.addChild(RIGHT_SLEEVE, ModelPartBuilder.create().uv(40, 32).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.pivot(-5.0f, 2.0f, 0.0f));
        }
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(1.9f, 12.0f, 0.0f));
        modelPartData.addChild(LEFT_PANTS, ModelPartBuilder.create().uv(0, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.pivot(1.9f, 12.0f, 0.0f));
        modelPartData.addChild(RIGHT_PANTS, ModelPartBuilder.create().uv(0, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
        modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
        return modelData;
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), (Iterable)ImmutableList.of((Object)this.leftPants, (Object)this.rightPants, (Object)this.leftSleeve, (Object)this.rightSleeve, (Object)this.jacket));
    }

    public void renderEars(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.ear.copyTransform(this.head);
        this.ear.pivotX = 0.0f;
        this.ear.pivotY = 0.0f;
        this.ear.render(matrices, vertices, light, overlay);
    }

    public void renderCape(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.cloak.render(matrices, vertices, light, overlay);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        super.setAngles(livingEntity, f, g, h, i, j);
        this.leftPants.copyTransform(this.leftLeg);
        this.rightPants.copyTransform(this.rightLeg);
        this.leftSleeve.copyTransform(this.leftArm);
        this.rightSleeve.copyTransform(this.rightArm);
        this.jacket.copyTransform(this.body);
        if (((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
            if (((Entity)livingEntity).isInSneakingPose()) {
                this.cloak.pivotZ = 1.4f;
                this.cloak.pivotY = 1.85f;
            } else {
                this.cloak.pivotZ = 0.0f;
                this.cloak.pivotY = 0.0f;
            }
        } else if (((Entity)livingEntity).isInSneakingPose()) {
            this.cloak.pivotZ = 0.3f;
            this.cloak.pivotY = 0.8f;
        } else {
            this.cloak.pivotZ = -1.1f;
            this.cloak.pivotY = -0.85f;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
        this.cloak.visible = visible;
        this.ear.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float f = 0.5f * (float)(arm == Arm.RIGHT ? 1 : -1);
            modelPart.pivotX += f;
            modelPart.rotate(matrices);
            modelPart.pivotX -= f;
        } else {
            modelPart.rotate(matrices);
        }
    }

    public ModelPart getRandomPart(Random random) {
        return this.parts.get(random.nextInt(this.parts.size()));
    }
}

