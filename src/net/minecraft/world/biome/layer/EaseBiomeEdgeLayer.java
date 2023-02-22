/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class EaseBiomeEdgeLayer
extends Enum<EaseBiomeEdgeLayer>
implements CrossSamplingLayer {
    public static final /* enum */ EaseBiomeEdgeLayer INSTANCE = new EaseBiomeEdgeLayer();
    private static final /* synthetic */ EaseBiomeEdgeLayer[] field_16093;

    public static EaseBiomeEdgeLayer[] values() {
        return (EaseBiomeEdgeLayer[])field_16093.clone();
    }

    public static EaseBiomeEdgeLayer valueOf(String string) {
        return Enum.valueOf(EaseBiomeEdgeLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int[] is = new int[1];
        if (this.isMountainBiome(is, center) || this.areEdgesSimilar(is, n, e, s, w, center, 38, 37) || this.areEdgesSimilar(is, n, e, s, w, center, 39, 37) || this.areEdgesSimilar(is, n, e, s, w, center, 32, 5)) {
            return is[0];
        }
        if (center == 2 && (n == 12 || e == 12 || w == 12 || s == 12)) {
            return 34;
        }
        if (center == 6) {
            if (n == 2 || e == 2 || w == 2 || s == 2 || n == 30 || e == 30 || w == 30 || s == 30 || n == 12 || e == 12 || w == 12 || s == 12) {
                return 1;
            }
            if (n == 21 || s == 21 || e == 21 || w == 21 || n == 168 || s == 168 || e == 168 || w == 168) {
                return 23;
            }
        }
        return center;
    }

    private boolean isMountainBiome(int[] ids, int id) {
        if (!BiomeLayers.areSimilar(id, 3)) {
            return false;
        }
        ids[0] = id;
        return true;
    }

    private boolean areEdgesSimilar(int[] ids, int n, int e, int s, int w, int center, int id1, int id2) {
        if (center != id1) {
            return false;
        }
        ids[0] = BiomeLayers.areSimilar(n, id1) && BiomeLayers.areSimilar(e, id1) && BiomeLayers.areSimilar(w, id1) && BiomeLayers.areSimilar(s, id1) ? center : id2;
        return true;
    }

    private static /* synthetic */ EaseBiomeEdgeLayer[] method_36773() {
        return new EaseBiomeEdgeLayer[]{INSTANCE};
    }

    static {
        field_16093 = EaseBiomeEdgeLayer.method_36773();
    }
}

