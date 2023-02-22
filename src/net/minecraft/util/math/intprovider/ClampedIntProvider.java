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
package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;

public class ClampedIntProvider
extends IntProvider {
    public static final Codec<ClampedIntProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)IntProvider.VALUE_CODEC.fieldOf("source").forGetter(clampedIntProvider -> clampedIntProvider.source), (App)Codec.INT.fieldOf("min_inclusive").forGetter(clampedIntProvider -> clampedIntProvider.min), (App)Codec.INT.fieldOf("max_inclusive").forGetter(clampedIntProvider -> clampedIntProvider.max)).apply((Applicative)instance, ClampedIntProvider::new)).comapFlatMap(clampedIntProvider -> {
        if (clampedIntProvider.max < clampedIntProvider.min) {
            return DataResult.error((String)("Max must be at least min, min_inclusive: " + clampedIntProvider.min + ", max_inclusive: " + clampedIntProvider.max));
        }
        return DataResult.success((Object)clampedIntProvider);
    }, Function.identity());
    private final IntProvider source;
    private int min;
    private int max;

    public static ClampedIntProvider create(IntProvider source, int min, int max) {
        return new ClampedIntProvider(source, min, max);
    }

    public ClampedIntProvider(IntProvider source, int min, int max) {
        this.source = source;
        this.min = min;
        this.max = max;
    }

    @Override
    public int get(Random random) {
        return MathHelper.clamp(this.source.get(random), this.min, this.max);
    }

    @Override
    public int getMin() {
        return Math.max(this.min, this.source.getMin());
    }

    @Override
    public int getMax() {
        return Math.min(this.max, this.source.getMax());
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED;
    }
}

