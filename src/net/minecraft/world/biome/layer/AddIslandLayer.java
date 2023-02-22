/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddIslandLayer
extends Enum<AddIslandLayer>
implements CrossSamplingLayer {
    public static final /* enum */ AddIslandLayer INSTANCE = new AddIslandLayer();
    private static final /* synthetic */ AddIslandLayer[] field_16159;

    public static AddIslandLayer[] values() {
        return (AddIslandLayer[])field_16159.clone();
    }

    public static AddIslandLayer valueOf(String string) {
        return Enum.valueOf(AddIslandLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (BiomeLayers.isShallowOcean(center) && BiomeLayers.isShallowOcean(n) && BiomeLayers.isShallowOcean(e) && BiomeLayers.isShallowOcean(w) && BiomeLayers.isShallowOcean(s) && context.nextInt(2) == 0) {
            return 1;
        }
        return center;
    }

    private static /* synthetic */ AddIslandLayer[] method_36781() {
        return new AddIslandLayer[]{INSTANCE};
    }

    static {
        field_16159 = AddIslandLayer.method_36781();
    }
}

