/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class VoidStartPlatformFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockPos START_BLOCK = new BlockPos(8, 3, 8);
    private static final ChunkPos START_CHUNK = new ChunkPos(START_BLOCK);

    public VoidStartPlatformFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    private static int getDistance(int x1, int z1, int x2, int z2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (VoidStartPlatformFeature.getDistance(chunkPos.x, chunkPos.z, VoidStartPlatformFeature.START_CHUNK.x, VoidStartPlatformFeature.START_CHUNK.z) > 1) {
            return true;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = chunkPos.getStartZ(); i <= chunkPos.getEndZ(); ++i) {
            for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); ++j) {
                if (VoidStartPlatformFeature.getDistance(START_BLOCK.getX(), START_BLOCK.getZ(), j, i) > 16) continue;
                mutable.set(j, START_BLOCK.getY(), i);
                if (mutable.equals(START_BLOCK)) {
                    structureWorldAccess.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
                    continue;
                }
                structureWorldAccess.setBlockState(mutable, Blocks.STONE.getDefaultState(), 2);
            }
        }
        return true;
    }
}

