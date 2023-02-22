/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

public interface LunarWorldView
extends WorldView {
    public long getLunarTime();

    default public float getMoonSize() {
        return DimensionType.MOON_SIZES[this.getDimension().getMoonPhase(this.getLunarTime())];
    }

    default public float getSkyAngle(float tickDelta) {
        return this.getDimension().getSkyAngle(this.getLunarTime());
    }

    @Environment(value=EnvType.CLIENT)
    default public int getMoonPhase() {
        return this.getDimension().getMoonPhase(this.getLunarTime());
    }
}

