/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class BiomeTagProvider
extends AbstractTagProvider<Biome> {
    public BiomeTagProvider(DataGenerator dataGenerator) {
        super(dataGenerator, BuiltinRegistries.BIOME);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(BiomeTags.IS_DEEP_OCEAN).add(BiomeKeys.DEEP_FROZEN_OCEAN).add(BiomeKeys.DEEP_COLD_OCEAN).add(BiomeKeys.DEEP_OCEAN).add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_DEEP_OCEAN).add(BiomeKeys.FROZEN_OCEAN).add(BiomeKeys.OCEAN).add(BiomeKeys.COLD_OCEAN).add(BiomeKeys.LUKEWARM_OCEAN).add(BiomeKeys.WARM_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.IS_BEACH).add(BiomeKeys.BEACH).add(BiomeKeys.SNOWY_BEACH);
        this.getOrCreateTagBuilder(BiomeTags.IS_RIVER).add(BiomeKeys.RIVER).add(BiomeKeys.FROZEN_RIVER);
        this.getOrCreateTagBuilder(BiomeTags.IS_MOUNTAIN).add(BiomeKeys.MEADOW).add(BiomeKeys.FROZEN_PEAKS).add(BiomeKeys.JAGGED_PEAKS).add(BiomeKeys.STONY_PEAKS).add(BiomeKeys.SNOWY_SLOPES);
        this.getOrCreateTagBuilder(BiomeTags.IS_BADLANDS).add(BiomeKeys.BADLANDS).add(BiomeKeys.ERODED_BADLANDS).add(BiomeKeys.WOODED_BADLANDS);
        this.getOrCreateTagBuilder(BiomeTags.IS_HILL).add(BiomeKeys.WINDSWEPT_HILLS).add(BiomeKeys.WINDSWEPT_FOREST).add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS);
        this.getOrCreateTagBuilder(BiomeTags.IS_TAIGA).add(BiomeKeys.TAIGA).add(BiomeKeys.SNOWY_TAIGA).add(BiomeKeys.OLD_GROWTH_PINE_TAIGA).add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
        this.getOrCreateTagBuilder(BiomeTags.IS_JUNGLE).add(BiomeKeys.BAMBOO_JUNGLE).add(BiomeKeys.JUNGLE).add(BiomeKeys.SPARSE_JUNGLE);
        this.getOrCreateTagBuilder(BiomeTags.IS_FOREST).add(BiomeKeys.FOREST).add(BiomeKeys.FLOWER_FOREST).add(BiomeKeys.BIRCH_FOREST).add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST).add(BiomeKeys.DARK_FOREST).add(BiomeKeys.GROVE);
        this.getOrCreateTagBuilder(BiomeTags.IS_NETHER).add(BiomeKeys.NETHER_WASTES).add(BiomeKeys.BASALT_DELTAS).add(BiomeKeys.SOUL_SAND_VALLEY).add(BiomeKeys.CRIMSON_FOREST).add(BiomeKeys.WARPED_FOREST);
        this.getOrCreateTagBuilder(BiomeTags.BURIED_TREASURE_HAS_STRUCTURE).addTag(BiomeTags.IS_BEACH);
        this.getOrCreateTagBuilder(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE).add(BiomeKeys.DESERT);
        this.getOrCreateTagBuilder(BiomeTags.IGLOO_HAS_STRUCTURE).add(BiomeKeys.SNOWY_TAIGA).add(BiomeKeys.SNOWY_PLAINS).add(BiomeKeys.SNOWY_SLOPES);
        this.getOrCreateTagBuilder(BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE).add(BiomeKeys.BAMBOO_JUNGLE).add(BiomeKeys.JUNGLE);
        this.getOrCreateTagBuilder(BiomeTags.MINESHAFT_HAS_STRUCTURE).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER).addTag(BiomeTags.IS_BEACH).addTag(BiomeTags.IS_MOUNTAIN).addTag(BiomeTags.IS_HILL).addTag(BiomeTags.IS_TAIGA).addTag(BiomeTags.IS_JUNGLE).addTag(BiomeTags.IS_FOREST).add(BiomeKeys.STONY_SHORE);
        this.getOrCreateTagBuilder(BiomeTags.MINESHAFT_HAS_STRUCTURE).add(BiomeKeys.MUSHROOM_FIELDS).add(BiomeKeys.ICE_SPIKES).add(BiomeKeys.WINDSWEPT_SAVANNA).add(BiomeKeys.DESERT).add(BiomeKeys.SAVANNA).add(BiomeKeys.SNOWY_PLAINS).add(BiomeKeys.PLAINS).add(BiomeKeys.SUNFLOWER_PLAINS).add(BiomeKeys.SWAMP).add(BiomeKeys.SAVANNA_PLATEAU).add(BiomeKeys.DRIPSTONE_CAVES).add(BiomeKeys.LUSH_CAVES);
        this.getOrCreateTagBuilder(BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE).addTag(BiomeTags.IS_BADLANDS);
        this.getOrCreateTagBuilder(BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE).addTag(BiomeTags.IS_DEEP_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE).add(BiomeKeys.FROZEN_OCEAN).add(BiomeKeys.COLD_OCEAN).add(BiomeKeys.OCEAN).add(BiomeKeys.DEEP_FROZEN_OCEAN).add(BiomeKeys.DEEP_COLD_OCEAN).add(BiomeKeys.DEEP_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE).add(BiomeKeys.LUKEWARM_OCEAN).add(BiomeKeys.WARM_OCEAN).add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE).add(BiomeKeys.DESERT).add(BiomeKeys.PLAINS).add(BiomeKeys.SAVANNA).add(BiomeKeys.SNOWY_PLAINS).add(BiomeKeys.TAIGA).addTag(BiomeTags.IS_MOUNTAIN).add(BiomeKeys.GROVE);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE).add(BiomeKeys.DESERT);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE).addTag(BiomeTags.IS_JUNGLE);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE).addTag(BiomeTags.IS_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE).add(BiomeKeys.SWAMP);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE).addTag(BiomeTags.IS_BADLANDS).addTag(BiomeTags.IS_HILL).add(BiomeKeys.SAVANNA_PLATEAU).add(BiomeKeys.WINDSWEPT_SAVANNA).add(BiomeKeys.STONY_SHORE).addTag(BiomeTags.IS_MOUNTAIN);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE).addTag(BiomeTags.IS_BEACH).addTag(BiomeTags.IS_RIVER).addTag(BiomeTags.IS_TAIGA).addTag(BiomeTags.IS_FOREST).add(BiomeKeys.MUSHROOM_FIELDS).add(BiomeKeys.ICE_SPIKES).add(BiomeKeys.DRIPSTONE_CAVES).add(BiomeKeys.LUSH_CAVES).add(BiomeKeys.SAVANNA).add(BiomeKeys.SNOWY_PLAINS).add(BiomeKeys.PLAINS).add(BiomeKeys.SUNFLOWER_PLAINS);
        this.getOrCreateTagBuilder(BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE).addTag(BiomeTags.IS_BEACH);
        this.getOrCreateTagBuilder(BiomeTags.SHIPWRECK_HAS_STRUCTURE).addTag(BiomeTags.IS_OCEAN);
        this.getOrCreateTagBuilder(BiomeTags.SWAMP_HUT_HAS_STRUCTURE).add(BiomeKeys.SWAMP);
        this.getOrCreateTagBuilder(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE).add(BiomeKeys.DESERT);
        this.getOrCreateTagBuilder(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE).add(BiomeKeys.PLAINS).add(BiomeKeys.MEADOW);
        this.getOrCreateTagBuilder(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE).add(BiomeKeys.SAVANNA);
        this.getOrCreateTagBuilder(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE).add(BiomeKeys.SNOWY_PLAINS);
        this.getOrCreateTagBuilder(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE).add(BiomeKeys.TAIGA);
        this.getOrCreateTagBuilder(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE).add(BiomeKeys.DARK_FOREST);
        this.getOrCreateTagBuilder(BiomeTags.STRONGHOLD_HAS_STRUCTURE).add(BiomeKeys.PLAINS).add(BiomeKeys.SUNFLOWER_PLAINS).add(BiomeKeys.SNOWY_PLAINS).add(BiomeKeys.ICE_SPIKES).add(BiomeKeys.DESERT).add(BiomeKeys.FOREST).add(BiomeKeys.FLOWER_FOREST).add(BiomeKeys.BIRCH_FOREST).add(BiomeKeys.DARK_FOREST).add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST).add(BiomeKeys.OLD_GROWTH_PINE_TAIGA).add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA).add(BiomeKeys.TAIGA).add(BiomeKeys.SNOWY_TAIGA).add(BiomeKeys.SAVANNA).add(BiomeKeys.SAVANNA_PLATEAU).add(BiomeKeys.WINDSWEPT_HILLS).add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS).add(BiomeKeys.WINDSWEPT_FOREST).add(BiomeKeys.WINDSWEPT_SAVANNA).add(BiomeKeys.JUNGLE).add(BiomeKeys.SPARSE_JUNGLE).add(BiomeKeys.BAMBOO_JUNGLE).add(BiomeKeys.BADLANDS).add(BiomeKeys.ERODED_BADLANDS).add(BiomeKeys.WOODED_BADLANDS).add(BiomeKeys.MEADOW).add(BiomeKeys.GROVE).add(BiomeKeys.SNOWY_SLOPES).add(BiomeKeys.FROZEN_PEAKS).add(BiomeKeys.JAGGED_PEAKS).add(BiomeKeys.STONY_PEAKS).add(BiomeKeys.MUSHROOM_FIELDS).add(BiomeKeys.DRIPSTONE_CAVES).add(BiomeKeys.LUSH_CAVES);
        this.getOrCreateTagBuilder(BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE).addTag(BiomeTags.IS_NETHER);
        this.getOrCreateTagBuilder(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE).add(BiomeKeys.SOUL_SAND_VALLEY);
        this.getOrCreateTagBuilder(BiomeTags.BASTION_REMNANT_HAS_STRUCTURE).add(BiomeKeys.CRIMSON_FOREST).add(BiomeKeys.NETHER_WASTES).add(BiomeKeys.SOUL_SAND_VALLEY).add(BiomeKeys.WARPED_FOREST);
        this.getOrCreateTagBuilder(BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE).addTag(BiomeTags.IS_NETHER);
        this.getOrCreateTagBuilder(BiomeTags.END_CITY_HAS_STRUCTURE).add(BiomeKeys.END_HIGHLANDS).add(BiomeKeys.END_MIDLANDS);
    }

    @Override
    public String getName() {
        return "Biome Tags";
    }
}

