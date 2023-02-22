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
import net.minecraft.client.util.math.Rotation3;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    default public Rotation3 getRotation() {
        return Rotation3.identity();
    }

    default public boolean isShaded() {
        return false;
    }
}

