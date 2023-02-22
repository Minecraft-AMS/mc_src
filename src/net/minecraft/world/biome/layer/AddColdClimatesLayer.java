/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, int se) {
        if (BiomeLayers.isShallowOcean(se)) {
            return se;
        }
        int i = context.nextInt(6);
        if (i == 0) {
            return 4;
        }
        if (i == 1) {
            return 3;
        }
        return 1;
    }
}

