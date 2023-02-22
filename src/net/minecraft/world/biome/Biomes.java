/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import java.util.Collections;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BadlandsBiome;
import net.minecraft.world.biome.BadlandsPlateauBiome;
import net.minecraft.world.biome.BambooJungleBiome;
import net.minecraft.world.biome.BambooJungleHillsBiome;
import net.minecraft.world.biome.BeachBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BirchForestBiome;
import net.minecraft.world.biome.BirchForestHillsBiome;
import net.minecraft.world.biome.ColdOceanBiome;
import net.minecraft.world.biome.DarkForestBiome;
import net.minecraft.world.biome.DarkForestHillsBiome;
import net.minecraft.world.biome.DeepColdOceanBiome;
import net.minecraft.world.biome.DeepFrozenOceanBiome;
import net.minecraft.world.biome.DeepLukewarmOceanBiome;
import net.minecraft.world.biome.DeepOceanBiome;
import net.minecraft.world.biome.DeepWarmOceanBiome;
import net.minecraft.world.biome.DesertBiome;
import net.minecraft.world.biome.DesertHillsBiome;
import net.minecraft.world.biome.DesertLakesBiome;
import net.minecraft.world.biome.EndBarrensBiome;
import net.minecraft.world.biome.EndBiome;
import net.minecraft.world.biome.EndHighlandsBiome;
import net.minecraft.world.biome.EndIslandsSmallBiome;
import net.minecraft.world.biome.EndMidlandsBiome;
import net.minecraft.world.biome.ErodedBadlandsBiome;
import net.minecraft.world.biome.FlowerForestBiome;
import net.minecraft.world.biome.ForestBiome;
import net.minecraft.world.biome.FrozenOceanBiome;
import net.minecraft.world.biome.FrozenRiverBiome;
import net.minecraft.world.biome.GiantSpruceTaigaBiome;
import net.minecraft.world.biome.GiantSpruceTaigaHillsBiome;
import net.minecraft.world.biome.GiantTreeTaigaBiome;
import net.minecraft.world.biome.GiantTreeTaigaHillsBiome;
import net.minecraft.world.biome.GravellyMountainsBiome;
import net.minecraft.world.biome.IceSpikesBiome;
import net.minecraft.world.biome.JungleBiome;
import net.minecraft.world.biome.JungleEdgeBiome;
import net.minecraft.world.biome.JungleHillsBiome;
import net.minecraft.world.biome.LukewarmOceanBiome;
import net.minecraft.world.biome.ModifiedBadlandsPlateauBiome;
import net.minecraft.world.biome.ModifiedGravellyMountainsBiome;
import net.minecraft.world.biome.ModifiedJungleBiome;
import net.minecraft.world.biome.ModifiedJungleEdgeBiome;
import net.minecraft.world.biome.ModifiedWoodedBadlandsPlateauBiome;
import net.minecraft.world.biome.MountainEdgeBiome;
import net.minecraft.world.biome.MountainsBiome;
import net.minecraft.world.biome.MushroomFieldShoreBiome;
import net.minecraft.world.biome.MushroomFieldsBiome;
import net.minecraft.world.biome.NetherBiome;
import net.minecraft.world.biome.OceanBiome;
import net.minecraft.world.biome.PlainsBiome;
import net.minecraft.world.biome.RiverBiome;
import net.minecraft.world.biome.SavannaBiome;
import net.minecraft.world.biome.SavannaPlateauBiome;
import net.minecraft.world.biome.ShatteredSavannaBiome;
import net.minecraft.world.biome.ShatteredSavannaPlateauBiome;
import net.minecraft.world.biome.SnowyBeachBiome;
import net.minecraft.world.biome.SnowyMountainsBiome;
import net.minecraft.world.biome.SnowyTaigaBiome;
import net.minecraft.world.biome.SnowyTaigaHillsBiome;
import net.minecraft.world.biome.SnowyTaigaMountainsBiome;
import net.minecraft.world.biome.SnowyTundraBiome;
import net.minecraft.world.biome.StoneShoreBiome;
import net.minecraft.world.biome.SunflowerPlainsBiome;
import net.minecraft.world.biome.SwampBiome;
import net.minecraft.world.biome.SwampHillsBiome;
import net.minecraft.world.biome.TaigaBiome;
import net.minecraft.world.biome.TaigaHillsBiome;
import net.minecraft.world.biome.TaigaMountainsBiome;
import net.minecraft.world.biome.TallBirchForestBiome;
import net.minecraft.world.biome.TallBirchHillsBiome;
import net.minecraft.world.biome.VoidBiome;
import net.minecraft.world.biome.WarmOceanBiome;
import net.minecraft.world.biome.WoodedBadlandsPlateauBiome;
import net.minecraft.world.biome.WoodedHillsBiome;
import net.minecraft.world.biome.WoodedMountainsBiome;

