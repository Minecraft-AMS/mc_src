/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.math.DoubleMath
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.shape;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.DisjointPairList;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.FractionalPairList;
import net.minecraft.util.shape.IdentityPairList;
import net.minecraft.util.shape.PairList;
import net.minecraft.util.shape.SimplePairList;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.SlicedVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldView;

public final class VoxelShapes {
    private static final VoxelShape FULL_CUBE = Util.make(() -> {
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(1, 1, 1);
        ((VoxelSet)voxelSet).set(0, 0, 0, true, true);
        return new SimpleVoxelShape(voxelSet);
    });
    public static final VoxelShape UNBOUNDED = VoxelShapes.cuboid(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final VoxelShape EMPTY = new ArrayVoxelShape((VoxelSet)new BitSetVoxelSet(0, 0, 0), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}));

    public static VoxelShape empty() {
        return EMPTY;
    }

    public static VoxelShape fullCube() {
        return FULL_CUBE;
    }

    public static VoxelShape cuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return VoxelShapes.cuboid(new Box(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static VoxelShape cuboid(Box box) {
        int i = VoxelShapes.findRequiredBitResolution(box.minX, box.maxX);
        int j = VoxelShapes.findRequiredBitResolution(box.minY, box.maxY);
        int k = VoxelShapes.findRequiredBitResolution(box.minZ, box.maxZ);
        if (i < 0 || j < 0 || k < 0) {
            return new ArrayVoxelShape(VoxelShapes.FULL_CUBE.voxels, new double[]{box.minX, box.maxX}, new double[]{box.minY, box.maxY}, new double[]{box.minZ, box.maxZ});
        }
        if (i == 0 && j == 0 && k == 0) {
            return box.contains(0.5, 0.5, 0.5) ? VoxelShapes.fullCube() : VoxelShapes.empty();
        }
        int l = 1 << i;
        int m = 1 << j;
        int n = 1 << k;
        int o = (int)Math.round(box.minX * (double)l);
        int p = (int)Math.round(box.maxX * (double)l);
        int q = (int)Math.round(box.minY * (double)m);
        int r = (int)Math.round(box.maxY * (double)m);
        int s = (int)Math.round(box.minZ * (double)n);
        int t = (int)Math.round(box.maxZ * (double)n);
        BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(l, m, n, o, q, s, p, r, t);
        for (long u = (long)o; u < (long)p; ++u) {
            for (long v = (long)q; v < (long)r; ++v) {
                for (long w = (long)s; w < (long)t; ++w) {
                    bitSetVoxelSet.set((int)u, (int)v, (int)w, false, true);
                }
            }
        }
        return new SimpleVoxelShape(bitSetVoxelSet);
    }

    private static int findRequiredBitResolution(double min, double max) {
        if (min < -1.0E-7 || max > 1.0000001) {
            return -1;
        }
        for (int i = 0; i <= 3; ++i) {
            boolean bl2;
            double d = min * (double)(1 << i);
            double e = max * (double)(1 << i);
            boolean bl = Math.abs(d - Math.floor(d)) < 1.0E-7;
            boolean bl3 = bl2 = Math.abs(e - Math.floor(e)) < 1.0E-7;
            if (!bl || !bl2) continue;
            return i;
        }
        return -1;
    }

    protected static long lcm(int a, int b) {
        return (long)a * (long)(b / IntMath.gcd((int)a, (int)b));
    }

    public static VoxelShape union(VoxelShape first, VoxelShape second) {
        return VoxelShapes.combineAndSimplify(first, second, BooleanBiFunction.OR);
    }

    public static VoxelShape union(VoxelShape first, VoxelShape ... others) {
        return Arrays.stream(others).reduce(first, VoxelShapes::union);
    }

    public static VoxelShape combineAndSimplify(VoxelShape first, VoxelShape second, BooleanBiFunction function) {
        return VoxelShapes.combine(first, second, function).simplify();
    }

    public static VoxelShape combine(VoxelShape one, VoxelShape two, BooleanBiFunction function) {
        if (function.apply(false, false)) {
            throw Util.throwOrPause(new IllegalArgumentException());
        }
        if (one == two) {
            return function.apply(true, true) ? one : VoxelShapes.empty();
        }
        boolean bl = function.apply(true, false);
        boolean bl2 = function.apply(false, true);
        if (one.isEmpty()) {
            return bl2 ? two : VoxelShapes.empty();
        }
        if (two.isEmpty()) {
            return bl ? one : VoxelShapes.empty();
        }
        PairList pairList = VoxelShapes.createListPair(1, one.getPointPositions(Direction.Axis.X), two.getPointPositions(Direction.Axis.X), bl, bl2);
        PairList pairList2 = VoxelShapes.createListPair(pairList.getPairs().size() - 1, one.getPointPositions(Direction.Axis.Y), two.getPointPositions(Direction.Axis.Y), bl, bl2);
        PairList pairList3 = VoxelShapes.createListPair((pairList.getPairs().size() - 1) * (pairList2.getPairs().size() - 1), one.getPointPositions(Direction.Axis.Z), two.getPointPositions(Direction.Axis.Z), bl, bl2);
        BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.combine(one.voxels, two.voxels, pairList, pairList2, pairList3, function);
        if (pairList instanceof FractionalPairList && pairList2 instanceof FractionalPairList && pairList3 instanceof FractionalPairList) {
            return new SimpleVoxelShape(bitSetVoxelSet);
        }
        return new ArrayVoxelShape((VoxelSet)bitSetVoxelSet, pairList.getPairs(), pairList2.getPairs(), pairList3.getPairs());
    }

    public static boolean matchesAnywhere(VoxelShape shape1, VoxelShape shape2, BooleanBiFunction predicate) {
        if (predicate.apply(false, false)) {
            throw Util.throwOrPause(new IllegalArgumentException());
        }
        if (shape1 == shape2) {
            return predicate.apply(true, true);
        }
        if (shape1.isEmpty()) {
            return predicate.apply(false, !shape2.isEmpty());
        }
        if (shape2.isEmpty()) {
            return predicate.apply(!shape1.isEmpty(), false);
        }
        boolean bl = predicate.apply(true, false);
        boolean bl2 = predicate.apply(false, true);
        for (Direction.Axis axis : AxisCycleDirection.AXES) {
            if (shape1.getMax(axis) < shape2.getMin(axis) - 1.0E-7) {
                return bl || bl2;
            }
            if (!(shape2.getMax(axis) < shape1.getMin(axis) - 1.0E-7)) continue;
            return bl || bl2;
        }
        PairList pairList = VoxelShapes.createListPair(1, shape1.getPointPositions(Direction.Axis.X), shape2.getPointPositions(Direction.Axis.X), bl, bl2);
        PairList pairList2 = VoxelShapes.createListPair(pairList.getPairs().size() - 1, shape1.getPointPositions(Direction.Axis.Y), shape2.getPointPositions(Direction.Axis.Y), bl, bl2);
        PairList pairList3 = VoxelShapes.createListPair((pairList.getPairs().size() - 1) * (pairList2.getPairs().size() - 1), shape1.getPointPositions(Direction.Axis.Z), shape2.getPointPositions(Direction.Axis.Z), bl, bl2);
        return VoxelShapes.matchesAnywhere(pairList, pairList2, pairList3, shape1.voxels, shape2.voxels, predicate);
    }

    private static boolean matchesAnywhere(PairList mergedX, PairList mergedY, PairList mergedZ, VoxelSet shape1, VoxelSet shape2, BooleanBiFunction predicate) {
        return !mergedX.forEachPair((x1, x2, index1) -> mergedY.forEachPair((y1, y2, index2) -> mergedZ.forEachPair((z1, z2, index3) -> !predicate.apply(shape1.inBoundsAndContains(x1, y1, z1), shape2.inBoundsAndContains(x2, y2, z2)))));
    }

    public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> shapes, double maxDist) {
        Iterator iterator = shapes.iterator();
        while (iterator.hasNext()) {
            if (Math.abs(maxDist) < 1.0E-7) {
                return 0.0;
            }
            maxDist = ((VoxelShape)iterator.next()).calculateMaxDistance(axis, box, maxDist);
        }
        return maxDist;
    }

    public static double calculatePushVelocity(Direction.Axis axis, Box box, WorldView world, double initial, ShapeContext context, Stream<VoxelShape> shapes) {
        return VoxelShapes.calculatePushVelocity(box, world, initial, context, AxisCycleDirection.between(axis, Direction.Axis.Z), shapes);
    }

    private static double calculatePushVelocity(Box box, WorldView world, double initial, ShapeContext context, AxisCycleDirection direction, Stream<VoxelShape> shapes) {
        if (box.getXLength() < 1.0E-6 || box.getYLength() < 1.0E-6 || box.getZLength() < 1.0E-6) {
            return initial;
        }
        if (Math.abs(initial) < 1.0E-7) {
            return 0.0;
        }
        AxisCycleDirection axisCycleDirection = direction.opposite();
        Direction.Axis axis = axisCycleDirection.cycle(Direction.Axis.X);
        Direction.Axis axis2 = axisCycleDirection.cycle(Direction.Axis.Y);
        Direction.Axis axis3 = axisCycleDirection.cycle(Direction.Axis.Z);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = MathHelper.floor(box.getMin(axis) - 1.0E-7) - 1;
        int j = MathHelper.floor(box.getMax(axis) + 1.0E-7) + 1;
        int k = MathHelper.floor(box.getMin(axis2) - 1.0E-7) - 1;
        int l = MathHelper.floor(box.getMax(axis2) + 1.0E-7) + 1;
        double d = box.getMin(axis3) - 1.0E-7;
        double e = box.getMax(axis3) + 1.0E-7;
        boolean bl = initial > 0.0;
        int m = bl ? MathHelper.floor(box.getMax(axis3) - 1.0E-7) - 1 : MathHelper.floor(box.getMin(axis3) + 1.0E-7) + 1;
        int n = VoxelShapes.clamp(initial, d, e);
        int o = bl ? 1 : -1;
        int p = m;
        while (bl ? p <= n : p >= n) {
            for (int q = i; q <= j; ++q) {
                for (int r = k; r <= l; ++r) {
                    int s = 0;
                    if (q == i || q == j) {
                        ++s;
                    }
                    if (r == k || r == l) {
                        ++s;
                    }
                    if (p == m || p == n) {
                        ++s;
                    }
                    if (s >= 3) continue;
                    mutable.set(axisCycleDirection, q, r, p);
                    BlockState blockState = world.getBlockState(mutable);
                    if (s == 1 && !blockState.exceedsCube() || s == 2 && !blockState.isOf(Blocks.MOVING_PISTON)) continue;
                    initial = blockState.getCollisionShape(world, mutable, context).calculateMaxDistance(axis3, box.offset(-mutable.getX(), -mutable.getY(), -mutable.getZ()), initial);
                    if (Math.abs(initial) < 1.0E-7) {
                        return 0.0;
                    }
                    n = VoxelShapes.clamp(initial, d, e);
                }
            }
            p += o;
        }
        double[] ds = new double[]{initial};
        shapes.forEach(voxelShape -> {
            ds[0] = voxelShape.calculateMaxDistance(axis3, box, ds[0]);
        });
        return ds[0];
    }

    private static int clamp(double value, double min, double max) {
        return value > 0.0 ? MathHelper.floor(max + value) + 1 : MathHelper.floor(min + value) - 1;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean isSideCovered(VoxelShape shape, VoxelShape neighbor, Direction direction) {
        if (shape == VoxelShapes.fullCube() && neighbor == VoxelShapes.fullCube()) {
            return true;
        }
        if (neighbor.isEmpty()) {
            return false;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getDirection();
        VoxelShape voxelShape = axisDirection == Direction.AxisDirection.POSITIVE ? shape : neighbor;
        VoxelShape voxelShape2 = axisDirection == Direction.AxisDirection.POSITIVE ? neighbor : shape;
        BooleanBiFunction booleanBiFunction = axisDirection == Direction.AxisDirection.POSITIVE ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
        return DoubleMath.fuzzyEquals((double)voxelShape.getMax(axis), (double)1.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)voxelShape2.getMin(axis), (double)0.0, (double)1.0E-7) && !VoxelShapes.matchesAnywhere(new SlicedVoxelShape(voxelShape, axis, voxelShape.voxels.getSize(axis) - 1), new SlicedVoxelShape(voxelShape2, axis, 0), booleanBiFunction);
    }

    public static VoxelShape extrudeFace(VoxelShape shape, Direction direction) {
        int i;
        boolean bl;
        if (shape == VoxelShapes.fullCube()) {
            return VoxelShapes.fullCube();
        }
        Direction.Axis axis = direction.getAxis();
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            bl = DoubleMath.fuzzyEquals((double)shape.getMax(axis), (double)1.0, (double)1.0E-7);
            i = shape.voxels.getSize(axis) - 1;
        } else {
            bl = DoubleMath.fuzzyEquals((double)shape.getMin(axis), (double)0.0, (double)1.0E-7);
            i = 0;
        }
        if (!bl) {
            return VoxelShapes.empty();
        }
        return new SlicedVoxelShape(shape, axis, i);
    }

    public static boolean adjacentSidesCoverSquare(VoxelShape one, VoxelShape two, Direction direction) {
        VoxelShape voxelShape2;
        if (one == VoxelShapes.fullCube() || two == VoxelShapes.fullCube()) {
            return true;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getDirection();
        VoxelShape voxelShape = axisDirection == Direction.AxisDirection.POSITIVE ? one : two;
        VoxelShape voxelShape3 = voxelShape2 = axisDirection == Direction.AxisDirection.POSITIVE ? two : one;
        if (!DoubleMath.fuzzyEquals((double)voxelShape.getMax(axis), (double)1.0, (double)1.0E-7)) {
            voxelShape = VoxelShapes.empty();
        }
        if (!DoubleMath.fuzzyEquals((double)voxelShape2.getMin(axis), (double)0.0, (double)1.0E-7)) {
            voxelShape2 = VoxelShapes.empty();
        }
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), VoxelShapes.combine(new SlicedVoxelShape(voxelShape, axis, voxelShape.voxels.getSize(axis) - 1), new SlicedVoxelShape(voxelShape2, axis, 0), BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }

    public static boolean unionCoversFullCube(VoxelShape one, VoxelShape two) {
        if (one == VoxelShapes.fullCube() || two == VoxelShapes.fullCube()) {
            return true;
        }
        if (one.isEmpty() && two.isEmpty()) {
            return false;
        }
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), VoxelShapes.combine(one, two, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }

    @VisibleForTesting
    protected static PairList createListPair(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond) {
        long l;
        int i = first.size() - 1;
        int j = second.size() - 1;
        if (first instanceof FractionalDoubleList && second instanceof FractionalDoubleList && (long)size * (l = VoxelShapes.lcm(i, j)) <= 256L) {
            return new FractionalPairList(i, j);
        }
        if (first.getDouble(i) < second.getDouble(0) - 1.0E-7) {
            return new DisjointPairList(first, second, false);
        }
        if (second.getDouble(j) < first.getDouble(0) - 1.0E-7) {
            return new DisjointPairList(second, first, true);
        }
        if (i == j && Objects.equals(first, second)) {
            if (first instanceof IdentityPairList) {
                return (PairList)first;
            }
            if (second instanceof IdentityPairList) {
                return (PairList)second;
            }
            return new IdentityPairList(first);
        }
        return new SimplePairList(first, second, includeFirst, includeSecond);
    }

    public static interface BoxConsumer {
        public void consume(double var1, double var3, double var5, double var7, double var9, double var11);
    }
}

