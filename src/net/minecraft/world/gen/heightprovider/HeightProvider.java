/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;

public abstract class HeightProvider {
    private static final Codec<Either<YOffset, HeightProvider>> field_31539 = Codec.either(YOffset.OFFSET_CODEC, (Codec)Registries.HEIGHT_PROVIDER_TYPE.getCodec().dispatch(HeightProvider::getType, HeightProviderType::codec));
    public static final Codec<HeightProvider> CODEC = field_31539.xmap(either -> (HeightProvider)either.map(ConstantHeightProvider::create, provider -> provider), provider -> provider.getType() == HeightProviderType.CONSTANT ? Either.left((Object)((ConstantHeightProvider)provider).getOffset()) : Either.right((Object)provider));

    public abstract int get(Random var1, HeightContext var2);

    public abstract HeightProviderType<?> getType();
}

