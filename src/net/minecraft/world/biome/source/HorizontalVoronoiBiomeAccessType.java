/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;

public final class HorizontalVoronoiBiomeAccessType
extends Enum<HorizontalVoronoiBiomeAccessType>
implements BiomeAccessType {
    public static final /* enum */ HorizontalVoronoiBiomeAccessType INSTANCE = new HorizontalVoronoiBiomeAccessType();
    private static final /* synthetic */ HorizontalVoronoiBiomeAccessType[] field_20647;

    public static HorizontalVoronoiBiomeAccessType[] values() {
        return (HorizontalVoronoiBiomeAccessType[])field_20647.clone();
    }

    public static HorizontalVoronoiBiomeAccessType valueOf(String string) {
        return Enum.valueOf(HorizontalVoronoiBiomeAccessType.class, string);
    }

    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
        return VoronoiBiomeAccessType.INSTANCE.getBiome(seed, x, 0, z, storage);
    }

    private static /* synthetic */ HorizontalVoronoiBiomeAccessType[] method_36703() {
        return new HorizontalVoronoiBiomeAccessType[]{INSTANCE};
    }

    static {
        field_20647 = HorizontalVoronoiBiomeAccessType.method_36703();
    }
}

