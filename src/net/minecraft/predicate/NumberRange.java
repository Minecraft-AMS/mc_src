/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public abstract class NumberRange<T extends Number> {
    public static final SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableText("argument.range.empty", new Object[0]));
    public static final SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType((Message)new TranslatableText("argument.range.swapped", new Object[0]));
    protected final T min;
    protected final T max;

    protected NumberRange(@Nullable T min, @Nullable T max) {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public T getMin() {
        return this.min;
    }

    @Nullable
    public T getMax() {
        return this.max;
    }

    public boolean isDummy() {
        return this.min == null && this.max == null;
    }

    public JsonElement toJson() {
        if (this.isDummy()) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        }
        JsonObject jsonObject = new JsonObject();
        if (this.min != null) {
            jsonObject.addProperty("min", this.min);
        }
        if (this.max != null) {
            jsonObject.addProperty("max", this.max);
        }
        return jsonObject;
    }

    protected static <T extends Number, R extends NumberRange<T>> R fromJson(@Nullable JsonElement json, R fallback, BiFunction<JsonElement, String, T> asNumber, Factory<T, R> factory) {
        if (json == null || json.isJsonNull()) {
            return fallback;
        }
        if (JsonHelper.isNumber(json)) {
            Number number = (Number)asNumber.apply(json, "value");
            return factory.create(number, number);
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "value");
        Number number2 = jsonObject.has("min") ? (Number)((Number)asNumber.apply(jsonObject.get("min"), "min")) : (Number)null;
        Number number3 = jsonObject.has("max") ? (Number)((Number)asNumber.apply(jsonObject.get("max"), "max")) : (Number)null;
        return factory.create(number2, number3);
    }

    protected static <T extends Number, R extends NumberRange<T>> R parse(StringReader commandReader, class_2098<T, R> arg, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier, Function<T, T> mapper) throws CommandSyntaxException {
        if (!commandReader.canRead()) {
            throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)commandReader);
        }
        int i = commandReader.getCursor();
        try {
            Number number2;
            Number number = (Number)NumberRange.map(NumberRange.fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
            if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
                commandReader.skip();
                commandReader.skip();
                number2 = (Number)NumberRange.map(NumberRange.fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
                if (number == null && number2 == null) {
                    throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)commandReader);
                }
            } else {
                number2 = number;
            }
            if (number == null && number2 == null) {
                throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)commandReader);
            }
            return arg.create(commandReader, number, number2);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            commandReader.setCursor(i);
            throw new CommandSyntaxException(commandSyntaxException.getType(), commandSyntaxException.getRawMessage(), commandSyntaxException.getInput(), i);
        }
    }

    @Nullable
    private static <T extends Number> T fromStringReader(StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && NumberRange.isNextCharValid(reader)) {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return (T)((Number)converter.apply(string));
        }
        catch (NumberFormatException numberFormatException) {
            throw exceptionTypeSupplier.get().createWithContext((ImmutableStringReader)reader, (Object)string);
        }
    }

    private static boolean isNextCharValid(StringReader reader) {
        char c = reader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (c == '.') {
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static <T> T map(@Nullable T object, Function<T, T> function) {
        return object == null ? null : (T)function.apply(object);
    }

    @FunctionalInterface
    public static interface class_2098<T extends Number, R extends NumberRange<T>> {
        public R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    public static interface Factory<T extends Number, R extends NumberRange<T>> {
        public R create(@Nullable T var1, @Nullable T var2);
    }

    public static class FloatRange
    extends NumberRange<Float> {
        public static final FloatRange ANY = new FloatRange(null, null);
        private final Double minSquared;
        private final Double maxSquared;

        private static FloatRange create(StringReader reader, @Nullable Float min, @Nullable Float max) throws CommandSyntaxException {
            if (min != null && max != null && min.floatValue() > max.floatValue()) {
                throw EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)reader);
            }
            return new FloatRange(min, max);
        }

        @Nullable
        private static Double squared(@Nullable Float value) {
            return value == null ? null : Double.valueOf(value.doubleValue() * value.doubleValue());
        }

        private FloatRange(@Nullable Float max, @Nullable Float float_) {
            super(max, float_);
            this.minSquared = FloatRange.squared(max);
            this.maxSquared = FloatRange.squared(float_);
        }

        public static FloatRange atLeast(float value) {
            return new FloatRange(Float.valueOf(value), null);
        }

        public boolean matches(float f) {
            if (this.min != null && ((Float)this.min).floatValue() > f) {
                return false;
            }
            return this.max == null || !(((Float)this.max).floatValue() < f);
        }

        public boolean matchesSquared(double d) {
            if (this.minSquared != null && this.minSquared > d) {
                return false;
            }
            return this.maxSquared == null || !(this.maxSquared < d);
        }

        public static FloatRange fromJson(@Nullable JsonElement element) {
            return FloatRange.fromJson(element, ANY, JsonHelper::asFloat, FloatRange::new);
        }

        public static FloatRange parse(StringReader reader) throws CommandSyntaxException {
            return FloatRange.parse(reader, float_ -> float_);
        }

        public static FloatRange parse(StringReader reader, Function<Float, Float> function) throws CommandSyntaxException {
            return FloatRange.parse(reader, FloatRange::create, Float::parseFloat, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidFloat(), function);
        }
    }

    public static class IntRange
    extends NumberRange<Integer> {
        public static final IntRange ANY = new IntRange(null, null);
        private final Long minSquared;
        private final Long maxSquared;

        private static IntRange method_9055(StringReader stringReader, @Nullable Integer min, @Nullable Integer max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)stringReader);
            }
            return new IntRange(min, max);
        }

        @Nullable
        private static Long squared(@Nullable Integer value) {
            return value == null ? null : Long.valueOf(value.longValue() * value.longValue());
        }

        private IntRange(@Nullable Integer max, @Nullable Integer integer) {
            super(max, integer);
            this.minSquared = IntRange.squared(max);
            this.maxSquared = IntRange.squared(integer);
        }

        public static IntRange exactly(int value) {
            return new IntRange(value, value);
        }

        public static IntRange atLeast(int value) {
            return new IntRange(value, null);
        }

        public boolean test(int i) {
            if (this.min != null && (Integer)this.min > i) {
                return false;
            }
            return this.max == null || (Integer)this.max >= i;
        }

        public static IntRange fromJson(@Nullable JsonElement element) {
            return IntRange.fromJson(element, ANY, JsonHelper::asInt, IntRange::new);
        }

        public static IntRange parse(StringReader stringReader) throws CommandSyntaxException {
            return IntRange.fromStringReader(stringReader, integer -> integer);
        }

        public static IntRange fromStringReader(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
            return IntRange.parse(stringReader, IntRange::method_9055, Integer::parseInt, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidInt(), function);
        }
    }
}

