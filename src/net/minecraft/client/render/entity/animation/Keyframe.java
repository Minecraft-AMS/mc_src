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
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public record Keyframe(float timestamp, Vec3f target, Transformation.Interpolation interpolation) {
}

