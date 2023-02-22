/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.CroppedVoxelSet;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

public class SlicedVoxelShape
extends VoxelShape {
    private final VoxelShape shape;
    private final Direction.Axis axis;
    private static final DoubleList POINTS = new FractionalDoubleList(1);

    public SlicedVoxelShape(VoxelShape shape, Direction.Axis axis, int sliceWidth) {
        super(SlicedVoxelShape.createVoxelSet(shape.voxels, axis, sliceWidth));
        this.shape = shape;
        this.axis = axis;
    }

    private static VoxelSet createVoxelSet(VoxelSet voxelSet, Direction.Axis axis, int sliceWidth) {
        return new CroppedVoxelSet(voxelSet, axis.choose(sliceWidth, 0, 0), axis.choose(0, sliceWidth, 0), axis.choose(0, 0, sliceWidth), axis.choose(sliceWidth + 1, voxelSet.xSize, voxelSet.xSize), axis.choose(voxelSet.ySize, sliceWidth + 1, voxelSet.ySize), axis.choose(voxelSet.zSize, voxelSet.zSize, sliceWidth + 1));
    }

    @Override
    protected DoubleList getPointPositions(Direction.Axis axis) {
        if (axis == this.axis) {
            return POINTS;
        }
        return this.shape.getPointPositions(axis);
    }
}

