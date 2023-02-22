/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Codec$ResultFunction
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.joml.Vector3f
 */
package net.minecraft.util.dynamic;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector3f;

public class Codecs {
    public static final Codec<JsonElement> JSON_ELEMENT = Codec.PASSTHROUGH.xmap(dynamic -> (JsonElement)dynamic.convert((DynamicOps)JsonOps.INSTANCE).getValue(), element -> new Dynamic((DynamicOps)JsonOps.INSTANCE, element));
    public static final Codec<Text> TEXT = JSON_ELEMENT.flatXmap(element -> {
        try {
            return DataResult.success((Object)Text.Serializer.fromJson(element));
        }
        catch (JsonParseException jsonParseException) {
            return DataResult.error((String)jsonParseException.getMessage());
        }
    }, text -> {
        try {
            return DataResult.success((Object)Text.Serializer.toJsonTree(text));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error((String)illegalArgumentException.getMessage());
        }
    });
    public static final Codec<Vector3f> VECTOR_3F = Codec.FLOAT.listOf().comapFlatMap(list2 -> Util.toArray(list2, 3).map(list -> new Vector3f(((Float)list.get(0)).floatValue(), ((Float)list.get(1)).floatValue(), ((Float)list.get(2)).floatValue())), vec3f -> ImmutableList.of((Object)Float.valueOf(vec3f.x()), (Object)Float.valueOf(vec3f.y()), (Object)Float.valueOf(vec3f.z())));
    public static final Codec<Integer> NONNEGATIVE_INT = Codecs.rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
    public static final Codec<Integer> POSITIVE_INT = Codecs.rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<Float> POSITIVE_FLOAT = Codecs.rangedFloat(0.0f, Float.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<Pattern> REGULAR_EXPRESSION = Codec.STRING.comapFlatMap(pattern -> {
        try {
            return DataResult.success((Object)Pattern.compile(pattern));
        }
        catch (PatternSyntaxException patternSyntaxException) {
            return DataResult.error((String)("Invalid regex pattern '" + pattern + "': " + patternSyntaxException.getMessage()));
        }
    }, Pattern::pattern);
    public static final Codec<Instant> INSTANT = Codecs.instant(DateTimeFormatter.ISO_INSTANT);
    public static final Codec<byte[]> BASE_64 = Codec.STRING.comapFlatMap(encoded -> {
        try {
            return DataResult.success((Object)Base64.getDecoder().decode((String)encoded));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error((String)"Malformed base64 string");
        }
    }, data -> Base64.getEncoder().encodeToString((byte[])data));
    public static final Codec<TagEntryId> TAG_ENTRY_ID = Codec.STRING.comapFlatMap(tagEntry -> tagEntry.startsWith("#") ? Identifier.validate(tagEntry.substring(1)).map(id -> new TagEntryId((Identifier)id, true)) : Identifier.validate(tagEntry).map(id -> new TagEntryId((Identifier)id, false)), TagEntryId::asString);
    public static final Function<Optional<Long>, OptionalLong> OPTIONAL_OF_LONG_TO_OPTIONAL_LONG = optional -> optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    public static final Function<OptionalLong, Optional<Long>> OPTIONAL_LONG_TO_OPTIONAL_OF_LONG = optionalLong -> optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty();
    public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM.xmap(stream -> BitSet.valueOf(stream.toArray()), set -> Arrays.stream(set.toLongArray()));
    private static final Codec<Property> GAME_PROFILE_PROPERTY = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("name").forGetter(Property::getName), (App)Codec.STRING.fieldOf("value").forGetter(Property::getValue), (App)Codec.STRING.optionalFieldOf("signature").forGetter(property -> Optional.ofNullable(property.getSignature()))).apply((Applicative)instance, (key, value, signature) -> new Property(key, value, (String)signature.orElse(null))));
    @VisibleForTesting
    public static final Codec<PropertyMap> GAME_PROFILE_PROPERTY_MAP = Codec.either((Codec)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.STRING.listOf()), (Codec)GAME_PROFILE_PROPERTY.listOf()).xmap(either -> {
        PropertyMap propertyMap = new PropertyMap();
        either.ifLeft(map -> map.forEach((key, values) -> {
            for (String string : values) {
                propertyMap.put(key, (Object)new Property(key, string));
            }
        })).ifRight(properties -> {
            for (Property property : properties) {
                propertyMap.put((Object)property.getName(), (Object)property);
            }
        });
        return propertyMap;
    }, properties -> com.mojang.datafixers.util.Either.right(properties.values().stream().toList()));
    public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create(instance -> instance.group((App)Codec.mapPair((MapCodec)Uuids.CODEC.xmap(Optional::of, optional -> optional.orElse(null)).optionalFieldOf("id", Optional.empty()), (MapCodec)Codec.STRING.xmap(Optional::of, optional -> optional.orElse(null)).optionalFieldOf("name", Optional.empty())).flatXmap(Codecs::createGameProfileFromPair, Codecs::createPairFromGameProfile).forGetter(Function.identity()), (App)GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("properties", (Object)new PropertyMap()).forGetter(GameProfile::getProperties)).apply((Applicative)instance, (profile, properties) -> {
        properties.forEach((key, property) -> profile.getProperties().put(key, property));
        return profile;
    }));

    public static <F, S> Codec<com.mojang.datafixers.util.Either<F, S>> xor(Codec<F> first, Codec<S> second) {
        return new Xor<F, S>(first, second);
    }

    public static <P, I> Codec<I> createCodecForPairObject(Codec<P> codec, String leftFieldName, String rightFieldName, BiFunction<P, P, DataResult<I>> combineFunction, Function<I, P> leftFunction, Function<I, P> rightFunction) {
        Codec codec2 = Codec.list(codec).comapFlatMap(list2 -> Util.toArray(list2, 2).flatMap(list -> {
            Object object = list.get(0);
            Object object2 = list.get(1);
            return (DataResult)combineFunction.apply(object, object2);
        }), pair -> ImmutableList.of(leftFunction.apply(pair), rightFunction.apply(pair)));
        Codec codec3 = RecordCodecBuilder.create(instance -> instance.group((App)codec.fieldOf(leftFieldName).forGetter(Pair::getFirst), (App)codec.fieldOf(rightFieldName).forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).comapFlatMap(pair -> (DataResult)combineFunction.apply(pair.getFirst(), pair.getSecond()), pair -> Pair.of(leftFunction.apply(pair), rightFunction.apply(pair)));
        Codec codec4 = new Either(codec2, codec3).xmap(either -> either.map(object -> object, object -> object), com.mojang.datafixers.util.Either::left);
        return Codec.either(codec, (Codec)codec4).comapFlatMap(either -> (DataResult)either.map(object -> (DataResult)combineFunction.apply(object, object), DataResult::success), pair -> {
            Object object2;
            Object object = leftFunction.apply(pair);
            if (Objects.equals(object, object2 = rightFunction.apply(pair))) {
                return com.mojang.datafixers.util.Either.left(object);
            }
            return com.mojang.datafixers.util.Either.right((Object)pair);
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

    public static <E> Codec<E> idChecked(Function<E, String> elementToId, Function<String, E> idToElement) {
        return Codec.STRING.flatXmap(id -> Optional.ofNullable(idToElement.apply((String)id)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name:" + id))), element -> Optional.ofNullable((String)elementToId.apply(element)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Element with unknown name: " + element))));
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

    public static <E> Codec<E> withLifecycle(Codec<E> originalCodec, final Function<E, Lifecycle> entryLifecycleGetter, final Function<E, Lifecycle> lifecycleGetter) {
        return originalCodec.mapResult(new Codec.ResultFunction<E>(){

            public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<E, T>> result) {
                return result.result().map(pair -> result.setLifecycle((Lifecycle)entryLifecycleGetter.apply(pair.getFirst()))).orElse(result);
            }

            public <T> DataResult<T> coApply(DynamicOps<T> ops, E input, DataResult<T> result) {
                return result.setLifecycle((Lifecycle)lifecycleGetter.apply(input));
            }

            public String toString() {
                return "WithLifecycle[" + entryLifecycleGetter + " " + lifecycleGetter + "]";
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
            final /* synthetic */ Function retriever;

            ContextRetrievalCodec(Function retriever) {
                this.retriever = retriever;
            }

            public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix;
            }

            public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
                return (DataResult)this.retriever.apply(ops);
            }

            public String toString() {
                return "ContextRetrievalCodec[" + this.retriever + "]";
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

    public static <A> Codec<A> exceptionCatching(final Codec<A> codec) {
        return Codec.of(codec, (Decoder)new Decoder<A>(){

            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                try {
                    return codec.decode(ops, input);
                }
                catch (Exception exception) {
                    return DataResult.error((String)("Cauch exception decoding " + input + ": " + exception.getMessage()));
                }
            }
        });
    }

    public static Codec<Instant> instant(DateTimeFormatter formatter) {
        return Codec.STRING.comapFlatMap(dateTimeString -> {
            try {
                return DataResult.success((Object)Instant.from(formatter.parse((CharSequence)dateTimeString)));
            }
            catch (Exception exception) {
                return DataResult.error((String)exception.getMessage());
            }
        }, formatter::format);
    }

    public static MapCodec<OptionalLong> optionalLong(MapCodec<Optional<Long>> codec) {
        return codec.xmap(OPTIONAL_OF_LONG_TO_OPTIONAL_LONG, OPTIONAL_LONG_TO_OPTIONAL_OF_LONG);
    }

    private static DataResult<GameProfile> createGameProfileFromPair(Pair<Optional<UUID>, Optional<String>> pair) {
        try {
            return DataResult.success((Object)new GameProfile((UUID)((Optional)pair.getFirst()).orElse(null), (String)((Optional)pair.getSecond()).orElse(null)));
        }
        catch (Throwable throwable) {
            return DataResult.error((String)throwable.getMessage());
        }
    }

    private static DataResult<Pair<Optional<UUID>, Optional<String>>> createPairFromGameProfile(GameProfile profile) {
        return DataResult.success((Object)Pair.of(Optional.ofNullable(profile.getId()), Optional.ofNullable(profile.getName())));
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

    public record TagEntryId(Identifier id, boolean tag) {
        @Override
        public String toString() {
            return this.asString();
        }

        private String asString() {
            return this.tag ? "#" + this.id : this.id.toString();
        }
    }
}

