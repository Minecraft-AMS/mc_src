/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public record ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>>(F feature, FC config) {
    public static final Codec<ConfiguredFeature<?, ?>> CODEC = Registry.FEATURE.getCodec().dispatch(configuredFeature -> configuredFeature.feature, Feature::getCodec);
    public static final Codec<RegistryEntry<ConfiguredFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_FEATURE_KEY, CODEC);
    public static final Codec<RegistryEntryList<ConfiguredFeature<?, ?>>> LIST_CODEC = RegistryCodecs.entryList(Registry.CONFIGURED_FEATURE_KEY, CODEC);

    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos origin) {
        return ((Feature)this.feature).generateIfValid(this.config, world, chunkGenerator, random, origin);
    }

    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return Stream.concat(Stream.of(this), this.config.getDecoratedFeatures());
    }

    @Override
    public String toString() {
        return "Configured: " + this.feature + ": " + this.config;
    }
}

