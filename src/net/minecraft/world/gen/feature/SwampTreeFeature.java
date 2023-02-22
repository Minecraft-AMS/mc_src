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
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class SwampTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();

    public SwampTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory, false);
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        BlockPos blockPos;
        int o;
        int m;
        int l;
        int k;
        int j;
        int i = random.nextInt(4) + 5;
        pos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos);
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
                k = 3;
            }
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (l = pos.getX() - k; l <= pos.getX() + k && bl; ++l) {
                for (m = pos.getZ() - k; m <= pos.getZ() + k && bl; ++m) {
                    if (j >= 0 && j < 256) {
                        mutable.set(l, j, m);
                        if (SwampTreeFeature.isAirOrLeaves(world, mutable)) continue;
                        if (SwampTreeFeature.isWater(world, mutable)) {
                            if (j <= pos.getY()) continue;
                            bl = false;
                            continue;
                        }
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
        if (!SwampTreeFeature.isNaturalDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        for (j = pos.getY() - 3 + i; j <= pos.getY() + i; ++j) {
            k = j - (pos.getY() + i);
            int n = 2 - k / 2;
            for (l = pos.getX() - n; l <= pos.getX() + n; ++l) {
                m = l - pos.getX();
                for (o = pos.getZ() - n; o <= pos.getZ() + n; ++o) {
                    int p = o - pos.getZ();
                    if (Math.abs(m) == n && Math.abs(p) == n && (random.nextInt(2) == 0 || k == 0) || !SwampTreeFeature.isAirOrLeaves(world, blockPos = new BlockPos(l, j, o)) && !SwampTreeFeature.isReplaceablePlant(world, blockPos)) continue;
                    this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
                }
            }
        }
        for (j = 0; j < i; ++j) {
            BlockPos blockPos2 = pos.up(j);
            if (!SwampTreeFeature.isAirOrLeaves(world, blockPos2) && !SwampTreeFeature.isWater(world, blockPos2)) continue;
            this.setBlockState(logPositions, world, blockPos2, LOG, blockBox);
        }
        for (j = pos.getY() - 3 + i; j <= pos.getY() + i; ++j) {
            int k2 = j - (pos.getY() + i);
            int n = 2 - k2 / 2;
            BlockPos.Mutable mutable2 = new BlockPos.Mutable();
            for (m = pos.getX() - n; m <= pos.getX() + n; ++m) {
                for (o = pos.getZ() - n; o <= pos.getZ() + n; ++o) {
                    mutable2.set(m, j, o);
                    if (!SwampTreeFeature.isLeaves(world, mutable2)) continue;
                    BlockPos blockPos3 = mutable2.west();
                    blockPos = mutable2.east();
                    BlockPos blockPos4 = mutable2.north();
                    BlockPos blockPos5 = mutable2.south();
                    if (random.nextInt(4) == 0 && SwampTreeFeature.isAir(world, blockPos3)) {
                        this.makeVines(world, blockPos3, VineBlock.EAST);
                    }
                    if (random.nextInt(4) == 0 && SwampTreeFeature.isAir(world, blockPos)) {
                        this.makeVines(world, blockPos, VineBlock.WEST);
                    }
                    if (random.nextInt(4) == 0 && SwampTreeFeature.isAir(world, blockPos4)) {
                        this.makeVines(world, blockPos4, VineBlock.SOUTH);
                    }
                    if (random.nextInt(4) != 0 || !SwampTreeFeature.isAir(world, blockPos5)) continue;
                    this.makeVines(world, blockPos5, VineBlock.NORTH);
                }
            }
        }
        return true;
    }

    private void makeVines(ModifiableTestableWorld world, BlockPos pos, BooleanProperty directionProperty) {
        BlockState blockState = (BlockState)Blocks.VINE.getDefaultState().with(directionProperty, true);
        this.setBlockState(world, pos, blockState);
        pos = pos.down();
        for (int i = 4; SwampTreeFeature.isAir(world, pos) && i > 0; --i) {
            this.setBlockState(world, pos, blockState);
            pos = pos.down();
        }
    }
}

