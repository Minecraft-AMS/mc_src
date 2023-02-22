/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MushroomBlock
extends Block {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

    public MushroomBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, true)).with(EAST, true)).with(SOUTH, true)).with(WEST, true)).with(UP, true)).with(DOWN, true));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, this != blockView.getBlockState(blockPos.down()).getBlock())).with(UP, this != blockView.getBlockState(blockPos.up()).getBlock())).with(NORTH, this != blockView.getBlockState(blockPos.north()).getBlock())).with(EAST, this != blockView.getBlockState(blockPos.east()).getBlock())).with(SOUTH, this != blockView.getBlockState(blockPos.south()).getBlock())).with(WEST, this != blockView.getBlockState(blockPos.west()).getBlock());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (neighborState.getBlock() == this) {
            return (BlockState)state.with(FACING_PROPERTIES.get(facing), false);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with(FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), state.get(NORTH))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), state.get(SOUTH))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), state.get(EAST))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), state.get(WEST))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), state.get(UP))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), state.get(DOWN));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with(FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), state.get(NORTH))).with(FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), state.get(SOUTH))).with(FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), state.get(EAST))).with(FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), state.get(WEST))).with(FACING_PROPERTIES.get(mirror.apply(Direction.UP)), state.get(UP))).with(FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), state.get(DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}

