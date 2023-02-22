/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import org.jetbrains.annotations.Nullable;

public final class Biome {
    public static final Codec<Biome> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Weather.CODEC.forGetter(biome -> biome.weather), (App)BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects), (App)GenerationSettings.CODEC.forGetter(biome -> biome.generationSettings), (App)SpawnSettings.CODEC.forGetter(biome -> biome.spawnSettings)).apply((Applicative)instance, Biome::new));
    public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Weather.CODEC.forGetter(biome -> biome.weather), (App)BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects)).apply((Applicative)instance, (weather, effects) -> new Biome((Weather)weather, (BiomeEffects)effects, GenerationSettings.INSTANCE, SpawnSettings.INSTANCE)));
    public static final Codec<RegistryEntry<Biome>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.BIOME, CODEC);
    public static final Codec<RegistryEntryList<Biome>> REGISTRY_ENTRY_LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.BIOME, CODEC);
    private static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler((Random)new ChunkRandom(new CheckedRandom(1234L)), (List<Integer>)ImmutableList.of((Object)0));
    static final OctaveSimplexNoiseSampler FROZEN_OCEAN_NOISE = new OctaveSimplexNoiseSampler((Random)new ChunkRandom(new CheckedRandom(3456L)), (List<Integer>)ImmutableList.of((Object)-2, (Object)-1, (Object)0));
    @Deprecated(forRemoval=true)
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler((Random)new ChunkRandom(new CheckedRandom(2345L)), (List<Integer>)ImmutableList.of((Object)0));
    private static final int MAX_TEMPERATURE_CACHE_SIZE = 1024;
    private final Weather weather;
    private final GenerationSettings generationSettings;
    private final SpawnSettings spawnSettings;
    private final BiomeEffects effects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            protected void rehash(int n) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    Biome(Weather weather, BiomeEffects effects, GenerationSettings generationSettings, SpawnSettings spawnSettings) {
        this.weather = weather;
        this.generationSettings = generationSettings;
        this.spawnSettings = spawnSettings;
        this.effects = effects;
    }

    public int getSkyColor() {
        return this.effects.getSkyColor();
    }

    public SpawnSettings getSpawnSettings() {
        return this.spawnSettings;
    }

    public Precipitation getPrecipitation() {
        return this.weather.precipitation;
    }

    public boolean hasHighHumidity() {
        return this.getDownfall() > 0.85f;
    }

    private float computeTemperature(BlockPos pos) {
        float f = this.weather.temperatureModifier.getModifiedTemperature(pos, this.getTemperature());
        if (pos.getY() > 80) {
            float g = (float)(TEMPERATURE_NOISE.sample((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f, false) * 8.0);
            return f - (g + (float)pos.getY() - 80.0f) * 0.05f / 40.0f;
        }
        return f;
    }

    @Deprecated
    private float getTemperature(BlockPos blockPos) {
        long l = blockPos.asLong();
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = this.temperatureCache.get();
        float f = long2FloatLinkedOpenHashMap.get(l);
        if (!Float.isNaN(f)) {
            return f;
        }
        float g = this.computeTemperature(blockPos);
        if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap.put(l, g);
        return g;
    }

    public boolean canSetIce(WorldView world, BlockPos blockPos) {
        return this.canSetIce(world, blockPos, true);
    }

    public boolean canSetIce(WorldView world, BlockPos pos, boolean doWaterCheck) {
        if (this.doesNotSnow(pos)) {
            return false;
        }
        if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, pos) < 10) {
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
                boolean bl;
                if (!doWaterCheck) {
                    return true;
                }
                boolean bl2 = bl = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
                if (!bl) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCold(BlockPos pos) {
        return !this.doesNotSnow(pos);
    }

    public boolean doesNotSnow(BlockPos pos) {
        return this.getTemperature(pos) >= 0.15f;
    }

    public boolean shouldGenerateLowerFrozenOceanSurface(BlockPos pos) {
        return this.getTemperature(pos) > 0.1f;
    }

    public boolean isHot(BlockPos pos) {
        return this.getTemperature(pos) > 1.0f;
    }

    public boolean canSetSnow(WorldView world, BlockPos pos) {
        BlockState blockState;
        if (this.doesNotSnow(pos)) {
            return false;
        }
        return pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, pos) < 10 && ((blockState = world.getBlockState(pos)).isAir() || blockState.isOf(Blocks.SNOW)) && Blocks.SNOW.getDefaultState().canPlaceAt(world, pos);
    }

    public GenerationSettings getGenerationSettings() {
        return this.generationSettings;
    }

    public int getFogColor() {
        return this.effects.getFogColor();
    }

    public int getGrassColorAt(double x, double z) {
        int i = this.effects.getGrassColor().orElseGet(this::getDefaultGrassColor);
        return this.effects.getGrassColorModifier().getModifiedGrassColor(x, z, i);
    }

    private int getDefaultGrassColor() {
        double d = MathHelper.clamp(this.weather.temperature, 0.0f, 1.0f);
        double e = MathHelper.clamp(this.weather.downfall, 0.0f, 1.0f);
        return GrassColors.getColor(d, e);
    }

    public int getFoliageColor() {
        return this.effects.getFoliageColor().orElseGet(this::getDefaultFoliageColor);
    }

    private int getDefaultFoliageColor() {
        double d = MathHelper.clamp(this.weather.temperature, 0.0f, 1.0f);
        double e = MathHelper.clamp(this.weather.downfall, 0.0f, 1.0f);
        return FoliageColors.getColor(d, e);
    }

    public float getDownfall() {
        return this.weather.downfall;
    }

    public float getTemperature() {
        return this.weather.temperature;
    }

    public BiomeEffects getEffects() {
        return this.effects;
    }

    public int getWaterColor() {
        return this.effects.getWaterColor();
    }

    public int getWaterFogColor() {
        return this.effects.getWaterFogColor();
    }

    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.effects.getParticleConfig();
    }

    public Optional<RegistryEntry<SoundEvent>> getLoopSound() {
        return this.effects.getLoopSound();
    }

    public Optional<BiomeMoodSound> getMoodSound() {
        return this.effects.getMoodSound();
    }

    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.effects.getAdditionsSound();
    }

    public Optional<MusicSound> getMusic() {
        return this.effects.getMusic();
    }

    static final class Weather
    extends Record {
        final Precipitation precipitation;
        final float temperature;
        final TemperatureModifier temperatureModifier;
        final float downfall;
        public static final MapCodec<Weather> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Precipitation.CODEC.fieldOf("precipitation").forGetter(weather -> weather.precipitation), (App)Codec.FLOAT.fieldOf("temperature").forGetter(weather -> Float.valueOf(weather.temperature)), (App)TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", (Object)TemperatureModifier.NONE).forGetter(weather -> weather.temperatureModifier), (App)Codec.FLOAT.fieldOf("downfall").forGetter(weather -> Float.valueOf(weather.downfall))).apply((Applicative)instance, Weather::new));

        Weather(Precipitation precipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
            this.precipitation = precipitation;
            this.temperature = temperature;
            this.temperatureModifier = temperatureModifier;
            this.downfall = downfall;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Weather.class, "precipitation;temperature;temperatureModifier;downfall", "precipitation", "temperature", "temperatureModifier", "downfall"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Weather.class, "precipitation;temperature;temperatureModifier;downfall", "precipitation", "temperature", "temperatureModifier", "downfall"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Weather.class, "precipitation;temperature;temperatureModifier;downfall", "precipitation", "temperature", "temperatureModifier", "downfall"}, this, object);
        }

        public Precipitation precipitation() {
            return this.precipitation;
        }

        public float temperature() {
            return this.temperature;
        }

        public TemperatureModifier temperatureModifier() {
            return this.temperatureModifier;
        }

        public float downfall() {
            return this.downfall;
        }
    }

    public static final class Precipitation
    extends Enum<Precipitation>
    implements StringIdentifiable {
        public static final /* enum */ Precipitation NONE = new Precipitation("none");
        public static final /* enum */ Precipitation RAIN = new Precipitation("rain");
        public static final /* enum */ Precipitation SNOW = new Precipitation("snow");
        public static final Codec<Precipitation> CODEC;
        private final String name;
        private static final /* synthetic */ Precipitation[] field_9386;

        public static Precipitation[] values() {
            return (Precipitation[])field_9386.clone();
        }

        public static Precipitation valueOf(String string) {
            return Enum.valueOf(Precipitation.class, string);
        }

        private Precipitation(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ Precipitation[] method_36699() {
            return new Precipitation[]{NONE, RAIN, SNOW};
        }

        static {
            field_9386 = Precipitation.method_36699();
            CODEC = StringIdentifiable.createCodec(Precipitation::values);
        }
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static abstract class TemperatureModifier
    extends Enum<TemperatureModifier>
    implements StringIdentifiable {
        public static final /* enum */ TemperatureModifier NONE = new TemperatureModifier("none"){

            @Override
            public float getModifiedTemperature(BlockPos pos, float temperature) {
                return temperature;
            }
        };
        public static final /* enum */ TemperatureModifier FROZEN = new TemperatureModifier("frozen"){

            @Override
            public float getModifiedTemperature(BlockPos pos, float temperature) {
                double g;
                double e;
                double d = FROZEN_OCEAN_NOISE.sample((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05, false) * 7.0;
                double f = d + (e = FOLIAGE_NOISE.sample((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2, false));
                if (f < 0.3 && (g = FOLIAGE_NOISE.sample((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09, false)) < 0.8) {
                    return 0.2f;
                }
                return temperature;
            }
        };
        private final String name;
        public static final Codec<TemperatureModifier> CODEC;
        private static final /* synthetic */ TemperatureModifier[] field_26412;

        public static TemperatureModifier[] values() {
            return (TemperatureModifier[])field_26412.clone();
        }

        public static TemperatureModifier valueOf(String string) {
            return Enum.valueOf(TemperatureModifier.class, string);
        }

        public abstract float getModifiedTemperature(BlockPos var1, float var2);

        TemperatureModifier(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ TemperatureModifier[] method_36700() {
            return new TemperatureModifier[]{NONE, FROZEN};
        }

        static {
            field_26412 = TemperatureModifier.method_36700();
            CODEC = StringIdentifiable.createCodec(TemperatureModifier::values);
        }
    }

    public static class Builder {
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private Float temperature;
        private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;
        @Nullable
        private Float downfall;
        @Nullable
        private BiomeEffects specialEffects;
        @Nullable
        private SpawnSettings spawnSettings;
        @Nullable
        private GenerationSettings generationSettings;

        public Builder precipitation(Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = Float.valueOf(temperature);
            return this;
        }

        public Builder downfall(float downfall) {
            this.downfall = Float.valueOf(downfall);
            return this;
        }

        public Builder effects(BiomeEffects effects) {
            this.specialEffects = effects;
            return this;
        }

        public Builder spawnSettings(SpawnSettings spawnSettings) {
            this.spawnSettings = spawnSettings;
            return this;
        }

        public Builder generationSettings(GenerationSettings generationSettings) {
            this.generationSettings = generationSettings;
            return this;
        }

        public Builder temperatureModifier(TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        public Biome build() {
            if (this.precipitation == null || this.temperature == null || this.downfall == null || this.specialEffects == null || this.spawnSettings == null || this.generationSettings == null) {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
            return new Biome(new Weather(this.precipitation, this.temperature.floatValue(), this.temperatureModifier, this.downfall.floatValue()), this.specialEffects, this.generationSettings, this.spawnSettings);
        }

        public String toString() {
            return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.spawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
        }
    }
}

