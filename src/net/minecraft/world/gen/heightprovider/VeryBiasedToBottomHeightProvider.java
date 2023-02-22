/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VeryBiasedToBottomHeightProvider
extends HeightProvider {
    public static final Codec<VeryBiasedToBottomHeightProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(veryBiasedToBottomHeightProvider -> veryBiasedToBottomHeightProvider.minOffset), (App)YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(veryBiasedToBottomHeightProvider -> veryBiasedToBottomHeightProvider.maxOffset), (App)Codec.intRange((int)1, (int)Integer.MAX_VALUE).optionalFieldOf("inner", (Object)1).forGetter(veryBiasedToBottomHeightProvider -> veryBiasedToBottomHeightProvider.inner)).apply((Applicative)instance, VeryBiasedToBottomHeightProvider::new));
    private static final Logger LOGGER = LogManager.getLogger();
    private final YOffset minOffset;
    private final YOffset maxOffset;
    private final int inner;

    private VeryBiasedToBottomHeightProvider(YOffset minOffset, YOffset maxOffset, int inner) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.inner = inner;
    }

    public static VeryBiasedToBottomHeightProvider create(YOffset minOffset, YOffset maxOffset, int inner) {
        return new VeryBiasedToBottomHeightProvider(minOffset, maxOffset, inner);
    }

    @Override
    public int get(Random random, HeightContext context) {
        int i = this.minOffset.getY(context);
        int j = this.maxOffset.getY(context);
        if (j - i - this.inner + 1 <= 0) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return i;
        }
        int k = MathHelper.nextInt(random, i + this.inner, j);
        int l = MathHelper.nextInt(random, i, k - 1);
        return MathHelper.nextInt(random, i, l - 1 + this.inner);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.VERY_BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "biased[" + this.minOffset + "-" + this.maxOffset + " inner: " + this.inner + "]";
    }
}
