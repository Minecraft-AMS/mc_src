/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;

public class CheckerboardBiomeSource
extends BiomeSource {
    private final Biome[] biomeArray;
    private final int gridSize;

    public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig config) {
        super((Set<Biome>)ImmutableSet.copyOf((Object[])config.getBiomes()));
        this.biomeArray = config.getBiomes();
        this.gridSize = config.getSize() + 2;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeArray[Math.abs(((biomeX >> this.gridSize) + (biomeZ >> this.gridSize)) % this.biomeArray.length)];
    }
}

