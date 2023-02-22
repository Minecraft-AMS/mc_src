/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddEdgeBiomesLayer
extends Enum<AddEdgeBiomesLayer>
implements CrossSamplingLayer {
    public static final /* enum */ AddEdgeBiomesLayer INSTANCE = new AddEdgeBiomesLayer();
    private static final IntSet SNOWY_IDS;
    private static final IntSet FOREST_IDS;
    private static final /* synthetic */ AddEdgeBiomesLayer[] field_16179;

    public static AddEdgeBiomesLayer[] values() {
        return (AddEdgeBiomesLayer[])field_16179.clone();
    }

    public static AddEdgeBiomesLayer valueOf(String string) {
        return Enum.valueOf(AddEdgeBiomesLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (center == 14) {
            if (BiomeLayers.isShallowOcean(n) || BiomeLayers.isShallowOcean(e) || BiomeLayers.isShallowOcean(s) || BiomeLayers.isShallowOcean(w)) {
                return 15;
            }
        } else if (FOREST_IDS.contains(center)) {
            if (!(AddEdgeBiomesLayer.isWooded(n) && AddEdgeBiomesLayer.isWooded(e) && AddEdgeBiomesLayer.isWooded(s) && AddEdgeBiomesLayer.isWooded(w))) {
                return 23;
            }
            if (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w)) {
                return 16;
            }
        } else if (center == 3 || center == 34 || center == 20) {
            if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                return 25;
            }
        } else if (SNOWY_IDS.contains(center)) {
            if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                return 26;
            }
        } else if (center == 37 || center == 38) {
            if (!(BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w) || this.isBadlands(n) && this.isBadlands(e) && this.isBadlands(s) && this.isBadlands(w))) {
                return 2;
            }
        } else if (!BiomeLayers.isOcean(center) && center != 7 && center != 6 && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
            return 16;
        }
        return center;
    }

    private static boolean isWooded(int id) {
        return FOREST_IDS.contains(id) || id == 4 || id == 5 || BiomeLayers.isOcean(id);
    }

    private boolean isBadlands(int id) {
        return id == 37 || id == 38 || id == 39 || id == 165 || id == 166 || id == 167;
    }

    private static /* synthetic */ AddEdgeBiomesLayer[] method_36785() {
        return new AddEdgeBiomesLayer[]{INSTANCE};
    }

    static {
        field_16179 = AddEdgeBiomesLayer.method_36785();
        SNOWY_IDS = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
        FOREST_IDS = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});
    }
}

