/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BellBlock
extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
    private static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
    private static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    private static final VoxelShape BELL_WAIST_SHAPE = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
    private static final VoxelShape BELL_LIP_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
    private static final VoxelShape BELL_SHAPE = VoxelShapes.union(BELL_LIP_SHAPE, BELL_WAIST_SHAPE);
    private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
    private static final VoxelShape EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
    private static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
    private static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
    private static final VoxelShape HANGING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

    public BellBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(ATTACHMENT, Attachment.FLOOR));
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity entity) {
        if (entity instanceof ProjectileEntity) {
            Entity entity2 = ((ProjectileEntity)entity).getOwner();
            PlayerEntity playerEntity = entity2 instanceof PlayerEntity ? (PlayerEntity)entity2 : null;
            this.ring(world, state, world.getBlockEntity(hitResult.getBlockPos()), hitResult, playerEntity, true);
        }
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return this.ring(world, state, world.getBlockEntity(pos), hit, player, true);
    }

    public boolean ring(World world, BlockState state, @Nullable BlockEntity blockEntity, BlockHitResult hitPosition, @Nullable PlayerEntity player, boolean checkHitPosition) {
        boolean bl;
        Direction direction = hitPosition.getSide();
        BlockPos blockPos = hitPosition.getBlockPos();
        boolean bl2 = bl = !checkHitPosition || this.isPointOnBell(state, direction, hitPosition.getPos().y - (double)blockPos.getY());
        if (!world.isClient && blockEntity instanceof BellBlockEntity && bl) {
            ((BellBlockEntity)blockEntity).activate(direction);
            this.ring(world, blockPos);
            if (player != null) {
                player.incrementStat(Stats.BELL_RING);
            }
            return true;
        }
        return true;
    }

    private boolean isPointOnBell(BlockState state, Direction side, double y) {
        if (side.getAxis() == Direction.Axis.Y || y > (double)0.8124f) {
            return false;
        }
        Direction direction = state.get(FACING);
        Attachment attachment = state.get(ATTACHMENT);
        switch (attachment) {
            case FLOOR: {
                return direction.getAxis() == side.getAxis();
            }
            case SINGLE_WALL: 
            case DOUBLE_WALL: {
                return direction.getAxis() != side.getAxis();
            }
            case CEILING: {
                return true;
            }
        }
        return false;
    }

    private void ring(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0f, 1.0f);
    }

    private VoxelShape getShape(BlockState state) {
        Direction direction = state.get(FACING);
        Attachment attachment = state.get(ATTACHMENT);
        if (attachment == Attachment.FLOOR) {
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                return NORTH_SOUTH_SHAPE;
            }
            return EAST_WEST_SHAPE;
        }
        if (attachment == Attachment.CEILING) {
            return HANGING_SHAPE;
        }
        if (attachment == Attachment.DOUBLE_WALL) {
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                return NORTH_SOUTH_WALLS_SHAPE;
            }
            return EAST_WEST_WALLS_SHAPE;
        }
        if (direction == Direction.NORTH) {
            return NORTH_WALL_SHAPE;
        }
        if (direction == Direction.SOUTH) {
            return SOUTH_WALL_SHAPE;
        }
        if (direction == Direction.EAST) {
            return EAST_WALL_SHAPE;
        }
        return WEST_WALL_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return this.getShape(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return this.getShape(state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        Direction.Axis axis = direction.getAxis();
        if (axis == Direction.Axis.Y) {
            BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(ATTACHMENT, direction == Direction.DOWN ? Attachment.CEILING : Attachment.FLOOR)).with(FACING, ctx.getPlayerFacing());
            if (blockState.canPlaceAt(ctx.getWorld(), blockPos)) {
                return blockState;
            }
        } else {
            boolean bl = axis == Direction.Axis.X && world.getBlockState(blockPos.west()).isSideSolidFullSquare(world, blockPos.west(), Direction.EAST) && world.getBlockState(blockPos.east()).isSideSolidFullSquare(world, blockPos.east(), Direction.WEST) || axis == Direction.Axis.Z && world.getBlockState(blockPos.north()).isSideSolidFullSquare(world, blockPos.north(), Direction.SOUTH) && world.getBlockState(blockPos.south()).isSideSolidFullSquare(world, blockPos.south(), Direction.NORTH);
            BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(FACING, direction.getOpposite())).with(ATTACHMENT, bl ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
            boolean bl2 = world.getBlockState(blockPos.down()).isSideSolidFullSquare(world, blockPos.down(), Direction.UP);
            if ((blockState = (BlockState)blockState.with(ATTACHMENT, bl2 ? Attachment.FLOOR : Attachment.CEILING)).canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        Attachment attachment = state.get(ATTACHMENT);
        Direction direction = BellBlock.getPlacementSide(state).getOpposite();
        if (direction == facing && !state.canPlaceAt(world, pos) && attachment != Attachment.DOUBLE_WALL) {
            return Blocks.AIR.getDefaultState();
        }
        if (facing.getAxis() == state.get(FACING).getAxis()) {
            if (attachment == Attachment.DOUBLE_WALL && !neighborState.isSideSolidFullSquare(world, neighborPos, facing)) {
                return (BlockState)((BlockState)state.with(ATTACHMENT, Attachment.SINGLE_WALL)).with(FACING, facing.getOpposite());
            }
            if (attachment == Attachment.SINGLE_WALL && direction.getOpposite() == facing && neighborState.isSideSolidFullSquare(world, neighborPos, state.get(FACING))) {
                return (BlockState)state.with(ATTACHMENT, Attachment.DOUBLE_WALL);
            }
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        return WallMountedBlock.canPlaceAt(world, pos, BellBlock.getPlacementSide(state).getOpposite());
    }

    private static Direction getPlacementSide(BlockState state) {
        switch (state.get(ATTACHMENT)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return state.get(FACING).getOpposite();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHMENT);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView view) {
        return new BellBlockEntity();
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}

