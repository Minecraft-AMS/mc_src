/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ProtoChunkTickListFix;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class ChunkHeightAndBiomeFix
extends DataFix {
    public static final String CONTEXT = "__context";
    private static final String NAME = "ChunkHeightAndBiomeFix";
    private static final int CHUNK_SECTIONS_IN_OLD_CHUNK = 16;
    private static final int CHUNK_SECTIONS_IN_NEW_CHUNK = 24;
    private static final int MIN_CHUNK_SECTION_Y = -4;
    public static final int field_36214 = 4096;
    private static final int field_36215 = 64;
    private static final int field_35022 = 9;
    private static final long field_35023 = 511L;
    private static final int field_35024 = 64;
    private static final String[] HEIGHTMAP_KEYS = new String[]{"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"};
    private static final Set<String> STATUSES_TO_SKIP_UPDATE = Set.of("surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> field_35668 = Set.of("noise", "surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> SURFACE_BLOCKS = Set.of("minecraft:air", "minecraft:basalt", "minecraft:bedrock", "minecraft:blackstone", "minecraft:calcite", "minecraft:cave_air", "minecraft:coarse_dirt", "minecraft:crimson_nylium", "minecraft:dirt", "minecraft:end_stone", "minecraft:grass_block", "minecraft:gravel", "minecraft:ice", "minecraft:lava", "minecraft:mycelium", "minecraft:nether_wart_block", "minecraft:netherrack", "minecraft:orange_terracotta", "minecraft:packed_ice", "minecraft:podzol", "minecraft:powder_snow", "minecraft:red_sand", "minecraft:red_sandstone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow_block", "minecraft:soul_sand", "minecraft:soul_soil", "minecraft:stone", "minecraft:terracotta", "minecraft:warped_nylium", "minecraft:warped_wart_block", "minecraft:water", "minecraft:white_terracotta");
    private static final int field_35026 = 16;
    private static final int field_35027 = 64;
    private static final int field_35028 = 1008;
    public static final String PLAINS_ID = "minecraft:plains";
    private static final Int2ObjectMap<String> RAW_BIOME_IDS = new Int2ObjectOpenHashMap();

    public ChunkHeightAndBiomeFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        OpticFinder opticFinder2 = opticFinder.type().findField("Sections");
        Schema schema = this.getOutputSchema();
        Type type2 = schema.getType(TypeReferences.CHUNK);
        Type type3 = type2.findField("Level").type();
        Type type4 = type3.findField("Sections").type();
        return this.fixTypeEverywhereTyped(NAME, type, type2, typed -> typed.updateTyped(opticFinder, type3, typed22 -> {
            Dynamic dynamic2 = (Dynamic)typed22.get(DSL.remainderFinder());
            OptionalDynamic optionalDynamic = ((Dynamic)typed.get(DSL.remainderFinder())).get(CONTEXT);
            String string = optionalDynamic.get("dimension").asString().result().orElse("");
            String string2 = optionalDynamic.get("generator").asString().result().orElse("");
            boolean bl = "minecraft:overworld".equals(string);
            MutableBoolean mutableBoolean = new MutableBoolean();
            int i = bl ? -4 : 0;
            Dynamic<?>[] dynamics = ChunkHeightAndBiomeFix.fixBiomes(dynamic2, bl, i, mutableBoolean);
            Dynamic<?> dynamic22 = ChunkHeightAndBiomeFix.fixPalette(dynamic2.createList(Stream.of(dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("Name"), (Object)dynamic2.createString("minecraft:air"))))));
            HashSet set = Sets.newHashSet();
            MutableObject mutableObject = new MutableObject(() -> null);
            typed22 = typed22.updateTyped(opticFinder2, type4, typed -> {
                IntOpenHashSet intSet = new IntOpenHashSet();
                Dynamic dynamic3 = (Dynamic)typed.write().result().orElseThrow(() -> new IllegalStateException("Malformed Chunk.Level.Sections"));
                List list = dynamic3.asStream().map(arg_0 -> ChunkHeightAndBiomeFix.method_38801(set, dynamic22, i, dynamics, (IntSet)intSet, mutableObject, arg_0)).collect(Collectors.toCollection(ArrayList::new));
                for (int j = 0; j < dynamics.length; ++j) {
                    int k = j + i;
                    if (!intSet.add(k)) continue;
                    Dynamic dynamic4 = dynamic2.createMap(Map.of(dynamic2.createString("Y"), dynamic2.createInt(k)));
                    dynamic4 = dynamic4.set("block_states", dynamic22);
                    dynamic4 = dynamic4.set("biomes", dynamics[j]);
                    list.add(dynamic4);
                }
                return (Typed)((Pair)type4.readTyped(dynamic2.createList(list.stream())).result().orElseThrow(() -> new IllegalStateException("ChunkHeightAndBiomeFix failed."))).getFirst();
            });
            return typed22.update(DSL.remainderFinder(), dynamic -> {
                if (bl) {
                    dynamic = this.fixStatus((Dynamic<?>)dynamic, set);
                }
                return ChunkHeightAndBiomeFix.fixLevel(dynamic, bl, mutableBoolean.booleanValue(), "minecraft:noise".equals(string2), (Supplier)mutableObject.getValue());
            });
        }));
    }

    private Dynamic<?> fixStatus(Dynamic<?> dynamic2, Set<String> set) {
        return dynamic2.update("Status", dynamic -> {
            boolean bl2;
            String string = dynamic.asString("empty");
            if (STATUSES_TO_SKIP_UPDATE.contains(string)) {
                return dynamic;
            }
            set.remove("minecraft:air");
            boolean bl = !set.isEmpty();
            set.removeAll(SURFACE_BLOCKS);
            boolean bl3 = bl2 = !set.isEmpty();
            if (bl2) {
                return dynamic.createString("liquid_carvers");
            }
            if ("noise".equals(string) || bl) {
                return dynamic.createString("noise");
            }
            if ("biomes".equals(string)) {
                return dynamic.createString("structure_references");
            }
            return dynamic;
        });
    }

    private static Dynamic<?>[] fixBiomes(Dynamic<?> dynamic, boolean bl, int i2, MutableBoolean mutableBoolean) {
        Object[] dynamics = new Dynamic[bl ? 24 : 16];
        int[] is = dynamic.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse(null);
        if (is != null && is.length == 1536) {
            mutableBoolean.setValue(true);
            for (int j2 = 0; j2 < 24; ++j2) {
                int k = j2;
                dynamics[j2] = ChunkHeightAndBiomeFix.fixBiomes(dynamic, j -> ChunkHeightAndBiomeFix.method_39519(is, k * 64 + j));
            }
        } else if (is != null && is.length == 1024) {
            int l;
            int j3 = 0;
            while (j3 < 16) {
                int k = j3 - i2;
                l = j3++;
                dynamics[k] = ChunkHeightAndBiomeFix.fixBiomes(dynamic, j -> ChunkHeightAndBiomeFix.method_39519(is, l * 64 + j));
            }
            if (bl) {
                Dynamic<?> dynamic2 = ChunkHeightAndBiomeFix.fixBiomes(dynamic, i -> ChunkHeightAndBiomeFix.method_39519(is, i % 16));
                Dynamic<?> dynamic3 = ChunkHeightAndBiomeFix.fixBiomes(dynamic, i -> ChunkHeightAndBiomeFix.method_39519(is, i % 16 + 1008));
                for (l = 0; l < 4; ++l) {
                    dynamics[l] = dynamic2;
                }
                for (l = 20; l < 24; ++l) {
                    dynamics[l] = dynamic3;
                }
            }
        } else {
            Arrays.fill(dynamics, ChunkHeightAndBiomeFix.fixPalette(dynamic.createList(Stream.of(dynamic.createString(PLAINS_ID)))));
        }
        return dynamics;
    }

    private static int method_39519(int[] is, int i) {
        return is[i] & 0xFF;
    }

    private static Dynamic<?> fixLevel(Dynamic<?> dynamic, boolean bl, boolean bl2, boolean bl3, Supplier<ProtoChunkTickListFix.class_6741> supplier) {
        Dynamic dynamic2;
        String string;
        dynamic = dynamic.remove("Biomes");
        if (!bl) {
            return ChunkHeightAndBiomeFix.fixCarvingMasks(dynamic, 16, 0);
        }
        if (bl2) {
            return ChunkHeightAndBiomeFix.fixCarvingMasks(dynamic, 24, 0);
        }
        dynamic = ChunkHeightAndBiomeFix.fixHeightmaps(dynamic);
        dynamic = ChunkHeightAndBiomeFix.fixChunkSectionList(dynamic, "Lights");
        dynamic = ChunkHeightAndBiomeFix.fixChunkSectionList(dynamic, "LiquidsToBeTicked");
        dynamic = ChunkHeightAndBiomeFix.fixChunkSectionList(dynamic, "PostProcessing");
        dynamic = ChunkHeightAndBiomeFix.fixChunkSectionList(dynamic, "ToBeTicked");
        dynamic = ChunkHeightAndBiomeFix.fixCarvingMasks(dynamic, 24, 4);
        dynamic = dynamic.update("UpgradeData", ChunkHeightAndBiomeFix::fixUpgradeData);
        if (!bl3) {
            return dynamic;
        }
        Optional optional = dynamic.get("Status").result();
        if (optional.isPresent() && !"empty".equals(string = (dynamic2 = (Dynamic)optional.get()).asString(""))) {
            dynamic = dynamic.set("blending_data", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("old_noise"), (Object)dynamic.createBoolean(field_35668.contains(string)))));
            ProtoChunkTickListFix.class_6741 lv = supplier.get();
            if (lv != null) {
                BitSet bitSet = new BitSet(256);
                boolean bl4 = string.equals("noise");
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        boolean bl6;
                        Dynamic<?> dynamic3 = lv.method_39265(j, 0, i);
                        boolean bl5 = dynamic3 != null && "minecraft:bedrock".equals(dynamic3.get("Name").asString(""));
                        boolean bl7 = bl6 = dynamic3 != null && "minecraft:air".equals(dynamic3.get("Name").asString(""));
                        if (bl6) {
                            bitSet.set(i * 16 + j);
                        }
                        bl4 |= bl5;
                    }
                }
                if (bl4 && bitSet.cardinality() != bitSet.size()) {
                    Dynamic dynamic4 = "full".equals(string) ? dynamic.createString("heightmaps") : dynamic2;
                    dynamic = dynamic.set("below_zero_retrogen", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("target_status"), (Object)dynamic4, (Object)dynamic.createString("missing_bedrock"), (Object)dynamic.createLongList(LongStream.of(bitSet.toLongArray())))));
                    dynamic = dynamic.set("Status", dynamic.createString("empty"));
                }
                dynamic = dynamic.set("isLightOn", dynamic.createBoolean(false));
            }
        }
        return dynamic;
    }

    private static <T> Dynamic<T> fixUpgradeData(Dynamic<T> dynamic2) {
        return dynamic2.update("Indices", dynamic -> {
            HashMap map = new HashMap();
            dynamic.getMapValues().result().ifPresent(map2 -> map2.forEach((dynamic, dynamic2) -> {
                try {
                    dynamic.asString().result().map(Integer::parseInt).ifPresent(integer -> {
                        int i = integer - -4;
                        map.put(dynamic.createString(Integer.toString(i)), dynamic2);
                    });
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }));
            return dynamic.createMap(map);
        });
    }

    private static Dynamic<?> fixCarvingMasks(Dynamic<?> dynamic, int i, int j) {
        Dynamic dynamic2 = dynamic.get("CarvingMasks").orElseEmptyMap();
        dynamic2 = dynamic2.updateMapValues(pair -> {
            long[] ls = BitSet.valueOf(((Dynamic)pair.getSecond()).asByteBuffer().array()).toLongArray();
            long[] ms = new long[64 * i];
            System.arraycopy(ls, 0, ms, 64 * j, ls.length);
            return Pair.of((Object)((Dynamic)pair.getFirst()), (Object)dynamic.createLongList(LongStream.of(ms)));
        });
        return dynamic.set("CarvingMasks", dynamic2);
    }

    private static Dynamic<?> fixChunkSectionList(Dynamic<?> dynamic, String string) {
        List list = dynamic.get(string).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
        if (list.size() == 24) {
            return dynamic;
        }
        Dynamic dynamic2 = dynamic.emptyList();
        for (int i = 0; i < 4; ++i) {
            list.add(0, dynamic2);
            list.add(dynamic2);
        }
        return dynamic.set(string, dynamic.createList(list.stream()));
    }

    private static Dynamic<?> fixHeightmaps(Dynamic<?> dynamic2) {
        return dynamic2.update("Heightmaps", dynamic -> {
            for (String string : HEIGHTMAP_KEYS) {
                dynamic = dynamic.update(string, ChunkHeightAndBiomeFix::fixHeightmap);
            }
            return dynamic;
        });
    }

    private static Dynamic<?> fixHeightmap(Dynamic<?> dynamic) {
        return dynamic.createLongList(dynamic.asLongStream().map(l -> {
            long m = 0L;
            int i = 0;
            while (i + 9 <= 64) {
                long n = l >> i & 0x1FFL;
                long o = n == 0L ? 0L : Math.min(n + 64L, 511L);
                m |= o << i;
                i += 9;
            }
            return m;
        }));
    }

    private static Dynamic<?> fixBiomes(Dynamic<?> dynamic, Int2IntFunction int2IntFunction) {
        int j;
        Int2IntLinkedOpenHashMap int2IntMap = new Int2IntLinkedOpenHashMap();
        for (int i = 0; i < 64; ++i) {
            j = int2IntFunction.applyAsInt(i);
            if (int2IntMap.containsKey(j)) continue;
            int2IntMap.put(j, int2IntMap.size());
        }
        Dynamic dynamic2 = dynamic.createList(int2IntMap.keySet().stream().map(integer -> dynamic.createString((String)RAW_BIOME_IDS.getOrDefault(integer.intValue(), (Object)PLAINS_ID))));
        j = ChunkHeightAndBiomeFix.ceilLog2(int2IntMap.size());
        if (j == 0) {
            return ChunkHeightAndBiomeFix.fixPalette(dynamic2);
        }
        int k = 64 / j;
        int l = (64 + k - 1) / k;
        long[] ls = new long[l];
        int m = 0;
        int n = 0;
        for (int o = 0; o < 64; ++o) {
            int p = int2IntFunction.applyAsInt(o);
            int n2 = m++;
            ls[n2] = ls[n2] | (long)int2IntMap.get(p) << n;
            if ((n += j) + j <= 64) continue;
            n = 0;
        }
        Dynamic dynamic3 = dynamic.createLongList(Arrays.stream(ls));
        return ChunkHeightAndBiomeFix.fixPaletteWithData(dynamic2, dynamic3);
    }

    private static Dynamic<?> fixPalette(Dynamic<?> dynamic) {
        return dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("palette"), dynamic));
    }

    private static Dynamic<?> fixPaletteWithData(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
        return dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("palette"), dynamic, (Object)dynamic.createString("data"), dynamic2));
    }

    private static Dynamic<?> fixPalette(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
        List list = dynamic.asStream().collect(Collectors.toCollection(ArrayList::new));
        if (list.size() == 1) {
            return ChunkHeightAndBiomeFix.fixPalette(dynamic);
        }
        dynamic = ChunkHeightAndBiomeFix.method_39781(dynamic, dynamic2, list);
        return ChunkHeightAndBiomeFix.fixPaletteWithData(dynamic, dynamic2);
    }

    private static Dynamic<?> method_39781(Dynamic<?> dynamic, Dynamic<?> dynamic2, List<Dynamic<?>> list) {
        int i;
        int j;
        long l = dynamic2.asLongStream().count() * 64L;
        long m = l / 4096L;
        if (m > (long)(j = ChunkHeightAndBiomeFix.ceilLog2(i = list.size()))) {
            Dynamic dynamic3 = dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("Name"), (Object)dynamic.createString("minecraft:air")));
            int k = (1 << (int)(m - 1L)) + 1;
            int n = k - i;
            for (int o = 0; o < n; ++o) {
                list.add(dynamic3);
            }
            return dynamic.createList(list.stream());
        }
        return dynamic;
    }

    public static int ceilLog2(int value) {
        if (value == 0) {
            return 0;
        }
        return (int)Math.ceil(Math.log(value) / Math.log(2.0));
    }

    private static /* synthetic */ Dynamic method_38801(Set set, Dynamic dynamic, int i, Dynamic[] dynamics, IntSet intSet, MutableObject mutableObject, Dynamic dynamic2) {
        int j = dynamic2.get("Y").asInt(0);
        Dynamic dynamic3 = (Dynamic)DataFixUtils.orElse(dynamic2.get("Palette").result().flatMap(dynamic22 -> {
            dynamic22.asStream().map(dynamic -> dynamic.get("Name").asString("minecraft:air")).forEach(set::add);
            return dynamic2.get("BlockStates").result().map(dynamic2 -> ChunkHeightAndBiomeFix.fixPalette(dynamic22, dynamic2));
        }), (Object)dynamic);
        Dynamic dynamic4 = dynamic2;
        int k = j - i;
        if (k >= 0 && k < dynamics.length) {
            dynamic4 = dynamic4.set("biomes", dynamics[k]);
        }
        intSet.add(j);
        if (dynamic2.get("Y").asInt(Integer.MAX_VALUE) == 0) {
            mutableObject.setValue(() -> {
                List list = dynamic3.get("palette").asList(Function.identity());
                long[] ls = dynamic3.get("data").asLongStream().toArray();
                return new ProtoChunkTickListFix.class_6741(list, ls);
            });
        }
        return dynamic4.set("block_states", dynamic3).remove("Palette").remove("BlockStates");
    }

    static {
        RAW_BIOME_IDS.put(0, (Object)"minecraft:ocean");
        RAW_BIOME_IDS.put(1, (Object)PLAINS_ID);
        RAW_BIOME_IDS.put(2, (Object)"minecraft:desert");
        RAW_BIOME_IDS.put(3, (Object)"minecraft:mountains");
        RAW_BIOME_IDS.put(4, (Object)"minecraft:forest");
        RAW_BIOME_IDS.put(5, (Object)"minecraft:taiga");
        RAW_BIOME_IDS.put(6, (Object)"minecraft:swamp");
        RAW_BIOME_IDS.put(7, (Object)"minecraft:river");
        RAW_BIOME_IDS.put(8, (Object)"minecraft:nether_wastes");
        RAW_BIOME_IDS.put(9, (Object)"minecraft:the_end");
        RAW_BIOME_IDS.put(10, (Object)"minecraft:frozen_ocean");
        RAW_BIOME_IDS.put(11, (Object)"minecraft:frozen_river");
        RAW_BIOME_IDS.put(12, (Object)"minecraft:snowy_tundra");
        RAW_BIOME_IDS.put(13, (Object)"minecraft:snowy_mountains");
        RAW_BIOME_IDS.put(14, (Object)"minecraft:mushroom_fields");
        RAW_BIOME_IDS.put(15, (Object)"minecraft:mushroom_field_shore");
        RAW_BIOME_IDS.put(16, (Object)"minecraft:beach");
        RAW_BIOME_IDS.put(17, (Object)"minecraft:desert_hills");
        RAW_BIOME_IDS.put(18, (Object)"minecraft:wooded_hills");
        RAW_BIOME_IDS.put(19, (Object)"minecraft:taiga_hills");
        RAW_BIOME_IDS.put(20, (Object)"minecraft:mountain_edge");
        RAW_BIOME_IDS.put(21, (Object)"minecraft:jungle");
        RAW_BIOME_IDS.put(22, (Object)"minecraft:jungle_hills");
        RAW_BIOME_IDS.put(23, (Object)"minecraft:jungle_edge");
        RAW_BIOME_IDS.put(24, (Object)"minecraft:deep_ocean");
        RAW_BIOME_IDS.put(25, (Object)"minecraft:stone_shore");
        RAW_BIOME_IDS.put(26, (Object)"minecraft:snowy_beach");
        RAW_BIOME_IDS.put(27, (Object)"minecraft:birch_forest");
        RAW_BIOME_IDS.put(28, (Object)"minecraft:birch_forest_hills");
        RAW_BIOME_IDS.put(29, (Object)"minecraft:dark_forest");
        RAW_BIOME_IDS.put(30, (Object)"minecraft:snowy_taiga");
        RAW_BIOME_IDS.put(31, (Object)"minecraft:snowy_taiga_hills");
        RAW_BIOME_IDS.put(32, (Object)"minecraft:giant_tree_taiga");
        RAW_BIOME_IDS.put(33, (Object)"minecraft:giant_tree_taiga_hills");
        RAW_BIOME_IDS.put(34, (Object)"minecraft:wooded_mountains");
        RAW_BIOME_IDS.put(35, (Object)"minecraft:savanna");
        RAW_BIOME_IDS.put(36, (Object)"minecraft:savanna_plateau");
        RAW_BIOME_IDS.put(37, (Object)"minecraft:badlands");
        RAW_BIOME_IDS.put(38, (Object)"minecraft:wooded_badlands_plateau");
        RAW_BIOME_IDS.put(39, (Object)"minecraft:badlands_plateau");
        RAW_BIOME_IDS.put(40, (Object)"minecraft:small_end_islands");
        RAW_BIOME_IDS.put(41, (Object)"minecraft:end_midlands");
        RAW_BIOME_IDS.put(42, (Object)"minecraft:end_highlands");
        RAW_BIOME_IDS.put(43, (Object)"minecraft:end_barrens");
        RAW_BIOME_IDS.put(44, (Object)"minecraft:warm_ocean");
        RAW_BIOME_IDS.put(45, (Object)"minecraft:lukewarm_ocean");
        RAW_BIOME_IDS.put(46, (Object)"minecraft:cold_ocean");
        RAW_BIOME_IDS.put(47, (Object)"minecraft:deep_warm_ocean");
        RAW_BIOME_IDS.put(48, (Object)"minecraft:deep_lukewarm_ocean");
        RAW_BIOME_IDS.put(49, (Object)"minecraft:deep_cold_ocean");
        RAW_BIOME_IDS.put(50, (Object)"minecraft:deep_frozen_ocean");
        RAW_BIOME_IDS.put(127, (Object)"minecraft:the_void");
        RAW_BIOME_IDS.put(129, (Object)"minecraft:sunflower_plains");
        RAW_BIOME_IDS.put(130, (Object)"minecraft:desert_lakes");
        RAW_BIOME_IDS.put(131, (Object)"minecraft:gravelly_mountains");
        RAW_BIOME_IDS.put(132, (Object)"minecraft:flower_forest");
        RAW_BIOME_IDS.put(133, (Object)"minecraft:taiga_mountains");
        RAW_BIOME_IDS.put(134, (Object)"minecraft:swamp_hills");
        RAW_BIOME_IDS.put(140, (Object)"minecraft:ice_spikes");
        RAW_BIOME_IDS.put(149, (Object)"minecraft:modified_jungle");
        RAW_BIOME_IDS.put(151, (Object)"minecraft:modified_jungle_edge");
        RAW_BIOME_IDS.put(155, (Object)"minecraft:tall_birch_forest");
        RAW_BIOME_IDS.put(156, (Object)"minecraft:tall_birch_hills");
        RAW_BIOME_IDS.put(157, (Object)"minecraft:dark_forest_hills");
        RAW_BIOME_IDS.put(158, (Object)"minecraft:snowy_taiga_mountains");
        RAW_BIOME_IDS.put(160, (Object)"minecraft:giant_spruce_taiga");
        RAW_BIOME_IDS.put(161, (Object)"minecraft:giant_spruce_taiga_hills");
        RAW_BIOME_IDS.put(162, (Object)"minecraft:modified_gravelly_mountains");
        RAW_BIOME_IDS.put(163, (Object)"minecraft:shattered_savanna");
        RAW_BIOME_IDS.put(164, (Object)"minecraft:shattered_savanna_plateau");
        RAW_BIOME_IDS.put(165, (Object)"minecraft:eroded_badlands");
        RAW_BIOME_IDS.put(166, (Object)"minecraft:modified_wooded_badlands_plateau");
        RAW_BIOME_IDS.put(167, (Object)"minecraft:modified_badlands_plateau");
        RAW_BIOME_IDS.put(168, (Object)"minecraft:bamboo_jungle");
        RAW_BIOME_IDS.put(169, (Object)"minecraft:bamboo_jungle_hills");
        RAW_BIOME_IDS.put(170, (Object)"minecraft:soul_sand_valley");
        RAW_BIOME_IDS.put(171, (Object)"minecraft:crimson_forest");
        RAW_BIOME_IDS.put(172, (Object)"minecraft:warped_forest");
        RAW_BIOME_IDS.put(173, (Object)"minecraft:basalt_deltas");
        RAW_BIOME_IDS.put(174, (Object)"minecraft:dripstone_caves");
        RAW_BIOME_IDS.put(175, (Object)"minecraft:lush_caves");
        RAW_BIOME_IDS.put(177, (Object)"minecraft:meadow");
        RAW_BIOME_IDS.put(178, (Object)"minecraft:grove");
        RAW_BIOME_IDS.put(179, (Object)"minecraft:snowy_slopes");
        RAW_BIOME_IDS.put(180, (Object)"minecraft:snowcapped_peaks");
        RAW_BIOME_IDS.put(181, (Object)"minecraft:lofty_peaks");
        RAW_BIOME_IDS.put(182, (Object)"minecraft:stony_peaks");
    }
}

