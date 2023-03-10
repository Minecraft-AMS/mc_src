/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;

public class ClampedNormalFloatProvider
extends FloatProvider {
    public static final Codec<ClampedNormalFloatProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("mean").forGetter(provider -> Float.valueOf(provider.mean)), (App)Codec.FLOAT.fieldOf("deviation").forGetter(provider -> Float.valueOf(provider.deviation)), (App)Codec.FLOAT.fieldOf("min").forGetter(provider -> Float.valueOf(provider.min)), (App)Codec.FLOAT.fieldOf("max").forGetter(provider -> Float.valueOf(provider.max))).apply((Applicative)instance, ClampedNormalFloatProvider::new)).comapFlatMap(provider -> {
        if (provider.max < provider.min) {
            return DataResult.error((String)("Max must be larger than min: [" + provider.min + ", " + provider.max + "]"));
        }
        return DataResult.success((Object)provider);
    }, Function.identity());
    private float mean;
    private float deviation;
    private float min;
    private float max;

    public static ClampedNormalFloatProvider create(float mean, float deviation, float min, float max) {
        return new ClampedNormalFloatProvider(mean, deviation, min, max);
    }

    private ClampedNormalFloatProvider(float mean, float deviation, float min, float max) {
        this.mean = mean;
        this.deviation = deviation;
        this.min = min;
        this.max = max;
    }

    @Override
    public float get(Random random) {
        return ClampedNormalFloatProvider.get(random, this.mean, this.deviation, this.min, this.max);
    }

    public static float get(Random random, float mean, float deviation, float min, float max) {
        return MathHelper.clamp(MathHelper.nextGaussian(random, mean, deviation), min, max);
    }

    @Override
    public float getMin() {
        return this.min;
    }

    @Override
    public float getMax() {
        return this.max;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
    }
}

