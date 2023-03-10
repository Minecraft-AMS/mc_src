/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RandomOffsetPlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceThresholdFilterPlacementModifier;

public class UndergroundPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> MONSTER_ROOM = PlacedFeatures.register("monster_room", UndergroundConfiguredFeatures.MONSTER_ROOM, CountPlacementModifier.of(10), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.getTop()), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> MONSTER_ROOM_DEEP = PlacedFeatures.register("monster_room_deep", UndergroundConfiguredFeatures.MONSTER_ROOM, CountPlacementModifier.of(4), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.aboveBottom(6), YOffset.fixed(-1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FOSSIL_UPPER = PlacedFeatures.register("fossil_upper", UndergroundConfiguredFeatures.FOSSIL_COAL, RarityFilterPlacementModifier.of(64), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.getTop()), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FOSSIL_LOWER = PlacedFeatures.register("fossil_lower", UndergroundConfiguredFeatures.FOSSIL_DIAMONDS, RarityFilterPlacementModifier.of(64), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(-8)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> DRIPSTONE_CLUSTER = PlacedFeatures.register("dripstone_cluster", UndergroundConfiguredFeatures.DRIPSTONE_CLUSTER, CountPlacementModifier.of(UniformIntProvider.create(48, 96)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> LARGE_DRIPSTONE = PlacedFeatures.register("large_dripstone", UndergroundConfiguredFeatures.LARGE_DRIPSTONE, CountPlacementModifier.of(UniformIntProvider.create(10, 48)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> POINTED_DRIPSTONE = PlacedFeatures.register("pointed_dripstone", UndergroundConfiguredFeatures.POINTED_DRIPSTONE, CountPlacementModifier.of(UniformIntProvider.create(192, 256)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, CountPlacementModifier.of(UniformIntProvider.create(1, 5)), RandomOffsetPlacementModifier.of(ClampedNormalIntProvider.of(0.0f, 3.0f, -10, 10), ClampedNormalIntProvider.of(0.0f, 0.6f, -2, 2)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> UNDERWATER_MAGMA = PlacedFeatures.register("underwater_magma", UndergroundConfiguredFeatures.UNDERWATER_MAGMA, CountPlacementModifier.of(UniformIntProvider.create(44, 52)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, SurfaceThresholdFilterPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -2), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> GLOW_LICHEN = PlacedFeatures.register("glow_lichen", UndergroundConfiguredFeatures.GLOW_LICHEN, CountPlacementModifier.of(UniformIntProvider.create(104, 157)), PlacedFeatures.BOTTOM_TO_120_RANGE, SquarePlacementModifier.of(), SurfaceThresholdFilterPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ROOTED_AZALEA_TREE = PlacedFeatures.register("rooted_azalea_tree", UndergroundConfiguredFeatures.ROOTED_AZALEA_TREE, CountPlacementModifier.of(UniformIntProvider.create(1, 2)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12), RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> CAVE_VINES = PlacedFeatures.register("cave_vines", UndergroundConfiguredFeatures.CAVE_VINE, CountPlacementModifier.of(188), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN), BlockPredicate.IS_AIR, 12), RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> LUSH_CAVES_VEGETATION = PlacedFeatures.register("lush_caves_vegetation", UndergroundConfiguredFeatures.MOSS_PATCH, CountPlacementModifier.of(125), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12), RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> LUSH_CAVES_CLAY = PlacedFeatures.register("lush_caves_clay", UndergroundConfiguredFeatures.LUSH_CAVES_CLAY, CountPlacementModifier.of(62), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12), RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> LUSH_CAVES_CEILING_VEGETATION = PlacedFeatures.register("lush_caves_ceiling_vegetation", UndergroundConfiguredFeatures.MOSS_PATCH_CEILING, CountPlacementModifier.of(125), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12), RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> SPORE_BLOSSOM = PlacedFeatures.register("spore_blossom", UndergroundConfiguredFeatures.SPORE_BLOSSOM, CountPlacementModifier.of(25), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12), RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> CLASSIC_VINES_CAVE_FEATURE = PlacedFeatures.register("classic_vines_cave_feature", VegetationConfiguredFeatures.VINES, CountPlacementModifier.of(256), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> AMETHYST_GEODE = PlacedFeatures.register("amethyst_geode", UndergroundConfiguredFeatures.AMETHYST_GEODE, RarityFilterPlacementModifier.of(24), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.aboveBottom(6), YOffset.fixed(30)), BiomePlacementModifier.of());
}

