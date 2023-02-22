/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public final class ApplyRiverLayer
extends Enum<ApplyRiverLayer>
implements MergingLayer,
IdentityCoordinateTransformer {
    public static final /* enum */ ApplyRiverLayer INSTANCE = new ApplyRiverLayer();
    private static final /* synthetic */ ApplyRiverLayer[] field_16166;

    public static ApplyRiverLayer[] values() {
        return (ApplyRiverLayer[])field_16166.clone();
    }

    public static ApplyRiverLayer valueOf(String string) {
        return Enum.valueOf(ApplyRiverLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int i = sampler1.sample(this.transformX(x), this.transformZ(z));
        int j = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (BiomeLayers.isOcean(i)) {
            return i;
        }
        if (j == 7) {
            if (i == 12) {
                return 11;
            }
            if (i == 14 || i == 15) {
                return 15;
            }
            return j & 0xFF;
        }
        return i;
    }

    private static /* synthetic */ ApplyRiverLayer[] method_36784() {
        return new ApplyRiverLayer[]{INSTANCE};
    }

    static {
        field_16166 = ApplyRiverLayer.method_36784();
    }
}

