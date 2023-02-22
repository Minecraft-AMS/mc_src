/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 */
package net.minecraft.block;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty EYE = Properties.EYE;
    protected static final VoxelShape FRAME_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    protected static final VoxelShape EYE_SHAPE = Block.createCuboidShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape FRAME_WITH_EYE_SHAPE = VoxelShapes.union(FRAME_SHAPE, EYE_SHAPE);
    private static BlockPattern COMPLETED_FRAME;

    public EndPortalFrameBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(EYE, false));
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return state.get(EYE) != false ? FRAME_WITH_EYE_SHAPE : FRAME_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite())).with(EYE, false);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (state.get(EYE).booleanValue()) {
            return 15;
        }
        return 0;
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
        builder.add(FACING, EYE);
    }

    public static BlockPattern getCompletedFramePattern() {
        if (COMPLETED_FRAME == null) {
            COMPLETED_FRAME = BlockPatternBuilder.start().aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?").where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY)).where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.SOUTH)))).where('>', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.WEST)))).where('v', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.NORTH)))).where('<', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.EAST)))).build();
        }
        return COMPLETED_FRAME;
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}

