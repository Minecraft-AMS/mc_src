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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveCarver;
import net.minecraft.world.gen.carver.UnderwaterRavineCarver;

public abstract class Carver<C extends CarverConfig> {
    public static final Carver<ProbabilityConfig> CAVE = Carver.register("cave", new CaveCarver((Function<Dynamic<?>, ? extends ProbabilityConfig>)((Function<Dynamic<?>, ProbabilityConfig>)ProbabilityConfig::deserialize), 256));
    public static final Carver<ProbabilityConfig> HELL_CAVE = Carver.register("hell_cave", new NetherCaveCarver(ProbabilityConfig::deserialize));
    public static final Carver<ProbabilityConfig> CANYON = Carver.register("canyon", new RavineCarver(ProbabilityConfig::deserialize));
    public static final Carver<ProbabilityConfig> UNDERWATER_CANYON = Carver.register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig::deserialize));
    public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = Carver.register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig::deserialize));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
    protected Set<Fluid> carvableFluids = ImmutableSet.of((Object)Fluids.WATER);
    private final Function<Dynamic<?>, ? extends C> configDeserializer;
    protected final int heightLimit;

    private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F carver) {
        return (F)Registry.register(Registry.CARVER, string, carver);
    }

    public Carver(Function<Dynamic<?>, ? extends C> configDeserializer, int heightLimit) {
        this.configDeserializer = configDeserializer;
        this.heightLimit = heightLimit;
    }

    public int getBranchFactor() {
        return 4;
    }

    protected boolean carveRegion(Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, int chunkX, int chunkZ, double x, double y, double z, double yaw, double pitch, BitSet carvingMask) {
        int n;
        int m;
        int l;
        int k;
        int j;
        Random random = new Random(seed + (long)chunkX + (long)chunkZ);
        double d = chunkX * 16 + 8;
        double e = chunkZ * 16 + 8;
        if (x < d - 16.0 - yaw * 2.0 || z < e - 16.0 - yaw * 2.0 || x > d + 16.0 + yaw * 2.0 || z > e + 16.0 + yaw * 2.0) {
            return false;
        }
        int i = Math.max(MathHelper.floor(x - yaw) - chunkX * 16 - 1, 0);
        if (this.isRegionUncarvable(chunk, chunkX, chunkZ, i, j = Math.min(MathHelper.floor(x + yaw) - chunkX * 16 + 1, 16), k = Math.max(MathHelper.floor(y - pitch) - 1, 1), l = Math.min(MathHelper.floor(y + pitch) + 1, this.heightLimit - 8), m = Math.max(MathHelper.floor(z - yaw) - chunkZ * 16 - 1, 0), n = Math.min(MathHelper.floor(z + yaw) - chunkZ * 16 + 1, 16))) {
            return false;
        }
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        for (int o = i; o < j; ++o) {
            int p = o + chunkX * 16;
            double f = ((double)p + 0.5 - x) / yaw;
            for (int q = m; q < n; ++q) {
                int r = q + chunkZ * 16;
                double g = ((double)r + 0.5 - z) / yaw;
                if (f * f + g * g >= 1.0) continue;
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                for (int s = l; s > k; --s) {
                    double h = ((double)s - 0.5 - y) / pitch;
                    if (this.isPositionExcluded(f, h, g, s)) continue;
                    bl |= this.carveAtPoint(chunk, posToBiome, carvingMask, random, mutable, mutable2, mutable3, seaLevel, chunkX, chunkZ, p, r, o, s, q, atomicBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, BlockPos.Mutable mutable3, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, AtomicBoolean foundSurface) {
        int i = relativeX | relativeZ << 4 | y << 8;
        if (carvingMask.get(i)) {
            return false;
        }
        carvingMask.set(i);
        mutable.set(x, y, z);
        BlockState blockState = chunk.getBlockState(mutable);
        BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable).setOffset(Direction.UP));
        if (blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.MYCELIUM) {
            foundSurface.set(true);
        }
        if (!this.canCarveBlock(blockState, blockState2)) {
            return false;
        }
        if (y < 11) {
            chunk.setBlockState(mutable, LAVA.getBlockState(), false);
        } else {
            chunk.setBlockState(mutable, CAVE_AIR, false);
            if (foundSurface.get()) {
                mutable3.set(mutable).setOffset(Direction.DOWN);
                if (chunk.getBlockState(mutable3).getBlock() == Blocks.DIRT) {
                    chunk.setBlockState(mutable3, posToBiome.apply(mutable).getSurfaceConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }

    public abstract boolean carve(Chunk var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, C var10);

    public abstract boolean shouldCarve(Random var1, int var2, int var3, C var4);

    protected boolean canAlwaysCarveBlock(BlockState state) {
        return this.alwaysCarvableBlocks.contains(state.getBlock());
    }

    protected boolean canCarveBlock(BlockState state, BlockState stateAbove) {
        Block block = state.getBlock();
        return this.canAlwaysCarveBlock(state) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !stateAbove.getFluidState().matches(FluidTags.WATER);
    }

    protected boolean isRegionUncarvable(Chunk chunk, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = relMinX; i < relMaxX; ++i) {
            for (int j = relMinZ; j < relMaxZ; ++j) {
                for (int k = minY - 1; k <= maxY + 1; ++k) {
                    if (this.carvableFluids.contains(chunk.getFluidState(mutable.set(i + mainChunkX * 16, k, j + mainChunkZ * 16)).getFluid())) {
                        return true;
                    }
                    if (k == maxY + 1 || this.isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, i, j)) continue;
                    k = maxY;
                }
            }
        }
        return false;
    }

    private boolean isOnBoundary(int minX, int maxX, int minZ, int maxZ, int x, int z) {
        return x == minX || x == maxX - 1 || z == minZ || z == maxZ - 1;
    }

    protected boolean canCarveBranch(int mainChunkX, int mainChunkZ, double x, double z, int branch, int branchCount, float baseWidth) {
        double d = mainChunkX * 16 + 8;
        double f = x - d;
        double e = mainChunkZ * 16 + 8;
        double g = z - e;
        double h = branchCount - branch;
        double i = baseWidth + 2.0f + 16.0f;
        return f * f + g * g - h * h <= i * i;
    }

    protected abstract boolean isPositionExcluded(double var1, double var3, double var5, int var7);
}

