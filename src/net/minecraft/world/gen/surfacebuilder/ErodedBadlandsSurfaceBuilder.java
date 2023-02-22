/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class ErodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public ErodedBadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        double e = 0.0;
        double f = Math.min(Math.abs(d), this.heightCutoffNoise.sample((double)i * 0.25, (double)j * 0.25, false) * 15.0);
        if (f > 0.0) {
            double g = 0.001953125;
            e = f * f * 2.5;
            double h = Math.abs(this.heightNoise.sample((double)i * 0.001953125, (double)j * 0.001953125, false));
            double o = Math.ceil(h * 50.0) + 14.0;
            if (e > o) {
                e = o;
            }
            e += 64.0;
        }
        int p = i & 0xF;
        int q = j & 0xF;
        BlockState blockState3 = WHITE_TERRACOTTA;
        SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
        BlockState blockState4 = surfaceConfig.getUnderMaterial();
        BlockState blockState5 = surfaceConfig.getTopMaterial();
        BlockState blockState6 = blockState4;
        int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
        int s = -1;
        boolean bl2 = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int t = Math.max(k, (int)e + 1); t >= m; --t) {
            BlockState blockState7;
            mutable.set(p, t, q);
            if (chunk.getBlockState(mutable).isAir() && t < (int)e) {
                chunk.setBlockState(mutable, blockState, false);
            }
            if ((blockState7 = chunk.getBlockState(mutable)).isAir()) {
                s = -1;
                continue;
            }
            if (!blockState7.isOf(blockState.getBlock())) continue;
            if (s == -1) {
                bl2 = false;
                if (r <= 0) {
                    blockState3 = Blocks.AIR.getDefaultState();
                    blockState6 = blockState;
                } else if (t >= l - 4 && t <= l + 1) {
                    blockState3 = WHITE_TERRACOTTA;
                    blockState6 = blockState4;
                }
                if (t < l && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                s = r + Math.max(0, t - l);
                if (t >= l - 1) {
                    if (t > l + 3 + r) {
                        BlockState blockState8 = t < 64 || t > 127 ? ORANGE_TERRACOTTA : (bl ? TERRACOTTA : this.calculateLayerBlockState(i, t, j));
                        chunk.setBlockState(mutable, blockState8, false);
                        continue;
                    }
                    chunk.setBlockState(mutable, blockState5, false);
                    bl2 = true;
                    continue;
                }
                chunk.setBlockState(mutable, blockState6, false);
                if (!blockState6.isOf(Blocks.WHITE_TERRACOTTA) && !blockState6.isOf(Blocks.ORANGE_TERRACOTTA) && !blockState6.isOf(Blocks.MAGENTA_TERRACOTTA) && !blockState6.isOf(Blocks.LIGHT_BLUE_TERRACOTTA) && !blockState6.isOf(Blocks.YELLOW_TERRACOTTA) && !blockState6.isOf(Blocks.LIME_TERRACOTTA) && !blockState6.isOf(Blocks.PINK_TERRACOTTA) && !blockState6.isOf(Blocks.GRAY_TERRACOTTA) && !blockState6.isOf(Blocks.LIGHT_GRAY_TERRACOTTA) && !blockState6.isOf(Blocks.CYAN_TERRACOTTA) && !blockState6.isOf(Blocks.PURPLE_TERRACOTTA) && !blockState6.isOf(Blocks.BLUE_TERRACOTTA) && !blockState6.isOf(Blocks.BROWN_TERRACOTTA) && !blockState6.isOf(Blocks.GREEN_TERRACOTTA) && !blockState6.isOf(Blocks.RED_TERRACOTTA) && !blockState6.isOf(Blocks.BLACK_TERRACOTTA)) continue;
                chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                continue;
            }
            if (s <= 0) continue;
            --s;
            if (bl2) {
                chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                continue;
            }
            chunk.setBlockState(mutable, this.calculateLayerBlockState(i, t, j), false);
        }
    }
}

