/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddColdClimatesLayer
extends Enum<AddColdClimatesLayer>
implements SouthEastSamplingLayer {
    public static final /* enum */ AddColdClimatesLayer INSTANCE = new AddColdClimatesLayer();
    private static final /* synthetic */ AddColdClimatesLayer[] field_16060;

    public static AddColdClimatesLayer[] values() {
        return (AddColdClimatesLayer[])field_16060.clone();
    }

    public static AddColdClimatesLayer valueOf(String string) {
        return Enum.valueOf(AddColdClimatesLayer.class, string);
    }

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

    private static /* synthetic */ AddColdClimatesLayer[] method_36772() {
        return new AddColdClimatesLayer[]{INSTANCE};
    }

    static {
        field_16060 = AddColdClimatesLayer.method_36772();
    }
}

