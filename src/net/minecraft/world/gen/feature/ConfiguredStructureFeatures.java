/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatureKeys;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.RangeFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> PILLAGER_OUTPOST = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.PILLAGER_OUTPOST, StructureFeature.PILLAGER_OUTPOST.configure(new StructurePoolFeatureConfig(PillagerOutpostGenerator.STRUCTURE_POOLS, 7), BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE, true, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1)})))));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MINESHAFT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MINESHAFT, StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.NORMAL), BiomeTags.MINESHAFT_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MINESHAFT_MESA = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MINESHAFT_MESA, StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.MESA), BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MANSION = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MANSION, StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> JUNGLE_PYRAMID = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.JUNGLE_PYRAMID, StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.DESERT_PYRAMID, StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> IGLOO = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.IGLOO, StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.IGLOO_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SHIPWRECK = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.SHIPWRECK, StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false), BiomeTags.SHIPWRECK_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SHIPWRECK_BEACHED = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.SHIPWRECK_BEACHED, StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true), BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SWAMP_HUT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.SWAMP_HUT, StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.SWAMP_HUT_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1)})), SpawnGroup.CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1)})))));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> STRONGHOLD = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.STRONGHOLD, StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.STRONGHOLD_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MONUMENT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MONUMENT, StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)})), SpawnGroup.UNDERGROUND_WATER_CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL), SpawnGroup.AXOLOTLS, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL))));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_COLD = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.OCEAN_RUIN_COLD, StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f), BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_WARM = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.OCEAN_RUIN_WARM, StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3f, 0.9f), BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> FORTRESS = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.FORTRESS, StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, NetherFortressFeature.MONSTER_SPAWNS))));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> NETHER_FOSSIL = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.NETHER_FOSSIL, StructureFeature.NETHER_FOSSIL.configure(new RangeFeatureConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))), BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> END_CITY = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.END_CITY, StructureFeature.ENDCITY.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.END_CITY_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> BURIED_TREASURE = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.BURIED_TREASURE, StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01f), BiomeTags.BURIED_TREASURE_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> BASTION_REMNANT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.BASTION_REMNANT, StructureFeature.BASTION_REMNANT.configure(new StructurePoolFeatureConfig(BastionRemnantGenerator.STRUCTURE_POOLS, 6), BiomeTags.BASTION_REMNANT_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> VILLAGE_PLAINS = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_PLAINS, StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(PlainsVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> VILLAGE_DESERT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_DESERT, StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(DesertVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> VILLAGE_SAVANNA = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_SAVANNA, StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(SavannaVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> VILLAGE_SNOWY = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_SNOWY, StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(SnowyVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> VILLAGE_TAIGA = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_TAIGA, StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(TaigaVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE, true));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD), BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_DESERT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_DESERT, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT), BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_JUNGLE, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE), BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_SWAMP = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_SWAMP, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP), BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_MOUNTAIN, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN), BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_OCEAN = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_OCEAN, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN), BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE));
    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_NETHER = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_NETHER, StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER), BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE));

    public static RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> getDefault() {
        return MINESHAFT;
    }

    private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<?, ?>> register(RegistryKey<ConfiguredStructureFeature<?, ?>> key, ConfiguredStructureFeature<FC, F> configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, key, configuredStructureFeature);
    }
}

