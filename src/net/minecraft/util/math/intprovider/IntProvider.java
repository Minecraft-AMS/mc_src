/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

public abstract class IntProvider {
    private static final Codec<Either<Integer, IntProvider>> INT_CODEC = Codec.either((Codec)Codec.INT, (Codec)Registry.INT_PROVIDER_TYPE.getCodec().dispatch(IntProvider::getType, IntProviderType::codec));
    public static final Codec<IntProvider> VALUE_CODEC = INT_CODEC.xmap(either -> (IntProvider)either.map(ConstantIntProvider::create, provider -> provider), provider -> provider.getType() == IntProviderType.CONSTANT ? Either.left((Object)((ConstantIntProvider)provider).getValue()) : Either.right((Object)provider));
    public static final Codec<IntProvider> NON_NEGATIVE_CODEC = IntProvider.createValidatingCodec(0, Integer.MAX_VALUE);
    public static final Codec<IntProvider> POSITIVE_CODEC = IntProvider.createValidatingCodec(1, Integer.MAX_VALUE);

    public static Codec<IntProvider> createValidatingCodec(int min, int max) {
        Function<IntProvider, DataResult> function = provider -> {
            if (provider.getMin() < min) {
                return DataResult.error((String)("Value provider too low: " + min + " [" + provider.getMin() + "-" + provider.getMax() + "]"));
            }
            if (provider.getMax() > max) {
                return DataResult.error((String)("Value provider too high: " + max + " [" + provider.getMin() + "-" + provider.getMax() + "]"));
            }
            return DataResult.success((Object)provider);
        };
        return VALUE_CODEC.flatXmap(function, function);
    }

    public abstract int get(Random var1);

    public abstract int getMin();

    public abstract int getMax();

    public abstract IntProviderType<?> getType();
}

