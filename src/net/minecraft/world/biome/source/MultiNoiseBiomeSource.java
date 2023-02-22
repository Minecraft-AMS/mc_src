/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private static final NoiseParameters DEFAULT_NOISE_PARAMETERS = new NoiseParameters(-7, (List<Double>)ImmutableList.of((Object)1.0, (Object)1.0));
    public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(instance2 -> instance2.group((App)Codec.LONG.fieldOf("seed").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed), (App)RecordCodecBuilder.create(instance -> instance.group((App)Biome.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).listOf().fieldOf("biomes").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints), (App)NoiseParameters.CODEC.fieldOf("temperature_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.temperatureNoiseParameters), (App)NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.humidityNoiseParameters), (App)NoiseParameters.CODEC.fieldOf("altitude_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.altitudeNoiseParameters), (App)NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.weirdnessNoiseParameters)).apply((Applicative)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(Instance.CODEC, CUSTOM_CODEC).xmap(either -> (MultiNoiseBiomeSource)either.map(Instance::getBiomeSource, Function.identity()), multiNoiseBiomeSource -> multiNoiseBiomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right((Object)multiNoiseBiomeSource))).codec();
    private final NoiseParameters temperatureNoiseParameters;
    private final NoiseParameters humidityNoiseParameters;
    private final NoiseParameters altitudeNoiseParameters;
    private final NoiseParameters weirdnessNoiseParameters;
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
    private final boolean threeDimensionalSampling;
    private final long seed;
    private final Optional<Pair<Registry<Biome>, Preset>> instance;

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, Optional<Pair<Registry<Biome>, Preset>> instance) {
        this(seed, biomePoints, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, instance);
    }

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, NoiseParameters temperatureNoiseParameters, NoiseParameters humidityNoiseParameters, NoiseParameters altitudeNoiseParameters, NoiseParameters weirdnessNoiseParameters) {
        this(seed, biomePoints, temperatureNoiseParameters, humidityNoiseParameters, altitudeNoiseParameters, weirdnessNoiseParameters, Optional.empty());
    }

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, NoiseParameters temperatureNoiseParameters, NoiseParameters humidityNoiseParameters, NoiseParameters altitudeNoiseParameters, NoiseParameters weirdnessNoiseParameters, Optional<Pair<Registry<Biome>, Preset>> instance) {
        super(biomePoints.stream().map(Pair::getSecond));
        this.seed = seed;
        this.instance = instance;
        this.temperatureNoiseParameters = temperatureNoiseParameters;
        this.humidityNoiseParameters = humidityNoiseParameters;
        this.altitudeNoiseParameters = altitudeNoiseParameters;
        this.weirdnessNoiseParameters = weirdnessNoiseParameters;
        this.temperatureNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed), temperatureNoiseParameters.getFirstOctave(), temperatureNoiseParameters.getAmplitudes());
        this.humidityNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed + 1L), humidityNoiseParameters.getFirstOctave(), humidityNoiseParameters.getAmplitudes());
        this.altitudeNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed + 2L), altitudeNoiseParameters.getFirstOctave(), altitudeNoiseParameters.getAmplitudes());
        this.weirdnessNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed + 3L), weirdnessNoiseParameters.getFirstOctave(), weirdnessNoiseParameters.getAmplitudes());
        this.biomePoints = biomePoints;
        this.threeDimensionalSampling = false;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new MultiNoiseBiomeSource(seed, this.biomePoints, this.temperatureNoiseParameters, this.humidityNoiseParameters, this.altitudeNoiseParameters, this.weirdnessNoiseParameters, this.instance);
    }

    private Optional<Instance> getInstance() {
        return this.instance.map(pair -> new Instance((Preset)pair.getSecond(), (Registry)pair.getFirst(), this.seed));
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = this.threeDimensionalSampling ? biomeY : 0;
        Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(biomeX, i, biomeZ), (float)this.humidityNoise.sample(biomeX, i, biomeZ), (float)this.altitudeNoise.sample(biomeX, i, biomeZ), (float)this.weirdnessNoise.sample(biomeX, i, biomeZ), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))).map(Pair::getSecond).map(Supplier::get).orElse(BuiltinBiomes.THE_VOID);
    }

    public boolean matchesInstance(long seed) {
        return this.seed == seed && this.instance.isPresent() && Objects.equals(this.instance.get().getSecond(), Preset.NETHER);
    }

    public static class Preset {
        private static final Map<Identifier, Preset> BY_IDENTIFIER = Maps.newHashMap();
        public static final Preset NETHER = new Preset(new Identifier("nether"), (Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource>)((Function3)(preset, registry, long_) -> new MultiNoiseBiomeSource((long)long_, (List)ImmutableList.of((Object)Pair.of((Object)new Biome.MixedNoisePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(BiomeKeys.NETHER_WASTES)), (Object)Pair.of((Object)new Biome.MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)), (Object)Pair.of((Object)new Biome.MixedNoisePoint(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(BiomeKeys.CRIMSON_FOREST)), (Object)Pair.of((Object)new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> registry.getOrThrow(BiomeKeys.WARPED_FOREST)), (Object)Pair.of((Object)new Biome.MixedNoisePoint(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> registry.getOrThrow(BiomeKeys.BASALT_DELTAS))), Optional.of(Pair.of((Object)registry, (Object)preset)))));
        private final Identifier id;
        private final Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction;

        public Preset(Identifier id, Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction) {
            this.id = id;
            this.biomeSourceFunction = biomeSourceFunction;
            BY_IDENTIFIER.put(id, this);
        }

        public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, long seed) {
            return (MultiNoiseBiomeSource)this.biomeSourceFunction.apply((Object)this, biomeRegistry, (Object)seed);
        }
    }

    static final class Instance {
        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.flatXmap(identifier -> Optional.ofNullable(Preset.BY_IDENTIFIER.get(identifier)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + identifier))), preset -> DataResult.success((Object)((Preset)preset).id)).fieldOf("preset").stable().forGetter(Instance::getPreset), (App)RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(Instance::getBiomeRegistry), (App)Codec.LONG.fieldOf("seed").stable().forGetter(Instance::getSeed)).apply((Applicative)instance, instance.stable(Instance::new)));
        private final Preset preset;
        private final Registry<Biome> biomeRegistry;
        private final long seed;

        private Instance(Preset preset, Registry<Biome> biomeRegistry, long seed) {
            this.preset = preset;
            this.biomeRegistry = biomeRegistry;
            this.seed = seed;
        }

        public Preset getPreset() {
            return this.preset;
        }

        public Registry<Biome> getBiomeRegistry() {
            return this.biomeRegistry;
        }

        public long getSeed() {
            return this.seed;
        }

        public MultiNoiseBiomeSource getBiomeSource() {
            return this.preset.getBiomeSource(this.biomeRegistry, this.seed);
        }
    }

    static class NoiseParameters {
        private final int firstOctave;
        private final DoubleList amplitudes;
        public static final Codec<NoiseParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("firstOctave").forGetter(NoiseParameters::getFirstOctave), (App)Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseParameters::getAmplitudes)).apply((Applicative)instance, NoiseParameters::new));

        public NoiseParameters(int firstOctave, List<Double> amplitudes) {
            this.firstOctave = firstOctave;
            this.amplitudes = new DoubleArrayList(amplitudes);
        }

        public int getFirstOctave() {
            return this.firstOctave;
        }

        public DoubleList getAmplitudes() {
            return this.amplitudes;
        }
    }
}

