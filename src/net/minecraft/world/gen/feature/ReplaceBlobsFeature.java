/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ReplaceBlobsFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class ReplaceBlobsFeature
extends Feature<ReplaceBlobsFeatureConfig> {
    public ReplaceBlobsFeature(Codec<ReplaceBlobsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ReplaceBlobsFeatureConfig replaceBlobsFeatureConfig) {
        Block block = replaceBlobsFeatureConfig.target.getBlock();
        BlockPos blockPos2 = ReplaceBlobsFeature.method_27107(structureWorldAccess, blockPos.mutableCopy().clamp(Direction.Axis.Y, 1, structureWorldAccess.getHeight() - 1), block);
        if (blockPos2 == null) {
            return false;
        }
        int i = replaceBlobsFeatureConfig.getRadius().getValue(random);
        boolean bl = false;
        for (BlockPos blockPos3 : BlockPos.iterateOutwards(blockPos2, i, i, i)) {
            if (blockPos3.getManhattanDistance(blockPos2) > i) break;
            BlockState blockState = structureWorldAccess.getBlockState(blockPos3);
            if (!blockState.isOf(block)) continue;
            this.setBlockState(structureWorldAccess, blockPos3, replaceBlobsFeatureConfig.state);
            bl = true;
        }
        return bl;
    }

    @Nullable
    private static BlockPos method_27107(WorldAccess worldAccess, BlockPos.Mutable mutable, Block block) {
        while (mutable.getY() > 1) {
            BlockState blockState = worldAccess.getBlockState(mutable);
            if (blockState.isOf(block)) {
                return mutable;
            }
            mutable.move(Direction.DOWN);
        }
        return null;
    }
}

