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
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public class HugeBrownMushroomFeature
extends HugeMushroomFeature {
    public HugeBrownMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected void generate(IWorld world, Random random, BlockPos blockPos, int i, BlockPos.Mutable pos, HugeMushroomFeatureConfig config) {
        int j = config.capSize;
        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean bl6;
                boolean bl = k == -j;
                boolean bl2 = k == j;
                boolean bl3 = l == -j;
                boolean bl4 = l == j;
                boolean bl5 = bl || bl2;
                boolean bl7 = bl6 = bl3 || bl4;
                if (bl5 && bl6) continue;
                pos.set(blockPos).setOffset(k, i, l);
                if (world.getBlockState(pos).isFullOpaque(world, pos)) continue;
                boolean bl72 = bl || bl6 && k == 1 - j;
                boolean bl8 = bl2 || bl6 && k == j - 1;
                boolean bl9 = bl3 || bl5 && l == 1 - j;
                boolean bl10 = bl4 || bl5 && l == j - 1;
                this.setBlockState(world, pos, (BlockState)((BlockState)((BlockState)((BlockState)config.capProvider.getBlockState(random, blockPos).with(MushroomBlock.WEST, bl72)).with(MushroomBlock.EAST, bl8)).with(MushroomBlock.NORTH, bl9)).with(MushroomBlock.SOUTH, bl10));
            }
        }
    }

    @Override
    protected int method_23372(int i, int j, int k, int l) {
        return l <= 3 ? 0 : k;
    }
}

