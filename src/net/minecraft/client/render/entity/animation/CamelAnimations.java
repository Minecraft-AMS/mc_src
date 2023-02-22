/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(value=EnvType.CLIENT)
public class CamelAnimations {
    public static final Animation WALKING = Animation.Builder.create(1.5f).looping().addBoneAnimation("root", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 2.5f), Transformation.Interpolations.CUBIC), new Keyframe(1.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -2.5f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 2.5f), Transformation.Interpolations.CUBIC))).addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(2.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(-2.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(2.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.125f, AnimationHelper.createRotationalVector(-2.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(2.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(-22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.4583f, AnimationHelper.createTranslationalVector(0.0f, 4.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.2083f, AnimationHelper.createTranslationalVector(0.0f, 4.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-20.4f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.375f, AnimationHelper.createRotationalVector(-22.5f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-20.4f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -0.21f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.0833f, AnimationHelper.createTranslationalVector(0.0f, 4.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.375f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, -0.21f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.625f, AnimationHelper.createRotationalVector(-22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(22.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0.0f, 4.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.625f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_ear", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -22.5f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.125f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -22.5f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_ear", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 22.5f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.125f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 22.5f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("tail", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(15.94102f, -8.42106f, 20.94102f), Transformation.Interpolations.CUBIC), new Keyframe(0.75f, AnimationHelper.createRotationalVector(15.94102f, 8.42106f, -20.94102f), Transformation.Interpolations.CUBIC), new Keyframe(1.5f, AnimationHelper.createRotationalVector(15.94102f, -8.42106f, 20.94102f), Transformation.Interpolations.CUBIC))).build();
    public static final Animation SITTING_TRANSITION = Animation.Builder.create(2.0f).addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.3f, AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.8f, AnimationHelper.createRotationalVector(24.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.3f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 1.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.8f, AnimationHelper.createTranslationalVector(0.0f, -6.0f, 1.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createTranslationalVector(0.0f, -19.9f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(-90.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(0.0f, -2.0f, 11.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, -2.0f, 11.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createTranslationalVector(0.0f, -8.4f, 11.4f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(-90.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(0.0f, -2.0f, 11.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, -2.0f, 11.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createTranslationalVector(0.0f, -8.4f, 11.4f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-10.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createRotationalVector(-15.0f, -3.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createRotationalVector(-65.0f, -9.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(-90.0f, -15.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 1.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createTranslationalVector(1.0f, -0.62f, 0.25f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createTranslationalVector(0.5f, -11.25f, 2.5f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createTranslationalVector(1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-10.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createRotationalVector(-15.0f, 3.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createRotationalVector(-65.0f, 9.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(-90.0f, 15.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 1.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createTranslationalVector(-1.0f, -0.62f, 0.25f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createTranslationalVector(-0.5f, -11.25f, 2.5f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createTranslationalVector(-1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.7f, AnimationHelper.createRotationalVector(-27.5f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(-21.25f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("tail", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.7f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createRotationalVector(80.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(50.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).build();
    public static final Animation SITTING = Animation.Builder.create(1.0f).addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -19.9f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(0.0f, -19.9f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(-90.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(-90.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, -15.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(-90.0f, -15.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, 15.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(-90.0f, 15.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(-1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createTranslationalVector(-1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("tail", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(50.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.0f, AnimationHelper.createRotationalVector(50.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).build();
    public static final Animation STANDING_TRANSITION = Animation.Builder.create(2.6f).addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.7f, AnimationHelper.createRotationalVector(-17.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.8f, AnimationHelper.createRotationalVector(-17.83f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.3f, AnimationHelper.createRotationalVector(-5.83f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -19.9f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.7f, AnimationHelper.createTranslationalVector(0.0f, -19.9f, -3.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.4f, AnimationHelper.createTranslationalVector(0.0f, -12.76f, -4.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.8f, AnimationHelper.createTranslationalVector(0.0f, -10.1f, -4.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.3f, AnimationHelper.createTranslationalVector(0.0f, -2.9f, -2.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(-90.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createRotationalVector(-49.06f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.8f, AnimationHelper.createRotationalVector(-22.5f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.3f, AnimationHelper.createRotationalVector(-25.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 8.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createTranslationalVector(0.0f, -7.14f, 4.42f), Transformation.Interpolations.LINEAR), new Keyframe(1.8f, AnimationHelper.createTranslationalVector(0.0f, -1.27f, -1.33f), Transformation.Interpolations.LINEAR), new Keyframe(2.3f, AnimationHelper.createTranslationalVector(0.0f, -1.27f, -0.33f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(-90.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createRotationalVector(-49.06f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.8f, AnimationHelper.createRotationalVector(-22.5f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.3f, AnimationHelper.createRotationalVector(-25.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 12.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0.0f, -20.6f, 8.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createTranslationalVector(0.0f, -7.14f, 4.42f), Transformation.Interpolations.LINEAR), new Keyframe(1.8f, AnimationHelper.createTranslationalVector(0.0f, -1.27f, -1.33f), Transformation.Interpolations.LINEAR), new Keyframe(2.3f, AnimationHelper.createTranslationalVector(0.0f, -1.27f, -0.33f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, -15.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.3f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.6f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createRotationalVector(-60.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createRotationalVector(35.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.2f, AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.3f, AnimationHelper.createTranslationalVector(-2.0f, -20.5f, 3.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.6f, AnimationHelper.createTranslationalVector(-2.0f, -20.5f, 3.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createTranslationalVector(-2.0f, -10.5f, 2.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(-2.0f, -0.4f, -3.9f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createTranslationalVector(-2.0f, -4.3f, -9.8f), Transformation.Interpolations.LINEAR), new Keyframe(2.2f, AnimationHelper.createTranslationalVector(-1.0f, -2.5f, -5.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, 15.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.3f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.6f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createRotationalVector(-60.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createRotationalVector(35.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.2f, AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(-1.0f, -20.5f, 5.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.3f, AnimationHelper.createTranslationalVector(2.0f, -20.5f, 3.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.6f, AnimationHelper.createTranslationalVector(2.0f, -20.5f, 3.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.1f, AnimationHelper.createTranslationalVector(2.0f, -10.5f, 2.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createTranslationalVector(2.0f, -0.4f, -3.9f), Transformation.Interpolations.LINEAR), new Keyframe(1.9f, AnimationHelper.createTranslationalVector(2.0f, -4.3f, -9.8f), Transformation.Interpolations.LINEAR), new Keyframe(2.2f, AnimationHelper.createTranslationalVector(1.0f, -2.5f, -5.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.3f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.8f, AnimationHelper.createRotationalVector(55.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.0f, AnimationHelper.createRotationalVector(65.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.4f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("tail", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(50.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.4f, AnimationHelper.createRotationalVector(55.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.9f, AnimationHelper.createRotationalVector(55.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(1.5f, AnimationHelper.createRotationalVector(17.5f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(2.6f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).build();
    public static final Animation DASHING = Animation.Builder.create(0.5f).looping().addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("tail", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(67.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.125f, AnimationHelper.createRotationalVector(112.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.25f, AnimationHelper.createRotationalVector(67.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(112.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.5f, AnimationHelper.createRotationalVector(67.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(10.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.125f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.25f, AnimationHelper.createRotationalVector(10.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.5f, AnimationHelper.createRotationalVector(10.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(44.97272f, 1.76749f, -1.76833f), Transformation.Interpolations.CUBIC), new Keyframe(0.125f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.25f, AnimationHelper.createRotationalVector(44.97272f, 1.76749f, -1.76833f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.5f, AnimationHelper.createRotationalVector(44.97272f, 1.76749f, -1.76833f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_front_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.125f, AnimationHelper.createRotationalVector(44.97272f, -1.76749f, 1.76833f), Transformation.Interpolations.CUBIC), new Keyframe(0.25f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(44.97272f, -1.76749f, 1.76833f), Transformation.Interpolations.CUBIC), new Keyframe(0.5f, AnimationHelper.createRotationalVector(-90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.125f, AnimationHelper.createRotationalVector(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.25f, AnimationHelper.createRotationalVector(90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.5f, AnimationHelper.createRotationalVector(90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_hind_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.125f, AnimationHelper.createRotationalVector(90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.25f, AnimationHelper.createRotationalVector(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.375f, AnimationHelper.createRotationalVector(90.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(0.5f, AnimationHelper.createRotationalVector(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_ear", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, -67.5f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, -67.5f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_ear", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 67.5f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, 67.5f, 0.0f), Transformation.Interpolations.LINEAR))).build();
    public static final Animation IDLING = Animation.Builder.create(4.0f).addBoneAnimation("tail", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(1.0f, AnimationHelper.createRotationalVector(4.98107f, 0.43523f, -4.98107f), Transformation.Interpolations.CUBIC), new Keyframe(3.0f, AnimationHelper.createRotationalVector(4.9872f, -0.29424f, 3.36745f), Transformation.Interpolations.CUBIC), new Keyframe(4.0f, AnimationHelper.createRotationalVector(5.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.0f, AnimationHelper.createRotationalVector(-2.5f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC), new Keyframe(4.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("left_ear", new Transformation(Transformation.Targets.ROTATE, new Keyframe(2.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -45.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.625f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 22.5f), Transformation.Interpolations.CUBIC), new Keyframe(2.75f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -45.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.875f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 22.5f), Transformation.Interpolations.CUBIC), new Keyframe(3.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -45.0f), Transformation.Interpolations.CUBIC))).addBoneAnimation("right_ear", new Transformation(Transformation.Targets.ROTATE, new Keyframe(2.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 45.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.625f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -22.5f), Transformation.Interpolations.CUBIC), new Keyframe(2.75f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 45.0f), Transformation.Interpolations.CUBIC), new Keyframe(2.875f, AnimationHelper.createRotationalVector(0.0f, 0.0f, -22.5f), Transformation.Interpolations.CUBIC), new Keyframe(3.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 45.0f), Transformation.Interpolations.CUBIC))).build();
}
