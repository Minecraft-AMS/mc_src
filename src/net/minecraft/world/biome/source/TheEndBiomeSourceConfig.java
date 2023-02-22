/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.source.BiomeSourceConfig;

public class TheEndBiomeSourceConfig
implements BiomeSourceConfig {
    private long seed;

    public TheEndBiomeSourceConfig setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public long getSeed() {
        return this.seed;
    }
}

