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

public class SpruceTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();

    public SpruceTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
        super(configFactory, emitNeighborBlockUpdates);
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        int p;
        int o;
        int n;
        int m;
        int i = random.nextInt(4) + 6;
        int j = 1 + random.nextInt(2);
        int k = i - j;
        int l = 2 + random.nextInt(2);
        boolean bl = true;
        if (pos.getY() < 1 || pos.getY() + i + 1 > 256) {
            return false;
        }
        for (m = pos.getY(); m <= pos.getY() + 1 + i && bl; ++m) {
            n = m - pos.getY() < j ? 0 : l;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (o = pos.getX() - n; o <= pos.getX() + n && bl; ++o) {
                for (p = pos.getZ() - n; p <= pos.getZ() + n && bl; ++p) {
                    if (m >= 0 && m < 256) {
                        mutable.set(o, m, p);
                        if (SpruceTreeFeature.isAirOrLeaves(world, mutable)) continue;
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
        if (!SpruceTreeFeature.isDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        m = random.nextInt(2);
        n = 1;
        int q = 0;
        for (o = 0; o <= k; ++o) {
            p = pos.getY() + i - o;
            for (int r = pos.getX() - m; r <= pos.getX() + m; ++r) {
                int s = r - pos.getX();
                for (int t = pos.getZ() - m; t <= pos.getZ() + m; ++t) {
                    BlockPos blockPos;
                    int u = t - pos.getZ();
                    if (Math.abs(s) == m && Math.abs(u) == m && m > 0 || !SpruceTreeFeature.isAirOrLeaves(world, blockPos = new BlockPos(r, p, t)) && !SpruceTreeFeature.isReplaceablePlant(world, blockPos)) continue;
                    this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
                }
            }
            if (m >= n) {
                m = q;
                q = 1;
                if (++n <= l) continue;
                n = l;
                continue;
            }
            ++m;
        }
        o = random.nextInt(3);
        for (p = 0; p < i - o; ++p) {
            if (!SpruceTreeFeature.isAirOrLeaves(world, pos.up(p))) continue;
            this.setBlockState(logPositions, world, pos.up(p), LOG, blockBox);
        }
        return true;
    }
}

