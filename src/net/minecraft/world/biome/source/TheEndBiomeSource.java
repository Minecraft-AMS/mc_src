/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(theEndBiomeSource -> null), (App)Codec.LONG.fieldOf("seed").stable().forGetter(theEndBiomeSource -> theEndBiomeSource.seed)).apply((Applicative)instance, instance.stable(TheEndBiomeSource::new)));
    private static final float field_30985 = -0.9f;
    public static final int field_30984 = 64;
    private static final long field_30986 = 4096L;
    private final SimplexNoiseSampler noise;
    private final long seed;
    private final RegistryEntry<Biome> centerBiome;
    private final RegistryEntry<Biome> highlandsBiome;
    private final RegistryEntry<Biome> midlandsBiome;
    private final RegistryEntry<Biome> smallIslandsBiome;
    private final RegistryEntry<Biome> barrensBiome;

    public TheEndBiomeSource(Registry<Biome> biomeRegistry, long seed) {
        this(seed, biomeRegistry.getOrCreateEntry(BiomeKeys.THE_END), biomeRegistry.getOrCreateEntry(BiomeKeys.END_HIGHLANDS), biomeRegistry.getOrCreateEntry(BiomeKeys.END_MIDLANDS), biomeRegistry.getOrCreateEntry(BiomeKeys.SMALL_END_ISLANDS), biomeRegistry.getOrCreateEntry(BiomeKeys.END_BARRENS));
    }

    private TheEndBiomeSource(long l, RegistryEntry<Biome> registryEntry, RegistryEntry<Biome> registryEntry2, RegistryEntry<Biome> registryEntry3, RegistryEntry<Biome> registryEntry4, RegistryEntry<Biome> registryEntry5) {
        super((List<RegistryEntry<Biome>>)ImmutableList.of(registryEntry, registryEntry2, registryEntry3, registryEntry4, registryEntry5));
        this.seed = l;
        this.centerBiome = registryEntry;
        this.highlandsBiome = registryEntry2;
        this.midlandsBiome = registryEntry3;
        this.smallIslandsBiome = registryEntry4;
        this.barrensBiome = registryEntry5;
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(l));
        chunkRandom.skip(17292);
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new TheEndBiomeSource(seed, this.centerBiome, this.highlandsBiome, this.midlandsBiome, this.smallIslandsBiome, this.barrensBiome);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int i = x >> 2;
        int j = z >> 2;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return this.centerBiome;
        }
        float f = TheEndBiomeSource.getNoiseAt(this.noise, i * 2 + 1, j * 2 + 1);
        if (f > 40.0f) {
            return this.highlandsBiome;
        }
        if (f >= 0.0f) {
            return this.midlandsBiome;
        }
        if (f < -20.0f) {
            return this.smallIslandsBiome;
        }
        return this.barrensBiome;
    }

    public boolean matches(long seed) {
        return this.seed == seed;
    }

    public static float getNoiseAt(SimplexNoiseSampler simplexNoiseSampler, int i, int j) {
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
                if (q * q + r * r <= 4096L || !(simplexNoiseSampler.sample(q, r) < (double)-0.9f)) continue;
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
}

