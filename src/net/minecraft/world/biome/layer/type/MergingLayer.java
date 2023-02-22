/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface MergingLayer
extends CoordinateTransformer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> layer1, LayerFactory<R> layer2) {
        return () -> {
            Object layerSampler = layer1.make();
            Object layerSampler2 = layer2.make();
            return context.createSampler((i, j) -> {
                context.initSeed(i, j);
                return this.sample(context, (LayerSampler)layerSampler, (LayerSampler)layerSampler2, i, j);
            }, layerSampler, layerSampler2);
        };
    }

    public int sample(LayerRandomnessSource var1, LayerSampler var2, LayerSampler var3, int var4, int var5);
}

