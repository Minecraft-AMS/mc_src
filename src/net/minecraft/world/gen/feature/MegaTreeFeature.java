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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class MegaTreeFeature<T extends FeatureConfig>
extends AbstractTreeFeature<T> {
    protected final int baseHeight;
    protected final BlockState log;
    protected final BlockState leaves;
    protected final int maxExtraHeight;

    public MegaTreeFeature(Function<Dynamic<?>, ? extends T> function, boolean emitNeighborBlockUpdates, int baseHeight, int maxExtraHeight, BlockState log, BlockState leaves) {
        super(function, emitNeighborBlockUpdates);
        this.baseHeight = baseHeight;
        this.maxExtraHeight = maxExtraHeight;
        this.log = log;
        this.leaves = leaves;
    }

    protected int getHeight(Random random) {
        int i = random.nextInt(3) + this.baseHeight;
        if (this.maxExtraHeight > 1) {
            i += random.nextInt(this.maxExtraHeight);
        }
        return i;
    }

    private boolean doesTreeFit(TestableWorld world, BlockPos pos, int height) {
        boolean bl = true;
        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) {
            return false;
        }
        for (int i = 0; i <= 1 + height; ++i) {
            int j = 2;
            if (i == 0) {
                j = 1;
            } else if (i >= 1 + height - 2) {
                j = 2;
            }
            for (int k = -j; k <= j && bl; ++k) {
                for (int l = -j; l <= j && bl; ++l) {
                    if (pos.getY() + i >= 0 && pos.getY() + i < 256 && MegaTreeFeature.canTreeReplace(world, pos.add(k, i, l))) continue;
                    bl = false;
                }
            }
        }
        return bl;
    }

    private boolean replaceGround(ModifiableTestableWorld world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        if (!MegaTreeFeature.isNaturalDirtOrGrass(world, blockPos) || pos.getY() < 2) {
            return false;
        }
        this.setToDirt(world, blockPos);
        this.setToDirt(world, blockPos.east());
        this.setToDirt(world, blockPos.south());
        this.setToDirt(world, blockPos.south().east());
        return true;
    }

    protected boolean checkTreeFitsAndReplaceGround(ModifiableTestableWorld world, BlockPos pos, int height) {
        return this.doesTreeFit(world, pos, height) && this.replaceGround(world, pos);
    }

    protected void makeSquaredLeafLayer(ModifiableTestableWorld modifiableTestableWorld, BlockPos pos, int radius, BlockBox blockBox, Set<BlockPos> set) {
        int i = radius * radius;
        for (int j = -radius; j <= radius + 1; ++j) {
            for (int k = -radius; k <= radius + 1; ++k) {
                BlockPos blockPos;
                int m;
                int l = Math.min(Math.abs(j), Math.abs(j - 1));
                if (l + (m = Math.min(Math.abs(k), Math.abs(k - 1))) >= 7 || l * l + m * m > i || !MegaTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos = pos.add(j, 0, k))) continue;
                this.setBlockState(set, modifiableTestableWorld, blockPos, this.leaves, blockBox);
            }
        }
    }

    protected void makeRoundLeafLayer(ModifiableTestableWorld modifiableTestableWorld, BlockPos pos, int radius, BlockBox blockBox, Set<BlockPos> set) {
        int i = radius * radius;
        for (int j = -radius; j <= radius; ++j) {
            for (int k = -radius; k <= radius; ++k) {
                BlockPos blockPos;
                if (j * j + k * k > i || !MegaTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos = pos.add(j, 0, k))) continue;
                this.setBlockState(set, modifiableTestableWorld, blockPos, this.leaves, blockBox);
            }
        }
    }
}
