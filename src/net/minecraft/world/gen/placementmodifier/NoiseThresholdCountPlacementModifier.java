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

public class NoiseThresholdCountPlacementModifier
extends AbstractCountPlacementModifier {
    public static final Codec<NoiseThresholdCountPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("noise_level").forGetter(noiseThresholdCountPlacementModifier -> noiseThresholdCountPlacementModifier.noiseLevel), (App)Codec.INT.fieldOf("below_noise").forGetter(noiseThresholdCountPlacementModifier -> noiseThresholdCountPlacementModifier.belowNoise), (App)Codec.INT.fieldOf("above_noise").forGetter(noiseThresholdCountPlacementModifier -> noiseThresholdCountPlacementModifier.aboveNoise)).apply((Applicative)instance, NoiseThresholdCountPlacementModifier::new));
    private final double noiseLevel;
    private final int belowNoise;
    private final int aboveNoise;

    private NoiseThresholdCountPlacementModifier(double noiseLevel, int belowNoise, int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }

    public static NoiseThresholdCountPlacementModifier of(double noiseLevel, int belowNoise, int aboveNoise) {
        return new NoiseThresholdCountPlacementModifier(noiseLevel, belowNoise, aboveNoise);
    }

    @Override
    protected int getCount(Random random, BlockPos pos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0, false);
        return d < this.noiseLevel ? this.belowNoise : this.aboveNoise;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.NOISE_THRESHOLD_COUNT;
    }
}

