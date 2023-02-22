/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class IceSpikeFeature
extends Feature<DefaultFeatureConfig> {
    public IceSpikeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        int l;
        int k;
        while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.down();
        }
        if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.SNOW_BLOCK)) {
            return false;
        }
        blockPos = blockPos.up(random.nextInt(4));
        int i = random.nextInt(4) + 7;
        int j = i / 4 + random.nextInt(2);
        if (j > 1 && random.nextInt(60) == 0) {
            blockPos = blockPos.up(10 + random.nextInt(30));
        }
        for (k = 0; k < i; ++k) {
            float f = (1.0f - (float)k / (float)i) * (float)j;
            l = MathHelper.ceil(f);
            for (int m = -l; m <= l; ++m) {
                float g = (float)MathHelper.abs(m) - 0.25f;
                for (int n = -l; n <= l; ++n) {
                    float h = (float)MathHelper.abs(n) - 0.25f;
                    if ((m != 0 || n != 0) && g * g + h * h > f * f || (m == -l || m == l || n == -l || n == l) && random.nextFloat() > 0.75f) continue;
                    BlockState blockState = structureWorldAccess.getBlockState(blockPos.add(m, k, n));
                    Block block = blockState.getBlock();
                    if (blockState.isAir() || IceSpikeFeature.isSoil(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                        this.setBlockState(structureWorldAccess, blockPos.add(m, k, n), Blocks.PACKED_ICE.getDefaultState());
                    }
                    if (k == 0 || l <= 1) continue;
                    blockState = structureWorldAccess.getBlockState(blockPos.add(m, -k, n));
                    block = blockState.getBlock();
                    if (!blockState.isAir() && !IceSpikeFeature.isSoil(block) && block != Blocks.SNOW_BLOCK && block != Blocks.ICE) continue;
                    this.setBlockState(structureWorldAccess, blockPos.add(m, -k, n), Blocks.PACKED_ICE.getDefaultState());
                }
            }
        }
        k = j - 1;
        if (k < 0) {
            k = 0;
        } else if (k > 1) {
            k = 1;
        }
        for (int o = -k; o <= k; ++o) {
            block5: for (l = -k; l <= k; ++l) {
                BlockPos blockPos2 = blockPos.add(o, -1, l);
                int p = 50;
                if (Math.abs(o) == 1 && Math.abs(l) == 1) {
                    p = random.nextInt(5);
                }
                while (blockPos2.getY() > 50) {
                    BlockState blockState2 = structureWorldAccess.getBlockState(blockPos2);
                    Block block2 = blockState2.getBlock();
                    if (!blockState2.isAir() && !IceSpikeFeature.isSoil(block2) && block2 != Blocks.SNOW_BLOCK && block2 != Blocks.ICE && block2 != Blocks.PACKED_ICE) continue block5;
                    this.setBlockState(structureWorldAccess, blockPos2, Blocks.PACKED_ICE.getDefaultState());
                    blockPos2 = blockPos2.down();
                    if (--p > 0) continue;
                    blockPos2 = blockPos2.down(random.nextInt(5) + 1);
                    p = random.nextInt(5);
                }
            }
        }
        return true;
    }
}

