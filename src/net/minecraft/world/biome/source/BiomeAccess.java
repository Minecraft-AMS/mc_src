/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
    private final Storage storage;
    private final long seed;
    private final BiomeAccessType type;

    public BiomeAccess(Storage storage, long seed, BiomeAccessType type) {
        this.storage = storage;
        this.seed = seed;
        this.type = type;
    }

    public BiomeAccess withSource(BiomeSource source) {
        return new BiomeAccess(source, this.seed, this.type);
    }

    public Biome getBiome(BlockPos pos) {
        return this.type.getBiome(this.seed, pos.getX(), pos.getY(), pos.getZ(), this.storage);
    }

    public static interface Storage {
        public Biome getBiomeForNoiseGen(int var1, int var2, int var3);
    }
}

