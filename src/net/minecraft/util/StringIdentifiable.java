/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StringIdentifiable {
    public String asString();

    public static <E extends Enum<E>> Codec<E> createCodec(Supplier<E[]> enumValues, Function<? super String, ? extends E> fromString) {
        Enum[] enums = (Enum[])enumValues.get();
        return StringIdentifiable.createCodec(object -> ((Enum)object).ordinal(), ordinal -> enums[ordinal], fromString);
    }

    public static <E extends StringIdentifiable> Codec<E> createCodec(final ToIntFunction<E> compressedEncoder, final IntFunction<E> compressedDecoder, final Function<? super String, ? extends E> decoder) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.mergeToPrimitive(object, dynamicOps.createInt(compressedEncoder.applyAsInt(stringIdentifiable)));
                }
                return dynamicOps.mergeToPrimitive(object, dynamicOps.createString(stringIdentifiable.asString()));
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.getNumberValue(object).flatMap(id -> Optional.ofNullable((StringIdentifiable)compressedDecoder.apply(id.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element id: " + id)))).map(stringIdentifiable -> Pair.of((Object)stringIdentifiable, (Object)dynamicOps.empty()));
                }
                return dynamicOps.getStringValue(object).flatMap(name -> Optional.ofNullable((StringIdentifiable)decoder.apply(name)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name: " + name)))).map(stringIdentifiable -> Pair.of((Object)stringIdentifiable, (Object)dynamicOps.empty()));
            }

            public String toString() {
                return "StringRepresentable[" + compressedEncoder + "]";
            }

            public /* synthetic */ DataResult encode(Object value, DynamicOps dynamicOps, Object object) {
                return this.encode((E)((StringIdentifiable)value), (DynamicOps<T>)dynamicOps, (T)object);
            }
        };
    }

    public static Keyable toKeyable(final StringIdentifiable[] values) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                if (dynamicOps.compressMaps()) {
                    return IntStream.range(0, values.length).mapToObj(arg_0 -> dynamicOps.createInt(arg_0));
                }
                return Arrays.stream(values).map(StringIdentifiable::asString).map(arg_0 -> dynamicOps.createString(arg_0));
            }
        };
    }
}

