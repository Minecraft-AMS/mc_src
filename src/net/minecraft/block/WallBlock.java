/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class WallBlock
extends HorizontalConnectingBlock {
    public static final BooleanProperty UP = Properties.UP;
    private final VoxelShape[] UP_OUTLINE_SHAPES;
    private final VoxelShape[] UP_COLLISION_SHAPES;

    public WallBlock(Block.Settings settings) {
        super(0.0f, 3.0f, 0.0f, 14.0f, 24.0f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(UP, true)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
        this.UP_OUTLINE_SHAPES = this.createShapes(4.0f, 3.0f, 16.0f, 0.0f, 14.0f);
        this.UP_COLLISION_SHAPES = this.createShapes(4.0f, 3.0f, 24.0f, 0.0f, 24.0f);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(UP).booleanValue()) {
            return this.UP_OUTLINE_SHAPES[this.getShapeIndex(state)];
        }
        return super.getOutlineShape(state, view, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(UP).booleanValue()) {
            return this.UP_COLLISION_SHAPES[this.getShapeIndex(state)];
        }
        return super.getCollisionShape(state, view, pos, context);
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }

    private boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
        Block block = state.getBlock();
        boolean bl = block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
        return !WallBlock.cannotConnect(block) && faceFullSquare || bl;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockState blockState = worldView.getBlockState(blockPos2);
        BlockState blockState2 = worldView.getBlockState(blockPos3);
        BlockState blockState3 = worldView.getBlockState(blockPos4);
        BlockState blockState4 = worldView.getBlockState(blockPos5);
        boolean bl = this.shouldConnectTo(blockState, blockState.isSideSolidFullSquare(worldView, blockPos2, Direction.SOUTH), Direction.SOUTH);
        boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos3, Direction.WEST), Direction.WEST);
        boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isSideSolidFullSquare(worldView, blockPos4, Direction.NORTH), Direction.NORTH);
        boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isSideSolidFullSquare(worldView, blockPos5, Direction.EAST), Direction.EAST);
        boolean bl5 = !(bl && !bl2 && bl3 && !bl4 || !bl && bl2 && !bl3 && bl4);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(UP, bl5 || !worldView.isAir(blockPos.up()))).with(NORTH, bl)).with(EAST, bl2)).with(SOUTH, bl3)).with(WEST, bl4)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
        Direction direction = facing.getOpposite();
        boolean bl = facing == Direction.NORTH ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction) : state.get(NORTH).booleanValue();
        boolean bl2 = facing == Direction.EAST ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction) : state.get(EAST).booleanValue();
        boolean bl3 = facing == Direction.SOUTH ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction) : state.get(SOUTH).booleanValue();
        boolean bl4 = facing == Direction.WEST ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction) : state.get(WEST).booleanValue();
        boolean bl5 = !(bl && !bl2 && bl3 && !bl4 || !bl && bl2 && !bl3 && bl4);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with(UP, bl5 || !world.isAir(pos.up()))).with(NORTH, bl)).with(EAST, bl2)).with(SOUTH, bl3)).with(WEST, bl4);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}

