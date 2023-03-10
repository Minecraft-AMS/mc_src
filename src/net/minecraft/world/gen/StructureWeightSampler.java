/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 */
package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightType;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

public class StructureWeightSampler
implements DensityFunctionTypes.class_7050 {
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
    private final ObjectList<StructurePiece> pieces;
    private final ObjectList<JigsawJunction> junctions;
    private final ObjectListIterator<StructurePiece> pieceIterator;
    private final ObjectListIterator<JigsawJunction> junctionIterator;

    protected StructureWeightSampler(StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        this.junctions = new ObjectArrayList(32);
        this.pieces = new ObjectArrayList(10);
        structureAccessor.method_41035(ChunkSectionPos.from(chunk), configuredStructureFeature -> configuredStructureFeature.field_37144).forEach(start -> {
            for (StructurePiece structurePiece : start.getChildren()) {
                if (!structurePiece.intersectsChunk(chunkPos, 12)) continue;
                if (structurePiece instanceof PoolStructurePiece) {
                    PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                    StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                    if (projection == StructurePool.Projection.RIGID) {
                        this.pieces.add((Object)poolStructurePiece);
                    }
                    for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
                        int k = jigsawJunction.getSourceX();
                        int l = jigsawJunction.getSourceZ();
                        if (k <= i - 12 || l <= j - 12 || k >= i + 15 + 12 || l >= j + 15 + 12) continue;
                        this.junctions.add((Object)jigsawJunction);
                    }
                    continue;
                }
                this.pieces.add((Object)structurePiece);
            }
        });
        this.pieceIterator = this.pieces.iterator();
        this.junctionIterator = this.junctions.iterator();
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
            StructurePiece structurePiece = (StructurePiece)this.pieceIterator.next();
            BlockBox blockBox = structurePiece.getBoundingBox();
            l = Math.max(0, Math.max(blockBox.getMinX() - i, i - blockBox.getMaxX()));
            m = j - (blockBox.getMinY() + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece)structurePiece).getGroundLevelDelta() : 0));
            int n = Math.max(0, Math.max(blockBox.getMinZ() - k, k - blockBox.getMaxZ()));
            StructureWeightType structureWeightType = structurePiece.getWeightType();
            if (structureWeightType == StructureWeightType.BURY) {
                d += StructureWeightSampler.getMagnitudeWeight(l, m, n);
                continue;
            }
            if (structureWeightType != StructureWeightType.BEARD) continue;
            d += StructureWeightSampler.getStructureWeight(l, m, n) * 0.8;
        }
        this.pieceIterator.back(this.pieces.size());
        while (this.junctionIterator.hasNext()) {
            JigsawJunction jigsawJunction = (JigsawJunction)this.junctionIterator.next();
            int o = i - jigsawJunction.getSourceX();
            l = j - jigsawJunction.getSourceGroundY();
            m = k - jigsawJunction.getSourceZ();
            d += StructureWeightSampler.getStructureWeight(o, l, m) * 0.4;
        }
        this.junctionIterator.back(this.junctions.size());
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

    private static double getStructureWeight(int x, int y, int z) {
        int i = x + 12;
        int j = y + 12;
        int k = z + 12;
        if (i < 0 || i >= 24) {
            return 0.0;
        }
        if (j < 0 || j >= 24) {
            return 0.0;
        }
        if (k < 0 || k >= 24) {
            return 0.0;
        }
        return STRUCTURE_WEIGHT_TABLE[k * 24 * 24 + i * 24 + j];
    }

    private static double calculateStructureWeight(int x, int y, int z) {
        double d = x * x + z * z;
        double e = (double)y + 0.5;
        double f = e * e;
        double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
        double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
        return h * g;
    }
}

