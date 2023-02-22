/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;

public class TaigaVillageData {
    public static void initialize() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8f), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()), (Object)new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.CAMPFIRE), AlwaysTrueRuleTest.INSTANCE, (BlockState)Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, false)), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.08f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SPRUCE_LOG, 0.08f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new BlockStateMatchRuleTest((BlockState)((BlockState)Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)), (Object)new StructureProcessorRule(new BlockStateMatchRuleTest((BlockState)((BlockState)Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true)), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.PUMPKIN_STEM.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()), (Object[])new StructureProcessorRule[0])));
        ImmutableList immutableList2 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/town_centers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/town_centers/taiga_meeting_point_1", (List<StructureProcessor>)immutableList2), (Object)49), (Object)new Pair((Object)new SinglePoolElement("village/taiga/town_centers/taiga_meeting_point_2", (List<StructureProcessor>)immutableList2), (Object)49), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/town_centers/taiga_meeting_point_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/town_centers/taiga_meeting_point_2", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        ImmutableList immutableList3 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.SPRUCE_PLANKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GRASS_PATH, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/streets"), new Identifier("village/taiga/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/corner_01", (List<StructureProcessor>)immutableList3), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/corner_02", (List<StructureProcessor>)immutableList3), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/corner_03", (List<StructureProcessor>)immutableList3), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/straight_01", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/straight_02", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/straight_03", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/straight_04", (List<StructureProcessor>)immutableList3), (Object)7), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/straight_05", (List<StructureProcessor>)immutableList3), (Object)7), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/straight_06", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/crossroad_01", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/crossroad_02", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/streets/crossroad_03", (List<StructureProcessor>)immutableList3), (Object)2), (Object[])new Pair[]{new Pair((Object)new SinglePoolElement("village/taiga/streets/crossroad_04", (List<StructureProcessor>)immutableList3), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/streets/crossroad_05", (List<StructureProcessor>)immutableList3), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/streets/crossroad_06", (List<StructureProcessor>)immutableList3), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/streets/turn_01", (List<StructureProcessor>)immutableList3), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/zombie/streets"), new Identifier("village/taiga/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/corner_01", (List<StructureProcessor>)immutableList3), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/corner_02", (List<StructureProcessor>)immutableList3), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/corner_03", (List<StructureProcessor>)immutableList3), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/straight_01", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/straight_02", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/straight_03", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/straight_04", (List<StructureProcessor>)immutableList3), (Object)7), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/straight_05", (List<StructureProcessor>)immutableList3), (Object)7), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/straight_06", (List<StructureProcessor>)immutableList3), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/crossroad_01", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/crossroad_02", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/crossroad_03", (List<StructureProcessor>)immutableList3), (Object)2), (Object[])new Pair[]{new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/crossroad_04", (List<StructureProcessor>)immutableList3), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/crossroad_05", (List<StructureProcessor>)immutableList3), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/crossroad_06", (List<StructureProcessor>)immutableList3), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/streets/turn_01", (List<StructureProcessor>)immutableList3), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        ImmutableList immutableList4 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.PUMPKIN_STEM.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/houses"), new Identifier("village/taiga/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_house_1", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_house_2", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_house_3", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_house_4", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_house_5", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_medium_house_1", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_medium_house_2", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_medium_house_3", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_medium_house_4", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_butcher_shop_1", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_tool_smith_1", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_fletcher_house_1", (List<StructureProcessor>)immutableList2), (Object)2), (Object[])new Pair[]{new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_shepherds_house_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_armorer_house_1", (List<StructureProcessor>)immutableList2), (Object)1), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_armorer_2", (List<StructureProcessor>)immutableList2), (Object)1), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_fisher_cottage_1", (List<StructureProcessor>)immutableList2), (Object)3), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_tannery_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_cartographer_house_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_library_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_masons_house_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_weaponsmith_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_weaponsmith_2", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_temple_1", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_large_farm_1", (List<StructureProcessor>)immutableList4), (Object)6), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_large_farm_2", (List<StructureProcessor>)immutableList4), (Object)6), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_farm_1", (List<StructureProcessor>)immutableList2), (Object)1), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_animal_pen_1", (List<StructureProcessor>)immutableList2), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)6)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/zombie/houses"), new Identifier("village/taiga/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_1", (List<StructureProcessor>)immutableList), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_2", (List<StructureProcessor>)immutableList), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_3", (List<StructureProcessor>)immutableList), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_4", (List<StructureProcessor>)immutableList), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_5", (List<StructureProcessor>)immutableList), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_3", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_4", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_butcher_shop_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_tool_smith_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_fletcher_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object[])new Pair[]{new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_shepherds_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_armorer_house_1", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_fisher_cottage_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_tannery_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_cartographer_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_library_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_masons_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_weaponsmith_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_weaponsmith_2", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_temple_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_large_farm_1", (List<StructureProcessor>)immutableList), (Object)6), new Pair((Object)new SinglePoolElement("village/taiga/zombie/houses/taiga_large_farm_2", (List<StructureProcessor>)immutableList), (Object)6), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_small_farm_1", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new SinglePoolElement("village/taiga/houses/taiga_animal_pen_1", (List<StructureProcessor>)immutableList), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)6)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/terminators"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/plains/terminators/terminator_01", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/plains/terminators/terminator_02", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/plains/terminators/terminator_03", (List<StructureProcessor>)immutableList3), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/plains/terminators/terminator_04", (List<StructureProcessor>)immutableList3), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_lamp_post_1"), (Object)10), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_1"), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_2"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_3"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_4"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_5"), (Object)2), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_6"), (Object)1), (Object)new Pair((Object)new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.PINE_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.PUMPKIN_PILE_CONFIG)), (Object)2), (Object)new Pair((Object)new FeaturePoolElement(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.TAIGA_GRASS_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.SWEET_BERRY_BUSH_CONFIG)), (Object)1), (Object[])new Pair[]{Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)4)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/zombie/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_1"), (Object)4), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_2"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_3"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/taiga_decoration_4"), (Object)1), (Object)new Pair((Object)new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.PINE_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.PUMPKIN_PILE_CONFIG)), (Object)2), (Object)new Pair((Object)new FeaturePoolElement(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.TAIGA_GRASS_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.SWEET_BERRY_BUSH_CONFIG)), (Object)1), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)4)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/villagers/baby"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/taiga/zombie/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new SinglePoolElement("village/taiga/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
    }
}

