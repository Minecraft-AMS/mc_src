/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public abstract class AbstractRedstoneGateBlock
extends HorizontalFacingBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final BooleanProperty POWERED = Properties.POWERED;

    protected AbstractRedstoneGateBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        return AbstractRedstoneGateBlock.topCoversMediumSquare(world, pos.down());
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (this.isLocked(world, pos, state)) {
            return;
        }
        boolean bl = state.get(POWERED);
        boolean bl2 = this.hasPower(world, pos, state);
        if (bl && !bl2) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, false), 2);
        } else if (!bl) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, true), 2);
            if (!bl2) {
                world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), TickPriority.HIGH);
            }
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return state.getWeakRedstonePower(view, pos, facing);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        if (!state.get(POWERED).booleanValue()) {
            return 0;
        }
        if (state.get(FACING) == facing) {
            return this.getOutputLevel(view, pos, state);
        }
        return 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (state.canPlaceAt(world, pos)) {
            this.updatePowered(world, pos, state);
            return;
        }
        BlockEntity blockEntity = this.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        AbstractRedstoneGateBlock.dropStacks(state, world, pos, blockEntity);
        world.removeBlock(pos, false);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        boolean bl2;
        if (this.isLocked(world, pos, state)) {
            return;
        }
        boolean bl = state.get(POWERED);
        if (bl != (bl2 = this.hasPower(world, pos, state)) && !world.getBlockTickScheduler().isTicking(pos, this)) {
            TickPriority tickPriority = TickPriority.HIGH;
            if (this.isTargetNotAligned(world, pos, state)) {
                tickPriority = TickPriority.EXTREMELY_HIGH;
            } else if (bl) {
                tickPriority = TickPriority.VERY_HIGH;
            }
            world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), tickPriority);
        }
    }

    public boolean isLocked(CollisionView world, BlockPos pos, BlockState state) {
        return false;
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        return this.getPower(world, pos, state) > 0;
    }

    protected int getPower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        int i = world.getEmittedRedstonePower(blockPos, direction);
        if (i >= 15) {
            return i;
        }
        BlockState blockState = world.getBlockState(blockPos);
        return Math.max(i, blockState.getBlock() == Blocks.REDSTONE_WIRE ? blockState.get(RedstoneWireBlock.POWER) : 0);
    }

    protected int getMaxInputLevelSides(CollisionView view, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        Direction direction2 = direction.rotateYClockwise();
        Direction direction3 = direction.rotateYCounterclockwise();
        return Math.max(this.getInputLevel(view, pos.offset(direction2), direction2), this.getInputLevel(view, pos.offset(direction3), direction3));
    }

    protected int getInputLevel(CollisionView view, BlockPos pos, Direction dir) {
        BlockState blockState = view.getBlockState(pos);
        Block block = blockState.getBlock();
        if (this.isValidInput(blockState)) {
            if (block == Blocks.REDSTONE_BLOCK) {
                return 15;
            }
            if (block == Blocks.REDSTONE_WIRE) {
                return blockState.get(RedstoneWireBlock.POWER);
            }
            return view.getEmittedStrongRedstonePower(pos, dir);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (this.hasPower(world, pos, state)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.getBlock() == newState.getBlock()) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, moved);
        this.updateTarget(world, pos, state);
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    protected boolean isValidInput(BlockState state) {
        return state.emitsRedstonePower();
    }

    protected int getOutputLevel(BlockView view, BlockPos pos, BlockState state) {
        return 15;
    }

    public static boolean isRedstoneGate(BlockState state) {
        return state.getBlock() instanceof AbstractRedstoneGateBlock;
    }

    public boolean isTargetNotAligned(BlockView world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING).getOpposite();
        BlockState blockState = world.getBlockState(pos.offset(direction));
        return AbstractRedstoneGateBlock.isRedstoneGate(blockState) && blockState.get(FACING) != direction;
    }

    protected abstract int getUpdateDelayInternal(BlockState var1);

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaque(BlockState state) {
        return true;
    }
}

