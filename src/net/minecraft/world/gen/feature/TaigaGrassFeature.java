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
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class TaigaGrassFeature
extends Feature<DefaultFeatureConfig> {
    public TaigaGrassFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    public BlockState getGrass(Random random) {
        return random.nextInt(5) > 0 ? Blocks.FERN.getDefaultState() : Blocks.GRASS.getDefaultState();
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockState blockState = this.getGrass(random);
        BlockState blockState2 = iWorld.getBlockState(blockPos);
        while ((blockState2.isAir() || blockState2.matches(BlockTags.LEAVES)) && blockPos.getY() > 0) {
            blockPos = blockPos.down();
            blockState2 = iWorld.getBlockState(blockPos);
        }
        int i = 0;
        for (int j = 0; j < 128; ++j) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (!iWorld.isAir(blockPos2) || !blockState.canPlaceAt(iWorld, blockPos2)) continue;
            iWorld.setBlockState(blockPos2, blockState, 2);
            ++i;
        }
        return i > 0;
    }
}

