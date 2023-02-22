/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

public class CaveCarver
extends Carver<ProbabilityConfig> {
    public CaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer, int heightLimit) {
        super(configDeserializer, heightLimit);
    }

    @Override
    public boolean shouldCarve(Random random, int i, int j, ProbabilityConfig probabilityConfig) {
        return random.nextFloat() <= probabilityConfig.probability;
    }

    @Override
    public boolean carve(Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig probabilityConfig) {
        int n = (this.getBranchFactor() * 2 - 1) * 16;
        int o = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);
        for (int p = 0; p < o; ++p) {
            float h;
            double d = j * 16 + random.nextInt(16);
            double e = this.getCaveY(random);
            double f = k * 16 + random.nextInt(16);
            int q = 1;
            if (random.nextInt(4) == 0) {
                double g = 0.5;
                h = 1.0f + random.nextFloat() * 6.0f;
                this.carveCave(chunk, function, random.nextLong(), i, l, m, d, e, f, h, 0.5, bitSet);
                q += random.nextInt(4);
            }
            for (int r = 0; r < q; ++r) {
                float s = random.nextFloat() * ((float)Math.PI * 2);
                h = (random.nextFloat() - 0.5f) / 4.0f;
                float t = this.getTunnelSystemWidth(random);
                int u = n - random.nextInt(n / 4);
                boolean v = false;
                this.carveTunnels(chunk, function, random.nextLong(), i, l, m, d, e, f, t, s, h, 0, u, this.getTunnelSystemHeightWidthRatio(), bitSet);
            }
        }
        return true;
    }

    protected int getMaxCaveCount() {
        return 15;
    }

    protected float getTunnelSystemWidth(Random random) {
        float f = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return f;
    }

    protected double getTunnelSystemHeightWidthRatio() {
        return 1.0;
    }

    protected int getCaveY(Random random) {
        return random.nextInt(random.nextInt(120) + 8);
    }

    protected void carveCave(Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, int mainChunkX, int mainChunkZ, double x, double y, double z, float yaw, double yawPitchRatio, BitSet carvingMask) {
        double d = 1.5 + (double)(MathHelper.sin(1.5707964f) * yaw);
        double e = d * yawPitchRatio;
        this.carveRegion(chunk, posToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x + 1.0, y, z, d, e, carvingMask);
    }

    protected void carveTunnels(Chunk chunk, Function<BlockPos, Biome> postToBiome, long seed, int seaLevel, int mainChunkX, int mainChunkZ, double x, double y, double z, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, BitSet carvingMask) {
        Random random = new Random(seed);
        int i = random.nextInt(branchCount / 2) + branchCount / 4;
        boolean bl = random.nextInt(6) == 0;
        float f = 0.0f;
        float g = 0.0f;
        for (int j = branchStartIndex; j < branchCount; ++j) {
            double d = 1.5 + (double)(MathHelper.sin((float)Math.PI * (float)j / (float)branchCount) * width);
            double e = d * yawPitchRatio;
            float h = MathHelper.cos(pitch);
            x += (double)(MathHelper.cos(yaw) * h);
            y += (double)MathHelper.sin(pitch);
            z += (double)(MathHelper.sin(yaw) * h);
            pitch *= bl ? 0.92f : 0.7f;
            pitch += g * 0.1f;
            yaw += f * 0.1f;
            g *= 0.9f;
            f *= 0.75f;
            g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (j == i && width > 1.0f) {
                this.carveTunnels(chunk, postToBiome, random.nextLong(), seaLevel, mainChunkX, mainChunkZ, x, y, z, random.nextFloat() * 0.5f + 0.5f, yaw - 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, carvingMask);
                this.carveTunnels(chunk, postToBiome, random.nextLong(), seaLevel, mainChunkX, mainChunkZ, x, y, z, random.nextFloat() * 0.5f + 0.5f, yaw + 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, carvingMask);
                return;
            }
            if (random.nextInt(4) == 0) continue;
            if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, j, branchCount, width)) {
                return;
            }
            this.carveRegion(chunk, postToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, d, e, carvingMask);
        }
    }

    @Override
    protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
        return scaledRelativeY <= -0.7 || scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
    }
}

