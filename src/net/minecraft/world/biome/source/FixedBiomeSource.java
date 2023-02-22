/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource {
    private final Biome biome;

    public FixedBiomeSource(FixedBiomeSourceConfig config) {
        this.biome = config.getBiome();
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biome;
    }

    @Override
    public Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl) {
        Object[] biomes = new Biome[width * height];
        Arrays.fill(biomes, 0, width * height, this.biome);
        return biomes;
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random) {
        if (biomes.contains(this.biome)) {
            return new BlockPos(x - radius + random.nextInt(radius * 2 + 1), 0, z - radius + random.nextInt(radius * 2 + 1));
        }
        return null;
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, this.biome::hasStructureFeature);
    }

    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            this.topMaterials.add(this.biome.getSurfaceConfig().getTopMaterial());
        }
        return this.topMaterials;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int z, int radius) {
        return Sets.newHashSet((Object[])new Biome[]{this.biome});
    }
}

