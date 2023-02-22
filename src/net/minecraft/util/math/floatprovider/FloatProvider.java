/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.registry.Registry;

public abstract class FloatProvider {
    private static final Codec<Either<Float, FloatProvider>> FLOAT_CODEC = Codec.either((Codec)Codec.FLOAT, (Codec)Registry.FLOAT_PROVIDER_TYPE.dispatch(FloatProvider::getType, FloatProviderType::codec));
    public static final Codec<FloatProvider> VALUE_CODEC = FLOAT_CODEC.xmap(either -> (FloatProvider)either.map(ConstantFloatProvider::create, floatProvider -> floatProvider), floatProvider -> floatProvider.getType() == FloatProviderType.CONSTANT ? Either.left((Object)Float.valueOf(((ConstantFloatProvider)floatProvider).getValue())) : Either.right((Object)floatProvider));

    public static Codec<FloatProvider> createValidatedCodec(float min, float max) {
        Function<FloatProvider, DataResult> function = provider -> {
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

    public abstract float get(Random var1);

    public abstract float getMin();

    public abstract float getMax();

    public abstract FloatProviderType<?> getType();
}

