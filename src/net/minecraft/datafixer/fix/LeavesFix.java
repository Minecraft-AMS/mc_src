/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.PackedIntegerArray;
import org.jetbrains.annotations.Nullable;

public class LeavesFix
extends DataFix {
    private static final int[][] field_5687 = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
    private static final Object2IntMap<String> field_5688 = (Object2IntMap)DataFixUtils.make((Object)new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
        object2IntOpenHashMap.put((Object)"minecraft:acacia_leaves", 0);
        object2IntOpenHashMap.put((Object)"minecraft:birch_leaves", 1);
        object2IntOpenHashMap.put((Object)"minecraft:dark_oak_leaves", 2);
        object2IntOpenHashMap.put((Object)"minecraft:jungle_leaves", 3);
        object2IntOpenHashMap.put((Object)"minecraft:oak_leaves", 4);
        object2IntOpenHashMap.put((Object)"minecraft:spruce_leaves", 5);
    });
    private static final Set<String> field_5686 = ImmutableSet.of((Object)"minecraft:acacia_bark", (Object)"minecraft:birch_bark", (Object)"minecraft:dark_oak_bark", (Object)"minecraft:jungle_bark", (Object)"minecraft:oak_bark", (Object)"minecraft:spruce_bark", (Object[])new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

    public LeavesFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        OpticFinder opticFinder2 = opticFinder.type().findField("Sections");
        Type type2 = opticFinder2.type();
        if (!(type2 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        }
        Type type3 = ((List.ListType)type2).getElement();
        OpticFinder opticFinder3 = DSL.typeFinder((Type)type3);
        return this.fixTypeEverywhereTyped("Leaves fix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> {
            int[] is = new int[]{0};
            Typed typed22 = typed.updateTyped(opticFinder2, typed2 -> {
                int m;
                int l;
                Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap(typed2.getAllTyped(opticFinder3).stream().map(typed -> new class_1192((Typed<?>)typed, this.getInputSchema())).collect(Collectors.toMap(class_1193::method_5077, arg -> arg)));
                if (int2ObjectMap.values().stream().allMatch(class_1193::method_5079)) {
                    return typed2;
                }
                ArrayList list = Lists.newArrayList();
                for (int i = 0; i < 7; ++i) {
                    list.add(new IntOpenHashSet());
                }
                for (class_1192 lv : int2ObjectMap.values()) {
                    if (lv.method_5079()) continue;
                    for (int j = 0; j < 4096; ++j) {
                        int k = lv.method_5075(j);
                        if (lv.method_5068(k)) {
                            ((IntSet)list.get(0)).add(lv.method_5077() << 12 | j);
                            continue;
                        }
                        if (!lv.method_5071(k)) continue;
                        l = this.method_5052(j);
                        m = this.method_5050(j);
                        is[0] = is[0] | LeavesFix.method_5061(l == 0, l == 15, m == 0, m == 15);
                    }
                }
                for (int i = 1; i < 7; ++i) {
                    IntSet intSet = (IntSet)list.get(i - 1);
                    IntSet intSet2 = (IntSet)list.get(i);
                    IntIterator intIterator = intSet.iterator();
                    while (intIterator.hasNext()) {
                        l = intIterator.nextInt();
                        m = this.method_5052(l);
                        int n = this.method_5062(l);
                        int o = this.method_5050(l);
                        for (int[] js : field_5687) {
                            int u;
                            int s;
                            int t;
                            class_1192 lv2;
                            int p = m + js[0];
                            int q = n + js[1];
                            int r = o + js[2];
                            if (p < 0 || p > 15 || r < 0 || r > 15 || q < 0 || q > 255 || (lv2 = (class_1192)int2ObjectMap.get(q >> 4)) == null || lv2.method_5079() || !lv2.method_5071(t = lv2.method_5075(s = LeavesFix.method_5051(p, q & 0xF, r))) || (u = lv2.method_5065(t)) <= i) continue;
                            lv2.method_5070(s, t, i);
                            intSet2.add(LeavesFix.method_5051(p, q, r));
                        }
                    }
                }
                return typed2.updateTyped(opticFinder3, arg_0 -> LeavesFix.method_5058((Int2ObjectMap)int2ObjectMap, arg_0));
            });
            if (is[0] != 0) {
                typed22 = typed22.update(DSL.remainderFinder(), dynamic -> {
                    Dynamic dynamic2 = (Dynamic)DataFixUtils.orElse((Optional)dynamic.get("UpgradeData").get(), (Object)dynamic.emptyMap());
                    return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | is[0]))));
                });
            }
            return typed22;
        }));
    }

    public static int method_5051(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private int method_5052(int i) {
        return i & 0xF;
    }

    private int method_5062(int i) {
        return i >> 8 & 0xFF;
    }

    private int method_5050(int i) {
        return i >> 4 & 0xF;
    }

    public static int method_5061(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = 0;
        if (bl3) {
            i = bl2 ? (i |= 2) : (bl ? (i |= 0x80) : (i |= 1));
        } else if (bl4) {
            i = bl ? (i |= 0x20) : (bl2 ? (i |= 8) : (i |= 0x10));
        } else if (bl2) {
            i |= 4;
        } else if (bl) {
            i |= 0x40;
        }
        return i;
    }

    private static /* synthetic */ Typed method_5058(Int2ObjectMap int2ObjectMap, Typed typed) {
        return ((class_1192)int2ObjectMap.get(((Dynamic)typed.get(DSL.remainderFinder())).get("Y").asInt(0))).method_5083(typed);
    }

    public static final class class_1192
    extends class_1193 {
        @Nullable
        private IntSet field_5689;
        @Nullable
        private IntSet field_5691;
        @Nullable
        private Int2IntMap field_5690;

        public class_1192(Typed<?> typed, Schema schema) {
            super(typed, schema);
        }

        @Override
        protected boolean method_5076() {
            this.field_5689 = new IntOpenHashSet();
            this.field_5691 = new IntOpenHashSet();
            this.field_5690 = new Int2IntOpenHashMap();
            for (int i = 0; i < this.field_5692.size(); ++i) {
                Dynamic dynamic = (Dynamic)this.field_5692.get(i);
                String string = dynamic.get("Name").asString("");
                if (field_5688.containsKey((Object)string)) {
                    boolean bl = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
                    this.field_5689.add(i);
                    this.field_5690.put(this.method_5082(string, bl, 7), i);
                    this.field_5692.set(i, this.method_5072(dynamic, string, bl, 7));
                }
                if (!field_5686.contains(string)) continue;
                this.field_5691.add(i);
            }
            return this.field_5689.isEmpty() && this.field_5691.isEmpty();
        }

        private Dynamic<?> method_5072(Dynamic<?> dynamic, String string, boolean bl, int i) {
            Dynamic dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("persistent", dynamic2.createString(bl ? "true" : "false"));
            dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(i)));
            Dynamic dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Properties", dynamic2);
            dynamic3 = dynamic3.set("Name", dynamic3.createString(string));
            return dynamic3;
        }

        public boolean method_5068(int i) {
            return this.field_5691.contains(i);
        }

        public boolean method_5071(int i) {
            return this.field_5689.contains(i);
        }

        private int method_5065(int i) {
            if (this.method_5068(i)) {
                return 0;
            }
            return Integer.parseInt(((Dynamic)this.field_5692.get(i)).get("Properties").get("distance").asString(""));
        }

        private void method_5070(int i, int j, int k) {
            int m;
            boolean bl;
            Dynamic dynamic = (Dynamic)this.field_5692.get(j);
            String string = dynamic.get("Name").asString("");
            int l = this.method_5082(string, bl = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true"), k);
            if (!this.field_5690.containsKey(l)) {
                m = this.field_5692.size();
                this.field_5689.add(m);
                this.field_5690.put(l, m);
                this.field_5692.add(this.method_5072(dynamic, string, bl, k));
            }
            m = this.field_5690.get(l);
            if (1 << this.field_5696.getElementBits() <= m) {
                PackedIntegerArray packedIntegerArray = new PackedIntegerArray(this.field_5696.getElementBits() + 1, 4096);
                for (int n = 0; n < 4096; ++n) {
                    packedIntegerArray.set(n, this.field_5696.get(n));
                }
                this.field_5696 = packedIntegerArray;
            }
            this.field_5696.set(i, m);
        }
    }

    public static abstract class class_1193 {
        private final Type<Pair<String, Dynamic<?>>> field_5695 = DSL.named((String)TypeReferences.BLOCK_STATE.typeName(), (Type)DSL.remainderType());
        protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_5693 = DSL.fieldFinder((String)"Palette", (Type)DSL.list(this.field_5695));
        protected final List<Dynamic<?>> field_5692;
        protected final int field_5694;
        @Nullable
        protected PackedIntegerArray field_5696;

        public class_1193(Typed<?> typed, Schema schema) {
            if (!Objects.equals(schema.getType(TypeReferences.BLOCK_STATE), this.field_5695)) {
                throw new IllegalStateException("Block state type is not what was expected.");
            }
            Optional optional = typed.getOptional(this.field_5693);
            this.field_5692 = optional.map(list -> list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse((List)ImmutableList.of());
            Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
            this.field_5694 = dynamic.get("Y").asInt(0);
            this.method_5074(dynamic);
        }

        protected void method_5074(Dynamic<?> dynamic) {
            if (this.method_5076()) {
                this.field_5696 = null;
            } else {
                long[] ls = ((LongStream)dynamic.get("BlockStates").asLongStreamOpt().get()).toArray();
                int i = Math.max(4, DataFixUtils.ceillog2((int)this.field_5692.size()));
                this.field_5696 = new PackedIntegerArray(i, 4096, ls);
            }
        }

        public Typed<?> method_5083(Typed<?> typed) {
            if (this.method_5079()) {
                return typed;
            }
            return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.field_5696.getStorage())))).set(this.field_5693, this.field_5692.stream().map(dynamic -> Pair.of((Object)TypeReferences.BLOCK_STATE.typeName(), (Object)dynamic)).collect(Collectors.toList()));
        }

        public boolean method_5079() {
            return this.field_5696 == null;
        }

        public int method_5075(int i) {
            return this.field_5696.get(i);
        }

        protected int method_5082(String string, boolean bl, int i) {
            return field_5688.get((Object)string) << 5 | (bl ? 16 : 0) | i;
        }

        int method_5077() {
            return this.field_5694;
        }

        protected abstract boolean method_5076();
    }
}
