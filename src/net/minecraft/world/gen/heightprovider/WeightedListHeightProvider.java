/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;

public class WeightedListHeightProvider
extends HeightProvider {
    public static final Codec<WeightedListHeightProvider> WEIGHTED_LIST_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DataPool.createCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter(weightedListHeightProvider -> weightedListHeightProvider.weightedList)).apply((Applicative)instance, WeightedListHeightProvider::new));
    private final DataPool<HeightProvider> weightedList;

    public WeightedListHeightProvider(DataPool<HeightProvider> weightedList) {
        this.weightedList = weightedList;
    }

    @Override
    public int get(Random random, HeightContext context) {
        return this.weightedList.getDataOrEmpty(random).orElseThrow(IllegalStateException::new).get(random, context);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.WEIGHTED_LIST;
    }
}

