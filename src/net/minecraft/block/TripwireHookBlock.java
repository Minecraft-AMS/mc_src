/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TripwireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripwireHookBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        switch (state.get(FACING)) {
            default: {
                return WEST_SHAPE;
            }
            case WEST: {
                return EAST_SHAPE;
            }
            case SOUTH: {
                return NORTH_SHAPE;
            }
            case NORTH: 
        }
        return SOUTH_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction) && !blockState.emitsRedstonePower();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions;
        BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
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
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        this.update(world, pos, state, false, false, -1, null);
    }

    public void update(World world, BlockPos pos, BlockState state, boolean beingRemoved, boolean bl, int i, @Nullable BlockState blockState) {
        BlockPos blockPos;
        Direction direction = state.get(FACING);
        boolean bl2 = state.get(ATTACHED);
        boolean bl3 = state.get(POWERED);
        boolean bl4 = !beingRemoved;
        boolean bl5 = false;
        int j = 0;
        BlockState[] blockStates = new BlockState[42];
        for (int k = 1; k < 42; ++k) {
            blockPos = pos.offset(direction, k);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.getBlock() == Blocks.TRIPWIRE_HOOK) {
                if (blockState2.get(FACING) != direction.getOpposite()) break;
                j = k;
                break;
            }
            if (blockState2.getBlock() == Blocks.TRIPWIRE || k == i) {
                if (k == i) {
                    blockState2 = (BlockState)MoreObjects.firstNonNull((Object)blockState, (Object)blockState2);
                }
                boolean bl6 = blockState2.get(TripwireBlock.DISARMED) == false;
                boolean bl7 = blockState2.get(TripwireBlock.POWERED);
                bl5 |= bl6 && bl7;
                blockStates[k] = blockState2;
                if (k != i) continue;
                world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
                bl4 &= bl6;
                continue;
            }
            blockStates[k] = null;
            bl4 = false;
        }
        BlockState blockState3 = (BlockState)((BlockState)this.getDefaultState().with(ATTACHED, bl4)).with(POWERED, bl5 &= (bl4 &= j > 1));
        if (j > 0) {
            blockPos = pos.offset(direction, j);
            Direction direction2 = direction.getOpposite();
            world.setBlockState(blockPos, (BlockState)blockState3.with(FACING, direction2), 3);
            this.updateNeighborsOnAxis(world, blockPos, direction2);
            this.playSound(world, blockPos, bl4, bl5, bl2, bl3);
        }
        this.playSound(world, pos, bl4, bl5, bl2, bl3);
        if (!beingRemoved) {
            world.setBlockState(pos, (BlockState)blockState3.with(FACING, direction), 3);
            if (bl) {
                this.updateNeighborsOnAxis(world, pos, direction);
            }
        }
        if (bl2 != bl4) {
            for (int l = 1; l < j; ++l) {
                BlockPos blockPos2 = pos.offset(direction, l);
                BlockState blockState4 = blockStates[l];
                if (blockState4 == null) continue;
                world.setBlockState(blockPos2, (BlockState)blockState4.with(ATTACHED, bl4), 3);
                if (world.getBlockState(blockPos2).isAir()) continue;
            }
        }
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        this.update(world, pos, state, false, true, -1, null);
    }

    private void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
        if (on && !off) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4f, 0.6f);
        } else if (!on && off) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4f, 0.5f);
        } else if (attached && !detached) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4f, 0.7f);
        } else if (!attached && detached) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4f, 1.2f / (world.random.nextFloat() * 0.2f + 0.9f));
        }
    }

    private void updateNeighborsOnAxis(World world, BlockPos pos, Direction direction) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(direction.getOpposite()), this);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.getBlock() == newState.getBlock()) {
            return;
        }
        boolean bl = state.get(ATTACHED);
        boolean bl2 = state.get(POWERED);
        if (bl || bl2) {
            this.update(world, pos, state, true, false, -1, null);
        }
        if (bl2) {
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
        }
        super.onBlockRemoved(state, world, pos, newState, moved);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        if (!state.get(POWERED).booleanValue()) {
            return 0;
        }
        if (state.get(FACING) == facing) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT_MIPPED;
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
        builder.add(FACING, POWERED, ATTACHED);
    }
}
