/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

@Environment(value=EnvType.CLIENT)
public class LogarithmicOption
extends DoubleOption {
    public LogarithmicOption(String string, double d, double e, float f, Function<GameOptions, Double> function, BiConsumer<GameOptions, Double> biConsumer, BiFunction<GameOptions, DoubleOption, String> biFunction) {
        super(string, d, e, f, function, biConsumer, biFunction);
    }

    @Override
    public double method_18611(double d) {
        return Math.log(d / this.min) / Math.log(this.max / this.min);
    }

    @Override
    public double method_18616(double d) {
        return this.min * Math.pow(Math.E, Math.log(this.max / this.min) * d);
    }
}

