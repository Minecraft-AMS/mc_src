/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class BiomesFix
extends DataFix {
    public static final Map<String, String> RENAMED_BIOMES = ImmutableMap.builder().put((Object)"minecraft:extreme_hills", (Object)"minecraft:mountains").put((Object)"minecraft:swampland", (Object)"minecraft:swamp").put((Object)"minecraft:hell", (Object)"minecraft:nether").put((Object)"minecraft:sky", (Object)"minecraft:the_end").put((Object)"minecraft:ice_flats", (Object)"minecraft:snowy_tundra").put((Object)"minecraft:ice_mountains", (Object)"minecraft:snowy_mountains").put((Object)"minecraft:mushroom_island", (Object)"minecraft:mushroom_fields").put((Object)"minecraft:mushroom_island_shore", (Object)"minecraft:mushroom_field_shore").put((Object)"minecraft:beaches", (Object)"minecraft:beach").put((Object)"minecraft:forest_hills", (Object)"minecraft:wooded_hills").put((Object)"minecraft:smaller_extreme_hills", (Object)"minecraft:mountain_edge").put((Object)"minecraft:stone_beach", (Object)"minecraft:stone_shore").put((Object)"minecraft:cold_beach", (Object)"minecraft:snowy_beach").put((Object)"minecraft:roofed_forest", (Object)"minecraft:dark_forest").put((Object)"minecraft:taiga_cold", (Object)"minecraft:snowy_taiga").put((Object)"minecraft:taiga_cold_hills", (Object)"minecraft:snowy_taiga_hills").put((Object)"minecraft:redwood_taiga", (Object)"minecraft:giant_tree_taiga").put((Object)"minecraft:redwood_taiga_hills", (Object)"minecraft:giant_tree_taiga_hills").put((Object)"minecraft:extreme_hills_with_trees", (Object)"minecraft:wooded_mountains").put((Object)"minecraft:savanna_rock", (Object)"minecraft:savanna_plateau").put((Object)"minecraft:mesa", (Object)"minecraft:badlands").put((Object)"minecraft:mesa_rock", (Object)"minecraft:wooded_badlands_plateau").put((Object)"minecraft:mesa_clear_rock", (Object)"minecraft:badlands_plateau").put((Object)"minecraft:sky_island_low", (Object)"minecraft:small_end_islands").put((Object)"minecraft:sky_island_medium", (Object)"minecraft:end_midlands").put((Object)"minecraft:sky_island_high", (Object)"minecraft:end_highlands").put((Object)"minecraft:sky_island_barren", (Object)"minecraft:end_barrens").put((Object)"minecraft:void", (Object)"minecraft:the_void").put((Object)"minecraft:mutated_plains", (Object)"minecraft:sunflower_plains").put((Object)"minecraft:mutated_desert", (Object)"minecraft:desert_lakes").put((Object)"minecraft:mutated_extreme_hills", (Object)"minecraft:gravelly_mountains").put((Object)"minecraft:mutated_forest", (Object)"minecraft:flower_forest").put((Object)"minecraft:mutated_taiga", (Object)"minecraft:taiga_mountains").put((Object)"minecraft:mutated_swampland", (Object)"minecraft:swamp_hills").put((Object)"minecraft:mutated_ice_flats", (Object)"minecraft:ice_spikes").put((Object)"minecraft:mutated_jungle", (Object)"minecraft:modified_jungle").put((Object)"minecraft:mutated_jungle_edge", (Object)"minecraft:modified_jungle_edge").put((Object)"minecraft:mutated_birch_forest", (Object)"minecraft:tall_birch_forest").put((Object)"minecraft:mutated_birch_forest_hills", (Object)"minecraft:tall_birch_hills").put((Object)"minecraft:mutated_roofed_forest", (Object)"minecraft:dark_forest_hills").put((Object)"minecraft:mutated_taiga_cold", (Object)"minecraft:snowy_taiga_mountains").put((Object)"minecraft:mutated_redwood_taiga", (Object)"minecraft:giant_spruce_taiga").put((Object)"minecraft:mutated_redwood_taiga_hills", (Object)"minecraft:giant_spruce_taiga_hills").put((Object)"minecraft:mutated_extreme_hills_with_trees", (Object)"minecraft:modified_gravelly_mountains").put((Object)"minecraft:mutated_savanna", (Object)"minecraft:shattered_savanna").put((Object)"minecraft:mutated_savanna_rock", (Object)"minecraft:shattered_savanna_plateau").put((Object)"minecraft:mutated_mesa", (Object)"minecraft:eroded_badlands").put((Object)"minecraft:mutated_mesa_rock", (Object)"minecraft:modified_wooded_badlands_plateau").put((Object)"minecraft:mutated_mesa_clear_rock", (Object)"minecraft:modified_badlands_plateau").put((Object)"minecraft:warm_deep_ocean", (Object)"minecraft:deep_warm_ocean").put((Object)"minecraft:lukewarm_deep_ocean", (Object)"minecraft:deep_lukewarm_ocean").put((Object)"minecraft:cold_deep_ocean", (Object)"minecraft:deep_cold_ocean").put((Object)"minecraft:frozen_deep_ocean", (Object)"minecraft:deep_frozen_ocean").build();

    public BiomesFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.BIOME.typeName(), (Type)DSL.namespacedString());
        if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.BIOME))) {
            throw new IllegalStateException("Biome type is not what was expected.");
        }
        return this.fixTypeEverywhere("Biomes fix", type, dynamicOps -> pair -> pair.mapSecond(string -> RENAMED_BIOMES.getOrDefault(string, (String)string)));
    }
}

