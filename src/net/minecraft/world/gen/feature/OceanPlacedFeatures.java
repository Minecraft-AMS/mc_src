/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.OceanConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CarvingMaskPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.NoiseBasedCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class OceanPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> SEAGRASS_WARM = PlacedFeatures.register("seagrass_warm", OceanConfiguredFeatures.SEAGRASS_SHORT, OceanPlacedFeatures.seagrassModifiers(80));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_NORMAL = PlacedFeatures.register("seagrass_normal", OceanConfiguredFeatures.SEAGRASS_SHORT, OceanPlacedFeatures.seagrassModifiers(48));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_COLD = PlacedFeatures.register("seagrass_cold", OceanConfiguredFeatures.SEAGRASS_SHORT, OceanPlacedFeatures.seagrassModifiers(32));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_RIVER = PlacedFeatures.register("seagrass_river", OceanConfiguredFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT, OceanPlacedFeatures.seagrassModifiers(48));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_SWAMP = PlacedFeatures.register("seagrass_swamp", OceanConfiguredFeatures.SEAGRASS_MID, OceanPlacedFeatures.seagrassModifiers(64));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_DEEP_WARM = PlacedFeatures.register("seagrass_deep_warm", OceanConfiguredFeatures.SEAGRASS_TALL, OceanPlacedFeatures.seagrassModifiers(80));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_DEEP = PlacedFeatures.register("seagrass_deep", OceanConfiguredFeatures.SEAGRASS_TALL, OceanPlacedFeatures.seagrassModifiers(48));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_DEEP_COLD = PlacedFeatures.register("seagrass_deep_cold", OceanConfiguredFeatures.SEAGRASS_TALL, OceanPlacedFeatures.seagrassModifiers(40));
    public static final RegistryEntry<PlacedFeature> SEAGRASS_SIMPLE = PlacedFeatures.register("seagrass_simple", OceanConfiguredFeatures.SEAGRASS_SIMPLE, CarvingMaskPlacementModifier.of(GenerationStep.Carver.LIQUID), RarityFilterPlacementModifier.of(10), BlockFilterPlacementModifier.of(BlockPredicate.allOf(BlockPredicate.matchingBlock(Blocks.STONE, new BlockPos(0, -1, 0)), BlockPredicate.matchingBlock(Blocks.WATER, BlockPos.ORIGIN), BlockPredicate.matchingBlock(Blocks.WATER, new BlockPos(0, 1, 0)))), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> SEA_PICKLE = PlacedFeatures.register("sea_pickle", OceanConfiguredFeatures.SEA_PICKLE, RarityFilterPlacementModifier.of(16), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> KELP_COLD = PlacedFeatures.register("kelp_cold", OceanConfiguredFeatures.KELP, NoiseBasedCountPlacementModifier.of(120, 80.0, 0.0), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> KELP_WARM = PlacedFeatures.register("kelp_warm", OceanConfiguredFeatures.KELP, NoiseBasedCountPlacementModifier.of(80, 80.0, 0.0), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> WARM_OCEAN_VEGETATION = PlacedFeatures.register("warm_ocean_vegetation", OceanConfiguredFeatures.WARM_OCEAN_VEGETATION, NoiseBasedCountPlacementModifier.of(20, 400.0, 0.0), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of());

    public static List<PlacementModifier> seagrassModifiers(int count) {
        return List.of(SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, CountPlacementModifier.of(count), BiomePlacementModifier.of());
    }
}

