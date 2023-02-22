/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world.gen;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class GeneratorOptions {
    public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorOptions::getSeed), (App)Codec.BOOL.fieldOf("generate_features").orElse((Object)true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), (App)Codec.BOOL.fieldOf("bonus_chest").orElse((Object)false).stable().forGetter(GeneratorOptions::hasBonusChest), (App)RegistryCodecs.dynamicRegistry(Registry.DIMENSION_KEY, Lifecycle.stable(), DimensionOptions.CODEC).xmap(DimensionOptions::method_29569, Function.identity()).fieldOf("dimensions").forGetter(GeneratorOptions::getDimensions), (App)Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)).apply((Applicative)instance, instance.stable(GeneratorOptions::new))).comapFlatMap(GeneratorOptions::validate, Function.identity());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long seed;
    private final boolean generateStructures;
    private final boolean bonusChest;
    private final Registry<DimensionOptions> options;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorOptions> validate() {
        DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            return DataResult.error((String)"Overworld settings missing");
        }
        if (this.isStable()) {
            return DataResult.success((Object)this, (Lifecycle)Lifecycle.stable());
        }
        return DataResult.success((Object)this);
    }

    private boolean isStable() {
        return DimensionOptions.hasDefaultSettings(this.seed, this.options);
    }

    public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> options) {
        this(seed, generateStructures, bonusChest, options, Optional.empty());
        DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> options, Optional<String> legacyCustomOptions) {
        this.seed = seed;
        this.generateStructures = generateStructures;
        this.bonusChest = bonusChest;
        this.options = options;
        this.legacyCustomOptions = legacyCustomOptions;
    }

    public static GeneratorOptions createDemo(DynamicRegistryManager registryManager) {
        int i = "North Carolina".hashCode();
        return new GeneratorOptions(i, true, true, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registryManager.get(Registry.DIMENSION_TYPE_KEY), DimensionType.createDefaultDimensionOptions(registryManager, i), GeneratorOptions.createOverworldGenerator(registryManager, i)));
    }

    public static GeneratorOptions getDefaultOptions(DynamicRegistryManager registryManager) {
        long l = new Random().nextLong();
        return new GeneratorOptions(l, true, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registryManager.get(Registry.DIMENSION_TYPE_KEY), DimensionType.createDefaultDimensionOptions(registryManager, l), GeneratorOptions.createOverworldGenerator(registryManager, l)));
    }

    public static NoiseChunkGenerator createOverworldGenerator(DynamicRegistryManager registryManager, long seed) {
        return GeneratorOptions.createOverworldGenerator(registryManager, seed, true);
    }

    public static NoiseChunkGenerator createOverworldGenerator(DynamicRegistryManager registryManager, long seed, boolean bl) {
        return GeneratorOptions.createGenerator(registryManager, seed, ChunkGeneratorSettings.OVERWORLD, bl);
    }

    public static NoiseChunkGenerator createGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settings) {
        return GeneratorOptions.createGenerator(registryManager, seed, settings, true);
    }

    public static NoiseChunkGenerator createGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settings, boolean bl) {
        Registry<Biome> registry = registryManager.get(Registry.BIOME_KEY);
        Registry<StructureSet> registry2 = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<ChunkGeneratorSettings> registry3 = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
        Registry<DoublePerlinNoiseSampler.NoiseParameters> registry4 = registryManager.get(Registry.NOISE_WORLDGEN);
        return new NoiseChunkGenerator(registry2, registry4, (BiomeSource)MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(registry, bl), seed, registry3.getOrCreateEntry(settings));
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateStructures() {
        return this.generateStructures;
    }

    public boolean hasBonusChest() {
        return this.bonusChest;
    }

    public static Registry<DimensionOptions> getRegistryWithReplacedOverworldGenerator(Registry<DimensionType> dimensionTypeRegistry, Registry<DimensionOptions> options, ChunkGenerator overworldGenerator) {
        DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
        RegistryEntry<DimensionType> registryEntry = dimensionOptions == null ? dimensionTypeRegistry.getOrCreateEntry(DimensionType.OVERWORLD_REGISTRY_KEY) : dimensionOptions.getDimensionTypeSupplier();
        return GeneratorOptions.getRegistryWithReplacedOverworld(options, registryEntry, overworldGenerator);
    }

    public static Registry<DimensionOptions> getRegistryWithReplacedOverworld(Registry<DimensionOptions> options, RegistryEntry<DimensionType> dimensionType, ChunkGenerator overworldGenerator) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
        ((MutableRegistry)mutableRegistry).add(DimensionOptions.OVERWORLD, new DimensionOptions(dimensionType, overworldGenerator), Lifecycle.stable());
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : options.getEntrySet()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == DimensionOptions.OVERWORLD) continue;
            ((MutableRegistry)mutableRegistry).add(registryKey, entry.getValue(), options.getEntryLifecycle(entry.getValue()));
        }
        return mutableRegistry;
    }

    public Registry<DimensionOptions> getDimensions() {
        return this.options;
    }

    public ChunkGenerator getChunkGenerator() {
        DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
        return dimensionOptions.getChunkGenerator();
    }

    public ImmutableSet<RegistryKey<World>> getWorlds() {
        return (ImmutableSet)this.getDimensions().getEntrySet().stream().map(Map.Entry::getKey).map(GeneratorOptions::toWorldKey).collect(ImmutableSet.toImmutableSet());
    }

    public static RegistryKey<World> toWorldKey(RegistryKey<DimensionOptions> dimensionOptionsKey) {
        return RegistryKey.of(Registry.WORLD_KEY, dimensionOptionsKey.getValue());
    }

    public static RegistryKey<DimensionOptions> toDimensionOptionsKey(RegistryKey<World> worldKey) {
        return RegistryKey.of(Registry.DIMENSION_KEY, worldKey.getValue());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof DebugChunkGenerator;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof FlatChunkGenerator;
    }

    public boolean isLegacyCustomizedType() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorOptions withBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, true, this.options, this.legacyCustomOptions);
    }

    public GeneratorOptions toggleGenerateStructures() {
        return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.options);
    }

    public GeneratorOptions toggleBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.options);
    }

    public static GeneratorOptions fromProperties(DynamicRegistryManager registryManager, ServerPropertiesHandler.WorldGenProperties worldGenProperties) {
        long l = GeneratorOptions.parseSeed(worldGenProperties.levelSeed()).orElse(new Random().nextLong());
        Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<Biome> registry2 = registryManager.get(Registry.BIOME_KEY);
        Registry<StructureSet> registry3 = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<DimensionOptions> registry4 = DimensionType.createDefaultDimensionOptions(registryManager, l);
        switch (worldGenProperties.levelType()) {
            case "flat": {
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)worldGenProperties.generatorSettings());
                return new GeneratorOptions(l, worldGenProperties.generateStructures(), false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry4, new FlatChunkGenerator(registry3, FlatChunkGeneratorConfig.CODEC.parse(dynamic).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseGet(() -> FlatChunkGeneratorConfig.getDefaultConfig(registry2, registry3)))));
            }
            case "debug_all_block_states": {
                return new GeneratorOptions(l, worldGenProperties.generateStructures(), false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry4, new DebugChunkGenerator(registry3, registry2)));
            }
            case "amplified": {
                return new GeneratorOptions(l, worldGenProperties.generateStructures(), false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry4, GeneratorOptions.createGenerator(registryManager, l, ChunkGeneratorSettings.AMPLIFIED)));
            }
            case "largebiomes": {
                return new GeneratorOptions(l, worldGenProperties.generateStructures(), false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry4, GeneratorOptions.createGenerator(registryManager, l, ChunkGeneratorSettings.LARGE_BIOMES)));
            }
        }
        return new GeneratorOptions(l, worldGenProperties.generateStructures(), false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry4, GeneratorOptions.createOverworldGenerator(registryManager, l)));
    }

    public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
        Registry<DimensionOptions> registry;
        long l = seed.orElse(this.seed);
        if (seed.isPresent()) {
            SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
            long m = seed.getAsLong();
            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntrySet()) {
                RegistryKey<DimensionOptions> registryKey = entry.getKey();
                ((MutableRegistry)mutableRegistry).add(registryKey, new DimensionOptions(entry.getValue().getDimensionTypeSupplier(), entry.getValue().getChunkGenerator().withSeed(m)), this.options.getEntryLifecycle(entry.getValue()));
            }
            registry = mutableRegistry;
        } else {
            registry = this.options;
        }
        GeneratorOptions generatorOptions = this.isDebugWorld() ? new GeneratorOptions(l, false, false, registry) : new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, registry);
        return generatorOptions;
    }

    public static OptionalLong parseSeed(String seed) {
        if (StringUtils.isEmpty((CharSequence)(seed = seed.trim()))) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(seed));
        }
        catch (NumberFormatException numberFormatException) {
            return OptionalLong.of(seed.hashCode());
        }
    }
}

