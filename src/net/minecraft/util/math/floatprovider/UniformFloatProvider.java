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

public class UniformFloatProvider
extends FloatProvider {
    public static final Codec<UniformFloatProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min_inclusive").forGetter(uniformFloatProvider -> Float.valueOf(uniformFloatProvider.min)), (App)Codec.FLOAT.fieldOf("max_exclusive").forGetter(uniformFloatProvider -> Float.valueOf(uniformFloatProvider.max))).apply((Applicative)instance, UniformFloatProvider::new)).comapFlatMap(uniformFloatProvider -> {
        if (uniformFloatProvider.max <= uniformFloatProvider.min) {
            return DataResult.error((String)("Max must be larger than min, min_inclusive: " + uniformFloatProvider.min + ", max_exclusive: " + uniformFloatProvider.max));
        }
        return DataResult.success((Object)uniformFloatProvider);
    }, Function.identity());
    private final float min;
    private final float max;

    private UniformFloatProvider(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public static UniformFloatProvider create(float min, float max) {
        if (max <= min) {
            throw new IllegalArgumentException("Max must exceed min");
        }
        return new UniformFloatProvider(min, max);
    }

    @Override
    public float get(Random random) {
        return MathHelper.nextBetween(random, this.min, this.max);
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
        return FloatProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.min + "-" + this.max + "]";
    }
}

