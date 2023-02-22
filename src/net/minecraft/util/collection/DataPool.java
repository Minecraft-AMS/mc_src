/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.dynamic.Codecs;

public class DataPool<E>
extends Pool<Weighted.Present<E>> {
    public static <E> Codec<DataPool<E>> method_39521(Codec<E> codec) {
        return Weighted.Present.createCodec(codec).listOf().xmap(DataPool::new, Pool::getEntries);
    }

    public static <E> Codec<DataPool<E>> createCodec(Codec<E> dataCodec) {
        return Codecs.nonEmptyList(Weighted.Present.createCodec(dataCodec).listOf()).xmap(DataPool::new, Pool::getEntries);
    }

    DataPool(List<? extends Weighted.Present<E>> list) {
        super(list);
    }

    public static <E> Builder<E> builder() {
        return new Builder();
    }

    public static <E> DataPool<E> empty() {
        return new DataPool<E>(List.of());
    }

    public static <E> DataPool<E> of(E object) {
        return new DataPool<E>(List.of(Weighted.of(object, 1)));
    }

    public Optional<E> getDataOrEmpty(Random random) {
        return this.getOrEmpty(random).map(Weighted.Present::getData);
    }

    public static class Builder<E> {
        private final ImmutableList.Builder<Weighted.Present<E>> entries = ImmutableList.builder();

        public Builder<E> add(E object, int weight) {
            this.entries.add(Weighted.of(object, weight));
            return this;
        }

        public DataPool<E> build() {
            return new DataPool(this.entries.build());
        }
    }
}

