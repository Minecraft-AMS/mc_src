/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.PileConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class VillagePlacedFeatures {
    public static final RegistryEntry<PlacedFeature> PILE_HAY = PlacedFeatures.register("pile_hay", PileConfiguredFeatures.PILE_HAY, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> PILE_MELON = PlacedFeatures.register("pile_melon", PileConfiguredFeatures.PILE_MELON, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> PILE_SNOW = PlacedFeatures.register("pile_snow", PileConfiguredFeatures.PILE_SNOW, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> PILE_ICE = PlacedFeatures.register("pile_ice", PileConfiguredFeatures.PILE_ICE, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> PILE_PUMPKIN = PlacedFeatures.register("pile_pumpkin", PileConfiguredFeatures.PILE_PUMPKIN, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> OAK = PlacedFeatures.register("oak", TreeConfiguredFeatures.OAK, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
    public static final RegistryEntry<PlacedFeature> ACACIA = PlacedFeatures.register("acacia", TreeConfiguredFeatures.ACACIA, PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING));
    public static final RegistryEntry<PlacedFeature> SPRUCE = PlacedFeatures.register("spruce", TreeConfiguredFeatures.SPRUCE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
    public static final RegistryEntry<PlacedFeature> PINE = PlacedFeatures.register("pine", TreeConfiguredFeatures.PINE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
    public static final RegistryEntry<PlacedFeature> PATCH_CACTUS = PlacedFeatures.register("patch_cactus", VegetationConfiguredFeatures.PATCH_CACTUS, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> FLOWER_PLAIN = PlacedFeatures.register("flower_plain", VegetationConfiguredFeatures.FLOWER_PLAIN, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> PATCH_TAIGA_GRASS = PlacedFeatures.register("patch_taiga_grass", VegetationConfiguredFeatures.PATCH_TAIGA_GRASS, new PlacementModifier[0]);
    public static final RegistryEntry<PlacedFeature> PATCH_BERRY_BUSH = PlacedFeatures.register("patch_berry_bush", VegetationConfiguredFeatures.PATCH_BERRY_BUSH, new PlacementModifier[0]);
}

