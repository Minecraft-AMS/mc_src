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
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.FrogAnimations;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FrogEntityModel<T extends FrogEntity>
extends SinglePartEntityModel<T> {
    private static final float field_39194 = 8000.0f;
    public static final float field_41378 = 0.5f;
    public static final float field_39193 = 1.5f;
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart tongue;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart croakingBody;

    public FrogEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.tongue = this.body.getChild("tongue");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.croakingBody = this.body.getChild("croaking_body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(3, 1).cuboid(-3.5f, -2.0f, -8.0f, 7.0f, 3.0f, 9.0f).uv(23, 22).cuboid(-3.5f, -1.0f, -8.0f, 7.0f, 0.0f, 9.0f), ModelTransform.pivot(0.0f, -2.0f, 4.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(23, 13).cuboid(-3.5f, -1.0f, -7.0f, 7.0f, 0.0f, 9.0f).uv(0, 13).cuboid(-3.5f, -2.0f, -7.0f, 7.0f, 3.0f, 9.0f), ModelTransform.pivot(0.0f, -2.0f, -1.0f));
        ModelPartData modelPartData5 = modelPartData4.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(-0.5f, 0.0f, 2.0f));
        modelPartData5.addChild("right_eye", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.pivot(-1.5f, -3.0f, -6.5f));
        modelPartData5.addChild("left_eye", ModelPartBuilder.create().uv(0, 5).cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.pivot(2.5f, -3.0f, -6.5f));
        modelPartData3.addChild("croaking_body", ModelPartBuilder.create().uv(26, 5).cuboid(-3.5f, -0.1f, -2.9f, 7.0f, 2.0f, 3.0f, new Dilation(-0.1f)), ModelTransform.pivot(0.0f, -1.0f, -5.0f));
        ModelPartData modelPartData6 = modelPartData3.addChild("tongue", ModelPartBuilder.create().uv(17, 13).cuboid(-2.0f, 0.0f, -7.1f, 4.0f, 0.0f, 7.0f), ModelTransform.pivot(0.0f, -1.01f, 1.0f));
        ModelPartData modelPartData7 = modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(0, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), ModelTransform.pivot(4.0f, -1.0f, -6.5f));
        modelPartData7.addChild("left_hand", ModelPartBuilder.create().uv(18, 40).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.pivot(0.0f, 3.0f, -1.0f));
        ModelPartData modelPartData8 = modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(0, 38).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), ModelTransform.pivot(-4.0f, -1.0f, -6.5f));
        modelPartData8.addChild("right_hand", ModelPartBuilder.create().uv(2, 40).cuboid(-4.0f, 0.01f, -5.0f, 8.0f, 0.0f, 8.0f), ModelTransform.pivot(0.0f, 3.0f, 0.0f));
        ModelPartData modelPartData9 = modelPartData2.addChild("left_leg", ModelPartBuilder.create().uv(14, 25).cuboid(-1.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), ModelTransform.pivot(3.5f, -3.0f, 4.0f));
        modelPartData9.addChild("left_foot", ModelPartBuilder.create().uv(2, 32).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.pivot(2.0f, 3.0f, 0.0f));
        ModelPartData modelPartData10 = modelPartData2.addChild("right_leg", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), ModelTransform.pivot(-3.5f, -3.0f, 4.0f));
        modelPartData10.addChild("right_foot", ModelPartBuilder.create().uv(18, 32).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.pivot(-2.0f, 3.0f, 0.0f));
        return TexturedModelData.of(modelData, 48, 48);
    }

    @Override
    public void setAngles(T frogEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        float k = (float)((Entity)frogEntity).getVelocity().horizontalLengthSquared();
        float l = MathHelper.clamp(k * 8000.0f, 0.5f, 1.5f);
        this.updateAnimation(((FrogEntity)frogEntity).longJumpingAnimationState, FrogAnimations.LONG_JUMPING, h);
        this.updateAnimation(((FrogEntity)frogEntity).croakingAnimationState, FrogAnimations.CROAKING, h);
        this.updateAnimation(((FrogEntity)frogEntity).usingTongueAnimationState, FrogAnimations.USING_TONGUE, h);
        this.updateAnimation(((FrogEntity)frogEntity).walkingAnimationState, FrogAnimations.WALKING, h, l);
        this.updateAnimation(((FrogEntity)frogEntity).swimmingAnimationState, FrogAnimations.SWIMMING, h);
        this.updateAnimation(((FrogEntity)frogEntity).idlingInWaterAnimationState, FrogAnimations.IDLING_IN_WATER, h);
        this.croakingBody.visible = ((FrogEntity)frogEntity).croakingAnimationState.isRunning();
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

