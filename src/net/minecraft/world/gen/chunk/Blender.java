/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableDouble
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class Blender {
    private static final Blender NO_BLENDING = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()){

        @Override
        public class_6956 method_39340(int i, int j) {
            return new class_6956(1.0, 0.0);
        }

        @Override
        public double method_39338(DensityFunction.NoisePos noisePos, double d) {
            return d;
        }

        @Override
        public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
            return biomeSupplier;
        }
    };
    private static final DoublePerlinNoiseSampler field_35681 = DoublePerlinNoiseSampler.create(new Xoroshiro128PlusPlusRandom(42L), BuiltinRegistries.NOISE_PARAMETERS.getOrThrow(NoiseParametersKeys.OFFSET));
    private static final int field_35502 = BiomeCoords.fromChunk(7) - 1;
    private static final int field_35503 = BiomeCoords.toChunk(field_35502 + 3);
    private static final int field_35504 = 2;
    private static final int field_35505 = BiomeCoords.toChunk(5);
    private static final double field_36222 = (double)BlendingData.OLD_HEIGHT_LIMIT.getHeight() / 2.0;
    private static final double field_36223 = (double)BlendingData.OLD_HEIGHT_LIMIT.getBottomY() + field_36222;
    private static final double field_36224 = 8.0;
    private final Long2ObjectOpenHashMap<BlendingData> field_36343;
    private final Long2ObjectOpenHashMap<BlendingData> field_36344;

    public static Blender getNoBlending() {
        return NO_BLENDING;
    }

    public static Blender getBlender(@Nullable ChunkRegion chunkRegion) {
        if (chunkRegion == null) {
            return NO_BLENDING;
        }
        Long2ObjectOpenHashMap long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
        Long2ObjectOpenHashMap long2ObjectOpenHashMap2 = new Long2ObjectOpenHashMap();
        ChunkPos chunkPos = chunkRegion.getCenterPos();
        for (int i = -field_35503; i <= field_35503; ++i) {
            for (int j = -field_35503; j <= field_35503; ++j) {
                int k = chunkPos.x + i;
                int l = chunkPos.z + j;
                BlendingData blendingData = BlendingData.getBlendingData(chunkRegion, k, l);
                if (blendingData == null) continue;
                long2ObjectOpenHashMap.put(ChunkPos.toLong(k, l), (Object)blendingData);
                if (i < -field_35505 || i > field_35505 || j < -field_35505 || j > field_35505) continue;
                long2ObjectOpenHashMap2.put(ChunkPos.toLong(k, l), (Object)blendingData);
            }
        }
        if (long2ObjectOpenHashMap.isEmpty() && long2ObjectOpenHashMap2.isEmpty()) {
            return NO_BLENDING;
        }
        return new Blender((Long2ObjectOpenHashMap<BlendingData>)long2ObjectOpenHashMap, (Long2ObjectOpenHashMap<BlendingData>)long2ObjectOpenHashMap2);
    }

    Blender(Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap, Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap2) {
        this.field_36343 = long2ObjectOpenHashMap;
        this.field_36344 = long2ObjectOpenHashMap2;
    }

    public class_6956 method_39340(int i, int j) {
        int l;
        int k = BiomeCoords.fromBlock(i);
        double d = this.method_39562(k, 0, l = BiomeCoords.fromBlock(j), BlendingData::method_39344);
        if (d != Double.MAX_VALUE) {
            return new class_6956(0.0, Blender.method_39337(d));
        }
        MutableDouble mutableDouble = new MutableDouble(0.0);
        MutableDouble mutableDouble2 = new MutableDouble(0.0);
        MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
        this.field_36343.forEach((long_, blendingData) -> blendingData.method_39351(BiomeCoords.fromChunk(ChunkPos.getPackedX(long_)), BiomeCoords.fromChunk(ChunkPos.getPackedZ(long_)), (k, l, d) -> {
            double e = MathHelper.hypot(k - k, l - l);
            if (e > (double)field_35502) {
                return;
            }
            if (e < mutableDouble3.doubleValue()) {
                mutableDouble3.setValue(e);
            }
            double f = 1.0 / (e * e * e * e);
            mutableDouble2.add(d * f);
            mutableDouble.add(f);
        }));
        if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
            return new class_6956(1.0, 0.0);
        }
        double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
        double f = MathHelper.clamp(mutableDouble3.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
        f = 3.0 * f * f - 2.0 * f * f * f;
        return new class_6956(f, Blender.method_39337(e));
    }

    private static double method_39337(double d) {
        double e = 1.0;
        double f = d + 0.5;
        double g = MathHelper.floorMod(f, 8.0);
        return 1.0 * (32.0 * (f - 128.0) - 3.0 * (f - 120.0) * g + 3.0 * g * g) / (128.0 * (32.0 - 3.0 * g));
    }

    public double method_39338(DensityFunction.NoisePos noisePos, double d) {
        int k;
        int j;
        int i = BiomeCoords.fromBlock(noisePos.blockX());
        double e = this.method_39562(i, j = noisePos.blockY() / 8, k = BiomeCoords.fromBlock(noisePos.blockZ()), BlendingData::method_39345);
        if (e != Double.MAX_VALUE) {
            return e;
        }
        MutableDouble mutableDouble = new MutableDouble(0.0);
        MutableDouble mutableDouble2 = new MutableDouble(0.0);
        MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
        this.field_36344.forEach((long_, blendingData) -> blendingData.method_39346(BiomeCoords.fromChunk(ChunkPos.getPackedX(long_)), BiomeCoords.fromChunk(ChunkPos.getPackedZ(long_)), j - 1, j + 1, (l, m, n, d) -> {
            double e = MathHelper.magnitude(i - l, (j - m) * 2, k - n);
            if (e > 2.0) {
                return;
            }
            if (e < mutableDouble3.doubleValue()) {
                mutableDouble3.setValue(e);
            }
            double f = 1.0 / (e * e * e * e);
            mutableDouble2.add(d * f);
            mutableDouble.add(f);
        }));
        if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
            return d;
        }
        double f = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
        double g = MathHelper.clamp(mutableDouble3.doubleValue() / 3.0, 0.0, 1.0);
        return MathHelper.lerp(g, f, d);
    }

    private double method_39562(int i, int j, int k, class_6781 arg) {
        int l = BiomeCoords.toChunk(i);
        int m = BiomeCoords.toChunk(k);
        boolean bl = (i & 3) == 0;
        boolean bl2 = (k & 3) == 0;
        double d = this.method_39565(arg, l, m, i, j, k);
        if (d == Double.MAX_VALUE) {
            if (bl && bl2) {
                d = this.method_39565(arg, l - 1, m - 1, i, j, k);
            }
            if (d == Double.MAX_VALUE) {
                if (bl) {
                    d = this.method_39565(arg, l - 1, m, i, j, k);
                }
                if (d == Double.MAX_VALUE && bl2) {
                    d = this.method_39565(arg, l, m - 1, i, j, k);
                }
            }
        }
        return d;
    }

    private double method_39565(class_6781 arg, int i, int j, int k, int l, int m) {
        BlendingData blendingData = (BlendingData)this.field_36343.get(ChunkPos.toLong(i, j));
        if (blendingData != null) {
            return arg.get(blendingData, k - BiomeCoords.fromChunk(i), l, m - BiomeCoords.fromChunk(j));
        }
        return Double.MAX_VALUE;
    }

    public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
        return (x, y, z, noise) -> {
            RegistryEntry<Biome> registryEntry = this.blendBiome(x, z);
            if (registryEntry == null) {
                return biomeSupplier.getBiome(x, y, z, noise);
            }
            return registryEntry;
        };
    }

    @Nullable
    private RegistryEntry<Biome> blendBiome(int x, int y) {
        double d = (double)x + field_35681.sample(x, 0.0, y) * 12.0;
        double e = (double)y + field_35681.sample(y, x, 0.0) * 12.0;
        MutableDouble mutableDouble = new MutableDouble(Double.POSITIVE_INFINITY);
        MutableObject mutableObject = new MutableObject();
        this.field_36343.forEach((long_, blendingData) -> blendingData.method_40028(BiomeCoords.fromChunk(ChunkPos.getPackedX(long_)), BiomeCoords.fromChunk(ChunkPos.getPackedZ(long_)), (i, j, registryEntry) -> {
            double f = MathHelper.hypot(d - (double)i, e - (double)j);
            if (f > (double)field_35502) {
                return;
            }
            if (f < mutableDouble.doubleValue()) {
                mutableObject.setValue((Object)registryEntry);
                mutableDouble.setValue(f);
            }
        }));
        if (mutableDouble.doubleValue() == Double.POSITIVE_INFINITY) {
            return null;
        }
        double f = MathHelper.clamp(mutableDouble.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
        if (f > 0.5) {
            return null;
        }
        return (RegistryEntry)mutableObject.getValue();
    }

    public static void tickLeavesAndFluids(ChunkRegion chunkRegion, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        boolean bl = chunk.usesOldNoise();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ());
        int i = BlendingData.OLD_HEIGHT_LIMIT.getBottomY();
        int j = BlendingData.OLD_HEIGHT_LIMIT.getTopY() - 1;
        if (bl) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, i - 1, l));
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, i, l));
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, j, l));
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, j + 1, l));
                }
            }
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (chunkRegion.getChunk(chunkPos.x + direction.getOffsetX(), chunkPos.z + direction.getOffsetZ()).usesOldNoise() == bl) continue;
            int m = direction == Direction.EAST ? 15 : 0;
            int n = direction == Direction.WEST ? 0 : 15;
            int o = direction == Direction.SOUTH ? 15 : 0;
            int p = direction == Direction.NORTH ? 0 : 15;
            for (int q = m; q <= n; ++q) {
                for (int r = o; r <= p; ++r) {
                    int s = Math.min(j, chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, q, r)) + 1;
                    for (int t = i; t < s; ++t) {
                        Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, q, t, r));
                    }
                }
            }
        }
    }

    private static void tickLeavesAndFluids(Chunk chunk, BlockPos pos) {
        FluidState fluidState;
        BlockState blockState = chunk.getBlockState(pos);
        if (blockState.isIn(BlockTags.LEAVES)) {
            chunk.markBlockForPostProcessing(pos);
        }
        if (!(fluidState = chunk.getFluidState(pos)).isEmpty()) {
            chunk.markBlockForPostProcessing(pos);
        }
    }

    public static void method_39809(StructureWorldAccess structureWorldAccess, ProtoChunk protoChunk) {
        ChunkPos chunkPos = protoChunk.getPos();
        class_6831 lv = Blender.method_39815(protoChunk.usesOldNoise(), BlendingData.getAdjacentChunksWithNoise(structureWorldAccess, chunkPos.x, chunkPos.z, true));
        if (lv == null) {
            return;
        }
        CarvingMask.MaskPredicate maskPredicate = (i, j, k) -> {
            double f;
            double e;
            double d = (double)i + 0.5 + field_35681.sample(i, j, k) * 4.0;
            return lv.getDistance(d, e = (double)j + 0.5 + field_35681.sample(j, k, i) * 4.0, f = (double)k + 0.5 + field_35681.sample(k, i, j) * 4.0) < 4.0;
        };
        Stream.of(GenerationStep.Carver.values()).map(protoChunk::getOrCreateCarvingMask).forEach(carvingMask -> carvingMask.setMaskPredicate(maskPredicate));
    }

    @Nullable
    public static class_6831 method_39815(boolean bl, Set<EightWayDirection> set) {
        if (!bl && set.isEmpty()) {
            return null;
        }
        ArrayList list = Lists.newArrayList();
        if (bl) {
            list.add(Blender.method_39812(null));
        }
        set.forEach(eightWayDirection -> list.add(Blender.method_39812(eightWayDirection)));
        return (d, e, f) -> {
            double g = Double.POSITIVE_INFINITY;
            for (class_6831 lv : list) {
                double h = lv.getDistance(d, e, f);
                if (!(h < g)) continue;
                g = h;
            }
            return g;
        };
    }

    private static class_6831 method_39812(@Nullable EightWayDirection eightWayDirection) {
        double d = 0.0;
        double e = 0.0;
        if (eightWayDirection != null) {
            for (Direction direction : eightWayDirection.getDirections()) {
                d += (double)(direction.getOffsetX() * 16);
                e += (double)(direction.getOffsetZ() * 16);
            }
        }
        double f2 = d;
        double g2 = e;
        return (f, g, h) -> Blender.method_39808(f - 8.0 - f2, g - field_36223, h - 8.0 - g2, 8.0, field_36222, 8.0);
    }

    private static double method_39808(double d, double e, double f, double g, double h, double i) {
        double j = Math.abs(d) - g;
        double k = Math.abs(e) - h;
        double l = Math.abs(f) - i;
        return MathHelper.magnitude(Math.max(0.0, j), Math.max(0.0, k), Math.max(0.0, l));
    }

    static interface class_6781 {
        public double get(BlendingData var1, int var2, int var3, int var4);
    }

    public record class_6956(double alpha, double blendingOffset) {
    }

    public static interface class_6831 {
        public double getDistance(double var1, double var3, double var5);
    }
}

