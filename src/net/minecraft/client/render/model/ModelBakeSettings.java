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
import net.minecraft.client.render.model.ModelRotation;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    default public ModelRotation getRotation() {
        return ModelRotation.X0_Y0;
    }

    default public boolean isShaded() {
        return false;
    }
}

