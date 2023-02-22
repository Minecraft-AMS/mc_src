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
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class OakTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();
    protected final int height;
    private final boolean hasVinesAndCocoa;
    private final BlockState log;
    private final BlockState leaves;

    public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
        this(configFactory, emitNeighborBlockUpdates, 4, LOG, LEAVES, false);
    }

    public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int height, BlockState log, BlockState leaves, boolean hasVinesAndCocoa) {
        super(function, bl);
        this.height = height;
        this.log = log;
        this.leaves = leaves;
        this.hasVinesAndCocoa = hasVinesAndCocoa;
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        BlockPos blockPos;
        int q;
        int p;
        int n;
        int m;
        int l;
        int k;
        int j;
        int i = this.getTreeHeight(random);
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
                        if (OakTreeFeature.canTreeReplace(world, mutable.set(l, j, m))) continue;
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
        if (!OakTreeFeature.isDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        j = 3;
        k = 0;
        for (n = pos.getY() - 3 + i; n <= pos.getY() + i; ++n) {
            l = n - (pos.getY() + i);
            m = 1 - l / 2;
            for (int o = pos.getX() - m; o <= pos.getX() + m; ++o) {
                p = o - pos.getX();
                for (q = pos.getZ() - m; q <= pos.getZ() + m; ++q) {
                    int r = q - pos.getZ();
                    if (Math.abs(p) == m && Math.abs(r) == m && (random.nextInt(2) == 0 || l == 0) || !OakTreeFeature.isAirOrLeaves(world, blockPos = new BlockPos(o, n, q)) && !OakTreeFeature.isReplaceablePlant(world, blockPos)) continue;
                    this.setBlockState(logPositions, world, blockPos, this.leaves, blockBox);
                }
            }
        }
        for (n = 0; n < i; ++n) {
            if (!OakTreeFeature.isAirOrLeaves(world, pos.up(n)) && !OakTreeFeature.isReplaceablePlant(world, pos.up(n))) continue;
            this.setBlockState(logPositions, world, pos.up(n), this.log, blockBox);
            if (!this.hasVinesAndCocoa || n <= 0) continue;
            if (random.nextInt(3) > 0 && OakTreeFeature.isAir(world, pos.add(-1, n, 0))) {
                this.makeVine(world, pos.add(-1, n, 0), VineBlock.EAST);
            }
            if (random.nextInt(3) > 0 && OakTreeFeature.isAir(world, pos.add(1, n, 0))) {
                this.makeVine(world, pos.add(1, n, 0), VineBlock.WEST);
            }
            if (random.nextInt(3) > 0 && OakTreeFeature.isAir(world, pos.add(0, n, -1))) {
                this.makeVine(world, pos.add(0, n, -1), VineBlock.SOUTH);
            }
            if (random.nextInt(3) <= 0 || !OakTreeFeature.isAir(world, pos.add(0, n, 1))) continue;
            this.makeVine(world, pos.add(0, n, 1), VineBlock.NORTH);
        }
        if (this.hasVinesAndCocoa) {
            for (n = pos.getY() - 3 + i; n <= pos.getY() + i; ++n) {
                l = n - (pos.getY() + i);
                m = 2 - l / 2;
                BlockPos.Mutable mutable2 = new BlockPos.Mutable();
                for (p = pos.getX() - m; p <= pos.getX() + m; ++p) {
                    for (q = pos.getZ() - m; q <= pos.getZ() + m; ++q) {
                        mutable2.set(p, n, q);
                        if (!OakTreeFeature.isLeaves(world, mutable2)) continue;
                        BlockPos blockPos2 = mutable2.west();
                        blockPos = mutable2.east();
                        BlockPos blockPos3 = mutable2.north();
                        BlockPos blockPos4 = mutable2.south();
                        if (random.nextInt(4) == 0 && OakTreeFeature.isAir(world, blockPos2)) {
                            this.makeVineColumn(world, blockPos2, VineBlock.EAST);
                        }
                        if (random.nextInt(4) == 0 && OakTreeFeature.isAir(world, blockPos)) {
                            this.makeVineColumn(world, blockPos, VineBlock.WEST);
                        }
                        if (random.nextInt(4) == 0 && OakTreeFeature.isAir(world, blockPos3)) {
                            this.makeVineColumn(world, blockPos3, VineBlock.SOUTH);
                        }
                        if (random.nextInt(4) != 0 || !OakTreeFeature.isAir(world, blockPos4)) continue;
                        this.makeVineColumn(world, blockPos4, VineBlock.NORTH);
                    }
                }
            }
            if (random.nextInt(5) == 0 && i > 5) {
                for (n = 0; n < 2; ++n) {
                    for (Direction direction : Direction.Type.HORIZONTAL) {
                        if (random.nextInt(4 - n) != 0) continue;
                        Direction direction2 = direction.getOpposite();
                        this.makeCocoa(world, random.nextInt(3), pos.add(direction2.getOffsetX(), i - 5 + n, direction2.getOffsetZ()), direction);
                    }
                }
            }
        }
        return true;
    }

    protected int getTreeHeight(Random random) {
        return this.height + random.nextInt(3);
    }

    private void makeCocoa(ModifiableWorld worlf, int age, BlockPos pos, Direction direction) {
        this.setBlockState(worlf, pos, (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, age)).with(CocoaBlock.FACING, direction));
    }

    private void makeVine(ModifiableWorld world, BlockPos pos, BooleanProperty directionProperty) {
        this.setBlockState(world, pos, (BlockState)Blocks.VINE.getDefaultState().with(directionProperty, true));
    }

    private void makeVineColumn(ModifiableTestableWorld world, BlockPos pos, BooleanProperty directionProperty) {
        this.makeVine(world, pos, directionProperty);
        pos = pos.down();
        for (int i = 4; OakTreeFeature.isAir(world, pos) && i > 0; --i) {
            this.makeVine(world, pos, directionProperty);
            pos = pos.down();
        }
    }
}

