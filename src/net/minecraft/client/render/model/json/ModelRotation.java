/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public record ModelRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
}

