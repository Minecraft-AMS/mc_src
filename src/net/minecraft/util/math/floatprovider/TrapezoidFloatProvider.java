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
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;

public class TrapezoidFloatProvider
extends FloatProvider {
    public static final Codec<TrapezoidFloatProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min").forGetter(provider -> Float.valueOf(provider.min)), (App)Codec.FLOAT.fieldOf("max").forGetter(provider -> Float.valueOf(provider.max)), (App)Codec.FLOAT.fieldOf("plateau").forGetter(provider -> Float.valueOf(provider.plateau))).apply((Applicative)instance, TrapezoidFloatProvider::new)).comapFlatMap(provider -> {
        if (provider.max < provider.min) {
            return DataResult.error((String)("Max must be larger than min: [" + provider.min + ", " + provider.max + "]"));
        }
        if (provider.plateau > provider.max - provider.min) {
            return DataResult.error((String)("Plateau can at most be the full span: [" + provider.min + ", " + provider.max + "]"));
        }
        return DataResult.success((Object)provider);
    }, Function.identity());
    private final float min;
    private final float max;
    private final float plateau;

    public static TrapezoidFloatProvider create(float min, float max, float plateau) {
        return new TrapezoidFloatProvider(min, max, plateau);
    }

    private TrapezoidFloatProvider(float min, float max, float plateau) {
        this.min = min;
        this.max = max;
        this.plateau = plateau;
    }

    @Override
    public float get(Random random) {
        float f = this.max - this.min;
        float g = (f - this.plateau) / 2.0f;
        float h = f - g;
        return this.min + random.nextFloat() * h + random.nextFloat() * g;
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
        return FloatProviderType.TRAPEZOID;
    }

    public String toString() {
        return "trapezoid(" + this.plateau + ") in [" + this.min + "-" + this.max + "]";
    }
}

