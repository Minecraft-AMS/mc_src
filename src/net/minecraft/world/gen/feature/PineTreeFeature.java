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

public class PineTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();

    public PineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory, false);
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        int p;
        int o;
        int n;
        int m;
        int i = random.nextInt(5) + 7;
        int j = i - random.nextInt(2) - 3;
        int k = i - j;
        int l = 1 + random.nextInt(k + 1);
        if (pos.getY() < 1 || pos.getY() + i + 1 > 256) {
            return false;
        }
        boolean bl = true;
        for (m = pos.getY(); m <= pos.getY() + 1 + i && bl; ++m) {
            n = 1;
            n = m - pos.getY() < j ? 0 : l;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (o = pos.getX() - n; o <= pos.getX() + n && bl; ++o) {
                for (p = pos.getZ() - n; p <= pos.getZ() + n && bl; ++p) {
                    if (m >= 0 && m < 256) {
                        if (PineTreeFeature.canTreeReplace(world, mutable.set(o, m, p))) continue;
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
        if (!PineTreeFeature.isNaturalDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        m = 0;
        for (n = pos.getY() + i; n >= pos.getY() + j; --n) {
            for (int q = pos.getX() - m; q <= pos.getX() + m; ++q) {
                o = q - pos.getX();
                for (p = pos.getZ() - m; p <= pos.getZ() + m; ++p) {
                    BlockPos blockPos;
                    int r = p - pos.getZ();
                    if (Math.abs(o) == m && Math.abs(r) == m && m > 0 || !PineTreeFeature.isAirOrLeaves(world, blockPos = new BlockPos(q, n, p))) continue;
                    this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
                }
            }
            if (m >= 1 && n == pos.getY() + j + 1) {
                --m;
                continue;
            }
            if (m >= l) continue;
            ++m;
        }
        for (n = 0; n < i - 1; ++n) {
            if (!PineTreeFeature.isAirOrLeaves(world, pos.up(n))) continue;
            this.setBlockState(logPositions, world, pos.up(n), LOG, blockBox);
        }
        return true;
    }
}

