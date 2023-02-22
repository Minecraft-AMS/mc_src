/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.layer.util.LayerSampler;

public interface LayerFactory<A extends LayerSampler> {
    public A make();
}

