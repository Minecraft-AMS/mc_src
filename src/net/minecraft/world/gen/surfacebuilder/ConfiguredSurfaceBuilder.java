/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
    public static final Codec<ConfiguredSurfaceBuilder<?>> CODEC = Registry.SURFACE_BUILDER.dispatch(configuredSurfaceBuilder -> configuredSurfaceBuilder.surfaceBuilder, SurfaceBuilder::getCodec);
    public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, CODEC);
    public final SurfaceBuilder<SC> surfaceBuilder;
    public final SC config;

    public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC config) {
        this.surfaceBuilder = surfaceBuilder;
        this.config = config;
    }

    public void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed) {
        this.surfaceBuilder.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, seed, this.config);
    }

    public void initSeed(long seed) {
        this.surfaceBuilder.initSeed(seed);
    }

    public SC getConfig() {
        return this.config;
    }
}

