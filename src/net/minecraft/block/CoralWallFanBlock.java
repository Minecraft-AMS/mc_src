/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralWallFanBlock
extends DeadCoralWallFanBlock {
    private final Block deadCoralBlock;

    protected CoralWallFanBlock(Block deadCoralBlock, Block.Settings settings) {
        super(settings);
        this.deadCoralBlock = deadCoralBlock;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        this.checkLivingConditions(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!CoralWallFanBlock.isInWater(state, world, pos)) {
            world.setBlockState(pos, (BlockState)((BlockState)this.deadCoralBlock.getDefaultState().with(WATERLOGGED, false)).with(FACING, state.get(FACING)), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        this.checkLivingConditions(state, world, pos);
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
}

