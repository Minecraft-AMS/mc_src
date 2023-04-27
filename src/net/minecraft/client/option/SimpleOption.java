/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public final class SimpleOption<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final PotentialValuesBasedCallbacks<Boolean> BOOLEAN = new PotentialValuesBasedCallbacks(ImmutableList.of((Object)Boolean.TRUE, (Object)Boolean.FALSE), Codec.BOOL);
    public static final ValueTextGetter<Boolean> BOOLEAN_TEXT_GETTER = (optionText, value) -> value != false ? ScreenTexts.ON : ScreenTexts.OFF;
    private final TooltipFactory<T> tooltipFactory;
    final Function<T, Text> textGetter;
    private final Callbacks<T> callbacks;
    private final Codec<T> codec;
    private final T defaultValue;
    private final Consumer<T> changeCallback;
    final Text text;
    T value;

    public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return SimpleOption.ofBoolean(key, SimpleOption.emptyTooltip(), defaultValue, changeCallback);
    }

    public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue) {
        return SimpleOption.ofBoolean(key, SimpleOption.emptyTooltip(), defaultValue, value -> {});
    }

    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactory<Boolean> tooltipFactory, boolean defaultValue) {
        return SimpleOption.ofBoolean(key, tooltipFactory, defaultValue, value -> {});
    }

    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactory<Boolean> tooltipFactory, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return SimpleOption.ofBoolean(key, tooltipFactory, BOOLEAN_TEXT_GETTER, defaultValue, changeCallback);
    }

    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactory<Boolean> tooltipFactory, ValueTextGetter<Boolean> valueTextGetter, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return new SimpleOption<Boolean>(key, tooltipFactory, valueTextGetter, BOOLEAN, defaultValue, changeCallback);
    }

    public SimpleOption(String key, TooltipFactory<T> tooltipFactory, ValueTextGetter<T> valueTextGetter, Callbacks<T> callbacks, T defaultValue, Consumer<T> changeCallback) {
        this(key, tooltipFactory, valueTextGetter, callbacks, callbacks.codec(), defaultValue, changeCallback);
    }

    public SimpleOption(String key, TooltipFactory<T> tooltipFactory, ValueTextGetter<T> valueTextGetter, Callbacks<T> callbacks, Codec<T> codec, T defaultValue, Consumer<T> changeCallback) {
        this.text = Text.translatable(key);
        this.tooltipFactory = tooltipFactory;
        this.textGetter = value -> valueTextGetter.toString(this.text, value);
        this.callbacks = callbacks;
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.changeCallback = changeCallback;
        this.value = this.defaultValue;
    }

    public static <T> TooltipFactory<T> emptyTooltip() {
        return value -> null;
    }

    public static <T> TooltipFactory<T> constantTooltip(Text text) {
        return value -> Tooltip.of(text);
    }

    public static <T extends TranslatableOption> ValueTextGetter<T> enumValueText() {
        return (optionText, value) -> value.getText();
    }

    public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
        return this.createWidget(options, x, y, width, value -> {});
    }

    public ClickableWidget createWidget(GameOptions options, int x, int y, int width, Consumer<T> changeCallback) {
        return this.callbacks.getWidgetCreator(this.tooltipFactory, options, x, y, width, changeCallback).apply(this);
    }

    public T getValue() {
        return this.value;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }

    public String toString() {
        return this.text.getString();
    }

    public void setValue(T value) {
        Object object = this.callbacks.validate(value).orElseGet(() -> {
            LOGGER.error("Illegal option value " + value + " for " + this.text);
            return this.defaultValue;
        });
        if (!MinecraftClient.getInstance().isRunning()) {
            this.value = object;
            return;
        }
        if (!Objects.equals(this.value, object)) {
            this.value = object;
            this.changeCallback.accept(this.value);
        }
    }

    public Callbacks<T> getCallbacks() {
        return this.callbacks;
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface TooltipFactory<T> {
        @Nullable
        public Tooltip apply(T var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ValueTextGetter<T> {
        public Text toString(Text var1, T var2);
    }

    @Environment(value=EnvType.CLIENT)
    public record PotentialValuesBasedCallbacks<T>(List<T> values, Codec<T> codec) implements CyclingCallbacks<T>
    {
        @Override
        public Optional<T> validate(T value) {
            return this.values.contains(value) ? Optional.of(value) : Optional.empty();
        }

        @Override
        public CyclingButtonWidget.Values<T> getValues() {
            return CyclingButtonWidget.Values.of(this.values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface Callbacks<T> {
        public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(TooltipFactory<T> var1, GameOptions var2, int var3, int var4, int var5, Consumer<T> var6);

        public Optional<T> validate(T var1);

        public Codec<T> codec();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DoubleSliderCallbacks
    extends Enum<DoubleSliderCallbacks>
    implements SliderCallbacks<Double> {
        public static final /* enum */ DoubleSliderCallbacks INSTANCE = new DoubleSliderCallbacks();
        private static final /* synthetic */ DoubleSliderCallbacks[] field_37876;

        public static DoubleSliderCallbacks[] values() {
            return (DoubleSliderCallbacks[])field_37876.clone();
        }

        public static DoubleSliderCallbacks valueOf(String string) {
            return Enum.valueOf(DoubleSliderCallbacks.class, string);
        }

        @Override
        public Optional<Double> validate(Double double_) {
            return double_ >= 0.0 && double_ <= 1.0 ? Optional.of(double_) : Optional.empty();
        }

        @Override
        public double toSliderProgress(Double double_) {
            return double_;
        }

        @Override
        public Double toValue(double d) {
            return d;
        }

        public <R> SliderCallbacks<R> withModifier(final DoubleFunction<? extends R> sliderProgressValueToValue, final ToDoubleFunction<? super R> valueToSliderProgressValue) {
            return new SliderCallbacks<R>(){

                @Override
                public Optional<R> validate(R value) {
                    return this.validate(valueToSliderProgressValue.applyAsDouble(value)).map(sliderProgressValueToValue::apply);
                }

                @Override
                public double toSliderProgress(R value) {
                    return this.toSliderProgress(valueToSliderProgressValue.applyAsDouble(value));
                }

                @Override
                public R toValue(double sliderProgress) {
                    return sliderProgressValueToValue.apply(this.toValue(sliderProgress));
                }

                @Override
                public Codec<R> codec() {
                    return this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsDouble);
                }
            };
        }

        @Override
        public Codec<Double> codec() {
            return Codec.either((Codec)Codec.doubleRange((double)0.0, (double)1.0), (Codec)Codec.BOOL).xmap(either -> (Double)either.map(value -> value, value -> value != false ? 1.0 : 0.0), Either::left);
        }

        @Override
        public /* synthetic */ Object toValue(double sliderProgress) {
            return this.toValue(sliderProgress);
        }

        private static /* synthetic */ DoubleSliderCallbacks[] method_41767() {
            return new DoubleSliderCallbacks[]{INSTANCE};
        }

        static {
            field_37876 = DoubleSliderCallbacks.method_41767();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record MaxSuppliableIntCallbacks(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements IntSliderCallbacks,
    TypeChangeableCallbacks<Integer>
    {
        @Override
        public Optional<Integer> validate(Integer integer) {
            return Optional.of(MathHelper.clamp(integer, this.minInclusive(), this.maxInclusive()));
        }

        @Override
        public int maxInclusive() {
            return this.maxSupplier.getAsInt();
        }

        @Override
        public Codec<Integer> codec() {
            return Codecs.validate(Codec.INT, value -> {
                int i = this.encodableMaxInclusive + 1;
                if (value.compareTo(this.minInclusive) >= 0 && value.compareTo(i) <= 0) {
                    return DataResult.success((Object)value);
                }
                return DataResult.error(() -> "Value " + value + " outside of range [" + this.minInclusive + ":" + i + "]", (Object)value);
            });
        }

        @Override
        public boolean isCycling() {
            return true;
        }

        @Override
        public CyclingButtonWidget.Values<Integer> getValues() {
            return CyclingButtonWidget.Values.of(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive) implements IntSliderCallbacks
    {
        @Override
        public Optional<Integer> validate(Integer integer) {
            return integer.compareTo(this.minInclusive()) >= 0 && integer.compareTo(this.maxInclusive()) <= 0 ? Optional.of(integer) : Optional.empty();
        }

        @Override
        public Codec<Integer> codec() {
            return Codec.intRange((int)this.minInclusive, (int)(this.maxInclusive + 1));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface IntSliderCallbacks
    extends SliderCallbacks<Integer> {
        public int minInclusive();

        public int maxInclusive();

        @Override
        default public double toSliderProgress(Integer integer) {
            return MathHelper.map(integer.intValue(), this.minInclusive(), this.maxInclusive(), 0.0f, 1.0f);
        }

        @Override
        default public Integer toValue(double d) {
            return MathHelper.floor(MathHelper.map(d, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive()));
        }

        default public <R> SliderCallbacks<R> withModifier(final IntFunction<? extends R> sliderProgressValueToValue, final ToIntFunction<? super R> valueToSliderProgressValue) {
            return new SliderCallbacks<R>(){

                @Override
                public Optional<R> validate(R value) {
                    return this.validate(valueToSliderProgressValue.applyAsInt(value)).map(sliderProgressValueToValue::apply);
                }

                @Override
                public double toSliderProgress(R value) {
                    return this.toSliderProgress(valueToSliderProgressValue.applyAsInt(value));
                }

                @Override
                public R toValue(double sliderProgress) {
                    return sliderProgressValueToValue.apply(this.toValue(sliderProgress));
                }

                @Override
                public Codec<R> codec() {
                    return this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsInt);
                }
            };
        }

        @Override
        default public /* synthetic */ Object toValue(double sliderProgress) {
            return this.toValue(sliderProgress);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class OptionSliderWidgetImpl<N>
    extends OptionSliderWidget {
        private final SimpleOption<N> option;
        private final SliderCallbacks<N> callbacks;
        private final TooltipFactory<N> tooltipFactory;
        private final Consumer<N> changeCallback;

        OptionSliderWidgetImpl(GameOptions options, int x, int y, int width, int height, SimpleOption<N> option, SliderCallbacks<N> callbacks, TooltipFactory<N> tooltipFactory, Consumer<N> changeCallback) {
            super(options, x, y, width, height, callbacks.toSliderProgress(option.getValue()));
            this.option = option;
            this.callbacks = callbacks;
            this.tooltipFactory = tooltipFactory;
            this.changeCallback = changeCallback;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(this.option.textGetter.apply(this.option.getValue()));
            this.setTooltip(this.tooltipFactory.apply(this.callbacks.toValue(this.value)));
        }

        @Override
        protected void applyValue() {
            this.option.setValue(this.callbacks.toValue(this.value));
            this.options.write();
            this.changeCallback.accept(this.option.getValue());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record LazyCyclingCallbacks<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements CyclingCallbacks<T>
    {
        @Override
        public Optional<T> validate(T value) {
            return this.validateValue.apply(value);
        }

        @Override
        public CyclingButtonWidget.Values<T> getValues() {
            return CyclingButtonWidget.Values.of((Collection)this.values.get());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record AlternateValuesSupportingCyclingCallbacks<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, CyclingCallbacks.ValueSetter<T> valueSetter, Codec<T> codec) implements CyclingCallbacks<T>
    {
        @Override
        public CyclingButtonWidget.Values<T> getValues() {
            return CyclingButtonWidget.Values.of(this.altCondition, this.values, this.altValues);
        }

        @Override
        public Optional<T> validate(T value) {
            return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface TypeChangeableCallbacks<T>
    extends CyclingCallbacks<T>,
    SliderCallbacks<T> {
        public boolean isCycling();

        @Override
        default public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback) {
            if (this.isCycling()) {
                return CyclingCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback);
            }
            return SliderCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface CyclingCallbacks<T>
    extends Callbacks<T> {
        public CyclingButtonWidget.Values<T> getValues();

        default public ValueSetter<T> valueSetter() {
            return SimpleOption::setValue;
        }

        @Override
        default public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback) {
            return option -> CyclingButtonWidget.builder(option.textGetter).values(this.getValues()).tooltip(tooltipFactory).initially(option.value).build(x, y, width, 20, option.text, (button, value) -> {
                this.valueSetter().set((SimpleOption<Object>)option, value);
                gameOptions.write();
                changeCallback.accept(value);
            });
        }

        @Environment(value=EnvType.CLIENT)
        public static interface ValueSetter<T> {
            public void set(SimpleOption<T> var1, T var2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface SliderCallbacks<T>
    extends Callbacks<T> {
        public double toSliderProgress(T var1);

        public T toValue(double var1);

        @Override
        default public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback) {
            return option -> new OptionSliderWidgetImpl(gameOptions, x, y, width, 20, option, this, tooltipFactory, changeCallback);
        }
    }
}

