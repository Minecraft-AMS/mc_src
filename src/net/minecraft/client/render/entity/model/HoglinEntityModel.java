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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityModel<T extends MobEntity>
extends AnimalModel<T> {
    private static final float HEAD_PITCH_START = 0.87266463f;
    private static final float HEAD_PITCH_END = -0.34906584f;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart mane;

    public HoglinEntityModel(ModelPart root) {
        super(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f);
        this.body = root.getChild("body");
        this.mane = this.body.getChild("mane");
        this.head = root.getChild("head");
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(1, 1).cuboid(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f), ModelTransform.pivot(0.0f, 7.0f, 0.0f));
        modelPartData2.addChild("mane", ModelPartBuilder.create().uv(90, 33).cuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new Dilation(0.001f)), ModelTransform.pivot(0.0f, -14.0f, -5.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("head", ModelPartBuilder.create().uv(61, 1).cuboid(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f), ModelTransform.of(0.0f, 2.0f, -12.0f, 0.87266463f, 0.0f, 0.0f));
        modelPartData3.addChild("right_ear", ModelPartBuilder.create().uv(1, 1).cuboid(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(-6.0f, -2.0f, -3.0f, 0.0f, 0.0f, -0.6981317f));
        modelPartData3.addChild("left_ear", ModelPartBuilder.create().uv(1, 6).cuboid(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(6.0f, -2.0f, -3.0f, 0.0f, 0.0f, 0.6981317f));
        modelPartData3.addChild("right_horn", ModelPartBuilder.create().uv(10, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(-7.0f, 2.0f, -12.0f));
        modelPartData3.addChild("left_horn", ModelPartBuilder.create().uv(1, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(7.0f, 2.0f, -12.0f));
        int i = 14;
        int j = 11;
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(66, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.pivot(-4.0f, 10.0f, -8.5f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(41, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.pivot(4.0f, 10.0f, -8.5f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(21, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.pivot(-5.0f, 13.0f, 10.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(0, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.pivot(5.0f, 13.0f, 10.0f));
        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg, (Object)this.rightHindLeg, (Object)this.leftHindLeg);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        this.rightEar.roll = -0.6981317f - g * MathHelper.sin(f);
        this.leftEar.roll = 0.6981317f + g * MathHelper.sin(f);
        this.head.yaw = i * ((float)Math.PI / 180);
        int k = ((Hoglin)mobEntity).getMovementCooldownTicks();
        float l = 1.0f - (float)MathHelper.abs(10 - 2 * k) / 10.0f;
        this.head.pitch = MathHelper.lerp(l, 0.87266463f, -0.34906584f);
        if (((LivingEntity)mobEntity).isBaby()) {
            this.head.pivotY = MathHelper.lerp(l, 2.0f, 5.0f);
            this.mane.pivotZ = -3.0f;
        } else {
            this.head.pivotY = 2.0f;
            this.mane.pivotZ = -7.0f;
        }
        float m = 1.2f;
        this.rightFrontLeg.pitch = MathHelper.cos(f) * 1.2f * g;
        this.rightHindLeg.pitch = this.leftFrontLeg.pitch = MathHelper.cos(f + (float)Math.PI) * 1.2f * g;
        this.leftHindLeg.pitch = this.rightFrontLeg.pitch;
    }
}

