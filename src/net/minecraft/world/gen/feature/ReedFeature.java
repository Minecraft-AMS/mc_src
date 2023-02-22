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
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ReedFeature
extends Feature<DefaultFeatureConfig> {
    public ReedFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        int i = 0;
        for (int j = 0; j < 20; ++j) {
            BlockPos blockPos3;
            BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            if (!iWorld.isAir(blockPos2) || !iWorld.getFluidState((blockPos3 = blockPos2.down()).west()).matches(FluidTags.WATER) && !iWorld.getFluidState(blockPos3.east()).matches(FluidTags.WATER) && !iWorld.getFluidState(blockPos3.north()).matches(FluidTags.WATER) && !iWorld.getFluidState(blockPos3.south()).matches(FluidTags.WATER)) continue;
            int k = 2 + random.nextInt(random.nextInt(3) + 1);
            for (int l = 0; l < k; ++l) {
                if (!Blocks.SUGAR_CANE.getDefaultState().canPlaceAt(iWorld, blockPos2)) continue;
                iWorld.setBlockState(blockPos2.up(l), Blocks.SUGAR_CANE.getDefaultState(), 2);
                ++i;
            }
        }
        return i > 0;
    }
}
