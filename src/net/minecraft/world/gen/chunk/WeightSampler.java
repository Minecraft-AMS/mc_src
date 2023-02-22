/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.chunk;

@FunctionalInterface
public interface WeightSampler {
    public static final WeightSampler DEFAULT = (d, i, j, k) -> d;

    public double sample(double var1, int var3, int var4, int var5);
}