public abstract class Biomes {
    public static final Biome OCEAN;
    public static final Biome DEFAULT;
    public static final Biome PLAINS;
    public static final Biome DESERT;
    public static final Biome MOUNTAINS;
    public static final Biome FOREST;
    public static final Biome TAIGA;
    public static final Biome SWAMP;
    public static final Biome RIVER;
    public static final Biome NETHER;
    public static final Biome THE_END;
    public static final Biome FROZEN_OCEAN;
    public static final Biome FROZEN_RIVER;
    public static final Biome SNOWY_TUNDRA;
    public static final Biome SNOWY_MOUNTAINS;
    public static final Biome MUSHROOM_FIELDS;
    public static final Biome MUSHROOM_FIELD_SHORE;
    public static final Biome BEACH;
    public static final Biome DESERT_HILLS;
    public static final Biome WOODED_HILLS;
    public static final Biome TAIGA_HILLS;
    public static final Biome MOUNTAIN_EDGE;
    public static final Biome JUNGLE;
    public static final Biome JUNGLE_HILLS;
    public static final Biome JUNGLE_EDGE;
    public static final Biome DEEP_OCEAN;
    public static final Biome STONE_SHORE;
    public static final Biome SNOWY_BEACH;
    public static final Biome BIRCH_FOREST;
    public static final Biome BIRCH_FOREST_HILLS;
    public static final Biome DARK_FOREST;
    public static final Biome SNOWY_TAIGA;
    public static final Biome SNOWY_TAIGA_HILLS;
    public static final Biome GIANT_TREE_TAIGA;
    public static final Biome GIANT_TREE_TAIGA_HILLS;
    public static final Biome WOODED_MOUNTAINS;
    public static final Biome SAVANNA;
    public static final Biome SAVANNA_PLATEAU;
    public static final Biome BADLANDS;
    public static final Biome WOODED_BADLANDS_PLATEAU;
    public static final Biome BADLANDS_PLATEAU;
    public static final Biome SMALL_END_ISLANDS;
    public static final Biome END_MIDLANDS;
    public static final Biome END_HIGHLANDS;
    public static final Biome END_BARRENS;
    public static final Biome WARM_OCEAN;
    public static final Biome LUKEWARM_OCEAN;
    public static final Biome COLD_OCEAN;
    public static final Biome DEEP_WARM_OCEAN;
    public static final Biome DEEP_LUKEWARM_OCEAN;
    public static final Biome DEEP_COLD_OCEAN;
    public static final Biome DEEP_FROZEN_OCEAN;
    public static final Biome THE_VOID;
    public static final Biome SUNFLOWER_PLAINS;
    public static final Biome DESERT_LAKES;
    public static final Biome GRAVELLY_MOUNTAINS;
    public static final Biome FLOWER_FOREST;
    public static final Biome TAIGA_MOUNTAINS;
    public static final Biome SWAMP_HILLS;
    public static final Biome ICE_SPIKES;
    public static final Biome MODIFIED_JUNGLE;
    public static final Biome MODIFIED_JUNGLE_EDGE;
    public static final Biome TALL_BIRCH_FOREST;
    public static final Biome TALL_BIRCH_HILLS;
    public static final Biome DARK_FOREST_HILLS;
    public static final Biome SNOWY_TAIGA_MOUNTAINS;
    public static final Biome GIANT_SPRUCE_TAIGA;
    public static final Biome GIANT_SPRUCE_TAIGA_HILLS;
    public static final Biome MODIFIED_GRAVELLY_MOUNTAINS;
    public static final Biome SHATTERED_SAVANNA;
    public static final Biome SHATTERED_SAVANNA_PLATEAU;
    public static final Biome ERODED_BADLANDS;
    public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU;
    public static final Biome MODIFIED_BADLANDS_PLATEAU;
    public static final Biome BAMBOO_JUNGLE;
    public static final Biome BAMBOO_JUNGLE_HILLS;

