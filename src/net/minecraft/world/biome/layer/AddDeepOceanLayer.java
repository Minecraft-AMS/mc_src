/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddDeepOceanLayer
extends Enum<AddDeepOceanLayer>
implements CrossSamplingLayer {
    public static final /* enum */ AddDeepOceanLayer INSTANCE = new AddDeepOceanLayer();
    private static final /* synthetic */ AddDeepOceanLayer[] field_16053;

    public static AddDeepOceanLayer[] values() {
        return (AddDeepOceanLayer[])field_16053.clone();
    }

    public static AddDeepOceanLayer valueOf(String string) {
        return Enum.valueOf(AddDeepOceanLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (BiomeLayers.isShallowOcean(center)) {
            int i = 0;
            if (BiomeLayers.isShallowOcean(n)) {
                ++i;
            }
            if (BiomeLayers.isShallowOcean(e)) {
                ++i;
            }
            if (BiomeLayers.isShallowOcean(w)) {
                ++i;
            }
            if (BiomeLayers.isShallowOcean(s)) {
                ++i;
            }
            if (i > 3) {
                if (center == 44) {
                    return 47;
                }
                if (center == 45) {
                    return 48;
                }
                if (center == 0) {
                    return 24;
                }
                if (center == 46) {
                    return 49;
                }
                if (center == 10) {
                    return 50;
                }
                return 24;
            }
        }
        return center;
    }

    private static /* synthetic */ AddDeepOceanLayer[] method_36766() {
        return new AddDeepOceanLayer[]{INSTANCE};
    }

    static {
        field_16053 = AddDeepOceanLayer.method_36766();
    }
}

