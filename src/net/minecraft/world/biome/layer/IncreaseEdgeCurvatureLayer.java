/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class IncreaseEdgeCurvatureLayer
extends Enum<IncreaseEdgeCurvatureLayer>
implements DiagonalCrossSamplingLayer {
    public static final /* enum */ IncreaseEdgeCurvatureLayer INSTANCE = new IncreaseEdgeCurvatureLayer();
    private static final /* synthetic */ IncreaseEdgeCurvatureLayer[] field_16057;

    public static IncreaseEdgeCurvatureLayer[] values() {
        return (IncreaseEdgeCurvatureLayer[])field_16057.clone();
    }

    public static IncreaseEdgeCurvatureLayer valueOf(String string) {
        return Enum.valueOf(IncreaseEdgeCurvatureLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center) {
        if (!(!BiomeLayers.isShallowOcean(center) || BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(se))) {
            int i = 1;
            int j = 1;
            if (!BiomeLayers.isShallowOcean(nw) && context.nextInt(i++) == 0) {
                j = nw;
            }
            if (!BiomeLayers.isShallowOcean(ne) && context.nextInt(i++) == 0) {
                j = ne;
            }
            if (!BiomeLayers.isShallowOcean(sw) && context.nextInt(i++) == 0) {
                j = sw;
            }
            if (!BiomeLayers.isShallowOcean(se) && context.nextInt(i++) == 0) {
                j = se;
            }
            if (context.nextInt(3) == 0) {
                return j;
            }
            return j == 4 ? 4 : center;
        }
        if (!BiomeLayers.isShallowOcean(center) && (BiomeLayers.isShallowOcean(nw) || BiomeLayers.isShallowOcean(sw) || BiomeLayers.isShallowOcean(ne) || BiomeLayers.isShallowOcean(se)) && context.nextInt(5) == 0) {
            if (BiomeLayers.isShallowOcean(nw)) {
                return center == 4 ? 4 : nw;
            }
            if (BiomeLayers.isShallowOcean(sw)) {
                return center == 4 ? 4 : sw;
            }
            if (BiomeLayers.isShallowOcean(ne)) {
                return center == 4 ? 4 : ne;
            }
            if (BiomeLayers.isShallowOcean(se)) {
                return center == 4 ? 4 : se;
            }
        }
        return center;
    }

    private static /* synthetic */ IncreaseEdgeCurvatureLayer[] method_36770() {
        return new IncreaseEdgeCurvatureLayer[]{INSTANCE};
    }

    static {
        field_16057 = IncreaseEdgeCurvatureLayer.method_36770();
    }
}

