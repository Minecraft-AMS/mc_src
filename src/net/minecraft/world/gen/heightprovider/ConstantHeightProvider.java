/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;

public class ConstantHeightProvider
extends HeightProvider {
    public static final ConstantHeightProvider ZERO = new ConstantHeightProvider(YOffset.fixed(0));
    public static final Codec<ConstantHeightProvider> CONSTANT_CODEC = Codec.either(YOffset.OFFSET_CODEC, (Codec)RecordCodecBuilder.create((T instance) -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("value").forGetter(constantHeightProvider -> constantHeightProvider.offset)).apply((Applicative)instance, ConstantHeightProvider::new))).xmap(either -> (ConstantHeightProvider)either.map(ConstantHeightProvider::create, constantHeightProvider -> constantHeightProvider), constantHeightProvider -> Either.left((Object)constantHeightProvider.offset));
    private final YOffset offset;

    public static ConstantHeightProvider create(YOffset offset) {
        return new ConstantHeightProvider(offset);
    }

    private ConstantHeightProvider(YOffset offset) {
        this.offset = offset;
    }

    public YOffset getOffset() {
        return this.offset;
    }

    @Override
    public int get(Random random, HeightContext context) {
        return this.offset.getY(context);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.CONSTANT;
    }

    public String toString() {
        return this.offset.toString();
    }
}

