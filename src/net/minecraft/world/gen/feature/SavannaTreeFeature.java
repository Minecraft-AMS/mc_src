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
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class SavannaTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.ACACIA_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();

    public SavannaTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
        super(configFactory, emitNeighborBlockUpdates);
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        int q;
        int m;
        int l;
        int k;
        int i = random.nextInt(3) + random.nextInt(3) + 5;
        boolean bl = true;
        if (pos.getY() < 1 || pos.getY() + i + 1 > 256) {
            return false;
        }
        for (int j = pos.getY(); j <= pos.getY() + 1 + i; ++j) {
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
                        if (SavannaTreeFeature.canTreeReplace(world, mutable.set(l, j, m))) continue;
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
        if (!SavannaTreeFeature.isNaturalDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        k = i - random.nextInt(4) - 1;
        int n = 3 - random.nextInt(3);
        l = pos.getX();
        m = pos.getZ();
        int o = 0;
        for (int p = 0; p < i; ++p) {
            BlockPos blockPos;
            q = pos.getY() + p;
            if (p >= k && n > 0) {
                l += direction.getOffsetX();
                m += direction.getOffsetZ();
                --n;
            }
            if (!SavannaTreeFeature.isAirOrLeaves(world, blockPos = new BlockPos(l, q, m))) continue;
            this.addLog(logPositions, world, blockPos, blockBox);
            o = q;
        }
        BlockPos blockPos2 = new BlockPos(l, o, m);
        for (q = -3; q <= 3; ++q) {
            for (int r = -3; r <= 3; ++r) {
                if (Math.abs(q) == 3 && Math.abs(r) == 3) continue;
                this.addLeaves(logPositions, world, blockPos2.add(q, 0, r), blockBox);
            }
        }
        blockPos2 = blockPos2.up();
        for (q = -1; q <= 1; ++q) {
            for (int r = -1; r <= 1; ++r) {
                this.addLeaves(logPositions, world, blockPos2.add(q, 0, r), blockBox);
            }
        }
        this.addLeaves(logPositions, world, blockPos2.east(2), blockBox);
        this.addLeaves(logPositions, world, blockPos2.west(2), blockBox);
        this.addLeaves(logPositions, world, blockPos2.south(2), blockBox);
        this.addLeaves(logPositions, world, blockPos2.north(2), blockBox);
        l = pos.getX();
        m = pos.getZ();
        Direction direction2 = Direction.Type.HORIZONTAL.random(random);
        if (direction2 != direction) {
            int t;
            q = k - random.nextInt(2) - 1;
            int r = 1 + random.nextInt(3);
            o = 0;
            for (int s = q; s < i && r > 0; ++s, --r) {
                if (s < 1) continue;
                t = pos.getY() + s;
                BlockPos blockPos3 = new BlockPos(l += direction2.getOffsetX(), t, m += direction2.getOffsetZ());
                if (!SavannaTreeFeature.isAirOrLeaves(world, blockPos3)) continue;
                this.addLog(logPositions, world, blockPos3, blockBox);
                o = t;
            }
            if (o > 0) {
                BlockPos blockPos4 = new BlockPos(l, o, m);
                for (t = -2; t <= 2; ++t) {
                    for (int u = -2; u <= 2; ++u) {
                        if (Math.abs(t) == 2 && Math.abs(u) == 2) continue;
                        this.addLeaves(logPositions, world, blockPos4.add(t, 0, u), blockBox);
                    }
                }
                blockPos4 = blockPos4.up();
                for (t = -1; t <= 1; ++t) {
                    for (int u = -1; u <= 1; ++u) {
                        this.addLeaves(logPositions, world, blockPos4.add(t, 0, u), blockBox);
                    }
                }
            }
        }
        return true;
    }

    private void addLog(Set<BlockPos> logPositions, ModifiableWorld world, BlockPos pos, BlockBox blockBox) {
        this.setBlockState(logPositions, world, pos, LOG, blockBox);
    }

    private void addLeaves(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BlockBox blockBox) {
        if (SavannaTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos)) {
            this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, blockBox);
        }
    }
}

