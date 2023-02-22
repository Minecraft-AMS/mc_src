/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import java.util.Random;

public class ChunkRandom
extends Random {
    private int sampleCount;

    public ChunkRandom() {
    }

    public ChunkRandom(long seed) {
        super(seed);
    }

    public void consume(int count) {
        for (int i = 0; i < count; ++i) {
            this.next(1);
        }
    }

    @Override
    protected int next(int count) {
        ++this.sampleCount;
        return super.next(count);
    }

    public long setTerrainSeed(int chunkX, int chunkZ) {
        long l = (long)chunkX * 341873128712L + (long)chunkZ * 132897987541L;
        this.setSeed(l);
        return l;
    }

    public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong() | 1L;
        long m = this.nextLong() | 1L;
        long n = (long)blockX * l + (long)blockZ * m ^ worldSeed;
        this.setSeed(n);
        return n;
    }

    public long setDecoratorSeed(long populationSeed, int index, int step) {
        long l = populationSeed + (long)index + (long)(10000 * step);
        this.setSeed(l);
        return l;
    }

    public long setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong();
        long m = this.nextLong();
        long n = (long)chunkX * l ^ (long)chunkZ * m ^ worldSeed;
        this.setSeed(n);
        return n;
    }

    public long setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
        long l = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + (long)salt;
        this.setSeed(l);
        return l;
    }

    public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
        return new Random(worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler);
    }
}

