/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public final class ApplyOceanTemperatureLayer
extends Enum<ApplyOceanTemperatureLayer>
implements MergingLayer,
IdentityCoordinateTransformer {
    public static final /* enum */ ApplyOceanTemperatureLayer INSTANCE = new ApplyOceanTemperatureLayer();
    private static final /* synthetic */ ApplyOceanTemperatureLayer[] field_16122;

    public static ApplyOceanTemperatureLayer[] values() {
        return (ApplyOceanTemperatureLayer[])field_16122.clone();
    }

    public static ApplyOceanTemperatureLayer valueOf(String string) {
        return Enum.valueOf(ApplyOceanTemperatureLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int i = sampler1.sample(this.transformX(x), this.transformZ(z));
        int j = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (!BiomeLayers.isOcean(i)) {
            return i;
        }
        int k = 8;
        int l = 4;
        for (int m = -8; m <= 8; m += 4) {
            for (int n = -8; n <= 8; n += 4) {
                int o = sampler1.sample(this.transformX(x + m), this.transformZ(z + n));
                if (BiomeLayers.isOcean(o)) continue;
                if (j == 44) {
                    return 45;
                }
                if (j != 10) continue;
                return 46;
            }
        }
        if (i == 24) {
            if (j == 45) {
                return 48;
            }
            if (j == 0) {
                return 24;
            }
            if (j == 46) {
                return 49;
            }
            if (j == 10) {
                return 50;
            }
        }
        return j;
    }

    private static /* synthetic */ ApplyOceanTemperatureLayer[] method_36777() {
        return new ApplyOceanTemperatureLayer[]{INSTANCE};
    }

    static {
        field_16122 = ApplyOceanTemperatureLayer.method_36777();
    }
}

