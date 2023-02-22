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
import java.util.function.Function;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class PlainsVillageData {
    public static final StructurePool field_26253 = StructurePools.register(new StructurePool(new Identifier("village/plains/town_centers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/town_centers/plains_fountain_01", StructureProcessorLists.MOSSIFY_20_PERCENT), (Object)50), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/town_centers/plains_meeting_point_1", StructureProcessorLists.MOSSIFY_20_PERCENT), (Object)50), (Object)Pair.of(StructurePoolElement.method_30425("village/plains/town_centers/plains_meeting_point_2"), (Object)50), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/town_centers/plains_meeting_point_3", StructureProcessorLists.MOSSIFY_70_PERCENT), (Object)50), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_fountain_01", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_meeting_point_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_meeting_point_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_meeting_point_3", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
    }

    static {
        StructurePools.register(new StructurePool(new Identifier("village/plains/streets"), new Identifier("village/plains/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/corner_01", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/corner_02", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/corner_03", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_01", StructureProcessorLists.STREET_PLAINS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_02", StructureProcessorLists.STREET_PLAINS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_03", StructureProcessorLists.STREET_PLAINS), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_04", StructureProcessorLists.STREET_PLAINS), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_05", StructureProcessorLists.STREET_PLAINS), (Object)3), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_06", StructureProcessorLists.STREET_PLAINS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_01", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_02", StructureProcessorLists.STREET_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_03", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_04", StructureProcessorLists.STREET_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_05", StructureProcessorLists.STREET_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_06", StructureProcessorLists.STREET_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/streets/turn_01", StructureProcessorLists.STREET_PLAINS), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("village/plains/zombie/streets"), new Identifier("village/plains/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/corner_01", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/corner_02", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/corner_03", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_01", StructureProcessorLists.STREET_PLAINS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_02", StructureProcessorLists.STREET_PLAINS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_03", StructureProcessorLists.STREET_PLAINS), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_04", StructureProcessorLists.STREET_PLAINS), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_05", StructureProcessorLists.STREET_PLAINS), (Object)3), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_06", StructureProcessorLists.STREET_PLAINS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_01", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_02", StructureProcessorLists.STREET_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_03", StructureProcessorLists.STREET_PLAINS), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_04", StructureProcessorLists.STREET_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_05", StructureProcessorLists.STREET_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_06", StructureProcessorLists.STREET_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/turn_01", StructureProcessorLists.STREET_PLAINS), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("village/plains/houses"), new Identifier("village/plains/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_3", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_4", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_5", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_6", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_7", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_8", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)3), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_medium_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_medium_house_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_big_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_butcher_shop_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_butcher_shop_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tool_smith_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_fletcher_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_shepherds_house_1"), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_armorer_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_fisher_cottage_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tannery_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_cartographer_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)5), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_masons_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_weaponsmith_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_3", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_4", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_stable_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_stable_2"), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_large_farm_1", StructureProcessorLists.FARM_PLAINS), (Object)4), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_farm_1", StructureProcessorLists.FARM_PLAINS), (Object)4), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_animal_pen_1"), (Object)1), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_animal_pen_2"), (Object)1), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_animal_pen_3"), (Object)5), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_accessory_1"), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_meeting_point_4", StructureProcessorLists.MOSSIFY_70_PERCENT), (Object)3), Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_meeting_point_5"), (Object)1), Pair.of(StructurePoolElement.method_30438(), (Object)10)}), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/plains/zombie/houses"), new Identifier("village/plains/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_3", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_4", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_5", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_6", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_7", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_8", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_medium_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_medium_house_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_big_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_butcher_shop_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_butcher_shop_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tool_smith_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_fletcher_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_shepherds_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_armorer_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_fisher_cottage_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tannery_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_cartographer_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)3), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_masons_house_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_weaponsmith_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_3", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_4", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_stable_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_stable_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)2), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_large_farm_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)4), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_farm_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)4), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_animal_pen_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_animal_pen_2", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_animal_pen_3", StructureProcessorLists.ZOMBIE_PLAINS), (Object)5), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_meeting_point_4", StructureProcessorLists.ZOMBIE_PLAINS), (Object)3), Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_meeting_point_5", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), Pair.of(StructurePoolElement.method_30438(), (Object)10)}), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/plains/terminators"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", StructureProcessorLists.STREET_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", StructureProcessorLists.STREET_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", StructureProcessorLists.STREET_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", StructureProcessorLists.STREET_PLAINS), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("village/plains/trees"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/plains/decor"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/plains/plains_lamp_1"), (Object)2), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.FLOWER_PLAIN), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), (Object)1), (Object)Pair.of(StructurePoolElement.method_30438(), (Object)2)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/plains/zombie/decor"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/plains_lamp_1", StructureProcessorLists.ZOMBIE_PLAINS), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.FLOWER_PLAIN), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), (Object)1), (Object)Pair.of(StructurePoolElement.method_30438(), (Object)2)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/plains/villagers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/plains/villagers/nitwit"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/plains/villagers/baby"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/plains/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/plains/zombie/villagers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/plains/zombie/villagers/nitwit"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/plains/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/common/animals"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cows_1"), (Object)7), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/pigs_1"), (Object)7), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_1"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_2"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_3"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_4"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_5"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_1"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_2"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30438(), (Object)5)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/common/sheep"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_1"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_2"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/common/cats"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_black"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_british"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_calico"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_persian"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_ragdoll"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_red"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_siamese"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_tabby"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_white"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_jellie"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30438(), (Object)3)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/common/butcher_animals"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/cows_1"), (Object)3), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/pigs_1"), (Object)3), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_1"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_2"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/common/iron_golem"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/common/iron_golem"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/common/well_bottoms"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/common/well_bottom"), (Object)1)), StructurePool.Projection.RIGID));
    }
}

