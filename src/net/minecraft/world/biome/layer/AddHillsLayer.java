/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AddHillsLayer
extends Enum<AddHillsLayer>
implements MergingLayer,
NorthWestCoordinateTransformer {
    public static final /* enum */ AddHillsLayer INSTANCE = new AddHillsLayer();
    private static final Logger LOGGER;
    private static final Int2IntMap MUTATED_BIOMES;
    private static final /* synthetic */ AddHillsLayer[] field_16141;

    public static AddHillsLayer[] values() {
        return (AddHillsLayer[])field_16141.clone();
    }

    public static AddHillsLayer valueOf(String string) {
        return Enum.valueOf(AddHillsLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int i = sampler1.sample(this.transformX(x + 1), this.transformZ(z + 1));
        int j = sampler2.sample(this.transformX(x + 1), this.transformZ(z + 1));
        if (i > 255) {
            LOGGER.debug("old! {}", (Object)i);
        }
        int k = (j - 2) % 29;
        if (!BiomeLayers.isShallowOcean(i) && j >= 2 && k == 1) {
            return MUTATED_BIOMES.getOrDefault(i, i);
        }
        if (context.nextInt(3) == 0 || k == 0) {
            int l = i;
            if (i == 2) {
                l = 17;
            } else if (i == 4) {
                l = 18;
            } else if (i == 27) {
                l = 28;
            } else if (i == 29) {
                l = 1;
            } else if (i == 5) {
                l = 19;
            } else if (i == 32) {
                l = 33;
            } else if (i == 30) {
                l = 31;
            } else if (i == 1) {
                l = context.nextInt(3) == 0 ? 18 : 4;
            } else if (i == 12) {
                l = 13;
            } else if (i == 21) {
                l = 22;
            } else if (i == 168) {
                l = 169;
            } else if (i == 0) {
                l = 24;
            } else if (i == 45) {
                l = 48;
            } else if (i == 46) {
                l = 49;
            } else if (i == 10) {
                l = 50;
            } else if (i == 3) {
                l = 34;
            } else if (i == 35) {
                l = 36;
            } else if (BiomeLayers.areSimilar(i, 38)) {
                l = 37;
            } else if ((i == 24 || i == 48 || i == 49 || i == 50) && context.nextInt(3) == 0) {
                int n = l = context.nextInt(2) == 0 ? 1 : 4;
            }
            if (k == 0 && l != i) {
                l = MUTATED_BIOMES.getOrDefault(l, i);
            }
            if (l != i) {
                int m = 0;
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 0)), i)) {
                    ++m;
                }
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 2), this.transformZ(z + 1)), i)) {
                    ++m;
                }
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 0), this.transformZ(z + 1)), i)) {
                    ++m;
                }
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 2)), i)) {
                    ++m;
                }
                if (m >= 3) {
                    return l;
                }
            }
        }
        return i;
    }

    private static /* synthetic */ AddHillsLayer[] method_36780() {
        return new AddHillsLayer[]{INSTANCE};
    }

    static {
        field_16141 = AddHillsLayer.method_36780();
        LOGGER = LogManager.getLogger();
        MUTATED_BIOMES = (Int2IntMap)Util.make(new Int2IntOpenHashMap(), map -> {
            map.put(1, 129);
            map.put(2, 130);
            map.put(3, 131);
            map.put(4, 132);
            map.put(5, 133);
            map.put(6, 134);
            map.put(12, 140);
            map.put(21, 149);
            map.put(23, 151);
            map.put(27, 155);
            map.put(28, 156);
            map.put(29, 157);
            map.put(30, 158);
            map.put(32, 160);
            map.put(33, 161);
            map.put(34, 162);
            map.put(35, 163);
            map.put(36, 164);
            map.put(37, 165);
            map.put(38, 166);
            map.put(39, 167);
        });
    }
}

