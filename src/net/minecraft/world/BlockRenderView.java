/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public interface BlockRenderView
extends BlockView {
    public Biome getBiome(BlockPos var1);

    public int getLightLevel(LightType var1, BlockPos var2);

    default public boolean isSkyVisible(BlockPos pos) {
        return this.getLightLevel(LightType.SKY, pos) >= this.getMaxLightLevel();
    }

    @Environment(value=EnvType.CLIENT)
    default public int getLightmapIndex(BlockPos pos, int i) {
        int j = this.getLightLevel(LightType.SKY, pos);
        int k = this.getLightLevel(LightType.BLOCK, pos);
        if (k < i) {
            k = i;
        }
        return j << 20 | k << 4;
    }
}

