/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public interface StringIdentifiable {
    public static final int field_38377 = 16;

    public String asString();

    public static <E extends Enum<E>> Codec<E> createCodec(Supplier<E[]> enumValues) {
        Enum[] enums = (Enum[])enumValues.get();
        if (enums.length > 16) {
            Map<String, Enum> map = Arrays.stream(enums).collect(Collectors.toMap(identifiable -> ((StringIdentifiable)identifiable).asString(), enum_ -> enum_));
            return new Codec(enums, id -> id == null ? null : (Enum)map.get(id));
        }
        return new Codec(enums, id -> {
            for (Enum enum_ : enums) {
                if (!((StringIdentifiable)((Object)enum_)).asString().equals(id)) continue;
                return enum_;
            }
            return null;
        });
    }

    public static Keyable toKeyable(final StringIdentifiable[] values) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Arrays.stream(values).map(StringIdentifiable::asString).map(arg_0 -> ops.createString(arg_0));
            }
        };
    }

    @Deprecated
    public static class Codec<E extends Enum<E>>
    implements com.mojang.serialization.Codec<E> {
        private final com.mojang.serialization.Codec<E> base;
        private final Function<String, E> idToIdentifiable;

        public Codec(E[] values, Function<String, E> idToIdentifiable) {
            this.base = Codecs.orCompressed(Codecs.idChecked(identifiable -> ((StringIdentifiable)identifiable).asString(), idToIdentifiable), Codecs.rawIdChecked(enum_ -> ((Enum)enum_).ordinal(), ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1));
            this.idToIdentifiable = idToIdentifiable;
        }

        public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
            return this.base.decode(ops, input);
        }

        public <T> DataResult<T> encode(E enum_, DynamicOps<T> dynamicOps, T object) {
            return this.base.encode(enum_, dynamicOps, object);
        }

        @Nullable
        public E byId(@Nullable String id) {
            return (E)((Enum)this.idToIdentifiable.apply(id));
        }

        public E byId(@Nullable String id, E fallback) {
            return (E)((Enum)Objects.requireNonNullElse(this.byId(id), fallback));
        }

        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((E)((Enum)input), (DynamicOps<T>)ops, (T)prefix);
        }
    }
}

