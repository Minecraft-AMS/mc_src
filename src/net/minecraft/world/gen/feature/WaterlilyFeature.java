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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class WaterlilyFeature
extends Feature<DefaultFeatureConfig> {
    public WaterlilyFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockPos blockPos3;
        BlockPos blockPos2 = blockPos;
        while (blockPos2.getY() > 0 && iWorld.isAir(blockPos3 = blockPos2.down())) {
            blockPos2 = blockPos3;
        }
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos4 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            BlockState blockState = Blocks.LILY_PAD.getDefaultState();
            if (!iWorld.isAir(blockPos4) || !blockState.canPlaceAt(iWorld, blockPos4)) continue;
            iWorld.setBlockState(blockPos4, blockState, 2);
        }
        return true;
    }
}

