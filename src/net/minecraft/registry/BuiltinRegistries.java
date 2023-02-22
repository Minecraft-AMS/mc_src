/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.List;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.dimension.DimensionTypeRegistrar;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.noise.BuiltinNoiseParameters;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.structure.Structures;

public class BuiltinRegistries {
    private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder().addRegistry(RegistryKeys.DIMENSION_TYPE, DimensionTypeRegistrar::bootstrap).addRegistry(RegistryKeys.CONFIGURED_CARVER, ConfiguredCarvers::bootstrap).addRegistry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap).addRegistry(RegistryKeys.PLACED_FEATURE, PlacedFeatures::bootstrap).addRegistry(RegistryKeys.STRUCTURE, Structures::bootstrap).addRegistry(RegistryKeys.STRUCTURE_SET, StructureSets::bootstrap).addRegistry(RegistryKeys.PROCESSOR_LIST, StructureProcessorLists::bootstrap).addRegistry(RegistryKeys.TEMPLATE_POOL, StructurePools::bootstrap).addRegistry(RegistryKeys.BIOME, BuiltinBiomes::bootstrap).addRegistry(RegistryKeys.NOISE_PARAMETERS, BuiltinNoiseParameters::bootstrap).addRegistry(RegistryKeys.DENSITY_FUNCTION, DensityFunctions::bootstrap).addRegistry(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ChunkGeneratorSettings::bootstrap).addRegistry(RegistryKeys.WORLD_PRESET, WorldPresets::bootstrap).addRegistry(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPresets::bootstrap).addRegistry(RegistryKeys.MESSAGE_TYPE, MessageType::bootstrap);

    private static void validate(RegistryWrapper.WrapperLookup wrapperLookup) {
        RegistryWrapper.Impl<PlacedFeature> registryEntryLookup = wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
        wrapperLookup.getWrapperOrThrow(RegistryKeys.BIOME).streamEntries().forEach(biome -> {
            Identifier identifier = biome.registryKey().getValue();
            List<RegistryEntryList<PlacedFeature>> list = ((Biome)biome.value()).getGenerationSettings().getFeatures();
            list.stream().flatMap(RegistryEntryList::stream).forEach(placedFeature -> placedFeature.getKeyOrValue().ifLeft(key -> {
                RegistryEntry.Reference reference = registryEntryLookup.getOrThrow((RegistryKey<PlacedFeature>)key);
                if (!BuiltinRegistries.hasBiomePlacementModifier((PlacedFeature)reference.value())) {
                    Util.error("Placed feature " + key.getValue() + " in biome " + identifier + " is missing BiomeFilter.biome()");
                }
            }).ifRight(value -> {
                if (!BuiltinRegistries.hasBiomePlacementModifier(value)) {
                    Util.error("Placed inline feature in biome " + biome + " is missing BiomeFilter.biome()");
                }
            }));
        });
    }

    private static boolean hasBiomePlacementModifier(PlacedFeature placedFeature) {
        return placedFeature.placementModifiers().contains(BiomePlacementModifier.of());
    }

    public static RegistryWrapper.WrapperLookup createWrapperLookup() {
        DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
        RegistryWrapper.WrapperLookup wrapperLookup = REGISTRY_BUILDER.createWrapperLookup(immutable);
        BuiltinRegistries.validate(wrapperLookup);
        return wrapperLookup;
    }
}

