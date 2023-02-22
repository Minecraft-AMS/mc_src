/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FluidBlock
extends Block
implements FluidDrainable {
    public static final IntProperty LEVEL = Properties.LEVEL_15;
    protected final BaseFluid fluid;
    private final List<FluidState> statesByLevel;

    protected FluidBlock(BaseFluid fluid, Block.Settings settings) {
        super(settings);
        this.fluid = fluid;
        this.statesByLevel = Lists.newArrayList();
        this.statesByLevel.add(fluid.getStill(false));
        for (int i = 1; i < 8; ++i) {
            this.statesByLevel.add(fluid.getFlowing(8 - i, false));
        }
        this.statesByLevel.add(fluid.getFlowing(8, true));
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    @Override
    public void onRandomTick(BlockState state, World world, BlockPos pos, Random random) {
        world.getFluidState(pos).onRandomTick(world, pos, random);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return !this.fluid.matches(FluidTags.LAVA);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        int i = state.get(LEVEL);
        return this.statesByLevel.get(Math.min(i, 8));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
        if (neighbor.getFluidState().getFluid().matchesType(this.fluid)) {
            return true;
        }
        return super.isOpaque(state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public int getTickRate(CollisionView world) {
        return this.fluid.getTickRate(world);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (this.receiveNeighborFluids(world, pos, state)) {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.getTickRate(world));
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (state.getFluidState().isStill() || neighborState.getFluidState().isStill()) {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (this.receiveNeighborFluids(world, pos, state)) {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.getTickRate(world));
        }
    }

    public boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
        if (this.fluid.matches(FluidTags.LAVA)) {
            boolean bl = false;
            for (Direction direction : Direction.values()) {
                if (direction == Direction.DOWN || !world.getFluidState(pos.offset(direction)).matches(FluidTags.WATER)) continue;
                bl = true;
                break;
            }
            if (bl) {
                FluidState fluidState = world.getFluidState(pos);
                if (fluidState.isStill()) {
                    world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }
                if (fluidState.getHeight(world, pos) >= 0.44444445f) {
                    world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }
            }
        }
        return true;
    }

    private void playExtinguishSound(IWorld world, BlockPos pos) {
        world.playLevelEvent(1501, pos, 0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
        if (state.get(LEVEL) == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            return this.fluid;
        }
        return Fluids.EMPTY;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (this.fluid.matches(FluidTags.LAVA)) {
            entity.setInLava();
        }
    }
}
