/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherCaveCarver
extends CaveCarver {
    public NetherCaveCarver(Codec<ProbabilityConfig> configCodec) {
        super(configCodec, 128);
        this.alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
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
    protected boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, BlockPos.Mutable mutable3, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, MutableBoolean mutableBoolean) {
        int i = relativeX | relativeZ << 4 | y << 8;
        if (carvingMask.get(i)) {
            return false;
        }
        carvingMask.set(i);
        mutable.set(x, y, z);
        if (this.canAlwaysCarveBlock(chunk.getBlockState(mutable))) {
            BlockState blockState = y <= 31 ? LAVA.getBlockState() : CAVE_AIR;
            chunk.setBlockState(mutable, blockState, false);
            return true;
        }
        return false;
    }
}

