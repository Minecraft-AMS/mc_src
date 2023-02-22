/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class NetherSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState GLOWSTONE = Blocks.SOUL_SAND.getDefaultState();
    protected long seed;
    protected OctavePerlinNoiseSampler noise;

    public NetherSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, TernarySurfaceConfig ternarySurfaceConfig) {
        int n = l + 1;
        int o = i & 0xF;
        int p = j & 0xF;
        double e = 0.03125;
        boolean bl = this.noise.sample((double)i * 0.03125, (double)j * 0.03125, 0.0) + random.nextDouble() * 0.2 > 0.0;
        boolean bl2 = this.noise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) + random.nextDouble() * 0.2 > 0.0;
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int r = -1;
        BlockState blockState3 = NETHERRACK;
        BlockState blockState4 = NETHERRACK;
        for (int s = 127; s >= 0; --s) {
            mutable.set(o, s, p);
            BlockState blockState5 = chunk.getBlockState(mutable);
            if (blockState5.getBlock() == null || blockState5.isAir()) {
                r = -1;
                continue;
            }
            if (blockState5.getBlock() != blockState.getBlock()) continue;
            if (r == -1) {
                if (q <= 0) {
                    blockState3 = CAVE_AIR;
                    blockState4 = NETHERRACK;
                } else if (s >= n - 4 && s <= n + 1) {
                    blockState3 = NETHERRACK;
                    blockState4 = NETHERRACK;
                    if (bl2) {
                        blockState3 = GRAVEL;
                        blockState4 = NETHERRACK;
                    }
                    if (bl) {
                        blockState3 = GLOWSTONE;
                        blockState4 = GLOWSTONE;
                    }
                }
                if (s < n && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                r = q;
                if (s >= n - 1) {
                    chunk.setBlockState(mutable, blockState3, false);
                    continue;
                }
                chunk.setBlockState(mutable, blockState4, false);
                continue;
            }
            if (r <= 0) continue;
            --r;
            chunk.setBlockState(mutable, blockState4, false);
        }
    }

    @Override
    public void initSeed(long seed) {
        if (this.seed != seed || this.noise == null) {
            this.noise = new OctavePerlinNoiseSampler(new ChunkRandom(seed), 4);
        }
        this.seed = seed;
    }
}

