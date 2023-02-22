/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BigDripleafStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.Tilt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BigDripleafBlock
extends HorizontalFacingBlock
implements Fertilizable,
Waterloggable {
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final EnumProperty<Tilt> TILT = Properties.TILT;
    private static final int field_31015 = -1;
    private static final Object2IntMap<Tilt> NEXT_TILT_DELAYS = (Object2IntMap)Util.make(new Object2IntArrayMap(), delays -> {
        delays.defaultReturnValue(-1);
        delays.put((Object)Tilt.UNSTABLE, 10);
        delays.put((Object)Tilt.PARTIAL, 10);
        delays.put((Object)Tilt.FULL, 100);
    });
    private static final int field_31016 = 5;
    private static final int field_31017 = 6;
    private static final int field_31018 = 11;
    private static final int field_31019 = 13;
    private static final Map<Tilt, VoxelShape> SHAPES_FOR_TILT = ImmutableMap.of((Object)Tilt.NONE, (Object)Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 15.0, 16.0), (Object)Tilt.UNSTABLE, (Object)Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 15.0, 16.0), (Object)Tilt.PARTIAL, (Object)Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 13.0, 16.0), (Object)Tilt.FULL, (Object)VoxelShapes.empty());
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, VoxelShape> SHAPES_FOR_DIRECTION = ImmutableMap.of((Object)Direction.NORTH, (Object)VoxelShapes.combine(BigDripleafStemBlock.NORTH_SHAPE, BASE_SHAPE, BooleanBiFunction.ONLY_FIRST), (Object)Direction.SOUTH, (Object)VoxelShapes.combine(BigDripleafStemBlock.SOUTH_SHAPE, BASE_SHAPE, BooleanBiFunction.ONLY_FIRST), (Object)Direction.EAST, (Object)VoxelShapes.combine(BigDripleafStemBlock.EAST_SHAPE, BASE_SHAPE, BooleanBiFunction.ONLY_FIRST), (Object)Direction.WEST, (Object)VoxelShapes.combine(BigDripleafStemBlock.WEST_SHAPE, BASE_SHAPE, BooleanBiFunction.ONLY_FIRST));
    private final Map<BlockState, VoxelShape> shapes;

    protected BigDripleafBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WATERLOGGED, false)).with(FACING, Direction.NORTH)).with(TILT, Tilt.NONE));
        this.shapes = this.getShapesForStates(BigDripleafBlock::getShapeForState);
    }

    private static VoxelShape getShapeForState(BlockState state) {
        return VoxelShapes.union(SHAPES_FOR_TILT.get(state.get(TILT)), SHAPES_FOR_DIRECTION.get(state.get(FACING)));
    }

    public static void grow(WorldAccess world, Random random, BlockPos pos, Direction direction) {
        int j;
        int i = MathHelper.nextInt(random, 2, 5);
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (j = 0; j < i && BigDripleafBlock.canGrowInto(world, mutable, world.getBlockState(mutable)); ++j) {
            mutable.move(Direction.UP);
        }
        int k = pos.getY() + j - 1;
        mutable.setY(pos.getY());
        while (mutable.getY() < k) {
            BigDripleafStemBlock.placeStemAt(world, mutable, world.getFluidState(mutable), direction);
            mutable.move(Direction.UP);
        }
        BigDripleafBlock.placeDripleafAt(world, mutable, world.getFluidState(mutable), direction);
    }

    private static boolean canGrowInto(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.SMALL_DRIPLEAF);
    }

    protected static boolean canGrowInto(HeightLimitView world, BlockPos pos, BlockState state) {
        return !world.isOutOfHeightLimit(pos) && BigDripleafBlock.canGrowInto(state);
    }

    protected static boolean placeDripleafAt(WorldAccess world, BlockPos pos, FluidState fluidState, Direction direction) {
        BlockState blockState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.getDefaultState().with(WATERLOGGED, fluidState.isEqualAndStill(Fluids.WATER))).with(FACING, direction);
        return world.setBlockState(pos, blockState, 3);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        this.changeTilt(state, world, hit.getBlockPos(), Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isOf(this) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM) || blockState.isIn(BlockTags.BIG_DRIPLEAF_PLACEABLE);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.get(WATERLOGGED).booleanValue()) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.UP && neighborState.isOf(this)) {
            return Blocks.BIG_DRIPLEAF_STEM.getStateWithProperties(state);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState blockState = world.getBlockState(pos.up());
        return BigDripleafBlock.canGrowInto(blockState);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState blockState;
        BlockPos blockPos = pos.up();
        if (BigDripleafBlock.canGrowInto(world, blockPos, blockState = world.getBlockState(blockPos))) {
            Direction direction = state.get(FACING);
            BigDripleafStemBlock.placeStemAt(world, pos, state.getFluidState(), direction);
            BigDripleafBlock.placeDripleafAt(world, blockPos, blockState.getFluidState(), direction);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.get(TILT) == Tilt.NONE && BigDripleafBlock.isEntityAbove(pos, entity) && !world.isReceivingRedstonePower(pos)) {
            this.changeTilt(state, world, pos, Tilt.UNSTABLE, null);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isReceivingRedstonePower(pos)) {
            BigDripleafBlock.resetTilt(state, world, pos);
            return;
        }
        Tilt tilt = state.get(TILT);
        if (tilt == Tilt.UNSTABLE) {
            this.changeTilt(state, world, pos, Tilt.PARTIAL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
        } else if (tilt == Tilt.PARTIAL) {
            this.changeTilt(state, world, pos, Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
        } else if (tilt == Tilt.FULL) {
            BigDripleafBlock.resetTilt(state, world, pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            BigDripleafBlock.resetTilt(state, world, pos);
        }
    }

    private static void playTiltSound(World world, BlockPos pos, SoundEvent soundEvent) {
        float f = MathHelper.nextBetween(world.random, 0.8f, 1.2f);
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, f);
    }

    private static boolean isEntityAbove(BlockPos pos, Entity entity) {
        return entity.isOnGround() && entity.getPos().y > (double)((float)pos.getY() + 0.6875f);
    }

    private void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt, @Nullable SoundEvent sound) {
        int i;
        BigDripleafBlock.changeTilt(state, world, pos, tilt);
        if (sound != null) {
            BigDripleafBlock.playTiltSound(world, pos, sound);
        }
        if ((i = NEXT_TILT_DELAYS.getInt((Object)tilt)) != -1) {
            world.createAndScheduleBlockTick(pos, this, i);
        }
    }

    private static void resetTilt(BlockState state, World world, BlockPos pos) {
        BigDripleafBlock.changeTilt(state, world, pos, Tilt.NONE);
        if (state.get(TILT) != Tilt.NONE) {
            BigDripleafBlock.playTiltSound(world, pos, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
        }
    }

    private static void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt) {
        Tilt tilt2 = state.get(TILT);
        world.setBlockState(pos, (BlockState)state.with(TILT, tilt), 2);
        if (tilt.isStable() && tilt != tilt2) {
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_FOR_TILT.get(state.get(TILT));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapes.get(state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = blockState.isOf(Blocks.BIG_DRIPLEAF) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM);
        return (BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, fluidState.isEqualAndStill(Fluids.WATER))).with(FACING, bl ? blockState.get(FACING) : ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, TILT);
    }
}

