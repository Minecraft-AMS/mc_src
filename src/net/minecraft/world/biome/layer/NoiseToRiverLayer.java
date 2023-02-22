/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class NoiseToRiverLayer
extends Enum<NoiseToRiverLayer>
implements CrossSamplingLayer {
    public static final /* enum */ NoiseToRiverLayer INSTANCE = new NoiseToRiverLayer();
    private static final /* synthetic */ NoiseToRiverLayer[] field_16169;

    public static NoiseToRiverLayer[] values() {
        return (NoiseToRiverLayer[])field_16169.clone();
    }

    public static NoiseToRiverLayer valueOf(String string) {
        return Enum.valueOf(NoiseToRiverLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int i = NoiseToRiverLayer.isValidForRiver(center);
        if (i == NoiseToRiverLayer.isValidForRiver(w) && i == NoiseToRiverLayer.isValidForRiver(n) && i == NoiseToRiverLayer.isValidForRiver(e) && i == NoiseToRiverLayer.isValidForRiver(s)) {
            return -1;
        }
        return 7;
    }

    private static int isValidForRiver(int value) {
        if (value >= 2) {
            return 2 + (value & 1);
        }
        return value;
    }

    private static /* synthetic */ NoiseToRiverLayer[] method_36783() {
        return new NoiseToRiverLayer[]{INSTANCE};
    }

    static {
        field_16169 = NoiseToRiverLayer.method_36783();
    }
}