    private static Biome register(int rawId, String id, Biome biome) {
        Registry.register(Registry.BIOME, rawId, id, biome);
        if (biome.hasParent()) {
            Biome.PARENT_BIOME_ID_MAP.set(biome, Registry.BIOME.getRawId(Registry.BIOME.get(new Identifier(biome.parent))));
        }
        return biome;
    }

    static {
        DEFAULT = OCEAN = Biomes.register(0, "ocean", new OceanBiome());
        PLAINS = Biomes.register(1, "plains", new PlainsBiome());
        DESERT = Biomes.register(2, "desert", new DesertBiome());
        MOUNTAINS = Biomes.register(3, "mountains", new MountainsBiome());
        FOREST = Biomes.register(4, "forest", new ForestBiome());
        TAIGA = Biomes.register(5, "taiga", new TaigaBiome());
        SWAMP = Biomes.register(6, "swamp", new SwampBiome());
        RIVER = Biomes.register(7, "river", new RiverBiome());
        NETHER = Biomes.register(8, "nether", new NetherBiome());
        THE_END = Biomes.register(9, "the_end", new EndBiome());
        FROZEN_OCEAN = Biomes.register(10, "frozen_ocean", new FrozenOceanBiome());
        FROZEN_RIVER = Biomes.register(11, "frozen_river", new FrozenRiverBiome());
        SNOWY_TUNDRA = Biomes.register(12, "snowy_tundra", new SnowyTundraBiome());
        SNOWY_MOUNTAINS = Biomes.register(13, "snowy_mountains", new SnowyMountainsBiome());
        MUSHROOM_FIELDS = Biomes.register(14, "mushroom_fields", new MushroomFieldsBiome());
        MUSHROOM_FIELD_SHORE = Biomes.register(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
        BEACH = Biomes.register(16, "beach", new BeachBiome());
        DESERT_HILLS = Biomes.register(17, "desert_hills", new DesertHillsBiome());
        WOODED_HILLS = Biomes.register(18, "wooded_hills", new WoodedHillsBiome());
        TAIGA_HILLS = Biomes.register(19, "taiga_hills", new TaigaHillsBiome());
        MOUNTAIN_EDGE = Biomes.register(20, "mountain_edge", new MountainEdgeBiome());
        JUNGLE = Biomes.register(21, "jungle", new JungleBiome());
        JUNGLE_HILLS = Biomes.register(22, "jungle_hills", new JungleHillsBiome());
        JUNGLE_EDGE = Biomes.register(23, "jungle_edge", new JungleEdgeBiome());
        DEEP_OCEAN = Biomes.register(24, "deep_ocean", new DeepOceanBiome());
        STONE_SHORE = Biomes.register(25, "stone_shore", new StoneShoreBiome());
        SNOWY_BEACH = Biomes.register(26, "snowy_beach", new SnowyBeachBiome());
        BIRCH_FOREST = Biomes.register(27, "birch_forest", new BirchForestBiome());
        BIRCH_FOREST_HILLS = Biomes.register(28, "birch_forest_hills", new BirchForestHillsBiome());
        DARK_FOREST = Biomes.register(29, "dark_forest", new DarkForestBiome());
        SNOWY_TAIGA = Biomes.register(30, "snowy_taiga", new SnowyTaigaBiome());
        SNOWY_TAIGA_HILLS = Biomes.register(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
        GIANT_TREE_TAIGA = Biomes.register(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
        GIANT_TREE_TAIGA_HILLS = Biomes.register(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
        WOODED_MOUNTAINS = Biomes.register(34, "wooded_mountains", new WoodedMountainsBiome());
        SAVANNA = Biomes.register(35, "savanna", new SavannaBiome());
        SAVANNA_PLATEAU = Biomes.register(36, "savanna_plateau", new SavannaPlateauBiome());
        BADLANDS = Biomes.register(37, "badlands", new BadlandsBiome());
        WOODED_BADLANDS_PLATEAU = Biomes.register(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
        BADLANDS_PLATEAU = Biomes.register(39, "badlands_plateau", new BadlandsPlateauBiome());
        SMALL_END_ISLANDS = Biomes.register(40, "small_end_islands", new EndIslandsSmallBiome());
        END_MIDLANDS = Biomes.register(41, "end_midlands", new EndMidlandsBiome());
        END_HIGHLANDS = Biomes.register(42, "end_highlands", new EndHighlandsBiome());
        END_BARRENS = Biomes.register(43, "end_barrens", new EndBarrensBiome());
        WARM_OCEAN = Biomes.register(44, "warm_ocean", new WarmOceanBiome());
        LUKEWARM_OCEAN = Biomes.register(45, "lukewarm_ocean", new LukewarmOceanBiome());
        COLD_OCEAN = Biomes.register(46, "cold_ocean", new ColdOceanBiome());
        DEEP_WARM_OCEAN = Biomes.register(47, "deep_warm_ocean", new DeepWarmOceanBiome());
        DEEP_LUKEWARM_OCEAN = Biomes.register(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
        DEEP_COLD_OCEAN = Biomes.register(49, "deep_cold_ocean", new DeepColdOceanBiome());
        DEEP_FROZEN_OCEAN = Biomes.register(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
        THE_VOID = Biomes.register(127, "the_void", new VoidBiome());
        SUNFLOWER_PLAINS = Biomes.register(129, "sunflower_plains", new SunflowerPlainsBiome());
        DESERT_LAKES = Biomes.register(130, "desert_lakes", new DesertLakesBiome());
        GRAVELLY_MOUNTAINS = Biomes.register(131, "gravelly_mountains", new GravellyMountainsBiome());
        FLOWER_FOREST = Biomes.register(132, "flower_forest", new FlowerForestBiome());
        TAIGA_MOUNTAINS = Biomes.register(133, "taiga_mountains", new TaigaMountainsBiome());
        SWAMP_HILLS = Biomes.register(134, "swamp_hills", new SwampHillsBiome());
        ICE_SPIKES = Biomes.register(140, "ice_spikes", new IceSpikesBiome());
        MODIFIED_JUNGLE = Biomes.register(149, "modified_jungle", new ModifiedJungleBiome());
        MODIFIED_JUNGLE_EDGE = Biomes.register(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
        TALL_BIRCH_FOREST = Biomes.register(155, "tall_birch_forest", new TallBirchForestBiome());
        TALL_BIRCH_HILLS = Biomes.register(156, "tall_birch_hills", new TallBirchHillsBiome());
        DARK_FOREST_HILLS = Biomes.register(157, "dark_forest_hills", new DarkForestHillsBiome());
        SNOWY_TAIGA_MOUNTAINS = Biomes.register(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
        GIANT_SPRUCE_TAIGA = Biomes.register(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
        GIANT_SPRUCE_TAIGA_HILLS = Biomes.register(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
        MODIFIED_GRAVELLY_MOUNTAINS = Biomes.register(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
        SHATTERED_SAVANNA = Biomes.register(163, "shattered_savanna", new ShatteredSavannaBiome());
        SHATTERED_SAVANNA_PLATEAU = Biomes.register(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
        ERODED_BADLANDS = Biomes.register(165, "eroded_badlands", new ErodedBadlandsBiome());
        MODIFIED_WOODED_BADLANDS_PLATEAU = Biomes.register(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
        MODIFIED_BADLANDS_PLATEAU = Biomes.register(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
        BAMBOO_JUNGLE = Biomes.register(168, "bamboo_jungle", new BambooJungleBiome());
        BAMBOO_JUNGLE_HILLS = Biomes.register(169, "bamboo_jungle_hills", new BambooJungleHillsBiome());
        Collections.addAll(Biome.BIOMES, OCEAN, PLAINS, DESERT, MOUNTAINS, FOREST, TAIGA, SWAMP, RIVER, FROZEN_RIVER, SNOWY_TUNDRA, SNOWY_MOUNTAINS, MUSHROOM_FIELDS, MUSHROOM_FIELD_SHORE, BEACH, DESERT_HILLS, WOODED_HILLS, TAIGA_HILLS, JUNGLE, JUNGLE_HILLS, JUNGLE_EDGE, DEEP_OCEAN, STONE_SHORE, SNOWY_BEACH, BIRCH_FOREST, BIRCH_FOREST_HILLS, DARK_FOREST, SNOWY_TAIGA, SNOWY_TAIGA_HILLS, GIANT_TREE_TAIGA, GIANT_TREE_TAIGA_HILLS, WOODED_MOUNTAINS, SAVANNA, SAVANNA_PLATEAU, BADLANDS, WOODED_BADLANDS_PLATEAU, BADLANDS_PLATEAU);
    }
}

