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

public class TrapezoidHeightProvider
extends HeightProvider {
    public static final Codec<TrapezoidHeightProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(trapezoidHeightProvider -> trapezoidHeightProvider.minOffset), (App)YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(trapezoidHeightProvider -> trapezoidHeightProvider.maxOffset), (App)Codec.INT.optionalFieldOf("plateau", (Object)0).forGetter(trapezoidHeightProvider -> trapezoidHeightProvider.plateau)).apply((Applicative)instance, TrapezoidHeightProvider::new));
    private static final Logger LOGGER = LogManager.getLogger();
    private final YOffset minOffset;
    private final YOffset maxOffset;
    private final int plateau;

    private TrapezoidHeightProvider(YOffset minOffset, YOffset maxOffset, int plateau) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.plateau = plateau;
    }

    public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset, int plateau) {
        return new TrapezoidHeightProvider(minOffset, maxOffset, plateau);
    }

    public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset) {
        return TrapezoidHeightProvider.create(minOffset, maxOffset, 0);
    }

    @Override
    public int get(Random random, HeightContext context) {
        int j;
        int i = this.minOffset.getY(context);
        if (i > (j = this.maxOffset.getY(context))) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return i;
        }
        int k = j - i;
        if (this.plateau >= k) {
            return MathHelper.nextBetween(random, i, j);
        }
        int l = (k - this.plateau) / 2;
        int m = k - l;
        return i + MathHelper.nextBetween(random, 0, m) + MathHelper.nextBetween(random, 0, l);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.TRAPEZOID;
    }

    public String toString() {
        if (this.plateau == 0) {
            return "triangle (" + this.minOffset + "-" + this.maxOffset + ")";
        }
        return "trapezoid(" + this.plateau + ") in [" + this.minOffset + "-" + this.maxOffset + "]";
    }
}

