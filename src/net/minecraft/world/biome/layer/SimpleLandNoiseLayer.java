/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class SimpleLandNoiseLayer
extends Enum<SimpleLandNoiseLayer>
implements IdentitySamplingLayer {
    public static final /* enum */ SimpleLandNoiseLayer INSTANCE = new SimpleLandNoiseLayer();
    private static final /* synthetic */ SimpleLandNoiseLayer[] field_16156;

    public static SimpleLandNoiseLayer[] values() {
        return (SimpleLandNoiseLayer[])field_16156.clone();
    }

    public static SimpleLandNoiseLayer valueOf(String string) {
        return Enum.valueOf(SimpleLandNoiseLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int value) {
        return BiomeLayers.isShallowOcean(value) ? value : context.nextInt(299999) + 2;
    }

    private static /* synthetic */ SimpleLandNoiseLayer[] method_36782() {
        return new SimpleLandNoiseLayer[]{INSTANCE};
    }

    static {
        field_16156 = SimpleLandNoiseLayer.method_36782();
    }
}

