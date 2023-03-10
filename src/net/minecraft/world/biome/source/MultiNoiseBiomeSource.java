/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;

public class MultiNoiseBiomeSource
extends BiomeSource {
    public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(instance2 -> instance2.group((App)Codecs.nonEmptyList(RecordCodecBuilder.create(instance -> instance.group((App)MultiNoiseUtil.NoiseHypercube.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).listOf()).xmap(MultiNoiseUtil.Entries::new, MultiNoiseUtil.Entries::getEntries).fieldOf("biomes").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomeEntries)).apply((Applicative)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(Instance.CODEC, CUSTOM_CODEC).xmap(either -> (MultiNoiseBiomeSource)either.map(Instance::getBiomeSource, Function.identity()), multiNoiseBiomeSource -> multiNoiseBiomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right((Object)multiNoiseBiomeSource))).codec();
    private final MultiNoiseUtil.Entries<RegistryEntry<Biome>> biomeEntries;
    private final Optional<Instance> instance;

    private MultiNoiseBiomeSource(MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries) {
        this(entries, Optional.empty());
    }

    MultiNoiseBiomeSource(MultiNoiseUtil.Entries<RegistryEntry<Biome>> biomeEntries, Optional<Instance> instance) {
        super(biomeEntries.getEntries().stream().map(Pair::getSecond));
        this.instance = instance;
        this.biomeEntries = biomeEntries;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return this;
    }

    private Optional<Instance> getInstance() {
        return this.instance;
    }

    public boolean matchesInstance(Preset instance) {
        return this.instance.isPresent() && Objects.equals(this.instance.get().preset(), instance);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return this.getBiomeAtPoint(noise.sample(x, y, z));
    }

    @Debug
    public RegistryEntry<Biome> getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point) {
        return this.biomeEntries.method_39529(point);
    }

    @Override
    public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        int i = BiomeCoords.fromBlock(pos.getX());
        int j = BiomeCoords.fromBlock(pos.getY());
        int k = BiomeCoords.fromBlock(pos.getZ());
        MultiNoiseUtil.NoiseValuePoint noiseValuePoint = noiseSampler.sample(i, j, k);
        float f = MultiNoiseUtil.method_38666(noiseValuePoint.continentalnessNoise());
        float g = MultiNoiseUtil.method_38666(noiseValuePoint.erosionNoise());
        float h = MultiNoiseUtil.method_38666(noiseValuePoint.temperatureNoise());
        float l = MultiNoiseUtil.method_38666(noiseValuePoint.humidityNoise());
        float m = MultiNoiseUtil.method_38666(noiseValuePoint.weirdnessNoise());
        double d = VanillaTerrainParameters.getNormalizedWeirdness(m);
        VanillaBiomeParameters vanillaBiomeParameters = new VanillaBiomeParameters();
        info.add("Biome builder PV: " + VanillaBiomeParameters.getWeirdnessDescription(d) + " C: " + vanillaBiomeParameters.getContinentalnessDescription(f) + " E: " + vanillaBiomeParameters.getErosionDescription(g) + " T: " + vanillaBiomeParameters.getTemperatureDescription(h) + " H: " + vanillaBiomeParameters.getHumidityDescription(l));
    }

    record Instance(Preset preset, Registry<Biome> biomeRegistry) {
        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.flatXmap(id -> Optional.ofNullable(Preset.BY_IDENTIFIER.get(id)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + id))), preset -> DataResult.success((Object)preset.id)).fieldOf("preset").stable().forGetter(Instance::preset), (App)RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(Instance::biomeRegistry)).apply((Applicative)instance, instance.stable(Instance::new)));

        public MultiNoiseBiomeSource getBiomeSource() {
            return this.preset.getBiomeSource(this, true);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Instance.class, "preset;biomes", "preset", "biomeRegistry"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Instance.class, "preset;biomes", "preset", "biomeRegistry"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Instance.class, "preset;biomes", "preset", "biomeRegistry"}, this, object);
        }
    }

    public static class Preset {
        static final Map<Identifier, Preset> BY_IDENTIFIER = Maps.newHashMap();
        public static final Preset NETHER = new Preset(new Identifier("nether"), registry -> new MultiNoiseUtil.Entries(ImmutableList.of((Object)Pair.of((Object)MultiNoiseUtil.createNoiseHypercube(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f), registry.getOrCreateEntry(BiomeKeys.NETHER_WASTES)), (Object)Pair.of((Object)MultiNoiseUtil.createNoiseHypercube(0.0f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f), registry.getOrCreateEntry(BiomeKeys.SOUL_SAND_VALLEY)), (Object)Pair.of((Object)MultiNoiseUtil.createNoiseHypercube(0.4f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f), registry.getOrCreateEntry(BiomeKeys.CRIMSON_FOREST)), (Object)Pair.of((Object)MultiNoiseUtil.createNoiseHypercube(0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.375f), registry.getOrCreateEntry(BiomeKeys.WARPED_FOREST)), (Object)Pair.of((Object)MultiNoiseUtil.createNoiseHypercube(-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.175f), registry.getOrCreateEntry(BiomeKeys.BASALT_DELTAS)))));
        public static final Preset OVERWORLD = new Preset(new Identifier("overworld"), registry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            new VanillaBiomeParameters().writeVanillaBiomeParameters(pair -> builder.add((Object)pair.mapSecond(registry::getOrCreateEntry)));
            return new MultiNoiseUtil.Entries(builder.build());
        });
        final Identifier id;
        private final Function<Registry<Biome>, MultiNoiseUtil.Entries<RegistryEntry<Biome>>> biomeSourceFunction;

        public Preset(Identifier id, Function<Registry<Biome>, MultiNoiseUtil.Entries<RegistryEntry<Biome>>> biomeSourceFunction) {
            this.id = id;
            this.biomeSourceFunction = biomeSourceFunction;
            BY_IDENTIFIER.put(id, this);
        }

        MultiNoiseBiomeSource getBiomeSource(Instance instance, boolean useInstance) {
            MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries = this.biomeSourceFunction.apply(instance.biomeRegistry());
            return new MultiNoiseBiomeSource(entries, useInstance ? Optional.of(instance) : Optional.empty());
        }

        public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, boolean useInstance) {
            return this.getBiomeSource(new Instance(this, biomeRegistry), useInstance);
        }

        public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry) {
            return this.getBiomeSource(biomeRegistry, true);
        }
    }
}

