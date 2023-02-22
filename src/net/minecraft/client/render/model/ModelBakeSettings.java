/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AffineTransformation;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    default public AffineTransformation getRotation() {
        return AffineTransformation.identity();
    }

    default public boolean isUvLocked() {
        return false;
    }
}

