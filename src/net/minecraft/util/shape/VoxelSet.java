/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.shape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;

public abstract class VoxelSet {
    private static final Direction.Axis[] AXES = Direction.Axis.values();
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;

    protected VoxelSet(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public boolean inBoundsAndContains(AxisCycleDirection cycle, int x, int y, int z) {
        return this.inBoundsAndContains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }

    public boolean inBoundsAndContains(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) {
            return false;
        }
        if (x >= this.xSize || y >= this.ySize || z >= this.zSize) {
            return false;
        }
        return this.contains(x, y, z);
    }

    public boolean contains(AxisCycleDirection cycle, int x, int y, int z) {
        return this.contains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }

    public abstract boolean contains(int var1, int var2, int var3);

    public abstract void set(int var1, int var2, int var3, boolean var4, boolean var5);

    public boolean isEmpty() {
        for (Direction.Axis axis : AXES) {
            if (this.getMin(axis) < this.getMax(axis)) continue;
            return true;
        }
        return false;
    }

    public abstract int getMin(Direction.Axis var1);

    public abstract int getMax(Direction.Axis var1);

    @Environment(value=EnvType.CLIENT)
    public int method_1043(Direction.Axis axis, int i, int j) {
        int k = this.getSize(axis);
        if (i < 0 || j < 0) {
            return k;
        }
        Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
        if (i >= this.getSize(axis2) || j >= this.getSize(axis3)) {
            return k;
        }
        AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);
        for (int l = 0; l < k; ++l) {
            if (!this.contains(axisCycleDirection, l, i, j)) continue;
            return l;
        }
        return k;
    }

    @Environment(value=EnvType.CLIENT)
    public int method_1058(Direction.Axis axis, int i, int j) {
        if (i < 0 || j < 0) {
            return 0;
        }
        Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
        if (i >= this.getSize(axis2) || j >= this.getSize(axis3)) {
            return 0;
        }
        int k = this.getSize(axis);
        AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);
        for (int l = k - 1; l >= 0; --l) {
            if (!this.contains(axisCycleDirection, l, i, j)) continue;
            return l + 1;
        }
        return 0;
    }

    public int getSize(Direction.Axis axis) {
        return axis.choose(this.xSize, this.ySize, this.zSize);
    }

    public int getXSize() {
        return this.getSize(Direction.Axis.X);
    }

    public int getYSize() {
        return this.getSize(Direction.Axis.Y);
    }

    public int getZSize() {
        return this.getSize(Direction.Axis.Z);
    }

    @Environment(value=EnvType.CLIENT)
    public void forEachEdge(class_253 arg, boolean bl) {
        this.forEachEdge(arg, AxisCycleDirection.NONE, bl);
        this.forEachEdge(arg, AxisCycleDirection.FORWARD, bl);
        this.forEachEdge(arg, AxisCycleDirection.BACKWARD, bl);
    }

    @Environment(value=EnvType.CLIENT)
    private void forEachEdge(class_253 arg, AxisCycleDirection direction, boolean bl) {
        AxisCycleDirection axisCycleDirection = direction.opposite();
        int i = this.getSize(axisCycleDirection.cycle(Direction.Axis.X));
        int j = this.getSize(axisCycleDirection.cycle(Direction.Axis.Y));
        int k = this.getSize(axisCycleDirection.cycle(Direction.Axis.Z));
        for (int l = 0; l <= i; ++l) {
            for (int m = 0; m <= j; ++m) {
                int n = -1;
                for (int o = 0; o <= k; ++o) {
                    int p = 0;
                    int q = 0;
                    for (int r = 0; r <= 1; ++r) {
                        for (int s = 0; s <= 1; ++s) {
                            if (!this.inBoundsAndContains(axisCycleDirection, l + r - 1, m + s - 1, o)) continue;
                            ++p;
                            q ^= r ^ s;
                        }
                    }
                    if (p == 1 || p == 3 || p == 2 && !(q & true)) {
                        if (bl) {
                            if (n != -1) continue;
                            n = o;
                            continue;
                        }
                        arg.consume(axisCycleDirection.choose(l, m, o, Direction.Axis.X), axisCycleDirection.choose(l, m, o, Direction.Axis.Y), axisCycleDirection.choose(l, m, o, Direction.Axis.Z), axisCycleDirection.choose(l, m, o + 1, Direction.Axis.X), axisCycleDirection.choose(l, m, o + 1, Direction.Axis.Y), axisCycleDirection.choose(l, m, o + 1, Direction.Axis.Z));
                        continue;
                    }
                    if (n == -1) continue;
                    arg.consume(axisCycleDirection.choose(l, m, n, Direction.Axis.X), axisCycleDirection.choose(l, m, n, Direction.Axis.Y), axisCycleDirection.choose(l, m, n, Direction.Axis.Z), axisCycleDirection.choose(l, m, o, Direction.Axis.X), axisCycleDirection.choose(l, m, o, Direction.Axis.Y), axisCycleDirection.choose(l, m, o, Direction.Axis.Z));
                    n = -1;
                }
            }
        }
    }

    protected boolean isColumnFull(int minZ, int maxZ, int x, int y) {
        for (int i = minZ; i < maxZ; ++i) {
            if (this.inBoundsAndContains(x, y, i)) continue;
            return false;
        }
        return true;
    }

    protected void setColumn(int minZ, int maxZ, int x, int y, boolean included) {
        for (int i = minZ; i < maxZ; ++i) {
            this.set(x, y, i, false, included);
        }
    }

    protected boolean isRectangleFull(int minX, int maxX, int minZ, int maxZ, int y) {
        for (int i = minX; i < maxX; ++i) {
            if (this.isColumnFull(minZ, maxZ, i, y)) continue;
            return false;
        }
        return true;
    }

    public void forEachBox(class_253 consumer, boolean largest) {
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(this);
        for (int i = 0; i <= this.xSize; ++i) {
            for (int j = 0; j <= this.ySize; ++j) {
                int k = -1;
                for (int l = 0; l <= this.zSize; ++l) {
                    int q;
                    if (voxelSet.inBoundsAndContains(i, j, l)) {
                        if (largest) {
                            if (k != -1) continue;
                            k = l;
                            continue;
                        }
                        consumer.consume(i, j, l, i + 1, j + 1, l + 1);
                        continue;
                    }
                    if (k == -1) continue;
                    int m = i;
                    int n = i;
                    int o = j;
                    int p = j;
                    ((VoxelSet)voxelSet).setColumn(k, l, i, j, false);
                    while (((VoxelSet)voxelSet).isColumnFull(k, l, m - 1, o)) {
                        ((VoxelSet)voxelSet).setColumn(k, l, m - 1, o, false);
                        --m;
                    }
                    while (((VoxelSet)voxelSet).isColumnFull(k, l, n + 1, o)) {
                        ((VoxelSet)voxelSet).setColumn(k, l, n + 1, o, false);
                        ++n;
                    }
                    while (voxelSet.isRectangleFull(m, n + 1, k, l, o - 1)) {
                        for (q = m; q <= n; ++q) {
                            ((VoxelSet)voxelSet).setColumn(k, l, q, o - 1, false);
                        }
                        --o;
                    }
                    while (voxelSet.isRectangleFull(m, n + 1, k, l, p + 1)) {
                        for (q = m; q <= n; ++q) {
                            ((VoxelSet)voxelSet).setColumn(k, l, q, p + 1, false);
                        }
                        ++p;
                    }
                    consumer.consume(m, o, k, n + 1, p + 1, l);
                    k = -1;
                }
            }
        }
    }

    public void method_1046(class_252 arg) {
        this.method_1061(arg, AxisCycleDirection.NONE);
        this.method_1061(arg, AxisCycleDirection.FORWARD);
        this.method_1061(arg, AxisCycleDirection.BACKWARD);
    }

    private void method_1061(class_252 arg, AxisCycleDirection axisCycleDirection) {
        AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
        Direction.Axis axis = axisCycleDirection2.cycle(Direction.Axis.Z);
        int i = this.getSize(axisCycleDirection2.cycle(Direction.Axis.X));
        int j = this.getSize(axisCycleDirection2.cycle(Direction.Axis.Y));
        int k = this.getSize(axis);
        Direction direction = Direction.from(axis, Direction.AxisDirection.NEGATIVE);
        Direction direction2 = Direction.from(axis, Direction.AxisDirection.POSITIVE);
        for (int l = 0; l < i; ++l) {
            for (int m = 0; m < j; ++m) {
                boolean bl = false;
                for (int n = 0; n <= k; ++n) {
                    boolean bl2;
                    boolean bl3 = bl2 = n != k && this.contains(axisCycleDirection2, l, m, n);
                    if (!bl && bl2) {
                        arg.consume(direction, axisCycleDirection2.choose(l, m, n, Direction.Axis.X), axisCycleDirection2.choose(l, m, n, Direction.Axis.Y), axisCycleDirection2.choose(l, m, n, Direction.Axis.Z));
                    }
                    if (bl && !bl2) {
                        arg.consume(direction2, axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.X), axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.Y), axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.Z));
                    }
                    bl = bl2;
                }
            }
        }
    }

    public static interface class_252 {
        public void consume(Direction var1, int var2, int var3, int var4);
    }

    public static interface class_253 {
        public void consume(int var1, int var2, int var3, int var4, int var5, int var6);
    }
}

