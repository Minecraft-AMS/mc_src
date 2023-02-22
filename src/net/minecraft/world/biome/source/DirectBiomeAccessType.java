/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeCoords;

public final class DirectBiomeAccessType
extends Enum<DirectBiomeAccessType>
implements BiomeAccessType {
    public static final /* enum */ DirectBiomeAccessType INSTANCE = new DirectBiomeAccessType();
    private static final /* synthetic */ DirectBiomeAccessType[] field_24410;

    public static DirectBiomeAccessType[] values() {
        return (DirectBiomeAccessType[])field_24410.clone();
    }

    public static DirectBiomeAccessType valueOf(String string) {
        return Enum.valueOf(DirectBiomeAccessType.class, string);
    }

    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
        return storage.getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(z));
    }

    private static /* synthetic */ DirectBiomeAccessType[] method_36704() {
        return new DirectBiomeAccessType[]{INSTANCE};
    }

    static {
        field_24410 = DirectBiomeAccessType.method_36704();
    }
}

