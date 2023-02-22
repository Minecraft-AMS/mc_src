/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class OceanTemperatureLayer
extends Enum<OceanTemperatureLayer>
implements InitLayer {
    public static final /* enum */ OceanTemperatureLayer INSTANCE = new OceanTemperatureLayer();
    private static final /* synthetic */ OceanTemperatureLayer[] field_16106;

    public static OceanTemperatureLayer[] values() {
        return (OceanTemperatureLayer[])field_16106.clone();
    }

    public static OceanTemperatureLayer valueOf(String string) {
        return Enum.valueOf(OceanTemperatureLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int x, int y) {
        PerlinNoiseSampler perlinNoiseSampler = context.getNoiseSampler();
        double d = perlinNoiseSampler.sample((double)x / 8.0, (double)y / 8.0, 0.0);
        if (d > 0.4) {
            return 44;
        }
        if (d > 0.2) {
            return 45;
        }
        if (d < -0.4) {
            return 10;
        }
        if (d < -0.2) {
            return 46;
        }
        return 0;
    }

    private static /* synthetic */ OceanTemperatureLayer[] method_36776() {
        return new OceanTemperatureLayer[]{INSTANCE};
    }

    static {
        field_16106 = OceanTemperatureLayer.method_36776();
    }
}

