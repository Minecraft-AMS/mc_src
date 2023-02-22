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
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.VillagePlacedFeatures;

public class TaigaVillageData {
    public static final RegistryEntry<StructurePool> STRUCTURE_POOLS = StructurePools.register(new StructurePool(new Identifier("village/taiga/town_centers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/town_centers/taiga_meeting_point_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)49), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/town_centers/taiga_meeting_point_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)49), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/town_centers/taiga_meeting_point_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/town_centers/taiga_meeting_point_2", StructureProcessorLists.ZOMBIE_TAIGA), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
    }

    static {
        StructurePools.register(new StructurePool(new Identifier("village/taiga/streets"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/turn_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/zombie/streets"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/turn_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/houses"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_3", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_4", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_5", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_3", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_4", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_butcher_shop_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tool_smith_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fletcher_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_shepherds_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fisher_cottage_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)3), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tannery_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_cartographer_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_library_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_masons_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_2", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_temple_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_1", StructureProcessorLists.FARM_TAIGA), (Object)6), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_2", StructureProcessorLists.FARM_TAIGA), (Object)6), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_farm_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_animal_pen_1", StructureProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.ofEmpty(), (Object)6)}), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/zombie/houses"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_2", StructureProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_3", StructureProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_4", StructureProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_5", StructureProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_2", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_3", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_4", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_butcher_shop_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_tool_smith_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fletcher_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_shepherds_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)1), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_fisher_cottage_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tannery_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_cartographer_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_library_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_masons_house_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_weaponsmith_2", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_temple_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)6), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_large_farm_2", StructureProcessorLists.ZOMBIE_TAIGA), (Object)6), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_farm_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)1), Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_animal_pen_1", StructureProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.ofEmpty(), (Object)6)}), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/terminators"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/decor"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_lamp_post_1"), (Object)10), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_1"), (Object)4), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_2"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_3"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_4"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_5"), (Object)2), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_6"), (Object)1), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.SPRUCE), (Object)4), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PINE), (Object)4), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PILE_PUMPKIN), (Object)2), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PATCH_TAIGA_GRASS), (Object)4), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PATCH_BERRY_BUSH), (Object)1), (Object[])new Pair[]{Pair.of(StructurePoolElement.ofEmpty(), (Object)4)}), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/zombie/decor"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_1"), (Object)4), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_2"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_3"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_4"), (Object)1), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.SPRUCE), (Object)4), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PINE), (Object)4), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PILE_PUMPKIN), (Object)2), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PATCH_TAIGA_GRASS), (Object)4), (Object)Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PATCH_BERRY_BUSH), (Object)1), (Object)Pair.of(StructurePoolElement.ofEmpty(), (Object)4)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/villagers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/nitwit"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/baby"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("village/taiga/zombie/villagers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/zombie/villagers/nitwit"), (Object)1), (Object)Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
    }
}

