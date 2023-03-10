/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  org.slf4j.Logger
 */
package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.noise.BuiltinNoiseParameters;
import org.slf4j.Logger;

public class BuiltinRegistries {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, Supplier<? extends RegistryEntry<?>>> DEFAULT_VALUE_SUPPLIERS = Maps.newLinkedHashMap();
    private static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry(RegistryKey.ofRegistry(new Identifier("root")), Lifecycle.experimental(), null);
    public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
    public static final Registry<ConfiguredCarver<?>> CONFIGURED_CARVER = BuiltinRegistries.addRegistry(Registry.CONFIGURED_CARVER_KEY, () -> ConfiguredCarvers.CAVE);
    public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = BuiltinRegistries.addRegistry(Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeatures::getDefaultConfiguredFeature);
    public static final Registry<PlacedFeature> PLACED_FEATURE = BuiltinRegistries.addRegistry(Registry.PLACED_FEATURE_KEY, PlacedFeatures::getDefaultPlacedFeature);
    public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = BuiltinRegistries.addRegistry(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, ConfiguredStructureFeatures::getDefault);
    public static final Registry<StructureSet> STRUCTURE_SET = BuiltinRegistries.addRegistry(Registry.STRUCTURE_SET_KEY, StructureSets::initAndGetDefault);
    public static final Registry<StructureProcessorList> STRUCTURE_PROCESSOR_LIST = BuiltinRegistries.addRegistry(Registry.STRUCTURE_PROCESSOR_LIST_KEY, () -> StructureProcessorLists.ZOMBIE_PLAINS);
    public static final Registry<StructurePool> STRUCTURE_POOL = BuiltinRegistries.addRegistry(Registry.STRUCTURE_POOL_KEY, StructurePools::initDefaultPools);
    public static final Registry<Biome> BIOME = BuiltinRegistries.addRegistry(Registry.BIOME_KEY, BuiltinBiomes::getDefaultBiome);
    public static final Registry<DoublePerlinNoiseSampler.NoiseParameters> NOISE_PARAMETERS = BuiltinRegistries.addRegistry(Registry.NOISE_WORLDGEN, BuiltinNoiseParameters::init);
    public static final Registry<DensityFunction> DENSITY_FUNCTION = BuiltinRegistries.addRegistry(Registry.DENSITY_FUNCTION_KEY, DensityFunctions::init);
    public static final Registry<ChunkGeneratorSettings> CHUNK_GENERATOR_SETTINGS = BuiltinRegistries.addRegistry(Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings::getInstance);
    public static final DynamicRegistryManager DYNAMIC_REGISTRY_MANAGER;

    private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Supplier<? extends RegistryEntry<? extends T>> defaultValueSupplier) {
        return BuiltinRegistries.addRegistry(registryRef, Lifecycle.stable(), defaultValueSupplier);
    }

    private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Supplier<? extends RegistryEntry<? extends T>> defaultValueSupplier) {
        return BuiltinRegistries.addRegistry(registryRef, new SimpleRegistry(registryRef, lifecycle, null), defaultValueSupplier, lifecycle);
    }

    private static <T, R extends MutableRegistry<T>> R addRegistry(RegistryKey<? extends Registry<T>> registryRef, R registry, Supplier<? extends RegistryEntry<? extends T>> defaultValueSupplier, Lifecycle lifecycle) {
        Identifier identifier = registryRef.getValue();
        DEFAULT_VALUE_SUPPLIERS.put(identifier, defaultValueSupplier);
        ROOT.add(registryRef, registry, lifecycle);
        return registry;
    }

    public static <V extends T, T> RegistryEntry<V> method_40360(Registry<T> registry, String id, V value) {
        RegistryEntry<T> registryEntry = BuiltinRegistries.add(registry, new Identifier(id), value);
        return registryEntry;
    }

    public static <T> RegistryEntry<T> add(Registry<T> registry, String id, T object) {
        return BuiltinRegistries.add(registry, new Identifier(id), object);
    }

    public static <T> RegistryEntry<T> add(Registry<T> registry, Identifier id, T object) {
        return BuiltinRegistries.add(registry, RegistryKey.of(registry.getKey(), id), object);
    }

    public static <T> RegistryEntry<T> add(Registry<T> registry, RegistryKey<T> key, T object) {
        return ((MutableRegistry)registry).add(key, object, Lifecycle.stable());
    }

    public static void init() {
    }

    static {
        DEFAULT_VALUE_SUPPLIERS.forEach((id, supplier) -> {
            if (!((RegistryEntry)supplier.get()).hasKeyAndValue()) {
                LOGGER.error("Unable to bootstrap registry '{}'", id);
            }
        });
        Registry.validate(ROOT);
        DYNAMIC_REGISTRY_MANAGER = DynamicRegistryManager.of(REGISTRIES);
    }
}

