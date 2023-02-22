/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Doubles
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.chunk;

import com.google.common.primitives.Doubles;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class BlendingData {
    private static final double field_35514 = 0.1;
    protected static final HeightLimitView OLD_HEIGHT_LIMIT = new HeightLimitView(){

        @Override
        public int getHeight() {
            return 256;
        }

        @Override
        public int getBottomY() {
            return 0;
        }
    };
    protected static final int field_36280 = 4;
    protected static final int field_35511 = 8;
    protected static final int field_36281 = 2;
    private static final int field_35516 = 2;
    private static final int field_35683 = BiomeCoords.fromBlock(16);
    private static final int field_35684 = field_35683 - 1;
    private static final int field_35685 = field_35683;
    private static final int field_35686 = 2 * field_35684 + 1;
    private static final int field_35687 = 2 * field_35685 + 1;
    private static final int field_35518 = field_35686 + field_35687;
    private static final int field_35688 = field_35683 + 1;
    private static final List<Block> SURFACE_BLOCKS = List.of(Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT);
    protected static final double field_35513 = Double.MAX_VALUE;
    private final boolean oldNoise;
    private boolean field_35690;
    private final double[] heights;
    private final List<RegistryEntry<Biome>> field_36345;
    private final transient double[][] field_35693;
    private final transient double[] field_35694;
    private static final Codec<double[]> field_35695 = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
    public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("old_noise").forGetter(BlendingData::usesOldNoise), (App)field_35695.optionalFieldOf("heights").forGetter(blendingData -> DoubleStream.of(blendingData.heights).anyMatch(d -> d != Double.MAX_VALUE) ? Optional.of(blendingData.heights) : Optional.empty())).apply((Applicative)instance, BlendingData::new)).comapFlatMap(BlendingData::method_39573, Function.identity());

    private static DataResult<BlendingData> method_39573(BlendingData blendingData) {
        if (blendingData.heights.length != field_35518) {
            return DataResult.error((String)("heights has to be of length " + field_35518));
        }
        return DataResult.success((Object)blendingData);
    }

    private BlendingData(boolean oldNoise, Optional<double[]> optional) {
        this.oldNoise = oldNoise;
        this.heights = optional.orElse(Util.make(new double[field_35518], ds -> Arrays.fill(ds, Double.MAX_VALUE)));
        this.field_35693 = new double[field_35518][];
        this.field_35694 = new double[field_35688 * field_35688];
        ObjectArrayList objectArrayList = new ObjectArrayList(field_35518);
        objectArrayList.size(field_35518);
        this.field_36345 = objectArrayList;
    }

    public boolean usesOldNoise() {
        return this.oldNoise;
    }

    @Nullable
    public static BlendingData getBlendingData(ChunkRegion chunkRegion, int chunkX, int chunkZ) {
        Chunk chunk = chunkRegion.getChunk(chunkX, chunkZ);
        BlendingData blendingData = chunk.getBlendingData();
        if (blendingData == null || !blendingData.usesOldNoise()) {
            return null;
        }
        blendingData.method_39572(chunk, BlendingData.getAdjacentChunksWithNoise(chunkRegion, chunkX, chunkZ, false));
        return blendingData;
    }

    public static Set<EightWayDirection> getAdjacentChunksWithNoise(StructureWorldAccess access, int chunkX, int chunkZ, boolean newNoise) {
        EnumSet<EightWayDirection> set = EnumSet.noneOf(EightWayDirection.class);
        for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
            int i = chunkX;
            int j = chunkZ;
            for (Direction direction : eightWayDirection.getDirections()) {
                i += direction.getOffsetX();
                j += direction.getOffsetZ();
            }
            if (access.getChunk(i, j).usesOldNoise() != newNoise) continue;
            set.add(eightWayDirection);
        }
        return set;
    }

    private void method_39572(Chunk chunk, Set<EightWayDirection> set) {
        int i;
        if (this.field_35690) {
            return;
        }
        Arrays.fill(this.field_35694, 1.0);
        if (set.contains((Object)EightWayDirection.NORTH) || set.contains((Object)EightWayDirection.WEST) || set.contains((Object)EightWayDirection.NORTH_WEST)) {
            this.method_39347(BlendingData.method_39578(0, 0), chunk, 0, 0);
        }
        if (set.contains((Object)EightWayDirection.NORTH)) {
            for (i = 1; i < field_35683; ++i) {
                this.method_39347(BlendingData.method_39578(i, 0), chunk, 4 * i, 0);
            }
        }
        if (set.contains((Object)EightWayDirection.WEST)) {
            for (i = 1; i < field_35683; ++i) {
                this.method_39347(BlendingData.method_39578(0, i), chunk, 0, 4 * i);
            }
        }
        if (set.contains((Object)EightWayDirection.EAST)) {
            for (i = 1; i < field_35683; ++i) {
                this.method_39347(BlendingData.method_39582(field_35685, i), chunk, 15, 4 * i);
            }
        }
        if (set.contains((Object)EightWayDirection.SOUTH)) {
            for (i = 0; i < field_35683; ++i) {
                this.method_39347(BlendingData.method_39582(i, field_35685), chunk, 4 * i, 15);
            }
        }
        if (set.contains((Object)EightWayDirection.EAST) && set.contains((Object)EightWayDirection.NORTH_EAST)) {
            this.method_39347(BlendingData.method_39582(field_35685, 0), chunk, 15, 0);
        }
        if (set.contains((Object)EightWayDirection.EAST) && set.contains((Object)EightWayDirection.SOUTH) && set.contains((Object)EightWayDirection.SOUTH_EAST)) {
            this.method_39347(BlendingData.method_39582(field_35685, field_35685), chunk, 15, 15);
        }
        this.field_35690 = true;
    }

    private void method_39347(int index, Chunk chunk, int x, int z) {
        if (this.heights[index] == Double.MAX_VALUE) {
            this.heights[index] = BlendingData.getSurfaceHeight(chunk, x, z);
        }
        this.field_35693[index] = BlendingData.method_39354(chunk, x, z, MathHelper.floor(this.heights[index]));
        this.field_36345.set(index, chunk.getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(MathHelper.floor(this.heights[index])), BiomeCoords.fromBlock(z)));
    }

    private static int getSurfaceHeight(Chunk chunk, int x, int z) {
        int i = chunk.hasHeightmap(Heightmap.Type.WORLD_SURFACE_WG) ? Math.min(chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1, OLD_HEIGHT_LIMIT.getTopY()) : OLD_HEIGHT_LIMIT.getTopY();
        int j = OLD_HEIGHT_LIMIT.getBottomY();
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, i, z);
        while (mutable.getY() > j) {
            mutable.move(Direction.DOWN);
            if (!SURFACE_BLOCKS.contains(chunk.getBlockState(mutable).getBlock())) continue;
            return mutable.getY();
        }
        return j;
    }

    private static double method_39905(Chunk chunk, BlockPos.Mutable mutable) {
        return BlendingData.isCollidableAndNotTreeAt(chunk, mutable.move(Direction.DOWN)) ? 1.0 : -1.0;
    }

    private static double method_39906(Chunk chunk, BlockPos.Mutable mutable) {
        double d = 0.0;
        for (int i = 0; i < 7; ++i) {
            d += BlendingData.method_39905(chunk, mutable);
        }
        return d;
    }

    private static double[] method_39354(Chunk chunk, int x, int z, int i) {
        double f;
        double e;
        int j;
        double[] ds = new double[BlendingData.method_39576()];
        Arrays.fill(ds, -1.0);
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, OLD_HEIGHT_LIMIT.getTopY(), z);
        double d = BlendingData.method_39906(chunk, mutable);
        for (j = ds.length - 2; j >= 0; --j) {
            e = BlendingData.method_39905(chunk, mutable);
            f = BlendingData.method_39906(chunk, mutable);
            ds[j] = (d + e + f) / 15.0;
            d = f;
        }
        j = MathHelper.floorDiv(i, 8);
        if (j >= 1 && j < ds.length) {
            e = ((double)i + 0.5) % 8.0 / 8.0;
            f = (1.0 - e) / e;
            double g = Math.max(f, 1.0) * 0.25;
            ds[j] = -f / g;
            ds[j - 1] = 1.0 / g;
        }
        return ds;
    }

    private static boolean isCollidableAndNotTreeAt(Chunk chunk, BlockPos pos) {
        BlockState blockState = chunk.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        if (blockState.isIn(BlockTags.LEAVES)) {
            return false;
        }
        if (blockState.isIn(BlockTags.LOGS)) {
            return false;
        }
        if (blockState.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || blockState.isOf(Blocks.RED_MUSHROOM_BLOCK)) {
            return false;
        }
        return !blockState.getCollisionShape(chunk, pos).isEmpty();
    }

    protected double method_39344(int i, int j, int k) {
        if (i == field_35685 || k == field_35685) {
            return this.heights[BlendingData.method_39582(i, k)];
        }
        if (i == 0 || k == 0) {
            return this.heights[BlendingData.method_39578(i, k)];
        }
        return Double.MAX_VALUE;
    }

    private static double method_39575(@Nullable double[] ds, int i) {
        if (ds == null) {
            return Double.MAX_VALUE;
        }
        int j = i - BlendingData.method_39581();
        if (j < 0 || j >= ds.length) {
            return Double.MAX_VALUE;
        }
        return ds[j] * 0.1;
    }

    protected double method_39345(int i, int j, int k) {
        if (j == BlendingData.method_39583()) {
            return this.field_35694[this.method_39569(i, k)] * 0.1;
        }
        if (i == field_35685 || k == field_35685) {
            return BlendingData.method_39575(this.field_35693[BlendingData.method_39582(i, k)], j);
        }
        if (i == 0 || k == 0) {
            return BlendingData.method_39575(this.field_35693[BlendingData.method_39578(i, k)], j);
        }
        return Double.MAX_VALUE;
    }

    protected void method_40028(int i, int j, class_6853 arg) {
        for (int k = 0; k < this.field_36345.size(); ++k) {
            RegistryEntry<Biome> registryEntry = this.field_36345.get(k);
            if (registryEntry == null) continue;
            arg.consume(i + BlendingData.method_39343(k), j + BlendingData.method_39352(k), registryEntry);
        }
    }

    protected void method_39351(int i, int j, class_6751 arg) {
        for (int k = 0; k < this.heights.length; ++k) {
            double d = this.heights[k];
            if (d == Double.MAX_VALUE) continue;
            arg.consume(i + BlendingData.method_39343(k), j + BlendingData.method_39352(k), d);
        }
    }

    protected void method_39346(int i, int j, int k, int l, class_6750 arg) {
        int m = BlendingData.method_39581();
        int n = Math.max(0, k - m);
        int o = Math.min(BlendingData.method_39576(), l - m);
        for (int p = 0; p < this.field_35693.length; ++p) {
            double[] ds = this.field_35693[p];
            if (ds == null) continue;
            int q = i + BlendingData.method_39343(p);
            int r = j + BlendingData.method_39352(p);
            for (int s = n; s < o; ++s) {
                arg.consume(q, s + m, r, ds[s] * 0.1);
            }
        }
    }

    private int method_39569(int i, int j) {
        return i * field_35688 + j;
    }

    private static int method_39576() {
        return OLD_HEIGHT_LIMIT.countVerticalSections() * 2;
    }

    private static int method_39581() {
        return BlendingData.method_39583() + 1;
    }

    private static int method_39583() {
        return OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2;
    }

    private static int method_39578(int i, int j) {
        return field_35684 - i + j;
    }

    private static int method_39582(int i, int j) {
        return field_35686 + i + field_35685 - j;
    }

    private static int method_39343(int i) {
        if (i < field_35686) {
            return BlendingData.method_39355(field_35684 - i);
        }
        int j = i - field_35686;
        return field_35685 - BlendingData.method_39355(field_35685 - j);
    }

    private static int method_39352(int i) {
        if (i < field_35686) {
            return BlendingData.method_39355(i - field_35684);
        }
        int j = i - field_35686;
        return field_35685 - BlendingData.method_39355(j - field_35685);
    }

    private static int method_39355(int i) {
        return i & ~(i >> 31);
    }

    protected static interface class_6853 {
        public void consume(int var1, int var2, RegistryEntry<Biome> var3);
    }

    protected static interface class_6751 {
        public void consume(int var1, int var2, double var3);
    }

    protected static interface class_6750 {
        public void consume(int var1, int var2, int var3, double var4);
    }
}

