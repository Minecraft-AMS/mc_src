/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource {
    public static final Codec<FixedBiomeSource> CODEC = Biome.REGISTRY_CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome).stable().codec();
    private final Supplier<Biome> biome;

    public FixedBiomeSource(Biome biome) {
        this(() -> biome);
    }

    public FixedBiomeSource(Supplier<Biome> biome) {
        super((List<Biome>)ImmutableList.of((Object)biome.get()));
        this.biome = biome;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biome.get();
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl) {
        if (predicate.test(this.biome.get())) {
            if (bl) {
                return new BlockPos(x, y, z);
            }
            return new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        return Sets.newHashSet((Object[])new Biome[]{this.biome.get()});
    }
}

