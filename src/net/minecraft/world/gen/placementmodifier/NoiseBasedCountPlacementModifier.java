/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class NoiseBasedCountPlacementModifier
extends AbstractCountPlacementModifier {
    public static final Codec<NoiseBasedCountPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("noise_to_count_ratio").forGetter(noiseBasedCountPlacementModifier -> noiseBasedCountPlacementModifier.noiseToCountRatio), (App)Codec.DOUBLE.fieldOf("noise_factor").forGetter(noiseBasedCountPlacementModifier -> noiseBasedCountPlacementModifier.noiseFactor), (App)Codec.DOUBLE.fieldOf("noise_offset").orElse((Object)0.0).forGetter(noiseBasedCountPlacementModifier -> noiseBasedCountPlacementModifier.noiseOffset)).apply((Applicative)instance, NoiseBasedCountPlacementModifier::new));
    private final int noiseToCountRatio;
    private final double noiseFactor;
    private final double noiseOffset;

    private NoiseBasedCountPlacementModifier(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
        this.noiseToCountRatio = noiseToCountRatio;
        this.noiseFactor = noiseFactor;
        this.noiseOffset = noiseOffset;
    }

    public static NoiseBasedCountPlacementModifier of(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
        return new NoiseBasedCountPlacementModifier(noiseToCountRatio, noiseFactor, noiseOffset);
    }

    @Override
    protected int getCount(Random random, BlockPos pos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / this.noiseFactor, (double)pos.getZ() / this.noiseFactor, false);
        return (int)Math.ceil((d + this.noiseOffset) * (double)this.noiseToCountRatio);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.NOISE_BASED_COUNT;
    }
}

