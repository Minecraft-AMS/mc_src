/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class StructureSeparationDataFix
extends DataFix {
    private static final String VILLAGE_STRUCTURE_ID = "minecraft:village";
    private static final String DESERT_PYRAMID_STRUCTURE_ID = "minecraft:desert_pyramid";
    private static final String IGLOO_STRUCTURE_ID = "minecraft:igloo";
    private static final String JUNGLE_PYRAMID_STRUCTURE_ID = "minecraft:jungle_pyramid";
    private static final String SWAMP_HUT_STRUCTURE_ID = "minecraft:swamp_hut";
    private static final String PILLAGER_OUTPOST_STRUCTURE_ID = "minecraft:pillager_outpost";
    private static final String END_CITY_STRUCTURE_ID = "minecraft:endcity";
    private static final String MANSION_STRUCTURE_ID = "minecraft:mansion";
    private static final String MONUMENT_STRUCTURE_ID = "minecraft:monument";
    private static final ImmutableMap<String, Information> STRUCTURE_SPACING = ImmutableMap.builder().put((Object)"minecraft:village", (Object)new Information(32, 8, 10387312)).put((Object)"minecraft:desert_pyramid", (Object)new Information(32, 8, 14357617)).put((Object)"minecraft:igloo", (Object)new Information(32, 8, 14357618)).put((Object)"minecraft:jungle_pyramid", (Object)new Information(32, 8, 14357619)).put((Object)"minecraft:swamp_hut", (Object)new Information(32, 8, 14357620)).put((Object)"minecraft:pillager_outpost", (Object)new Information(32, 8, 165745296)).put((Object)"minecraft:monument", (Object)new Information(32, 5, 10387313)).put((Object)"minecraft:endcity", (Object)new Information(20, 11, 10387313)).put((Object)"minecraft:mansion", (Object)new Information(80, 20, 10387319)).build();

    public StructureSeparationDataFix(Schema outputSchema) {
        super(outputSchema, true);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("WorldGenSettings building", this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS), typed -> typed.update(DSL.remainderFinder(), StructureSeparationDataFix::method_28271));
    }

    private static <T> Dynamic<T> method_28268(long l, DynamicLike<T> dynamicLike, Dynamic<T> dynamic, Dynamic<T> dynamic2) {
        return dynamicLike.createMap((Map)ImmutableMap.of((Object)dynamicLike.createString("type"), (Object)dynamicLike.createString("minecraft:noise"), (Object)dynamicLike.createString("biome_source"), dynamic2, (Object)dynamicLike.createString("seed"), (Object)dynamicLike.createLong(l), (Object)dynamicLike.createString("settings"), dynamic));
    }

    private static <T> Dynamic<T> method_28272(Dynamic<T> dynamic, long l, boolean bl, boolean bl2) {
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:vanilla_layered")).put((Object)dynamic.createString("seed"), (Object)dynamic.createLong(l)).put((Object)dynamic.createString("large_biomes"), (Object)dynamic.createBoolean(bl2));
        if (bl) {
            builder.put((Object)dynamic.createString("legacy_biome_init_layer"), (Object)dynamic.createBoolean(bl));
        }
        return dynamic.createMap((Map)builder.build());
    }

    private static <T> Dynamic<T> method_28271(Dynamic<T> dynamic2) {
        Dynamic<T> dynamic22;
        DynamicOps dynamicOps = dynamic2.getOps();
        long l = dynamic2.get("RandomSeed").asLong(0L);
        Optional optional = dynamic2.get("generatorName").asString().map(string -> string.toLowerCase(Locale.ROOT)).result();
        Optional optional2 = dynamic2.get("legacy_custom_options").asString().result().map(Optional::of).orElseGet(() -> {
            if (optional.equals(Optional.of("customized"))) {
                return dynamic2.get("generatorOptions").asString().result();
            }
            return Optional.empty();
        });
        boolean bl = false;
        if (optional.equals(Optional.of("customized"))) {
            dynamic22 = StructureSeparationDataFix.method_29916(dynamic2, l);
        } else if (!optional.isPresent()) {
            dynamic22 = StructureSeparationDataFix.method_29916(dynamic2, l);
        } else {
            switch ((String)optional.get()) {
                case "flat": {
                    OptionalDynamic optionalDynamic = dynamic2.get("generatorOptions");
                    Map<Dynamic<T>, Dynamic<T>> map = StructureSeparationDataFix.method_28275(dynamicOps, optionalDynamic);
                    dynamic22 = dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:flat"), (Object)dynamic2.createString("settings"), (Object)dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("structures"), (Object)dynamic2.createMap(map), (Object)dynamic2.createString("layers"), (Object)optionalDynamic.get("layers").result().orElseGet(() -> dynamic2.createList(Stream.of(dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("height"), (Object)dynamic2.createInt(1), (Object)dynamic2.createString("block"), (Object)dynamic2.createString("minecraft:bedrock"))), dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("height"), (Object)dynamic2.createInt(2), (Object)dynamic2.createString("block"), (Object)dynamic2.createString("minecraft:dirt"))), dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("height"), (Object)dynamic2.createInt(1), (Object)dynamic2.createString("block"), (Object)dynamic2.createString("minecraft:grass_block")))))), (Object)dynamic2.createString("biome"), (Object)dynamic2.createString(optionalDynamic.get("biome").asString("minecraft:plains"))))));
                    break;
                }
                case "debug_all_block_states": {
                    dynamic22 = dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:debug")));
                    break;
                }
                case "buffet": {
                    Dynamic dynamic5;
                    Dynamic dynamic3;
                    OptionalDynamic optionalDynamic2 = dynamic2.get("generatorOptions");
                    OptionalDynamic optionalDynamic3 = optionalDynamic2.get("chunk_generator");
                    Optional optional3 = optionalDynamic3.get("type").asString().result();
                    if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
                        dynamic3 = dynamic2.createString("minecraft:caves");
                        bl = true;
                    } else {
                        dynamic3 = Objects.equals(optional3, Optional.of("minecraft:floating_islands")) ? dynamic2.createString("minecraft:floating_islands") : dynamic2.createString("minecraft:overworld");
                    }
                    Dynamic dynamic4 = optionalDynamic2.get("biome_source").result().orElseGet(() -> dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:fixed"))));
                    if (dynamic4.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
                        String string2 = dynamic4.get("options").get("biomes").asStream().findFirst().flatMap(dynamic -> dynamic.asString().result()).orElse("minecraft:ocean");
                        dynamic5 = dynamic4.remove("options").set("biome", dynamic2.createString(string2));
                    } else {
                        dynamic5 = dynamic4;
                    }
                    dynamic22 = StructureSeparationDataFix.method_28268(l, dynamic2, dynamic3, dynamic5);
                    break;
                }
                default: {
                    boolean bl2 = ((String)optional.get()).equals("default");
                    boolean bl3 = ((String)optional.get()).equals("default_1_1") || bl2 && dynamic2.get("generatorVersion").asInt(0) == 0;
                    boolean bl4 = ((String)optional.get()).equals("amplified");
                    boolean bl5 = ((String)optional.get()).equals("largebiomes");
                    dynamic22 = StructureSeparationDataFix.method_28268(l, dynamic2, dynamic2.createString(bl4 ? "minecraft:amplified" : "minecraft:overworld"), StructureSeparationDataFix.method_28272(dynamic2, l, bl3, bl5));
                }
            }
        }
        boolean bl6 = dynamic2.get("MapFeatures").asBoolean(true);
        boolean bl7 = dynamic2.get("BonusChest").asBoolean(false);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
        builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl6));
        builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl7));
        builder.put(dynamicOps.createString("dimensions"), StructureSeparationDataFix.method_29917(dynamic2, l, dynamic22, bl));
        optional2.ifPresent(string -> builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(string)));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    protected static <T> Dynamic<T> method_29916(Dynamic<T> dynamic, long l) {
        return StructureSeparationDataFix.method_28268(l, dynamic, dynamic.createString("minecraft:overworld"), StructureSeparationDataFix.method_28272(dynamic, l, false, false));
    }

    protected static <T> T method_29917(Dynamic<T> dynamic, long l, Dynamic<T> dynamic2, boolean bl) {
        DynamicOps dynamicOps = dynamic.getOps();
        return (T)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("minecraft:overworld"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:overworld" + (bl ? "_caves" : "")), (Object)dynamicOps.createString("generator"), (Object)dynamic2.getValue())), (Object)dynamicOps.createString("minecraft:the_nether"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:the_nether"), (Object)dynamicOps.createString("generator"), (Object)StructureSeparationDataFix.method_28268(l, dynamic, dynamic.createString("minecraft:nether"), dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:multi_noise"), (Object)dynamic.createString("seed"), (Object)dynamic.createLong(l), (Object)dynamic.createString("preset"), (Object)dynamic.createString("minecraft:nether")))).getValue())), (Object)dynamicOps.createString("minecraft:the_end"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:the_end"), (Object)dynamicOps.createString("generator"), (Object)StructureSeparationDataFix.method_28268(l, dynamic, dynamic.createString("minecraft:end"), dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:the_end"), (Object)dynamic.createString("seed"), (Object)dynamic.createLong(l)))).getValue()))));
    }

    private static <T> Map<Dynamic<T>, Dynamic<T>> method_28275(DynamicOps<T> dynamicOps, OptionalDynamic<T> optionalDynamic) {
        MutableInt mutableInt = new MutableInt(32);
        MutableInt mutableInt2 = new MutableInt(3);
        MutableInt mutableInt3 = new MutableInt(128);
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        HashMap map = Maps.newHashMap();
        if (!optionalDynamic.result().isPresent()) {
            mutableBoolean.setTrue();
            map.put(VILLAGE_STRUCTURE_ID, (Information)STRUCTURE_SPACING.get((Object)VILLAGE_STRUCTURE_ID));
        }
        optionalDynamic.get("structures").flatMap(Dynamic::getMapValues).result().ifPresent(map2 -> map2.forEach((dynamic, dynamic2) -> dynamic2.getMapValues().result().ifPresent(map2 -> map2.forEach((dynamic2, dynamic3) -> {
            String string = dynamic.asString("");
            String string2 = dynamic2.asString("");
            String string3 = dynamic3.asString("");
            if ("stronghold".equals(string)) {
                mutableBoolean.setTrue();
                switch (string2) {
                    case "distance": {
                        mutableInt.setValue(StructureSeparationDataFix.method_28280(string3, mutableInt.getValue(), 1));
                        return;
                    }
                    case "spread": {
                        mutableInt2.setValue(StructureSeparationDataFix.method_28280(string3, mutableInt2.getValue(), 1));
                        return;
                    }
                    case "count": {
                        mutableInt3.setValue(StructureSeparationDataFix.method_28280(string3, mutableInt3.getValue(), 1));
                        return;
                    }
                }
                return;
            }
            switch (string2) {
                case "distance": {
                    switch (string) {
                        case "village": {
                            StructureSeparationDataFix.method_28281(map, VILLAGE_STRUCTURE_ID, string3, 9);
                            return;
                        }
                        case "biome_1": {
                            StructureSeparationDataFix.method_28281(map, DESERT_PYRAMID_STRUCTURE_ID, string3, 9);
                            StructureSeparationDataFix.method_28281(map, IGLOO_STRUCTURE_ID, string3, 9);
                            StructureSeparationDataFix.method_28281(map, JUNGLE_PYRAMID_STRUCTURE_ID, string3, 9);
                            StructureSeparationDataFix.method_28281(map, SWAMP_HUT_STRUCTURE_ID, string3, 9);
                            StructureSeparationDataFix.method_28281(map, PILLAGER_OUTPOST_STRUCTURE_ID, string3, 9);
                            return;
                        }
                        case "endcity": {
                            StructureSeparationDataFix.method_28281(map, END_CITY_STRUCTURE_ID, string3, 1);
                            return;
                        }
                        case "mansion": {
                            StructureSeparationDataFix.method_28281(map, MANSION_STRUCTURE_ID, string3, 1);
                            return;
                        }
                    }
                    return;
                }
                case "separation": {
                    if ("oceanmonument".equals(string)) {
                        Information information = map.getOrDefault(MONUMENT_STRUCTURE_ID, (Information)STRUCTURE_SPACING.get((Object)MONUMENT_STRUCTURE_ID));
                        int i = StructureSeparationDataFix.method_28280(string3, information.separation, 1);
                        map.put(MONUMENT_STRUCTURE_ID, new Information(i, information.separation, information.salt));
                    }
                    return;
                }
                case "spacing": {
                    if ("oceanmonument".equals(string)) {
                        StructureSeparationDataFix.method_28281(map, MONUMENT_STRUCTURE_ID, string3, 1);
                    }
                    return;
                }
            }
        }))));
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)optionalDynamic.createString("structures"), (Object)optionalDynamic.createMap(map.entrySet().stream().collect(Collectors.toMap(entry -> optionalDynamic.createString((String)entry.getKey()), entry -> ((Information)entry.getValue()).method_28288(dynamicOps)))));
        if (mutableBoolean.isTrue()) {
            builder.put((Object)optionalDynamic.createString("stronghold"), (Object)optionalDynamic.createMap((Map)ImmutableMap.of((Object)optionalDynamic.createString("distance"), (Object)optionalDynamic.createInt(mutableInt.getValue().intValue()), (Object)optionalDynamic.createString("spread"), (Object)optionalDynamic.createInt(mutableInt2.getValue().intValue()), (Object)optionalDynamic.createString("count"), (Object)optionalDynamic.createInt(mutableInt3.getValue().intValue()))));
        }
        return builder.build();
    }

    private static int method_28279(String string, int i) {
        return NumberUtils.toInt((String)string, (int)i);
    }

    private static int method_28280(String string, int i, int j) {
        return Math.max(j, StructureSeparationDataFix.method_28279(string, i));
    }

    private static void method_28281(Map<String, Information> map, String string, String string2, int i) {
        Information information = map.getOrDefault(string, (Information)STRUCTURE_SPACING.get((Object)string));
        int j = StructureSeparationDataFix.method_28280(string2, information.spacing, i);
        map.put(string, new Information(j, information.separation, information.salt));
    }

    static final class Information {
        public static final Codec<Information> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("spacing").forGetter(information -> information.spacing), (App)Codec.INT.fieldOf("separation").forGetter(information -> information.separation), (App)Codec.INT.fieldOf("salt").forGetter(information -> information.salt)).apply((Applicative)instance, Information::new));
        final int spacing;
        final int separation;
        final int salt;

        public Information(int spacing, int separation, int salt) {
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
        }

        public <T> Dynamic<T> method_28288(DynamicOps<T> dynamicOps) {
            return new Dynamic(dynamicOps, CODEC.encodeStart(dynamicOps, (Object)this).result().orElse(dynamicOps.emptyMap()));
        }
    }
}

