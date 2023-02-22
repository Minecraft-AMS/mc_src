/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSourceConfig;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class TheEndBiomeSource
extends BiomeSource {
    private final SimplexNoiseSampler noise;
    private final ChunkRandom random;
    private final Biome[] biomes = new Biome[]{Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS};

    public TheEndBiomeSource(TheEndBiomeSourceConfig config) {
        this.random = new ChunkRandom(config.getSeed());
        this.random.consume(17292);
        this.noise = new SimplexNoiseSampler(this.random);
    }

    @Override
    public Biome getBiome(int x, int z) {
        int i = x >> 4;
        int j = z >> 4;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return Biomes.THE_END;
        }
        float f = this.method_8757(i * 2 + 1, j * 2 + 1);
        if (f > 40.0f) {
            return Biomes.END_HIGHLANDS;
        }
        if (f >= 0.0f) {
            return Biomes.END_MIDLANDS;
        }
        if (f < -20.0f) {
            return Biomes.SMALL_END_ISLANDS;
        }
        return Biomes.END_BARRENS;
    }

    @Override
    public Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl) {
        Biome[] biomes = new Biome[width * height];
        Long2ObjectOpenHashMap long2ObjectMap = new Long2ObjectOpenHashMap();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int k = i + x;
                int l = j + z;
                long m = ChunkPos.toLong(k, l);
                Biome biome = (Biome)long2ObjectMap.get(m);
                if (biome == null) {
                    biome = this.getBiome(k, l);
                    long2ObjectMap.put(m, (Object)biome);
                }
                biomes[i + j * width] = biome;
            }
        }
        return biomes;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int z, int radius) {
        int i = x - radius >> 2;
        int j = z - radius >> 2;
        int k = x + radius >> 2;
        int l = z + radius >> 2;
        int m = k - i + 1;
        int n = l - j + 1;
        return Sets.newHashSet((Object[])this.sampleBiomes(i, j, m, n));
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random) {
        int i = x - radius >> 2;
        int j = z - radius >> 2;
        int k = x + radius >> 2;
        int l = z + radius >> 2;
        int m = k - i + 1;
        int n = l - j + 1;
        Biome[] biomes2 = this.sampleBiomes(i, j, m, n);
        BlockPos blockPos = null;
        int o = 0;
        for (int p = 0; p < m * n; ++p) {
            int q = i + p % m << 2;
            int r = j + p / m << 2;
            if (!biomes.contains(biomes2[p])) continue;
            if (blockPos == null || random.nextInt(o + 1) == 0) {
                blockPos = new BlockPos(q, 0, r);
            }
            ++o;
        }
        return blockPos;
    }

    @Override
    public float method_8757(int i, int j) {
        int k = i / 2;
        int l = j / 2;
        int m = i % 2;
        int n = j % 2;
        float f = 100.0f - MathHelper.sqrt(i * i + j * j) * 8.0f;
        f = MathHelper.clamp(f, -100.0f, 80.0f);
        for (int o = -12; o <= 12; ++o) {
            for (int p = -12; p <= 12; ++p) {
                long q = k + o;
                long r = l + p;
                if (q * q + r * r <= 4096L || !(this.noise.sample(q, r) < (double)-0.9f)) continue;
                float g = (MathHelper.abs(q) * 3439.0f + MathHelper.abs(r) * 147.0f) % 13.0f + 9.0f;
                float h = m - o * 2;
                float s = n - p * 2;
                float t = 100.0f - MathHelper.sqrt(h * h + s * s) * g;
                t = MathHelper.clamp(t, -100.0f, 80.0f);
                f = Math.max(f, t);
            }
        }
        return f;
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, structureFeature -> {
            for (Biome biome : this.biomes) {
                if (!biome.hasStructureFeature(structureFeature)) continue;
                return true;
            }
            return false;
        });
    }

    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome biome : this.biomes) {
                this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }
}

