/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util.collection;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.collection.Weight;

public interface Weighted {
    public Weight getWeight();

    public static <T> Present<T> of(T data, int weight) {
        return new Present<T>(data, Weight.of(weight));
    }

    public static class Present<T>
    implements Weighted {
        private final T data;
        private final Weight weight;

        Present(T data, Weight weight) {
            this.data = data;
            this.weight = weight;
        }

        public T getData() {
            return this.data;
        }

        @Override
        public Weight getWeight() {
            return this.weight;
        }

        public static <E> Codec<Present<E>> createCodec(Codec<E> dataCodec) {
            return RecordCodecBuilder.create(instance -> instance.group((App)dataCodec.fieldOf("data").forGetter(Present::getData), (App)Weight.CODEC.fieldOf("weight").forGetter(Present::getWeight)).apply((Applicative)instance, Present::new));
        }
    }

    public static class Absent
    implements Weighted {
        private final Weight weight;

        public Absent(int weight) {
            this.weight = Weight.of(weight);
        }

        public Absent(Weight weight) {
            this.weight = weight;
        }

        @Override
        public Weight getWeight() {
            return this.weight;
        }
    }
}

