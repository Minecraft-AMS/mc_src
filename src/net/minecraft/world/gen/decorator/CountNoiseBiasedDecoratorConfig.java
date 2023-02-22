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

public class CountNoiseBiasedDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountNoiseBiasedDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("noise_to_count_ratio").forGetter(countNoiseBiasedDecoratorConfig -> countNoiseBiasedDecoratorConfig.noiseToCountRatio), (App)Codec.DOUBLE.fieldOf("noise_factor").forGetter(countNoiseBiasedDecoratorConfig -> countNoiseBiasedDecoratorConfig.noiseFactor), (App)Codec.DOUBLE.fieldOf("noise_offset").orElse((Object)0.0).forGetter(countNoiseBiasedDecoratorConfig -> countNoiseBiasedDecoratorConfig.noiseOffset)).apply((Applicative)instance, CountNoiseBiasedDecoratorConfig::new));
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;

    public CountNoiseBiasedDecoratorConfig(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
        this.noiseToCountRatio = noiseToCountRatio;
        this.noiseFactor = noiseFactor;
        this.noiseOffset = noiseOffset;
    }
}

