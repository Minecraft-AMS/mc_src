/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;

public class NetherCaveCarver
extends CaveCarver {
    public NetherCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function, 128);
        this.alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK});
        this.carvableFluids = ImmutableSet.of((Object)Fluids.LAVA, (Object)Fluids.WATER);
    }

    @Override
    protected int getMaxCaveCount() {
        return 10;
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }

    @Override
    protected double getTunnelSystemHeightWidthRatio() {
        return 5.0;
    }

    @Override
    protected int getCaveY(Random random) {
        return random.nextInt(this.heightLimit);
    }

    @Override
    protected boolean carveAtPoint(Chunk chunk, BitSet mask, Random random, BlockPos.Mutable pos1, BlockPos.Mutable pos2, BlockPos.Mutable pos3, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, AtomicBoolean atomicBoolean) {
        int i = relativeX | relativeZ << 4 | y << 8;
        if (mask.get(i)) {
            return false;
        }
        mask.set(i);
        pos1.set(x, y, z);
        if (this.canAlwaysCarveBlock(chunk.getBlockState(pos1))) {
            BlockState blockState = y <= 31 ? LAVA.getBlockState() : CAVE_AIR;
            chunk.setBlockState(pos1, blockState, false);
            return true;
        }
        return false;
    }
}
