/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DoubleOption
extends Option {
    protected final float step;
    protected final double min;
    protected double max;
    private final Function<GameOptions, Double> getter;
    private final BiConsumer<GameOptions, Double> setter;
    private final BiFunction<GameOptions, DoubleOption, Text> displayStringGetter;

    public DoubleOption(String key, double min, double max, float step, Function<GameOptions, Double> getter, BiConsumer<GameOptions, Double> setter, BiFunction<GameOptions, DoubleOption, Text> displayStringGetter) {
        super(key);
        this.min = min;
        this.max = max;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
        this.displayStringGetter = displayStringGetter;
    }

    @Override
    public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
        return new DoubleOptionSliderWidget(options, x, y, width, 20, this);
    }

    public double getRatio(double value) {
        return MathHelper.clamp((this.adjust(value) - this.min) / (this.max - this.min), 0.0, 1.0);
    }

    public double getValue(double ratio) {
        return this.adjust(MathHelper.lerp(MathHelper.clamp(ratio, 0.0, 1.0), this.min, this.max));
    }

    private double adjust(double value) {
        if (this.step > 0.0f) {
            value = this.step * (float)Math.round(value / (double)this.step);
        }
        return MathHelper.clamp(value, this.min, this.max);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void set(GameOptions options, double value) {
        this.setter.accept(options, value);
    }

    public double get(GameOptions options) {
        return this.getter.apply(options);
    }

    public Text getDisplayString(GameOptions options) {
        return this.displayStringGetter.apply(options, this);
    }
}
