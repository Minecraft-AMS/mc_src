/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeSource
implements BiomeAccess.Storage {
    private static final List<Biome> SPAWN_BIOMES = Lists.newArrayList((Object[])new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
    protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.newHashMap();
    protected final Set<BlockState> topMaterials = Sets.newHashSet();
    protected final Set<Biome> biomes;

    protected BiomeSource(Set<Biome> biomes) {
        this.biomes = biomes;
    }

    public List<Biome> getSpawnBiomes() {
        return SPAWN_BIOMES;
    }

    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        int i = x - radius >> 2;
        int j = y - radius >> 2;
        int k = z - radius >> 2;
        int l = x + radius >> 2;
        int m = y + radius >> 2;
        int n = z + radius >> 2;
        int o = l - i + 1;
        int p = m - j + 1;
        int q = n - k + 1;
        HashSet set = Sets.newHashSet();
        for (int r = 0; r < q; ++r) {
            for (int s = 0; s < o; ++s) {
                for (int t = 0; t < p; ++t) {
                    int u = i + s;
                    int v = j + t;
                    int w = k + r;
                    set.add(this.getBiomeForNoiseGen(u, v, w));
                }
            }
        }
        return set;
    }

    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, List<Biome> list, Random random) {
        int i = x - radius >> 2;
        int j = z - radius >> 2;
        int k = x + radius >> 2;
        int l = z + radius >> 2;
        int m = k - i + 1;
        int n = l - j + 1;
        int o = y >> 2;
        BlockPos blockPos = null;
        int p = 0;
        for (int q = 0; q < n; ++q) {
            for (int r = 0; r < m; ++r) {
                int s = i + r;
                int t = j + q;
                if (!list.contains(this.getBiomeForNoiseGen(s, o, t))) continue;
                if (blockPos == null || random.nextInt(p + 1) == 0) {
                    blockPos = new BlockPos(s << 2, y, t << 2);
                }
                ++p;
            }
        }
        return blockPos;
    }

    public float getNoiseRange(int i, int j) {
        return 0.0f;
    }

    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, structureFeature -> this.biomes.stream().anyMatch(biome -> biome.hasStructureFeature(structureFeature)));
    }

    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome biome : this.biomes) {
                this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }
}

