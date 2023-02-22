/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.TheEndBiomeCreator;
import net.minecraft.world.biome.TheNetherBiomeCreator;

public abstract class BuiltinBiomes {
    public static RegistryEntry<Biome> getDefaultBiome(Registry<Biome> registry) {
        BuiltinRegistries.add(registry, BiomeKeys.THE_VOID, OverworldBiomeCreator.createTheVoid());
        BuiltinRegistries.add(registry, BiomeKeys.PLAINS, OverworldBiomeCreator.createPlains(false, false, false));
        BuiltinRegistries.add(registry, BiomeKeys.SUNFLOWER_PLAINS, OverworldBiomeCreator.createPlains(true, false, false));
        BuiltinRegistries.add(registry, BiomeKeys.SNOWY_PLAINS, OverworldBiomeCreator.createPlains(false, true, false));
        BuiltinRegistries.add(registry, BiomeKeys.ICE_SPIKES, OverworldBiomeCreator.createPlains(false, true, true));
        BuiltinRegistries.add(registry, BiomeKeys.DESERT, OverworldBiomeCreator.createDesert());
        BuiltinRegistries.add(registry, BiomeKeys.SWAMP, OverworldBiomeCreator.createSwamp());
        BuiltinRegistries.add(registry, BiomeKeys.MANGROVE_SWAMP, OverworldBiomeCreator.createMangroveSwamp());
        BuiltinRegistries.add(registry, BiomeKeys.FOREST, OverworldBiomeCreator.createNormalForest(false, false, false));
        BuiltinRegistries.add(registry, BiomeKeys.FLOWER_FOREST, OverworldBiomeCreator.createNormalForest(false, false, true));
        BuiltinRegistries.add(registry, BiomeKeys.BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(true, false, false));
        BuiltinRegistries.add(registry, BiomeKeys.DARK_FOREST, OverworldBiomeCreator.createDarkForest());
        BuiltinRegistries.add(registry, BiomeKeys.OLD_GROWTH_BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(true, true, false));
        BuiltinRegistries.add(registry, BiomeKeys.OLD_GROWTH_PINE_TAIGA, OverworldBiomeCreator.createOldGrowthTaiga(false));
        BuiltinRegistries.add(registry, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomeCreator.createOldGrowthTaiga(true));
        BuiltinRegistries.add(registry, BiomeKeys.TAIGA, OverworldBiomeCreator.createTaiga(false));
        BuiltinRegistries.add(registry, BiomeKeys.SNOWY_TAIGA, OverworldBiomeCreator.createTaiga(true));
        BuiltinRegistries.add(registry, BiomeKeys.SAVANNA, OverworldBiomeCreator.createSavanna(false, false));
        BuiltinRegistries.add(registry, BiomeKeys.SAVANNA_PLATEAU, OverworldBiomeCreator.createSavanna(false, true));
        BuiltinRegistries.add(registry, BiomeKeys.WINDSWEPT_HILLS, OverworldBiomeCreator.createWindsweptHills(false));
        BuiltinRegistries.add(registry, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomeCreator.createWindsweptHills(false));
        BuiltinRegistries.add(registry, BiomeKeys.WINDSWEPT_FOREST, OverworldBiomeCreator.createWindsweptHills(true));
        BuiltinRegistries.add(registry, BiomeKeys.WINDSWEPT_SAVANNA, OverworldBiomeCreator.createSavanna(true, false));
        BuiltinRegistries.add(registry, BiomeKeys.JUNGLE, OverworldBiomeCreator.createJungle());
        BuiltinRegistries.add(registry, BiomeKeys.SPARSE_JUNGLE, OverworldBiomeCreator.createSparseJungle());
        BuiltinRegistries.add(registry, BiomeKeys.BAMBOO_JUNGLE, OverworldBiomeCreator.createNormalBambooJungle());
        BuiltinRegistries.add(registry, BiomeKeys.BADLANDS, OverworldBiomeCreator.createBadlands(false));
        BuiltinRegistries.add(registry, BiomeKeys.ERODED_BADLANDS, OverworldBiomeCreator.createBadlands(false));
        BuiltinRegistries.add(registry, BiomeKeys.WOODED_BADLANDS, OverworldBiomeCreator.createBadlands(true));
        BuiltinRegistries.add(registry, BiomeKeys.MEADOW, OverworldBiomeCreator.createMeadow());
        BuiltinRegistries.add(registry, BiomeKeys.GROVE, OverworldBiomeCreator.createGrove());
        BuiltinRegistries.add(registry, BiomeKeys.SNOWY_SLOPES, OverworldBiomeCreator.createSnowySlopes());
        BuiltinRegistries.add(registry, BiomeKeys.FROZEN_PEAKS, OverworldBiomeCreator.createFrozenPeaks());
        BuiltinRegistries.add(registry, BiomeKeys.JAGGED_PEAKS, OverworldBiomeCreator.createJaggedPeaks());
        BuiltinRegistries.add(registry, BiomeKeys.STONY_PEAKS, OverworldBiomeCreator.createStonyPeaks());
        BuiltinRegistries.add(registry, BiomeKeys.RIVER, OverworldBiomeCreator.createRiver(false));
        BuiltinRegistries.add(registry, BiomeKeys.FROZEN_RIVER, OverworldBiomeCreator.createRiver(true));
        BuiltinRegistries.add(registry, BiomeKeys.BEACH, OverworldBiomeCreator.createBeach(false, false));
        BuiltinRegistries.add(registry, BiomeKeys.SNOWY_BEACH, OverworldBiomeCreator.createBeach(true, false));
        BuiltinRegistries.add(registry, BiomeKeys.STONY_SHORE, OverworldBiomeCreator.createBeach(false, true));
        BuiltinRegistries.add(registry, BiomeKeys.WARM_OCEAN, OverworldBiomeCreator.createWarmOcean());
        BuiltinRegistries.add(registry, BiomeKeys.LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(false));
        BuiltinRegistries.add(registry, BiomeKeys.DEEP_LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(true));
        BuiltinRegistries.add(registry, BiomeKeys.OCEAN, OverworldBiomeCreator.createNormalOcean(false));
        BuiltinRegistries.add(registry, BiomeKeys.DEEP_OCEAN, OverworldBiomeCreator.createNormalOcean(true));
        BuiltinRegistries.add(registry, BiomeKeys.COLD_OCEAN, OverworldBiomeCreator.createColdOcean(false));
        BuiltinRegistries.add(registry, BiomeKeys.DEEP_COLD_OCEAN, OverworldBiomeCreator.createColdOcean(true));
        BuiltinRegistries.add(registry, BiomeKeys.FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(false));
        BuiltinRegistries.add(registry, BiomeKeys.DEEP_FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(true));
        BuiltinRegistries.add(registry, BiomeKeys.MUSHROOM_FIELDS, OverworldBiomeCreator.createMushroomFields());
        BuiltinRegistries.add(registry, BiomeKeys.DRIPSTONE_CAVES, OverworldBiomeCreator.createDripstoneCaves());
        BuiltinRegistries.add(registry, BiomeKeys.LUSH_CAVES, OverworldBiomeCreator.createLushCaves());
        BuiltinRegistries.add(registry, BiomeKeys.DEEP_DARK, OverworldBiomeCreator.createDeepDark());
        BuiltinRegistries.add(registry, BiomeKeys.NETHER_WASTES, TheNetherBiomeCreator.createNetherWastes());
        BuiltinRegistries.add(registry, BiomeKeys.WARPED_FOREST, TheNetherBiomeCreator.createWarpedForest());
        BuiltinRegistries.add(registry, BiomeKeys.CRIMSON_FOREST, TheNetherBiomeCreator.createCrimsonForest());
        BuiltinRegistries.add(registry, BiomeKeys.SOUL_SAND_VALLEY, TheNetherBiomeCreator.createSoulSandValley());
        BuiltinRegistries.add(registry, BiomeKeys.BASALT_DELTAS, TheNetherBiomeCreator.createBasaltDeltas());
        BuiltinRegistries.add(registry, BiomeKeys.THE_END, TheEndBiomeCreator.createTheEnd());
        BuiltinRegistries.add(registry, BiomeKeys.END_HIGHLANDS, TheEndBiomeCreator.createEndHighlands());
        BuiltinRegistries.add(registry, BiomeKeys.END_MIDLANDS, TheEndBiomeCreator.createEndMidlands());
        BuiltinRegistries.add(registry, BiomeKeys.SMALL_END_ISLANDS, TheEndBiomeCreator.createSmallEndIslands());
        return BuiltinRegistries.add(registry, BiomeKeys.END_BARRENS, TheEndBiomeCreator.createEndBarrens());
    }
}

