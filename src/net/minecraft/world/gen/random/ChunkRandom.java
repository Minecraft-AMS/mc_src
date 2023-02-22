/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.random;

import java.util.Random;
import java.util.function.LongFunction;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

public class ChunkRandom
extends Random
implements AbstractRandom {
    private final AbstractRandom baseRandom;
    private int sampleCount;

    public ChunkRandom(AbstractRandom baseRandom) {
        super(0L);
        this.baseRandom = baseRandom;
    }

    public int getSampleCount() {
        return this.sampleCount;
    }

    @Override
    public AbstractRandom derive() {
        return this.baseRandom.derive();
    }

    @Override
    public RandomDeriver createRandomDeriver() {
        return this.baseRandom.createRandomDeriver();
    }

    @Override
    public int next(int count) {
        ++this.sampleCount;
        AbstractRandom abstractRandom = this.baseRandom;
        if (abstractRandom instanceof AtomicSimpleRandom) {
            AtomicSimpleRandom atomicSimpleRandom = (AtomicSimpleRandom)abstractRandom;
            return atomicSimpleRandom.next(count);
        }
        return (int)(this.baseRandom.nextLong() >>> 64 - count);
    }

    @Override
    public synchronized void setSeed(long l) {
        if (this.baseRandom == null) {
            return;
        }
        this.baseRandom.setSeed(l);
    }

    public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong() | 1L;
        long m = this.nextLong() | 1L;
        long n = (long)blockX * l + (long)blockZ * m ^ worldSeed;
        this.setSeed(n);
        return n;
    }

    public void setDecoratorSeed(long populationSeed, int index, int step) {
        long l = populationSeed + (long)index + (long)(10000 * step);
        this.setSeed(l);
    }

    public void setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong();
        long m = this.nextLong();
        long n = (long)chunkX * l ^ (long)chunkZ * m ^ worldSeed;
        this.setSeed(n);
    }

    public void setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
        long l = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + (long)salt;
        this.setSeed(l);
    }

    public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
        return new Random(worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler);
    }

    public static final class RandomProvider
    extends Enum<RandomProvider> {
        public static final /* enum */ RandomProvider LEGACY = new RandomProvider(AtomicSimpleRandom::new);
        public static final /* enum */ RandomProvider XOROSHIRO = new RandomProvider(Xoroshiro128PlusPlusRandom::new);
        private final LongFunction<AbstractRandom> provider;
        private static final /* synthetic */ RandomProvider[] field_35145;

        public static RandomProvider[] values() {
            return (RandomProvider[])field_35145.clone();
        }

        public static RandomProvider valueOf(String string) {
            return Enum.valueOf(RandomProvider.class, string);
        }

        private RandomProvider(LongFunction<AbstractRandom> provider) {
            this.provider = provider;
        }

        public AbstractRandom create(long seed) {
            return this.provider.apply(seed);
        }

        private static /* synthetic */ RandomProvider[] method_39005() {
            return new RandomProvider[]{LEGACY, XOROSHIRO};
        }

        static {
            field_35145 = RandomProvider.method_39005();
        }
    }
}

