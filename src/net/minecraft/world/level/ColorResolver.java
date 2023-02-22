/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level;

import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.biome.Biome;

public interface ColorResolver {
    @DontObfuscate
    public int getColor(Biome var1, double var2, double var4);
}

