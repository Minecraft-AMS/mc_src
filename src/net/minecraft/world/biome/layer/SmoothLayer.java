/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class SmoothLayer
extends Enum<SmoothLayer>
implements CrossSamplingLayer {
    public static final /* enum */ SmoothLayer INSTANCE = new SmoothLayer();
    private static final /* synthetic */ SmoothLayer[] field_16170;

    public static SmoothLayer[] values() {
        return (SmoothLayer[])field_16170.clone();
    }

    public static SmoothLayer valueOf(String string) {
        return Enum.valueOf(SmoothLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        boolean bl2;
        boolean bl = e == w;
        boolean bl3 = bl2 = n == s;
        if (bl == bl2) {
            if (bl) {
                return context.nextInt(2) == 0 ? w : n;
            }
            return center;
        }
        return bl ? w : n;
    }

    private static /* synthetic */ SmoothLayer[] method_36786() {
        return new SmoothLayer[]{INSTANCE};
    }

    static {
        field_16170 = SmoothLayer.method_36786();
    }
}

