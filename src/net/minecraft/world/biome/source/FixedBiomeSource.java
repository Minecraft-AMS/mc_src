/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource
implements BiomeAccess.Storage {
    public static final Codec<FixedBiomeSource> CODEC = Biome.REGISTRY_CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome).stable().codec();
    private final RegistryEntry<Biome> biome;

    public FixedBiomeSource(RegistryEntry<Biome> registryEntry) {
        super((List<RegistryEntry<Biome>>)ImmutableList.of(registryEntry));
        this.biome = registryEntry;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return this.biome;
    }

    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biome;
    }

    @Override
    @Nullable
    public Pair<BlockPos, RegistryEntry<Biome>> locateBiome(int x, int y, int z, int radius, int blockCheckInterval, Predicate<RegistryEntry<Biome>> predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        if (predicate.test(this.biome)) {
            if (bl) {
                return Pair.of((Object)new BlockPos(x, y, z), this.biome);
            }
            return Pair.of((Object)new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1)), this.biome);
        }
        return null;
    }

    @Override
    public Set<RegistryEntry<Biome>> getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
        return Sets.newHashSet(Set.of(this.biome));
    }
}

