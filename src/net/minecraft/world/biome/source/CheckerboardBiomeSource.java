/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.biome.source;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class CheckerboardBiomeSource
extends BiomeSource {
    public static final Codec<CheckerboardBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Biome.field_26750.fieldOf("biomes").forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.biomeArray), (App)Codec.intRange((int)0, (int)62).fieldOf("scale").orElse((Object)2).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.scale)).apply((Applicative)instance, CheckerboardBiomeSource::new));
    private final List<Supplier<Biome>> biomeArray;
    private final int gridSize;
    private final int scale;

    public CheckerboardBiomeSource(List<Supplier<Biome>> biomeArray, int size) {
        super(biomeArray.stream());
        this.biomeArray = biomeArray;
        this.gridSize = size + 2;
        this.scale = size;
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
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeArray.get(Math.floorMod((biomeX >> this.gridSize) + (biomeZ >> this.gridSize), this.biomeArray.size())).get();
    }
}

