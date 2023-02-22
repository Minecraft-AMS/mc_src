/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class StructuresToConfiguredStructuresFix
extends DataFix {
    private static final Map<String, Mapping> STRUCTURE_TO_CONFIGURED_STRUCTURES_MAPPING = ImmutableMap.builder().put((Object)"mineshaft", (Object)Mapping.create(Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft")).put((Object)"shipwreck", (Object)Mapping.create(Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck")).put((Object)"ocean_ruin", (Object)Mapping.create(Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"), "minecraft:ocean_ruin_cold")).put((Object)"village", (Object)Mapping.create(Map.of(List.of("minecraft:desert"), "minecraft:village_desert", List.of("minecraft:savanna"), "minecraft:village_savanna", List.of("minecraft:snowy_plains"), "minecraft:village_snowy", List.of("minecraft:taiga"), "minecraft:village_taiga"), "minecraft:village_plains")).put((Object)"ruined_portal", (Object)Mapping.create(Map.of(List.of("minecraft:desert"), "minecraft:ruined_portal_desert", List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:windswept_hills", "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:stony_shore", "minecraft:meadow", "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes"), "minecraft:ruined_portal_mountain", List.of("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle"), "minecraft:ruined_portal_jungle", List.of("minecraft:deep_frozen_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:frozen_ocean", "minecraft:ocean", "minecraft:cold_ocean", "minecraft:lukewarm_ocean", "minecraft:warm_ocean"), "minecraft:ruined_portal_ocean"), "minecraft:ruined_portal")).put((Object)"pillager_outpost", (Object)Mapping.create("minecraft:pillager_outpost")).put((Object)"mansion", (Object)Mapping.create("minecraft:mansion")).put((Object)"jungle_pyramid", (Object)Mapping.create("minecraft:jungle_pyramid")).put((Object)"desert_pyramid", (Object)Mapping.create("minecraft:desert_pyramid")).put((Object)"igloo", (Object)Mapping.create("minecraft:igloo")).put((Object)"swamp_hut", (Object)Mapping.create("minecraft:swamp_hut")).put((Object)"stronghold", (Object)Mapping.create("minecraft:stronghold")).put((Object)"monument", (Object)Mapping.create("minecraft:monument")).put((Object)"fortress", (Object)Mapping.create("minecraft:fortress")).put((Object)"endcity", (Object)Mapping.create("minecraft:end_city")).put((Object)"buried_treasure", (Object)Mapping.create("minecraft:buried_treasure")).put((Object)"nether_fossil", (Object)Mapping.create("minecraft:nether_fossil")).put((Object)"bastion_remnant", (Object)Mapping.create("minecraft:bastion_remnant")).build();

    public StructuresToConfiguredStructuresFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type type2 = this.getInputSchema().getType(TypeReferences.CHUNK);
        return this.writeFixAndRead("StucturesToConfiguredStructures", type, type2, this::method_41012);
    }

    private Dynamic<?> method_41012(Dynamic<?> dynamic) {
        return dynamic.update("structures", dynamic22 -> dynamic22.update("starts", dynamic2 -> this.method_41015((Dynamic<?>)dynamic2, dynamic)).update("References", dynamic2 -> this.method_41020((Dynamic<?>)dynamic2, dynamic)));
    }

    private Dynamic<?> method_41015(Dynamic<?> dynamic3, Dynamic<?> dynamic22) {
        Map map = (Map)dynamic3.getMapValues().result().get();
        ArrayList list = new ArrayList();
        map.forEach((dynamic, dynamic2) -> {
            if (dynamic2.get("id").asString("INVALID").equals("INVALID")) {
                list.add(dynamic);
            }
        });
        for (Dynamic dynamic32 : list) {
            dynamic3 = dynamic3.remove(dynamic32.asString(""));
        }
        return dynamic3.updateMapValues(pair -> this.method_41010((Pair<Dynamic<?>, Dynamic<?>>)pair, dynamic22));
    }

    private Pair<Dynamic<?>, Dynamic<?>> method_41010(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
        Dynamic<?> dynamic2 = this.method_41022(pair, dynamic);
        return new Pair(dynamic2, (Object)((Dynamic)pair.getSecond()).set("id", dynamic2));
    }

    private Dynamic<?> method_41020(Dynamic<?> dynamic3, Dynamic<?> dynamic22) {
        Map map = (Map)dynamic3.getMapValues().result().get();
        ArrayList list = new ArrayList();
        map.forEach((dynamic, dynamic2) -> {
            if (dynamic2.asLongStream().count() == 0L) {
                list.add(dynamic);
            }
        });
        for (Dynamic dynamic32 : list) {
            dynamic3 = dynamic3.remove(dynamic32.asString(""));
        }
        return dynamic3.updateMapValues(pair -> this.method_41018((Pair<Dynamic<?>, Dynamic<?>>)pair, dynamic22));
    }

    private Pair<Dynamic<?>, Dynamic<?>> method_41018(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
        return pair.mapFirst(dynamic2 -> this.method_41022(pair, dynamic));
    }

    private Dynamic<?> method_41022(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
        Optional<String> optional;
        String string = ((Dynamic)pair.getFirst()).asString("UNKNOWN").toLowerCase(Locale.ROOT);
        Mapping mapping = STRUCTURE_TO_CONFIGURED_STRUCTURES_MAPPING.get(string);
        if (mapping == null) {
            throw new IllegalStateException("Found unknown structure: " + string);
        }
        Dynamic dynamic2 = (Dynamic)pair.getSecond();
        String string2 = mapping.fallback;
        if (!mapping.biomeMapping().isEmpty() && (optional = this.method_41013(dynamic, mapping)).isPresent()) {
            string2 = optional.get();
        }
        Dynamic dynamic3 = dynamic2.createString(string2);
        return dynamic3;
    }

    private Optional<String> method_41013(Dynamic<?> dynamic, Mapping mapping) {
        Object2IntArrayMap object2IntArrayMap = new Object2IntArrayMap();
        dynamic.get("sections").asList(Function.identity()).forEach(dynamic2 -> dynamic2.get("biomes").get("palette").asList(Function.identity()).forEach(dynamic -> {
            String string = mapping.biomeMapping().get(dynamic.asString(""));
            if (string != null) {
                object2IntArrayMap.mergeInt((Object)string, 1, Integer::sum);
            }
        }));
        return object2IntArrayMap.object2IntEntrySet().stream().max(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).map(Map.Entry::getKey);
    }

    static final class Mapping
    extends Record {
        private final Map<String, String> biomeMapping;
        final String fallback;

        private Mapping(Map<String, String> map, String string) {
            this.biomeMapping = map;
            this.fallback = string;
        }

        public static Mapping create(String mapping) {
            return new Mapping(Map.of(), mapping);
        }

        public static Mapping create(Map<List<String>, String> biomeMapping, String fallback) {
            return new Mapping(Mapping.flattenBiomeMapping(biomeMapping), fallback);
        }

        private static Map<String, String> flattenBiomeMapping(Map<List<String>, String> biomeMapping) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (Map.Entry<List<String>, String> entry : biomeMapping.entrySet()) {
                entry.getKey().forEach(string -> builder.put(string, (Object)((String)entry.getValue())));
            }
            return builder.build();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Mapping.class, "biomeMapping;fallback", "biomeMapping", "fallback"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Mapping.class, "biomeMapping;fallback", "biomeMapping", "fallback"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Mapping.class, "biomeMapping;fallback", "biomeMapping", "fallback"}, this, object);
        }

        public Map<String, String> biomeMapping() {
            return this.biomeMapping;
        }

        public String fallback() {
            return this.fallback;
        }
    }
}

