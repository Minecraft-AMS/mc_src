/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 */
package net.minecraft.world.gen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

public class StructureWeightSampler
implements DensityFunctionTypes.Beardifying {
    public static final int field_31461 = 12;
    private static final int field_31462 = 24;
    private static final float[] STRUCTURE_WEIGHT_TABLE = Util.make(new float[13824], array -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    array[i * 24 * 24 + j * 24 + k] = (float)StructureWeightSampler.calculateStructureWeight(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private final ObjectListIterator<class_7301> pieceIterator;
    private final ObjectListIterator<JigsawJunction> junctionIterator;

    public static StructureWeightSampler createStructureWeightSampler(StructureAccessor world, ChunkPos pos) {
        int i = pos.getStartX();
        int j = pos.getStartZ();
        ObjectArrayList objectList = new ObjectArrayList(10);
        ObjectArrayList objectList2 = new ObjectArrayList(32);
        world.getStructureStarts(pos, structure -> structure.getTerrainAdaptation() != StructureTerrainAdaptation.NONE).forEach(arg_0 -> StructureWeightSampler.method_42694(pos, (ObjectList)objectList, i, j, (ObjectList)objectList2, arg_0));
        return new StructureWeightSampler((ObjectListIterator<class_7301>)objectList.iterator(), (ObjectListIterator<JigsawJunction>)objectList2.iterator());
    }

    @VisibleForTesting
    public StructureWeightSampler(ObjectListIterator<class_7301> objectListIterator, ObjectListIterator<JigsawJunction> objectListIterator2) {
        this.pieceIterator = objectListIterator;
        this.junctionIterator = objectListIterator2;
    }

    @Override
    public double sample(DensityFunction.NoisePos pos) {
        int m;
        int l;
        int i = pos.blockX();
        int j = pos.blockY();
        int k = pos.blockZ();
        double d = 0.0;
        while (this.pieceIterator.hasNext()) {
            class_7301 lv = (class_7301)this.pieceIterator.next();
            BlockBox blockBox = lv.box();
            l = lv.groundLevelDelta();
            m = Math.max(0, Math.max(blockBox.getMinX() - i, i - blockBox.getMaxX()));
            int n = Math.max(0, Math.max(blockBox.getMinZ() - k, k - blockBox.getMaxZ()));
            int o = blockBox.getMinY() + l;
            int p = j - o;
            int q = switch (lv.terrainAdjustment()) {
                default -> throw new IncompatibleClassChangeError();
                case StructureTerrainAdaptation.NONE -> 0;
                case StructureTerrainAdaptation.BURY, StructureTerrainAdaptation.BEARD_THIN -> p;
                case StructureTerrainAdaptation.BEARD_BOX -> Math.max(0, Math.max(o - j, j - blockBox.getMaxY()));
            };
            d += (switch (lv.terrainAdjustment()) {
                default -> throw new IncompatibleClassChangeError();
                case StructureTerrainAdaptation.NONE -> 0.0;
                case StructureTerrainAdaptation.BURY -> StructureWeightSampler.getMagnitudeWeight(m, q, n);
                case StructureTerrainAdaptation.BEARD_THIN, StructureTerrainAdaptation.BEARD_BOX -> StructureWeightSampler.getStructureWeight(m, q, n, p) * 0.8;
            });
        }
        this.pieceIterator.back(Integer.MAX_VALUE);
        while (this.junctionIterator.hasNext()) {
            JigsawJunction jigsawJunction = (JigsawJunction)this.junctionIterator.next();
            int r = i - jigsawJunction.getSourceX();
            l = j - jigsawJunction.getSourceGroundY();
            m = k - jigsawJunction.getSourceZ();
            d += StructureWeightSampler.getStructureWeight(r, l, m, l) * 0.4;
        }
        this.junctionIterator.back(Integer.MAX_VALUE);
        return d;
    }

    @Override
    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }

    private static double getMagnitudeWeight(int x, int y, int z) {
        double d = MathHelper.magnitude(x, (double)y / 2.0, z);
        return MathHelper.clampedLerpFromProgress(d, 0.0, 6.0, 1.0, 0.0);
    }

    private static double getStructureWeight(int x, int y, int z, int i) {
        int j = x + 12;
        int k = y + 12;
        int l = z + 12;
        if (!(StructureWeightSampler.method_42692(j) && StructureWeightSampler.method_42692(k) && StructureWeightSampler.method_42692(l))) {
            return 0.0;
        }
        double d = (double)i + 0.5;
        double e = MathHelper.squaredMagnitude(x, d, z);
        double f = -d * MathHelper.fastInverseSqrt(e / 2.0) / 2.0;
        return f * (double)STRUCTURE_WEIGHT_TABLE[l * 24 * 24 + j * 24 + k];
    }

    private static boolean method_42692(int i) {
        return i >= 0 && i < 24;
    }

    private static double calculateStructureWeight(int x, int y, int z) {
        return StructureWeightSampler.method_42693(x, (double)y + 0.5, z);
    }

    private static double method_42693(int i, double d, int j) {
        double e = MathHelper.squaredMagnitude(i, d, j);
        double f = Math.pow(Math.E, -e / 16.0);
        return f;
    }

    private static /* synthetic */ void method_42694(ChunkPos pos, ObjectList objectList, int startX, int startZ, ObjectList objectList2, StructureStart start) {
        StructureTerrainAdaptation structureTerrainAdaptation = start.getStructure().getTerrainAdaptation();
        for (StructurePiece structurePiece : start.getChildren()) {
            if (!structurePiece.intersectsChunk(pos, 12)) continue;
            if (structurePiece instanceof PoolStructurePiece) {
                PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                if (projection == StructurePool.Projection.RIGID) {
                    objectList.add((Object)new class_7301(poolStructurePiece.getBoundingBox(), structureTerrainAdaptation, poolStructurePiece.getGroundLevelDelta()));
                }
                for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
                    int i = jigsawJunction.getSourceX();
                    int j = jigsawJunction.getSourceZ();
                    if (i <= startX - 12 || j <= startZ - 12 || i >= startX + 15 + 12 || j >= startZ + 15 + 12) continue;
                    objectList2.add((Object)jigsawJunction);
                }
                continue;
            }
            objectList.add((Object)new class_7301(structurePiece.getBoundingBox(), structureTerrainAdaptation, 0));
        }
    }

    @VisibleForTesting
    public record class_7301(BlockBox box, StructureTerrainAdaptation terrainAdjustment, int groundLevelDelta) {
    }
}

