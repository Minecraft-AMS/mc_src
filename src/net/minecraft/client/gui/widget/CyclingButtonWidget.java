/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CyclingButtonWidget<T>
extends PressableWidget
implements OrderableTooltip {
    static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of((Object)Boolean.TRUE, (Object)Boolean.FALSE);
    private final Text optionText;
    private int index;
    private T value;
    private final Values<T> values;
    private final Function<T, Text> valueToText;
    private final Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory;
    private final UpdateCallback<T> callback;
    private final TooltipFactory<T> tooltipFactory;
    private final boolean optionTextOmitted;

    CyclingButtonWidget(int x, int y, int width, int height, Text message, Text optionText, int index, T value, Values<T> values, Function<T, Text> valueToText, Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory, UpdateCallback<T> callback, TooltipFactory<T> tooltipFactory, boolean optionTextOmitted) {
        super(x, y, width, height, message);
        this.optionText = optionText;
        this.index = index;
        this.value = value;
        this.values = values;
        this.valueToText = valueToText;
        this.narrationMessageFactory = narrationMessageFactory;
        this.callback = callback;
        this.tooltipFactory = tooltipFactory;
        this.optionTextOmitted = optionTextOmitted;
    }

    @Override
    public void onPress() {
        if (Screen.hasShiftDown()) {
            this.cycle(-1);
        } else {
            this.cycle(1);
        }
    }

    private void cycle(int amount) {
        List<T> list = this.values.getCurrent();
        this.index = MathHelper.floorMod(this.index + amount, list.size());
        T object = list.get(this.index);
        this.internalSetValue(object);
        this.callback.onValueChange(this, object);
    }

    private T getValue(int offset) {
        List<T> list = this.values.getCurrent();
        return list.get(MathHelper.floorMod(this.index + offset, list.size()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 0.0) {
            this.cycle(-1);
        } else if (amount < 0.0) {
            this.cycle(1);
        }
        return true;
    }

    public void setValue(T value) {
        List<T> list = this.values.getCurrent();
        int i = list.indexOf(value);
        if (i != -1) {
            this.index = i;
        }
        this.internalSetValue(value);
    }

    private void internalSetValue(T value) {
        Text text = this.composeText(value);
        this.setMessage(text);
        this.value = value;
    }

    private Text composeText(T value) {
        return this.optionTextOmitted ? this.valueToText.apply(value) : this.composeGenericOptionText(value);
    }

    private MutableText composeGenericOptionText(T value) {
        return ScreenTexts.composeGenericOptionText(this.optionText, this.valueToText.apply(value));
    }

    public T getValue() {
        return this.value;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return this.narrationMessageFactory.apply(this);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            T object = this.getValue(1);
            Text text = this.composeText(object);
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)new TranslatableText("narration.cycle_button.usage.focused", text));
            } else {
                builder.put(NarrationPart.USAGE, (Text)new TranslatableText("narration.cycle_button.usage.hovered", text));
            }
        }
    }

    public MutableText getGenericNarrationMessage() {
        return CyclingButtonWidget.getNarrationMessage(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage());
    }

    @Override
    public List<OrderedText> getOrderedTooltip() {
        return (List)this.tooltipFactory.apply(this.value);
    }

    public static <T> Builder<T> builder(Function<T, Text> valueToText) {
        return new Builder<T>(valueToText);
    }

    public static Builder<Boolean> onOffBuilder(Text on, Text off) {
        return new Builder<Boolean>(value -> value != false ? on : off).values(BOOLEAN_VALUES);
    }

    public static Builder<Boolean> onOffBuilder() {
        return new Builder<Boolean>(value -> value != false ? ScreenTexts.ON : ScreenTexts.OFF).values(BOOLEAN_VALUES);
    }

    public static Builder<Boolean> onOffBuilder(boolean initialValue) {
        return CyclingButtonWidget.onOffBuilder().initially(initialValue);
    }

    @Environment(value=EnvType.CLIENT)
    static interface Values<T> {
        public List<T> getCurrent();

        public List<T> getDefaults();

        public static <T> Values<T> of(List<T> values) {
            ImmutableList list = ImmutableList.copyOf(values);
            return new Values<T>((List)list){
                final /* synthetic */ List field_27979;
                {
                    this.field_27979 = list;
                }

                @Override
                public List<T> getCurrent() {
                    return this.field_27979;
                }

                @Override
                public List<T> getDefaults() {
                    return this.field_27979;
                }
            };
        }

        public static <T> Values<T> of(final BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            ImmutableList list = ImmutableList.copyOf(defaults);
            ImmutableList list2 = ImmutableList.copyOf(alternatives);
            return new Values<T>(){
                final /* synthetic */ List field_27981;
                final /* synthetic */ List field_27982;
                {
                    this.field_27981 = list;
                    this.field_27982 = list2;
                }

                @Override
                public List<T> getCurrent() {
                    return alternativeToggle.getAsBoolean() ? this.field_27981 : this.field_27982;
                }

                @Override
                public List<T> getDefaults() {
                    return this.field_27982;
                }
            };
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface UpdateCallback<T> {
        public void onValueChange(CyclingButtonWidget var1, T var2);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface TooltipFactory<T>
    extends Function<T, List<OrderedText>> {
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder<T> {
        private int initialIndex;
        @Nullable
        private T value;
        private final Function<T, Text> valueToText;
        private TooltipFactory<T> tooltipFactory = value -> ImmutableList.of();
        private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
        private Values<T> values = Values.of(ImmutableList.of());
        private boolean optionTextOmitted;

        public Builder(Function<T, Text> valueToText) {
            this.valueToText = valueToText;
        }

        public Builder<T> values(List<T> values) {
            this.values = Values.of(values);
            return this;
        }

        @SafeVarargs
        public final Builder<T> values(T ... values) {
            return this.values((List<T>)ImmutableList.copyOf((Object[])values));
        }

        public Builder<T> values(List<T> defaults, List<T> alternatives) {
            this.values = Values.of(HAS_ALT_DOWN, defaults, alternatives);
            return this;
        }

        public Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            this.values = Values.of(alternativeToggle, defaults, alternatives);
            return this;
        }

        public Builder<T> tooltip(TooltipFactory<T> tooltipFactory) {
            this.tooltipFactory = tooltipFactory;
            return this;
        }

        public Builder<T> initially(T value) {
            this.value = value;
            int i = this.values.getDefaults().indexOf(value);
            if (i != -1) {
                this.initialIndex = i;
            }
            return this;
        }

        public Builder<T> narration(Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
            this.narrationMessageFactory = narrationMessageFactory;
            return this;
        }

        public Builder<T> omitKeyText() {
            this.optionTextOmitted = true;
            return this;
        }

        public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText) {
            return this.build(x, y, width, height, optionText, (button, value) -> {});
        }

        public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText, UpdateCallback<T> callback) {
            List<T> list = this.values.getDefaults();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            }
            T object = this.value != null ? this.value : list.get(this.initialIndex);
            Text text = this.valueToText.apply(object);
            Text text2 = this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text);
            return new CyclingButtonWidget<T>(x, y, width, height, text2, optionText, this.initialIndex, object, this.values, this.valueToText, this.narrationMessageFactory, callback, this.tooltipFactory, this.optionTextOmitted);
        }
    }
}

