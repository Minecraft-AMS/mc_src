/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;

public interface StringIdentifiable {
    public String asString();

    public static <E extends Enum<E>> Codec<E> createCodec(Supplier<E[]> enumValues, Function<String, E> fromString) {
        Enum[] enums = (Enum[])enumValues.get();
        return Codecs.orCompressed(Codecs.method_39508(object -> ((StringIdentifiable)object).asString(), fromString), Codecs.rawIdChecked(object -> ((Enum)object).ordinal(), ordinal -> ordinal >= 0 && ordinal < enums.length ? enums[ordinal] : null, -1));
    }

    public static Keyable toKeyable(final StringIdentifiable[] values) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return Arrays.stream(values).map(StringIdentifiable::asString).map(arg_0 -> dynamicOps.createString(arg_0));
            }
        };
    }
}

