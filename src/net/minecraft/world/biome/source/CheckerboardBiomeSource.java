/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class CheckerboardBiomeSource
extends BiomeSource {
    private final Biome[] biomeArray;
    private final int gridSize;

    public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig config) {
        this.biomeArray = config.getBiomes();
        this.gridSize = config.getSize() + 4;
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biomeArray[Math.abs(((x >> this.gridSize) + (z >> this.gridSize)) % this.biomeArray.length)];
    }

    @Override
    public Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl) {
        Biome[] biomes = new Biome[width * height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Biome biome;
                int k = Math.abs(((x + i >> this.gridSize) + (z + j >> this.gridSize)) % this.biomeArray.length);
                biomes[i * width + j] = biome = this.biomeArray[k];
            }
        }
        return biomes;
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random) {
        return null;
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, structureFeature -> {
            for (Biome biome : this.biomeArray) {
                if (!biome.hasStructureFeature(structureFeature)) continue;
                return true;
            }
            return false;
        });
    }

    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome biome : this.biomeArray) {
                this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int z, int radius) {
        return Sets.newHashSet((Object[])this.biomeArray);
    }
}

