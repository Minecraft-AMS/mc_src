/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class FlowerFeature<U extends FeatureConfig>
extends Feature<U> {
    public FlowerFeature(Codec<U> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, U config) {
        BlockState blockState = this.getFlowerState(random, pos, config);
        int i = 0;
        for (int j = 0; j < this.getFlowerAmount(config); ++j) {
            BlockPos blockPos = this.getPos(random, pos, config);
            if (!world.isAir(blockPos) || blockPos.getY() >= 255 || !blockState.canPlaceAt(world, blockPos) || !this.isPosValid(world, blockPos, config)) continue;
            world.setBlockState(blockPos, blockState, 2);
            ++i;
        }
        return i > 0;
    }

    public abstract boolean isPosValid(WorldAccess var1, BlockPos var2, U var3);

    public abstract int getFlowerAmount(U var1);

    public abstract BlockPos getPos(Random var1, BlockPos var2, U var3);

    public abstract BlockState getFlowerState(Random var1, BlockPos var2, U var3);
}

