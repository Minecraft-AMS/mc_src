/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IcebergFeatureConfig;

public class IcebergFeature
extends Feature<IcebergFeatureConfig> {
    public IcebergFeature(Function<Dynamic<?>, ? extends IcebergFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, IcebergFeatureConfig icebergFeatureConfig) {
        boolean bl3;
        int s;
        int r;
        int q;
        int p;
        int l;
        blockPos = new BlockPos(blockPos.getX(), iWorld.getSeaLevel(), blockPos.getZ());
        boolean bl = random.nextDouble() > 0.7;
        BlockState blockState = icebergFeatureConfig.state;
        double d = random.nextDouble() * 2.0 * Math.PI;
        int i = 11 - random.nextInt(5);
        int j = 3 + random.nextInt(3);
        boolean bl2 = random.nextDouble() > 0.7;
        int k = 11;
        int n = l = bl2 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;
        if (!bl2 && random.nextDouble() > 0.9) {
            l += random.nextInt(19) + 7;
        }
        int m = Math.min(l + random.nextInt(11), 18);
        int n2 = Math.min(l + random.nextInt(7) - random.nextInt(5), 11);
        int o = bl2 ? i : 11;
        for (p = -o; p < o; ++p) {
            for (q = -o; q < o; ++q) {
                for (r = 0; r < l; ++r) {
                    int n3 = s = bl2 ? this.method_13417(r, l, n2) : this.method_13419(random, r, l, n2);
                    if (!bl2 && p >= s) continue;
                    this.method_13426(iWorld, random, blockPos, l, p, r, q, s, o, bl2, j, d, bl, blockState);
                }
            }
        }
        this.method_13418(iWorld, blockPos, n2, l, bl2, i);
        for (p = -o; p < o; ++p) {
            for (q = -o; q < o; ++q) {
                for (r = -1; r > -m; --r) {
                    s = bl2 ? MathHelper.ceil((float)o * (1.0f - (float)Math.pow(r, 2.0) / ((float)m * 8.0f))) : o;
                    int t = this.method_13427(random, -r, m, n2);
                    if (p >= t) continue;
                    this.method_13426(iWorld, random, blockPos, m, p, r, q, t, s, bl2, j, d, bl, blockState);
                }
            }
        }
        boolean bl4 = bl2 ? random.nextDouble() > 0.1 : (bl3 = random.nextDouble() > 0.7);
        if (bl3) {
            this.method_13428(random, iWorld, n2, l, blockPos, bl2, i, d, j);
        }
        return true;
    }

    private void method_13428(Random random, IWorld iWorld, int i, int j, BlockPos blockPos, boolean bl, int k, double d, int l) {
        int r;
        int q;
        int m = random.nextBoolean() ? -1 : 1;
        int n = random.nextBoolean() ? -1 : 1;
        int o = random.nextInt(Math.max(i / 2 - 2, 1));
        if (random.nextBoolean()) {
            o = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
        }
        int p = random.nextInt(Math.max(i / 2 - 2, 1));
        if (random.nextBoolean()) {
            p = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
        }
        if (bl) {
            o = p = random.nextInt(Math.max(k - 5, 1));
        }
        BlockPos blockPos2 = new BlockPos(m * o, 0, n * p);
        double e = bl ? d + 1.5707963267948966 : random.nextDouble() * 2.0 * Math.PI;
        for (q = 0; q < j - 3; ++q) {
            r = this.method_13419(random, q, j, i);
            this.method_13415(r, q, blockPos, iWorld, false, e, blockPos2, k, l);
        }
        for (q = -1; q > -j + random.nextInt(5); --q) {
            r = this.method_13427(random, -q, j, i);
            this.method_13415(r, q, blockPos, iWorld, true, e, blockPos2, k, l);
        }
    }

    private void method_13415(int i, int j, BlockPos blockPos, IWorld iWorld, boolean bl, double d, BlockPos blockPos2, int k, int l) {
        int m = i + 1 + k / 3;
        int n = Math.min(i - 3, 3) + l / 2 - 1;
        for (int o = -m; o < m; ++o) {
            for (int p = -m; p < m; ++p) {
                BlockPos blockPos3;
                Block block;
                double e = this.method_13424(o, p, blockPos2, m, n, d);
                if (!(e < 0.0) || !this.method_13420(block = iWorld.getBlockState(blockPos3 = blockPos.add(o, j, p)).getBlock()) && block != Blocks.SNOW_BLOCK) continue;
                if (bl) {
                    this.setBlockState(iWorld, blockPos3, Blocks.WATER.getDefaultState());
                    continue;
                }
                this.setBlockState(iWorld, blockPos3, Blocks.AIR.getDefaultState());
                this.method_13422(iWorld, blockPos3);
            }
        }
    }

    private void method_13422(IWorld iWorld, BlockPos blockPos) {
        if (iWorld.getBlockState(blockPos.up()).getBlock() == Blocks.SNOW) {
            this.setBlockState(iWorld, blockPos.up(), Blocks.AIR.getDefaultState());
        }
    }

    private void method_13426(IWorld iWorld, Random random, BlockPos blockPos, int i, int j, int k, int l, int m, int n, boolean bl, int o, double d, boolean bl2, BlockState blockState) {
        double e;
        double d2 = e = bl ? this.method_13424(j, l, BlockPos.ORIGIN, n, this.method_13416(k, i, o), d) : this.method_13421(j, l, BlockPos.ORIGIN, m, random);
        if (e < 0.0) {
            double f;
            BlockPos blockPos2 = blockPos.add(j, k, l);
            double d3 = f = bl ? -0.5 : (double)(-6 - random.nextInt(3));
            if (e > f && random.nextDouble() > 0.9) {
                return;
            }
            this.method_13425(blockPos2, iWorld, random, i - k, i, bl, bl2, blockState);
        }
    }

    private void method_13425(BlockPos blockPos, IWorld iWorld, Random random, int i, int j, boolean bl, boolean bl2, BlockState blockState) {
        BlockState blockState2 = iWorld.getBlockState(blockPos);
        Block block = blockState2.getBlock();
        if (blockState2.getMaterial() == Material.AIR || block == Blocks.SNOW_BLOCK || block == Blocks.ICE || block == Blocks.WATER) {
            int k;
            boolean bl3 = !bl || random.nextDouble() > 0.05;
            int n = k = bl ? 3 : 2;
            if (bl2 && block != Blocks.WATER && (double)i <= (double)random.nextInt(Math.max(1, j / k)) + (double)j * 0.6 && bl3) {
                this.setBlockState(iWorld, blockPos, Blocks.SNOW_BLOCK.getDefaultState());
            } else {
                this.setBlockState(iWorld, blockPos, blockState);
            }
        }
    }

    private int method_13416(int i, int j, int k) {
        int l = k;
        if (i > 0 && j - i <= 3) {
            l -= 4 - (j - i);
        }
        return l;
    }

    private double method_13421(int i, int j, BlockPos blockPos, int k, Random random) {
        float f = 10.0f * MathHelper.clamp(random.nextFloat(), 0.2f, 0.8f) / (float)k;
        return (double)f + Math.pow(i - blockPos.getX(), 2.0) + Math.pow(j - blockPos.getZ(), 2.0) - Math.pow(k, 2.0);
    }

    private double method_13424(int i, int j, BlockPos blockPos, int k, int l, double d) {
        return Math.pow(((double)(i - blockPos.getX()) * Math.cos(d) - (double)(j - blockPos.getZ()) * Math.sin(d)) / (double)k, 2.0) + Math.pow(((double)(i - blockPos.getX()) * Math.sin(d) + (double)(j - blockPos.getZ()) * Math.cos(d)) / (double)l, 2.0) - 1.0;
    }

    private int method_13419(Random random, int i, int j, int k) {
        float f = 3.5f - random.nextFloat();
        float g = (1.0f - (float)Math.pow(i, 2.0) / ((float)j * f)) * (float)k;
        if (j > 15 + random.nextInt(5)) {
            int l = i < 3 + random.nextInt(6) ? i / 2 : i;
            g = (1.0f - (float)l / ((float)j * f * 0.4f)) * (float)k;
        }
        return MathHelper.ceil(g / 2.0f);
    }

    private int method_13417(int i, int j, int k) {
        float f = 1.0f;
        float g = (1.0f - (float)Math.pow(i, 2.0) / ((float)j * 1.0f)) * (float)k;
        return MathHelper.ceil(g / 2.0f);
    }

    private int method_13427(Random random, int i, int j, int k) {
        float f = 1.0f + random.nextFloat() / 2.0f;
        float g = (1.0f - (float)i / ((float)j * f)) * (float)k;
        return MathHelper.ceil(g / 2.0f);
    }

    private boolean method_13420(Block block) {
        return block == Blocks.PACKED_ICE || block == Blocks.SNOW_BLOCK || block == Blocks.BLUE_ICE;
    }

    private boolean method_13414(BlockView blockView, BlockPos blockPos) {
        return blockView.getBlockState(blockPos.down()).getMaterial() == Material.AIR;
    }

    private void method_13418(IWorld iWorld, BlockPos blockPos, int i, int j, boolean bl, int k) {
        int l = bl ? k : i / 2;
        for (int m = -l; m <= l; ++m) {
            for (int n = -l; n <= l; ++n) {
                for (int o = 0; o <= j; ++o) {
                    BlockPos blockPos2 = blockPos.add(m, o, n);
                    Block block = iWorld.getBlockState(blockPos2).getBlock();
                    if (!this.method_13420(block) && block != Blocks.SNOW) continue;
                    if (this.method_13414(iWorld, blockPos2)) {
                        this.setBlockState(iWorld, blockPos2, Blocks.AIR.getDefaultState());
                        this.setBlockState(iWorld, blockPos2.up(), Blocks.AIR.getDefaultState());
                        continue;
                    }
                    if (!this.method_13420(block)) continue;
                    Block[] blocks = new Block[]{iWorld.getBlockState(blockPos2.west()).getBlock(), iWorld.getBlockState(blockPos2.east()).getBlock(), iWorld.getBlockState(blockPos2.north()).getBlock(), iWorld.getBlockState(blockPos2.south()).getBlock()};
                    int p = 0;
                    for (Block block2 : blocks) {
                        if (this.method_13420(block2)) continue;
                        ++p;
                    }
                    if (p < 3) continue;
                    this.setBlockState(iWorld, blockPos2, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
}

