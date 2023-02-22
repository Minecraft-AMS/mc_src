/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NoiseSamplingConfig(double xzScale, double yScale, double xzFactor, double yFactor) {
    private static final Codec<Double> CODEC_RANGE = Codec.doubleRange((double)0.001, (double)1000.0);
    public static final Codec<NoiseSamplingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)CODEC_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingConfig::xzScale), (App)CODEC_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingConfig::yScale), (App)CODEC_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingConfig::xzFactor), (App)CODEC_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingConfig::yFactor)).apply((Applicative)instance, NoiseSamplingConfig::new));
}

