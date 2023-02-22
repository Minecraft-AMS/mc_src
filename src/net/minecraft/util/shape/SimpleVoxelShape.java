/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

public final class SimpleVoxelShape
extends VoxelShape {
    protected SimpleVoxelShape(VoxelSet shape) {
        super(shape);
    }

    @Override
    protected DoubleList getPointPositions(Direction.Axis axis) {
        return new FractionalDoubleList(this.voxels.getSize(axis));
    }

    @Override
    protected int getCoordIndex(Direction.Axis axis, double coord) {
        int i = this.voxels.getSize(axis);
        return MathHelper.clamp(MathHelper.floor(coord * (double)i), -1, i);
    }
}
