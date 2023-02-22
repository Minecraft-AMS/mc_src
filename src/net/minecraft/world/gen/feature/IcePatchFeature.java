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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IcePatchFeatureConfig;

public class IcePatchFeature
extends Feature<IcePatchFeatureConfig> {
    private final Block ICE = Blocks.PACKED_ICE;

    public IcePatchFeature(Function<Dynamic<?>, ? extends IcePatchFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, IcePatchFeatureConfig icePatchFeatureConfig) {
        while (iWorld.isAir(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.down();
        }
        if (iWorld.getBlockState(blockPos).getBlock() != Blocks.SNOW_BLOCK) {
            return false;
        }
        int i = random.nextInt(icePatchFeatureConfig.radius) + 2;
        boolean j = true;
        for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; ++k) {
            for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; ++l) {
                int n;
                int m = k - blockPos.getX();
                if (m * m + (n = l - blockPos.getZ()) * n > i * i) continue;
                for (int o = blockPos.getY() - 1; o <= blockPos.getY() + 1; ++o) {
                    BlockPos blockPos2 = new BlockPos(k, o, l);
                    Block block = iWorld.getBlockState(blockPos2).getBlock();
                    if (!IcePatchFeature.isDirt(block) && block != Blocks.SNOW_BLOCK && block != Blocks.ICE) continue;
                    iWorld.setBlockState(blockPos2, this.ICE.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

