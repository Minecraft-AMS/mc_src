/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountNoiseDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountNoiseDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("noise_level").forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.noiseLevel), (App)Codec.INT.fieldOf("below_noise").forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.belowNoise), (App)Codec.INT.fieldOf("above_noise").forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.aboveNoise)).apply((Applicative)instance, CountNoiseDecoratorConfig::new));
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public CountNoiseDecoratorConfig(double noiseLevel, int belowNoise, int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }
}

