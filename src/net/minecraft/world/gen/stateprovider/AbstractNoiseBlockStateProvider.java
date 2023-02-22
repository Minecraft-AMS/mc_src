/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public abstract class AbstractNoiseBlockStateProvider
extends BlockStateProvider {
    protected final long seed;
    protected final DoublePerlinNoiseSampler.NoiseParameters noiseParameters;
    protected final float scale;
    protected final DoublePerlinNoiseSampler noiseSampler;

    protected static <P extends AbstractNoiseBlockStateProvider> Products.P3<RecordCodecBuilder.Mu<P>, Long, DoublePerlinNoiseSampler.NoiseParameters, Float> fillCodecFields(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)Codec.LONG.fieldOf("seed").forGetter(abstractNoiseBlockStateProvider -> abstractNoiseBlockStateProvider.seed), (App)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(abstractNoiseBlockStateProvider -> abstractNoiseBlockStateProvider.noiseParameters), (App)Codecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(abstractNoiseBlockStateProvider -> Float.valueOf(abstractNoiseBlockStateProvider.scale)));
    }

    protected AbstractNoiseBlockStateProvider(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale) {
        this.seed = seed;
        this.noiseParameters = noiseParameters;
        this.scale = scale;
        this.noiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(seed)), noiseParameters);
    }

    protected double getNoiseValue(BlockPos pos, double scale) {
        return this.noiseSampler.sample((double)pos.getX() * scale, (double)pos.getY() * scale, (double)pos.getZ() * scale);
    }
}

