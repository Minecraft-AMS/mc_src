/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AnvilBlock
extends FallingBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    private static final VoxelShape X_STEP_SHAPE = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
    private static final VoxelShape X_STEM_SHAPE = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
    private static final VoxelShape X_FACE_SHAPE = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape Z_STEP_SHAPE = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
    private static final VoxelShape Z_STEM_SHAPE = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
    private static final VoxelShape Z_FACE_SHAPE = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
    private static final VoxelShape X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE);
    private static final VoxelShape Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE);
    private static final TranslatableText CONTAINER_NAME = new TranslatableText("container.repair", new Object[0]);

    public AnvilBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing().rotateYClockwise());
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.openContainer(state.createContainerFactory(world, pos));
        return true;
    }

    @Override
    @Nullable
    public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerFactory((i, playerInventory, playerEntity) -> new AnvilContainer(i, playerInventory, BlockContext.create(world, pos)), CONTAINER_NAME);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        Direction direction = state.get(FACING);
        if (direction.getAxis() == Direction.Axis.X) {
            return X_AXIS_SHAPE;
        }
        return Z_AXIS_SHAPE;
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setHurtEntities(true);
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
        world.playLevelEvent(1031, pos, 0);
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos) {
        world.playLevelEvent(1029, pos, 0);
    }

    @Nullable
    public static BlockState getLandingState(BlockState fallingState) {
        Block block = fallingState.getBlock();
        if (block == Blocks.ANVIL) {
            return (BlockState)Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, fallingState.get(FACING));
        }
        if (block == Blocks.CHIPPED_ANVIL) {
            return (BlockState)Blocks.DAMAGED_ANVIL.getDefaultState().with(FACING, fallingState.get(FACING));
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}
