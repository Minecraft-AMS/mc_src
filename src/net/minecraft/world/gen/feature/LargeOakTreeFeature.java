/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class LargeOakTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();

    public LargeOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
        super(configFactory, emitNeighborBlockUpdates);
    }

    private void makeLeafLayer(ModifiableTestableWorld modifiableTestableWorld, BlockPos pos, float radius, BlockBox blockBox, Set<BlockPos> set) {
        int i = (int)((double)radius + 0.618);
        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                BlockPos blockPos;
                if (!(Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(radius * radius)) || !LargeOakTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos = pos.add(j, 0, k))) continue;
                this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, blockBox);
            }
        }
    }

    private float getBaseBranchSize(int treeHeight, int branchCount) {
        if ((float)branchCount < (float)treeHeight * 0.3f) {
            return -1.0f;
        }
        float f = (float)treeHeight / 2.0f;
        float g = f - (float)branchCount;
        float h = MathHelper.sqrt(f * f - g * g);
        if (g == 0.0f) {
            h = f;
        } else if (Math.abs(g) >= f) {
            return 0.0f;
        }
        return h * 0.5f;
    }

    private float getLeafRadiusForLayer(int i) {
        if (i < 0 || i >= 5) {
            return -1.0f;
        }
        if (i == 0 || i == 4) {
            return 2.0f;
        }
        return 3.0f;
    }

    private void makeLeaves(ModifiableTestableWorld world, BlockPos pos, BlockBox blockBox, Set<BlockPos> set) {
        for (int i = 0; i < 5; ++i) {
            this.makeLeafLayer(world, pos.up(i), this.getLeafRadiusForLayer(i), blockBox, set);
        }
    }

    private int makeOrCheckBranch(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos start, BlockPos end, boolean make, BlockBox blockBox) {
        if (!make && Objects.equals(start, end)) {
            return -1;
        }
        BlockPos blockPos = end.add(-start.getX(), -start.getY(), -start.getZ());
        int i = this.getLongestSide(blockPos);
        float f = (float)blockPos.getX() / (float)i;
        float g = (float)blockPos.getY() / (float)i;
        float h = (float)blockPos.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockPos2 = start.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (make) {
                this.setBlockState(logPositions, world, blockPos2, (BlockState)LOG.with(LogBlock.AXIS, this.getLogAxis(start, blockPos2)), blockBox);
                continue;
            }
            if (LargeOakTreeFeature.canTreeReplace(world, blockPos2)) continue;
            return j;
        }
        return -1;
    }

    private int getLongestSide(BlockPos box) {
        int i = MathHelper.abs(box.getX());
        int j = MathHelper.abs(box.getY());
        int k = MathHelper.abs(box.getZ());
        if (k > i && k > j) {
            return k;
        }
        if (j > i) {
            return j;
        }
        return i;
    }

    private Direction.Axis getLogAxis(BlockPos branchStart, BlockPos branchEnd) {
        int j;
        Direction.Axis axis = Direction.Axis.Y;
        int i = Math.abs(branchEnd.getX() - branchStart.getX());
        int k = Math.max(i, j = Math.abs(branchEnd.getZ() - branchStart.getZ()));
        if (k > 0) {
            if (i == k) {
                axis = Direction.Axis.X;
            } else if (j == k) {
                axis = Direction.Axis.Z;
            }
        }
        return axis;
    }

    private void makeLeaves(ModifiableTestableWorld world, int treeHeight, BlockPos treePos, List<BranchPosition> branchPositions, BlockBox blockBox, Set<BlockPos> set) {
        for (BranchPosition branchPosition : branchPositions) {
            if (!this.isHighEnough(treeHeight, branchPosition.getEndY() - treePos.getY())) continue;
            this.makeLeaves(world, branchPosition, blockBox, set);
        }
    }

    private boolean isHighEnough(int treeHeight, int height) {
        return (double)height >= (double)treeHeight * 0.2;
    }

    private void makeTrunk(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos pos, int height, BlockBox blockBox) {
        this.makeOrCheckBranch(logPositions, world, pos, pos.up(height), true, blockBox);
    }

    private void makeBranches(Set<BlockPos> logPositions, ModifiableTestableWorld world, int treeHeight, BlockPos treePosition, List<BranchPosition> branchPositions, BlockBox blockBox) {
        for (BranchPosition branchPosition : branchPositions) {
            int i = branchPosition.getEndY();
            BlockPos blockPos = new BlockPos(treePosition.getX(), i, treePosition.getZ());
            if (blockPos.equals(branchPosition) || !this.isHighEnough(treeHeight, i - treePosition.getY())) continue;
            this.makeOrCheckBranch(logPositions, world, blockPos, branchPosition, true, blockBox);
        }
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        int m;
        Random random2 = new Random(random.nextLong());
        int i = this.getTreeHeight(logPositions, world, pos, 5 + random2.nextInt(12), blockBox);
        if (i == -1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        int j = (int)((double)i * 0.618);
        if (j >= i) {
            j = i - 1;
        }
        double d = 1.0;
        int k = (int)(1.382 + Math.pow(1.0 * (double)i / 13.0, 2.0));
        if (k < 1) {
            k = 1;
        }
        int l = pos.getY() + j;
        ArrayList list = Lists.newArrayList();
        list.add(new BranchPosition(pos.up(m), l));
        for (m = i - 5; m >= 0; --m) {
            float f = this.getBaseBranchSize(i, m);
            if (f < 0.0f) continue;
            for (int n = 0; n < k; ++n) {
                BlockPos blockPos2;
                double p;
                double h;
                double e = 1.0;
                double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
                double o = g * Math.sin(h = (double)(random2.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos blockPos = pos.add(o, (double)(m - 1), p = g * Math.cos(h) + 0.5);
                if (this.makeOrCheckBranch(logPositions, world, blockPos, blockPos2 = blockPos.up(5), false, blockBox) != -1) continue;
                int q = pos.getX() - blockPos.getX();
                int r = pos.getZ() - blockPos.getZ();
                double s = (double)blockPos.getY() - Math.sqrt(q * q + r * r) * 0.381;
                int t = s > (double)l ? l : (int)s;
                BlockPos blockPos3 = new BlockPos(pos.getX(), t, pos.getZ());
                if (this.makeOrCheckBranch(logPositions, world, blockPos3, blockPos, false, blockBox) != -1) continue;
                list.add(new BranchPosition(blockPos, blockPos3.getY()));
            }
        }
        this.makeLeaves(world, i, pos, list, blockBox, logPositions);
        this.makeTrunk(logPositions, world, pos, j, blockBox);
        this.makeBranches(logPositions, world, i, pos, list, blockBox);
        return true;
    }

    private int getTreeHeight(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos pos, int height, BlockBox blockBox) {
        if (!LargeOakTreeFeature.isDirtOrGrass(world, pos.down())) {
            return -1;
        }
        int i = this.makeOrCheckBranch(logPositions, world, pos, pos.up(height - 1), false, blockBox);
        if (i == -1) {
            return height;
        }
        if (i < 6) {
            return -1;
        }
        return i;
    }

    static class BranchPosition
    extends BlockPos {
        private final int endY;

        public BranchPosition(BlockPos pos, int endY) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.endY = endY;
        }

        public int getEndY() {
            return this.endY;
        }
    }
}

