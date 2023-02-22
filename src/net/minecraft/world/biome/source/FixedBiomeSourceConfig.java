/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceConfig;

public class FixedBiomeSourceConfig
implements BiomeSourceConfig {
    private Biome biome = Biomes.PLAINS;

    public FixedBiomeSourceConfig setBiome(Biome biome) {
        this.biome = biome;
        return this;
    }

    public Biome getBiome() {
        return this.biome;
    }
}

