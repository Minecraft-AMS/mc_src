/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaTreeFeature;

public class MegaJungleTreeFeature
extends MegaTreeFeature<DefaultFeatureConfig> {
    public MegaJungleTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
        super(function, bl, i, j, blockState, blockState2);
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        int i = this.getHeight(random);
        if (!this.checkTreeFitsAndReplaceGround(world, pos, i)) {
            return false;
        }
        this.makeLeaves(world, pos.up(i), 2, blockBox, logPositions);
        for (int j = pos.getY() + i - 2 - random.nextInt(4); j > pos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
            int m;
            float f = random.nextFloat() * ((float)Math.PI * 2);
            int k = pos.getX() + (int)(0.5f + MathHelper.cos(f) * 4.0f);
            int l = pos.getZ() + (int)(0.5f + MathHelper.sin(f) * 4.0f);
            for (m = 0; m < 5; ++m) {
                k = pos.getX() + (int)(1.5f + MathHelper.cos(f) * (float)m);
                l = pos.getZ() + (int)(1.5f + MathHelper.sin(f) * (float)m);
                this.setBlockState(logPositions, world, new BlockPos(k, j - 3 + m / 2, l), this.log, blockBox);
            }
            m = 1 + random.nextInt(2);
            int n = j;
            for (int o = n - m; o <= n; ++o) {
                int p = o - n;
                this.makeRoundLeafLayer(world, new BlockPos(k, o, l), 1 - p, blockBox, logPositions);
            }
        }
        for (int q = 0; q < i; ++q) {
            BlockPos blockPos4;
            BlockPos blockPos3;
            BlockPos blockPos = pos.up(q);
            if (MegaJungleTreeFeature.canTreeReplace(world, blockPos)) {
                this.setBlockState(logPositions, world, blockPos, this.log, blockBox);
                if (q > 0) {
                    this.tryMakingVine(world, random, blockPos.west(), VineBlock.EAST);
                    this.tryMakingVine(world, random, blockPos.north(), VineBlock.SOUTH);
                }
            }
            if (q >= i - 1) continue;
            BlockPos blockPos2 = blockPos.east();
            if (MegaJungleTreeFeature.canTreeReplace(world, blockPos2)) {
                this.setBlockState(logPositions, world, blockPos2, this.log, blockBox);
                if (q > 0) {
                    this.tryMakingVine(world, random, blockPos2.east(), VineBlock.WEST);
                    this.tryMakingVine(world, random, blockPos2.north(), VineBlock.SOUTH);
                }
            }
            if (MegaJungleTreeFeature.canTreeReplace(world, blockPos3 = blockPos.south().east())) {
                this.setBlockState(logPositions, world, blockPos3, this.log, blockBox);
                if (q > 0) {
                    this.tryMakingVine(world, random, blockPos3.east(), VineBlock.WEST);
                    this.tryMakingVine(world, random, blockPos3.south(), VineBlock.NORTH);
                }
            }
            if (!MegaJungleTreeFeature.canTreeReplace(world, blockPos4 = blockPos.south())) continue;
            this.setBlockState(logPositions, world, blockPos4, this.log, blockBox);
            if (q <= 0) continue;
            this.tryMakingVine(world, random, blockPos4.west(), VineBlock.EAST);
            this.tryMakingVine(world, random, blockPos4.south(), VineBlock.NORTH);
        }
        return true;
    }

    private void tryMakingVine(ModifiableTestableWorld world, Random random, BlockPos pos, BooleanProperty directionProperty) {
        if (random.nextInt(3) > 0 && MegaJungleTreeFeature.isAir(world, pos)) {
            this.setBlockState(world, pos, (BlockState)Blocks.VINE.getDefaultState().with(directionProperty, true));
        }
    }

    private void makeLeaves(ModifiableTestableWorld world, BlockPos pos, int i, BlockBox blockBox, Set<BlockPos> set) {
        int j = 2;
        for (int k = -2; k <= 0; ++k) {
            this.makeSquaredLeafLayer(world, pos.up(k), i + 1 - k, blockBox, set);
        }
    }
}

