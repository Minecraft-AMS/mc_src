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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.CamelAnimations;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CamelEntityModel<T extends CamelEntity>
extends SinglePartEntityModel<T> {
    private static final float field_40458 = 400.0f;
    private static final float field_41377 = 0.3f;
    private static final float field_40459 = 2.0f;
    private static final String SADDLE = "saddle";
    private static final String BRIDLE = "bridle";
    private static final String REINS = "reins";
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart[] saddleAndBridle;
    private final ModelPart[] reins;

    public CamelEntityModel(ModelPart root) {
        this.root = root;
        ModelPart modelPart = root.getChild("body");
        this.head = modelPart.getChild("head");
        this.saddleAndBridle = new ModelPart[]{modelPart.getChild(SADDLE), this.head.getChild(BRIDLE)};
        this.reins = new ModelPart[]{this.head.getChild(REINS)};
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.1f);
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 25).cuboid(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f), ModelTransform.pivot(0.0f, 4.0f, 9.5f));
        modelPartData2.addChild("hump", ModelPartBuilder.create().uv(74, 0).cuboid(-4.5f, -5.0f, -5.5f, 9.0f, 5.0f, 11.0f), ModelTransform.pivot(0.0f, -12.0f, -10.0f));
        modelPartData2.addChild("tail", ModelPartBuilder.create().uv(122, 0).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 0.0f), ModelTransform.pivot(0.0f, -9.0f, 3.5f));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(60, 24).cuboid(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f).uv(21, 0).cuboid(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f).uv(50, 0).cuboid(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f), ModelTransform.pivot(0.0f, -3.0f, -19.5f));
        modelPartData3.addChild("left_ear", ModelPartBuilder.create().uv(45, 0).cuboid(-0.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), ModelTransform.pivot(3.0f, -21.0f, -9.5f));
        modelPartData3.addChild("right_ear", ModelPartBuilder.create().uv(67, 0).cuboid(-2.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), ModelTransform.pivot(-3.0f, -21.0f, -9.5f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(58, 16).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(4.9f, 1.0f, 9.5f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(94, 16).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(-4.9f, 1.0f, 9.5f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(4.9f, 1.0f, -10.5f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(0, 26).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(-4.9f, 1.0f, -10.5f));
        modelPartData2.addChild(SADDLE, ModelPartBuilder.create().uv(74, 64).cuboid(-4.5f, -17.0f, -15.5f, 9.0f, 5.0f, 11.0f, dilation).uv(92, 114).cuboid(-3.5f, -20.0f, -15.5f, 7.0f, 3.0f, 11.0f, dilation).uv(0, 89).cuboid(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f, dilation), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData3.addChild(REINS, ModelPartBuilder.create().uv(98, 42).cuboid(3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f).uv(84, 57).cuboid(-3.5f, -18.0f, -2.0f, 7.0f, 7.0f, 0.0f).uv(98, 42).cuboid(-3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData3.addChild(BRIDLE, ModelPartBuilder.create().uv(60, 87).cuboid(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f, dilation).uv(21, 64).cuboid(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f, dilation).uv(50, 64).cuboid(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f, dilation).uv(74, 70).cuboid(2.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f).uv(74, 70).mirrored().cuboid(-3.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(T camelEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(camelEntity, i, j, h);
        this.updateVisibleParts(camelEntity);
        float k = (float)((Entity)camelEntity).getVelocity().horizontalLengthSquared();
        float l = MathHelper.clamp(k * 400.0f, 0.3f, 2.0f);
        this.updateAnimation(((CamelEntity)camelEntity).walkingAnimationState, CamelAnimations.WALKING, h, l);
        this.updateAnimation(((CamelEntity)camelEntity).sittingTransitionAnimationState, CamelAnimations.SITTING_TRANSITION, h, 1.0f);
        this.updateAnimation(((CamelEntity)camelEntity).sittingAnimationState, CamelAnimations.SITTING, h, 1.0f);
        this.updateAnimation(((CamelEntity)camelEntity).standingTransitionAnimationState, CamelAnimations.STANDING_TRANSITION, h, 1.0f);
        this.updateAnimation(((CamelEntity)camelEntity).idlingAnimationState, CamelAnimations.IDLING, h, 1.0f);
        this.updateAnimation(((CamelEntity)camelEntity).dashingAnimationState, CamelAnimations.DASHING, h, 1.0f);
    }

    private void setHeadAngles(T entity, float headYaw, float headPitch, float animationProgress) {
        headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
        headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);
        if (((CamelEntity)entity).getJumpCooldown() > 0) {
            float f = animationProgress - (float)((CamelEntity)entity).age;
            float g = 45.0f * ((float)((CamelEntity)entity).getJumpCooldown() - f) / 55.0f;
            headPitch = MathHelper.clamp(headPitch + g, -25.0f, 70.0f);
        }
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
    }

    private void updateVisibleParts(T camel) {
        boolean bl = ((AbstractHorseEntity)camel).isSaddled();
        boolean bl2 = ((Entity)camel).hasPassengers();
        for (ModelPart modelPart : this.saddleAndBridle) {
            modelPart.visible = bl;
        }
        for (ModelPart modelPart : this.reins) {
            modelPart.visible = bl2 && bl;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.child) {
            float f = 2.0f;
            float g = 1.1f;
            matrices.push();
            matrices.scale(0.45454544f, 0.41322312f, 0.45454544f);
            matrices.translate(0.0f, 2.0625f, 0.0f);
            this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.pop();
        } else {
            this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

