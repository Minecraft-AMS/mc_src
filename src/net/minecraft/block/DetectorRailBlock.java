/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DetectorRailBlock
extends AbstractRailBlock {
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final int SCHEDULED_TICK_DELAY = 20;

    public DetectorRailBlock(AbstractBlock.Settings settings) {
        super(true, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(SHAPE, RailShape.NORTH_SOUTH)).with(WATERLOGGED, false));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(world, pos, state);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(world, pos, state);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!state.get(POWERED).booleanValue()) {
            return 0;
        }
        return direction == Direction.UP ? 15 : 0;
    }

    private void updatePoweredStatus(World world, BlockPos pos, BlockState state) {
        BlockState blockState;
        if (!this.canPlaceAt(state, world, pos)) {
            return;
        }
        boolean bl = state.get(POWERED);
        boolean bl2 = false;
        List<AbstractMinecartEntity> list = this.getCarts(world, pos, AbstractMinecartEntity.class, entity -> true);
        if (!list.isEmpty()) {
            bl2 = true;
        }
        if (bl2 && !bl) {
            blockState = (BlockState)state.with(POWERED, true);
            world.setBlockState(pos, blockState, 3);
            this.updateNearbyRails(world, pos, blockState, true);
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.down(), this);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
        }
        if (!bl2 && bl) {
            blockState = (BlockState)state.with(POWERED, false);
            world.setBlockState(pos, blockState, 3);
            this.updateNearbyRails(world, pos, blockState, false);
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.down(), this);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
        }
        if (bl2) {
            world.scheduleBlockTick(pos, this, 20);
        }
        world.updateComparators(pos, this);
    }

    protected void updateNearbyRails(World world, BlockPos pos, BlockState state, boolean unpowering) {
        RailPlacementHelper railPlacementHelper = new RailPlacementHelper(world, pos, state);
        List<BlockPos> list = railPlacementHelper.getNeighbors();
        for (BlockPos blockPos : list) {
            BlockState blockState = world.getBlockState(blockPos);
            world.updateNeighbor(blockState, blockPos, blockState.getBlock(), pos, false);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        BlockState blockState = this.updateCurves(state, world, pos, notify);
        this.updatePoweredStatus(world, pos, blockState);
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (state.get(POWERED).booleanValue()) {
            List<CommandBlockMinecartEntity> list = this.getCarts(world, pos, CommandBlockMinecartEntity.class, cart -> true);
            if (!list.isEmpty()) {
                return list.get(0).getCommandExecutor().getSuccessCount();
            }
            List<AbstractMinecartEntity> list2 = this.getCarts(world, pos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
            if (!list2.isEmpty()) {
                return ScreenHandler.calculateComparatorOutput((Inventory)((Object)list2.get(0)));
            }
        }
        return 0;
    }

    private <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate) {
        return world.getEntitiesByClass(entityClass, this.getCartDetectionBox(pos), entityPredicate);
    }

    private Box getCartDetectionBox(BlockPos pos) {
        double d = 0.2;
        return new Box((double)pos.getX() + 0.2, pos.getY(), (double)pos.getZ() + 0.2, (double)(pos.getX() + 1) - 0.2, (double)(pos.getY() + 1) - 0.2, (double)(pos.getZ() + 1) - 0.2);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                switch (state.get(SHAPE)) {
                    case ASCENDING_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (state.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)state.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (state.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)state.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = state.get(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT: {
                switch (railShape) {
                    case ASCENDING_NORTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
                break;
            }
            case FRONT_BACK: {
                switch (railShape) {
                    case ASCENDING_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
                break;
            }
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, WATERLOGGED);
    }
}

