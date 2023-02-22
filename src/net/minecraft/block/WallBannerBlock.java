/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class WallBannerBlock
extends AbstractBannerBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 12.5, 16.0), (Object)Direction.SOUTH, (Object)Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.5, 2.0), (Object)Direction.WEST, (Object)Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 12.5, 16.0), (Object)Direction.EAST, (Object)Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)));

    public WallBannerBlock(DyeColor dyeColor, Block.Settings settings) {
        super(dyeColor, settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        return world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return FACING_TO_SHAPE.get(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions;
        BlockState blockState = this.getDefaultState();
        World collisionView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : directions = ctx.getPlacementDirections()) {
            Direction direction2;
            if (!direction.getAxis().isHorizontal() || !(blockState = (BlockState)blockState.with(FACING, direction2 = direction.getOpposite())).canPlaceAt(collisionView, blockPos)) continue;
            return blockState;
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
