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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class BirchTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.BIRCH_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
    private final boolean alwaysTall;

    public BirchTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates, boolean alwaysTall) {
        super(configFactory, emitNeighborBlockUpdates);
        this.alwaysTall = alwaysTall;
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        int m;
        int l;
        int k;
        int j;
        int i = random.nextInt(3) + 5;
        if (this.alwaysTall) {
            i += random.nextInt(7);
        }
        boolean bl = true;
        if (pos.getY() < 1 || pos.getY() + i + 1 > 256) {
            return false;
        }
        for (j = pos.getY(); j <= pos.getY() + 1 + i; ++j) {
            k = 1;
            if (j == pos.getY()) {
                k = 0;
            }
            if (j >= pos.getY() + 1 + i - 2) {
                k = 2;
            }
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (l = pos.getX() - k; l <= pos.getX() + k && bl; ++l) {
                for (m = pos.getZ() - k; m <= pos.getZ() + k && bl; ++m) {
                    if (j >= 0 && j < 256) {
                        if (BirchTreeFeature.canTreeReplace(world, mutable.set(l, j, m))) continue;
                        bl = false;
                        continue;
                    }
                    bl = false;
                }
            }
        }
        if (!bl) {
            return false;
        }
        if (!BirchTreeFeature.isDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        for (j = pos.getY() - 3 + i; j <= pos.getY() + i; ++j) {
            k = j - (pos.getY() + i);
            int n = 1 - k / 2;
            for (l = pos.getX() - n; l <= pos.getX() + n; ++l) {
                m = l - pos.getX();
                for (int o = pos.getZ() - n; o <= pos.getZ() + n; ++o) {
                    BlockPos blockPos;
                    int p = o - pos.getZ();
                    if (Math.abs(m) == n && Math.abs(p) == n && (random.nextInt(2) == 0 || k == 0) || !BirchTreeFeature.isAirOrLeaves(world, blockPos = new BlockPos(l, j, o))) continue;
                    this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
                }
            }
        }
        for (j = 0; j < i; ++j) {
            if (!BirchTreeFeature.isAirOrLeaves(world, pos.up(j))) continue;
            this.setBlockState(logPositions, world, pos.up(j), LOG, blockBox);
        }
        return true;
    }
}

