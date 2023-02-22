/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorOptions {
    public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorOptions::getSeed), (App)Codec.BOOL.fieldOf("generate_features").orElse((Object)true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), (App)Codec.BOOL.fieldOf("bonus_chest").orElse((Object)false).stable().forGetter(GeneratorOptions::hasBonusChest), (App)SimpleRegistry.createRegistryCodec(Registry.DIMENSION_KEY, Lifecycle.stable(), DimensionOptions.CODEC).xmap(DimensionOptions::method_29569, Function.identity()).fieldOf("dimensions").forGetter(GeneratorOptions::getDimensions), (App)Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)).apply((Applicative)instance, instance.stable(GeneratorOptions::new))).comapFlatMap(GeneratorOptions::method_28610, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private final long seed;
    private final boolean generateStructures;
    private final boolean bonusChest;
    private final SimpleRegistry<DimensionOptions> options;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorOptions> method_28610() {
        DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            return DataResult.error((String)"Overworld settings missing");
        }
        if (this.method_28611()) {
            return DataResult.success((Object)this, (Lifecycle)Lifecycle.stable());
        }
        return DataResult.success((Object)this);
    }

    private boolean method_28611() {
        return DimensionOptions.hasDefaultSettings(this.seed, this.options);
    }

    public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> options) {
        this(seed, generateStructures, bonusChest, options, Optional.empty());
        DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> options, Optional<String> legacyCustomOptions) {
        this.seed = seed;
        this.generateStructures = generateStructures;
        this.bonusChest = bonusChest;
        this.options = options;
        this.legacyCustomOptions = legacyCustomOptions;
    }

    public static GeneratorOptions method_31112(DynamicRegistryManager dynamicRegistryManager) {
        MutableRegistry<Biome> registry = dynamicRegistryManager.get(Registry.BIOME_KEY);
        int i = "North Carolina".hashCode();
        MutableRegistry<DimensionType> registry2 = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
        MutableRegistry<ChunkGeneratorSettings> registry3 = dynamicRegistryManager.get(Registry.NOISE_SETTINGS_WORLDGEN);
        return new GeneratorOptions(i, true, true, GeneratorOptions.method_28608(registry2, DimensionType.createDefaultDimensionOptions(registry2, registry, registry3, i), GeneratorOptions.createOverworldGenerator(registry, registry3, i)));
    }

    public static GeneratorOptions getDefaultOptions(Registry<DimensionType> registry, Registry<Biome> registry2, Registry<ChunkGeneratorSettings> registry3) {
        long l = new Random().nextLong();
        return new GeneratorOptions(l, true, false, GeneratorOptions.method_28608(registry, DimensionType.createDefaultDimensionOptions(registry, registry2, registry3, l), GeneratorOptions.createOverworldGenerator(registry2, registry3, l)));
    }

    public static NoiseChunkGenerator createOverworldGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
        return new NoiseChunkGenerator(new VanillaLayeredBiomeSource(seed, false, false, biomeRegistry), seed, () -> chunkGeneratorSettingsRegistry.getOrThrow(ChunkGeneratorSettings.OVERWORLD));
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

    public static SimpleRegistry<DimensionOptions> method_28608(Registry<DimensionType> registry, SimpleRegistry<DimensionOptions> simpleRegistry, ChunkGenerator chunkGenerator) {
        DimensionOptions dimensionOptions = simpleRegistry.get(DimensionOptions.OVERWORLD);
        Supplier<DimensionType> supplier = () -> dimensionOptions == null ? registry.getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY) : dimensionOptions.getDimensionType();
        return GeneratorOptions.method_29962(simpleRegistry, supplier, chunkGenerator);
    }

    public static SimpleRegistry<DimensionOptions> method_29962(SimpleRegistry<DimensionOptions> simpleRegistry, Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator) {
        SimpleRegistry<DimensionOptions> simpleRegistry2 = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental());
        simpleRegistry2.add(DimensionOptions.OVERWORLD, new DimensionOptions(supplier, chunkGenerator), Lifecycle.stable());
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == DimensionOptions.OVERWORLD) continue;
            simpleRegistry2.add(registryKey, entry.getValue(), simpleRegistry.getEntryLifecycle(entry.getValue()));
        }
        return simpleRegistry2;
    }

    public SimpleRegistry<DimensionOptions> getDimensions() {
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
        return (ImmutableSet)this.getDimensions().getEntries().stream().map(entry -> RegistryKey.of(Registry.WORLD_KEY, ((RegistryKey)entry.getKey()).getValue())).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof DebugChunkGenerator;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof FlatChunkGenerator;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLegacyCustomizedType() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorOptions withBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, true, this.options, this.legacyCustomOptions);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions toggleGenerateStructures() {
        return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.options);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions toggleBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.options);
    }

    public static GeneratorOptions fromProperties(DynamicRegistryManager registryManager, Properties properties) {
        String string2 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("generator-settings")), (Object)"");
        properties.put("generator-settings", string2);
        String string22 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("level-seed")), (Object)"");
        properties.put("level-seed", string22);
        String string3 = (String)properties.get("generate-structures");
        boolean bl = string3 == null || Boolean.parseBoolean(string3);
        properties.put("generate-structures", Objects.toString(bl));
        String string4 = (String)properties.get("level-type");
        String string5 = Optional.ofNullable(string4).map(string -> string.toLowerCase(Locale.ROOT)).orElse("default");
        properties.put("level-type", string5);
        long l = new Random().nextLong();
        if (!string22.isEmpty()) {
            try {
                long m = Long.parseLong(string22);
                if (m != 0L) {
                    l = m;
                }
            }
            catch (NumberFormatException numberFormatException) {
                l = string22.hashCode();
            }
        }
        MutableRegistry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        MutableRegistry<Biome> registry2 = registryManager.get(Registry.BIOME_KEY);
        MutableRegistry<ChunkGeneratorSettings> registry3 = registryManager.get(Registry.NOISE_SETTINGS_WORLDGEN);
        SimpleRegistry<DimensionOptions> simpleRegistry = DimensionType.createDefaultDimensionOptions(registry, registry2, registry3, l);
        switch (string5) {
            case "flat": {
                JsonObject jsonObject = !string2.isEmpty() ? JsonHelper.deserialize(string2) : new JsonObject();
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject);
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(registry, simpleRegistry, new FlatChunkGenerator(FlatChunkGeneratorConfig.CODEC.parse(dynamic).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseGet(() -> FlatChunkGeneratorConfig.getDefaultConfig(registry2)))));
            }
            case "debug_all_block_states": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(registry, simpleRegistry, new DebugChunkGenerator(registry2)));
            }
            case "amplified": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(registry, simpleRegistry, new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, false, registry2), l, () -> registry3.getOrThrow(ChunkGeneratorSettings.AMPLIFIED))));
            }
            case "largebiomes": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(registry, simpleRegistry, new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, true, registry2), l, () -> registry3.getOrThrow(ChunkGeneratorSettings.OVERWORLD))));
            }
        }
        return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(registry, simpleRegistry, GeneratorOptions.createOverworldGenerator(registry2, registry3, l)));
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
        SimpleRegistry<DimensionOptions> simpleRegistry;
        long l = seed.orElse(this.seed);
        if (seed.isPresent()) {
            simpleRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental());
            long m = seed.getAsLong();
            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntries()) {
                RegistryKey<DimensionOptions> registryKey = entry.getKey();
                simpleRegistry.add(registryKey, new DimensionOptions(entry.getValue().getDimensionTypeSupplier(), entry.getValue().getChunkGenerator().withSeed(m)), this.options.getEntryLifecycle(entry.getValue()));
            }
        } else {
            simpleRegistry = this.options;
        }
        GeneratorOptions generatorOptions = this.isDebugWorld() ? new GeneratorOptions(l, false, false, simpleRegistry) : new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, simpleRegistry);
        return generatorOptions;
    }
}
