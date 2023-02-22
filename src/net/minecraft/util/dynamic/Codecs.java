/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Codec$ResultFunction
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryEntryList;
import org.apache.commons.lang3.mutable.MutableObject;

public class Codecs {
    public static final Codec<Integer> NONNEGATIVE_INT = Codecs.rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
    public static final Codec<Integer> POSITIVE_INT = Codecs.rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<Float> POSITIVE_FLOAT = Codecs.rangedFloat(0.0f, Float.MAX_VALUE, v -> "Value must be positive: " + v);

    public static <F, S> Codec<com.mojang.datafixers.util.Either<F, S>> xor(Codec<F> first, Codec<S> second) {
        return new Xor<F, S>(first, second);
    }

    public static <P, I> Codec<I> createCodecForPairObject(Codec<P> codec, String leftFieldName, String rightFieldName, BiFunction<P, P, DataResult<I>> combineFunction, Function<I, P> leftFunction, Function<I, P> rightFunction) {
        Codec codec2 = Codec.list(codec).comapFlatMap(list2 -> Util.toArray(list2, 2).flatMap(list -> {
            Object object = list.get(0);
            Object object2 = list.get(1);
            return (DataResult)combineFunction.apply(object, object2);
        }), object -> ImmutableList.of(leftFunction.apply(object), rightFunction.apply(object)));
        Codec codec3 = RecordCodecBuilder.create(instance -> instance.group((App)codec.fieldOf(leftFieldName).forGetter(Pair::getFirst), (App)codec.fieldOf(rightFieldName).forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).comapFlatMap(pair -> (DataResult)combineFunction.apply(pair.getFirst(), pair.getSecond()), object -> Pair.of(leftFunction.apply(object), rightFunction.apply(object)));
        Codec codec4 = new Either(codec2, codec3).xmap(either -> either.map(object -> object, object -> object), com.mojang.datafixers.util.Either::left);
        return Codec.either(codec, (Codec)codec4).comapFlatMap(either -> (DataResult)either.map(object -> (DataResult)combineFunction.apply(object, object), DataResult::success), object -> {
            Object object3;
            Object object2 = leftFunction.apply(object);
            if (Objects.equals(object2, object3 = rightFunction.apply(object))) {
                return com.mojang.datafixers.util.Either.left(object2);
            }
            return com.mojang.datafixers.util.Either.right((Object)object);
        });
    }

    public static <A> Codec.ResultFunction<A> orElsePartial(final A object) {
        return new Codec.ResultFunction<A>(){

            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> result) {
                MutableObject mutableObject = new MutableObject();
                Optional optional = result.resultOrPartial(arg_0 -> ((MutableObject)mutableObject).setValue(arg_0));
                if (optional.isPresent()) {
                    return result;
                }
                return DataResult.error((String)("(" + (String)mutableObject.getValue() + " -> using default)"), (Object)Pair.of((Object)object, input));
            }

            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> result) {
                return result;
            }

            public String toString() {
                return "OrElsePartial[" + object + "]";
            }
        };
    }

    public static <E> Codec<E> rawIdChecked(ToIntFunction<E> elementToRawId, IntFunction<E> rawIdToElement, int errorRawId) {
        return Codec.INT.flatXmap(rawId -> Optional.ofNullable(rawIdToElement.apply((int)rawId)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element id: " + rawId))), element -> {
            int j = elementToRawId.applyAsInt(element);
            return j == errorRawId ? DataResult.error((String)("Element with unknown id: " + element)) : DataResult.success((Object)j);
        });
    }

    public static <E> Codec<E> method_39508(Function<E, String> function, Function<String, E> function2) {
        return Codec.STRING.flatXmap(string -> Optional.ofNullable(function2.apply((String)string)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name:" + string))), object -> Optional.ofNullable((String)function.apply(object)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Element with unknown name: " + object))));
    }

    public static <E> Codec<E> orCompressed(final Codec<E> uncompressedCodec, final Codec<E> compressedCodec) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
                if (ops.compressMaps()) {
                    return compressedCodec.encode(input, ops, prefix);
                }
                return uncompressedCodec.encode(input, ops, prefix);
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
                if (ops.compressMaps()) {
                    return compressedCodec.decode(ops, input);
                }
                return uncompressedCodec.decode(ops, input);
            }

            public String toString() {
                return uncompressedCodec + " orCompressed " + compressedCodec;
            }
        };
    }

    public static <E> Codec<E> withLifecycle(Codec<E> originalCodec, final Function<E, Lifecycle> function, final Function<E, Lifecycle> function2) {
        return originalCodec.mapResult(new Codec.ResultFunction<E>(){

            public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<E, T>> result) {
                return result.result().map(pair -> result.setLifecycle((Lifecycle)function.apply(pair.getFirst()))).orElse(result);
            }

            public <T> DataResult<T> coApply(DynamicOps<T> ops, E input, DataResult<T> result) {
                return result.setLifecycle((Lifecycle)function2.apply(input));
            }

            public String toString() {
                return "WithLifecycle[" + function + " " + function2 + "]";
            }
        });
    }

    private static <N extends Number> Function<N, DataResult<N>> createIntRangeChecker(N min, N max, Function<N, String> messageFactory) {
        return value -> {
            if (((Comparable)((Object)value)).compareTo(min) >= 0 && ((Comparable)((Object)value)).compareTo(max) <= 0) {
                return DataResult.success((Object)value);
            }
            return DataResult.error((String)((String)messageFactory.apply(value)));
        };
    }

    private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
        Function<Integer, DataResult<Integer>> function = Codecs.createIntRangeChecker(min, max, messageFactory);
        return Codec.INT.flatXmap(function, function);
    }

    private static <N extends Number> Function<N, DataResult<N>> createFloatRangeChecker(N min, N max, Function<N, String> messageFactory) {
        return value -> {
            if (((Comparable)((Object)value)).compareTo(min) > 0 && ((Comparable)((Object)value)).compareTo(max) <= 0) {
                return DataResult.success((Object)value);
            }
            return DataResult.error((String)((String)messageFactory.apply(value)));
        };
    }

    private static Codec<Float> rangedFloat(float min, float max, Function<Float, String> messageFactory) {
        Function<Float, DataResult<Float>> function = Codecs.createFloatRangeChecker(Float.valueOf(min), Float.valueOf(max), messageFactory);
        return Codec.FLOAT.flatXmap(function, function);
    }

    public static <T> Function<List<T>, DataResult<List<T>>> createNonEmptyListChecker() {
        return list -> {
            if (list.isEmpty()) {
                return DataResult.error((String)"List must have contents");
            }
            return DataResult.success((Object)list);
        };
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> originalCodec) {
        return originalCodec.flatXmap(Codecs.createNonEmptyListChecker(), Codecs.createNonEmptyListChecker());
    }

    public static <T> Function<RegistryEntryList<T>, DataResult<RegistryEntryList<T>>> createNonEmptyEntryListChecker() {
        return entries -> {
            if (entries.getStorage().right().filter(List::isEmpty).isPresent()) {
                return DataResult.error((String)"List must have contents");
            }
            return DataResult.success((Object)entries);
        };
    }

    public static <T> Codec<RegistryEntryList<T>> nonEmptyEntryList(Codec<RegistryEntryList<T>> originalCodec) {
        return originalCodec.flatXmap(Codecs.createNonEmptyEntryListChecker(), Codecs.createNonEmptyEntryListChecker());
    }

    public static <A> Codec<A> createLazy(Supplier<Codec<A>> supplier) {
        return new Lazy<A>(supplier);
    }

    public static <E> MapCodec<E> createContextRetrievalCodec(Function<DynamicOps<?>, DataResult<E>> retriever) {
        class ContextRetrievalCodec
        extends MapCodec<E> {
            final /* synthetic */ Function field_36397;

            ContextRetrievalCodec(Function retriever) {
                this.field_36397 = retriever;
            }

            public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix;
            }

            public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
                return (DataResult)this.field_36397.apply(ops);
            }

            public String toString() {
                return "ContextRetrievalCodec[" + this.field_36397 + "]";
            }

            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }
        }
        return new ContextRetrievalCodec(retriever);
    }

    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> createEqualTypeChecker(Function<E, T> typeGetter) {
        return collection -> {
            Iterator iterator = collection.iterator();
            if (iterator.hasNext()) {
                Object object = typeGetter.apply(iterator.next());
                while (iterator.hasNext()) {
                    Object object2 = iterator.next();
                    Object object3 = typeGetter.apply(object2);
                    if (object3 == object) continue;
                    return DataResult.error((String)("Mixed type list: element " + object2 + " had type " + object3 + ", but list is of type " + object));
                }
            }
            return DataResult.success((Object)collection, (Lifecycle)Lifecycle.stable());
        };
    }

    static final class Xor<F, S>
    implements Codec<com.mojang.datafixers.util.Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public Xor(Codec<F> first, Codec<S> second) {
            this.first = first;
            this.second = second;
        }

        public <T> DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult dataResult = this.first.decode(ops, input).map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::left));
            DataResult dataResult2 = this.second.decode(ops, input).map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::right));
            Optional optional = dataResult.result();
            Optional optional2 = dataResult2.result();
            if (optional.isPresent() && optional2.isPresent()) {
                return DataResult.error((String)("Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get()), (Object)((Pair)optional.get()));
            }
            return optional.isPresent() ? dataResult : dataResult2;
        }

        public <T> DataResult<T> encode(com.mojang.datafixers.util.Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
            return (DataResult)either.map(left -> this.first.encode(left, dynamicOps, object), right -> this.second.encode(right, dynamicOps, object));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Xor xor = (Xor)o;
            return Objects.equals(this.first, xor.first) && Objects.equals(this.second, xor.second);
        }

        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }

        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }

        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((com.mojang.datafixers.util.Either)input, ops, prefix);
        }
    }

    static final class Either<F, S>
    implements Codec<com.mojang.datafixers.util.Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public Either(Codec<F> first, Codec<S> second) {
            this.first = first;
            this.second = second;
        }

        public <T> DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult dataResult = this.first.decode(ops, input).map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::left));
            if (!dataResult.error().isPresent()) {
                return dataResult;
            }
            DataResult dataResult2 = this.second.decode(ops, input).map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::right));
            if (!dataResult2.error().isPresent()) {
                return dataResult2;
            }
            return dataResult.apply2((pair, pair2) -> pair2, dataResult2);
        }

        public <T> DataResult<T> encode(com.mojang.datafixers.util.Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
            return (DataResult)either.map(left -> this.first.encode(left, dynamicOps, object), right -> this.second.encode(right, dynamicOps, object));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Either either = (Either)o;
            return Objects.equals(this.first, either.first) && Objects.equals(this.second, either.second);
        }

        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }

        public String toString() {
            return "EitherCodec[" + this.first + ", " + this.second + "]";
        }

        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((com.mojang.datafixers.util.Either)input, ops, prefix);
        }
    }

    record Lazy<A>(Supplier<Codec<A>> delegate) implements Codec<A>
    {
        Lazy(Supplier<Codec<A>> supplier) {
            this.delegate = supplier = Suppliers.memoize(() -> supplier.get());
        }

        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
            return this.delegate.get().decode(ops, input);
        }

        public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
            return this.delegate.get().encode(input, ops, prefix);
        }
    }
}

