/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddMushroomIslandLayer
extends Enum<AddMushroomIslandLayer>
implements DiagonalCrossSamplingLayer {
    public static final /* enum */ AddMushroomIslandLayer INSTANCE = new AddMushroomIslandLayer();
    private static final /* synthetic */ AddMushroomIslandLayer[] field_16056;

    public static AddMushroomIslandLayer[] values() {
        return (AddMushroomIslandLayer[])field_16056.clone();
    }

    public static AddMushroomIslandLayer valueOf(String string) {
        return Enum.valueOf(AddMushroomIslandLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center) {
        if (BiomeLayers.isShallowOcean(center) && BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(se) && context.nextInt(100) == 0) {
            return 14;
        }
        return center;
    }

    private static /* synthetic */ AddMushroomIslandLayer[] method_36771() {
        return new AddMushroomIslandLayer[]{INSTANCE};
    }

    static {
        field_16056 = AddMushroomIslandLayer.method_36771();
    }
}

