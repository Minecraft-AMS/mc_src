/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
    protected final List<Entry<U>> entries;
    private final Random random = new Random();

    public WeightedList() {
        this(Lists.newArrayList());
    }

    private WeightedList(List<Entry<U>> entries) {
        this.entries = Lists.newArrayList(entries);
    }

    public static <U> Codec<WeightedList<U>> createCodec(Codec<U> codec) {
        return Entry.createCodec(codec).listOf().xmap(WeightedList::new, list -> list.entries);
    }

    public WeightedList<U> add(U item, int weight) {
        this.entries.add(new Entry(item, weight));
        return this;
    }

    public WeightedList<U> shuffle() {
        return this.shuffle(this.random);
    }

    public WeightedList<U> shuffle(Random random) {
        this.entries.forEach(entry -> ((Entry)entry).setShuffledOrder(random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(object -> ((Entry)object).getShuffledOrder()));
        return this;
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public Stream<U> stream() {
        return this.entries.stream().map(Entry::getElement);
    }

    public U pickRandom(Random random) {
        return this.shuffle(random).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public String toString() {
        return "WeightedList[" + this.entries + "]";
    }

    public static class Entry<T> {
        private final T item;
        private final int weight;
        private double shuffledOrder;

        private Entry(T item, int weight) {
            this.weight = weight;
            this.item = item;
        }

        private double getShuffledOrder() {
            return this.shuffledOrder;
        }

        private void setShuffledOrder(float random) {
            this.shuffledOrder = -Math.pow(random, 1.0f / (float)this.weight);
        }

        public T getElement() {
            return this.item;
        }

        public String toString() {
            return "" + this.weight + ":" + this.item;
        }

        public static <E> Codec<Entry<E>> createCodec(final Codec<E> codec) {
            return new Codec<Entry<E>>(){

                public <T> DataResult<Pair<Entry<E>, T>> decode(DynamicOps<T> ops, T object2) {
                    Dynamic dynamic = new Dynamic(ops, object2);
                    return dynamic.get("data").flatMap(arg_0 -> ((Codec)codec).parse(arg_0)).map(object -> new Entry(object, dynamic.get("weight").asInt(1))).map(entry -> Pair.of((Object)entry, (Object)ops.empty()));
                }

                public <T> DataResult<T> encode(Entry<E> entry, DynamicOps<T> dynamicOps, T object) {
                    return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", codec.encodeStart(dynamicOps, entry.item)).build(object);
                }

                public /* synthetic */ DataResult encode(Object entry, DynamicOps ops, Object object) {
                    return this.encode((Entry)entry, ops, object);
                }
            };
        }
    }
}

