/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 */
package net.minecraft.world.biome.source;

import com.google.common.hash.Hashing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
    static final int CHUNK_CENTER_OFFSET = BiomeCoords.fromBlock(8);
    private final Storage storage;
    private final long seed;
    private final BiomeAccessType type;

    public BiomeAccess(Storage storage, long seed, BiomeAccessType type) {
        this.storage = storage;
        this.seed = seed;
        this.type = type;
    }

    public static long hashSeed(long seed) {
        return Hashing.sha256().hashLong(seed).asLong();
    }

    public BiomeAccess withSource(BiomeSource source) {
        return new BiomeAccess(source, this.seed, this.type);
    }

    public Biome getBiome(BlockPos pos) {
        return this.type.getBiome(this.seed, pos.getX(), pos.getY(), pos.getZ(), this.storage);
    }

    public Biome getBiomeForNoiseGen(double x, double y, double z) {
        int i = BiomeCoords.fromBlock(MathHelper.floor(x));
        int j = BiomeCoords.fromBlock(MathHelper.floor(y));
        int k = BiomeCoords.fromBlock(MathHelper.floor(z));
        return this.getBiomeForNoiseGen(i, j, k);
    }

    public Biome getBiomeForNoiseGen(BlockPos pos) {
        int i = BiomeCoords.fromBlock(pos.getX());
        int j = BiomeCoords.fromBlock(pos.getY());
        int k = BiomeCoords.fromBlock(pos.getZ());
        return this.getBiomeForNoiseGen(i, j, k);
    }

    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.storage.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    public Biome getBiomeForNoiseGen(ChunkPos chunkPos) {
        return this.storage.getBiomeForNoiseGen(chunkPos);
    }

    public static interface Storage {
        public Biome getBiomeForNoiseGen(int var1, int var2, int var3);

        default public Biome getBiomeForNoiseGen(ChunkPos chunkPos) {
            return this.getBiomeForNoiseGen(BiomeCoords.fromChunk(chunkPos.x) + CHUNK_CENTER_OFFSET, 0, BiomeCoords.fromChunk(chunkPos.z) + CHUNK_CENTER_OFFSET);
        }
    }
}

