/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class FreezeTopLayerFeature
extends Feature<DefaultFeatureConfig> {
    public FreezeTopLayerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = blockPos.getX() + i;
                int l = blockPos.getZ() + j;
                int m = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);
                mutable.set(k, m, l);
                mutable2.set(mutable).setOffset(Direction.DOWN, 1);
                Biome biome = iWorld.getBiome(mutable);
                if (biome.canSetIce(iWorld, mutable2, false)) {
                    iWorld.setBlockState(mutable2, Blocks.ICE.getDefaultState(), 2);
                }
                if (!biome.canSetSnow(iWorld, mutable)) continue;
                iWorld.setBlockState(mutable, Blocks.SNOW.getDefaultState(), 2);
                BlockState blockState = iWorld.getBlockState(mutable2);
                if (!blockState.contains(SnowyBlock.SNOWY)) continue;
                iWorld.setBlockState(mutable2, (BlockState)blockState.with(SnowyBlock.SNOWY, true), 2);
            }
        }
        return true;
    }
}

