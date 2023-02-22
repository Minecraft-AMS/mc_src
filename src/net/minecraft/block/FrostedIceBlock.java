/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrostedIceBlock
extends IceBlock {
    public static final IntProperty AGE = Properties.AGE_3;

    public FrostedIceBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((random.nextInt(3) == 0 || this.canMelt(world, pos, 4)) && world.getLightLevel(pos) > 11 - state.get(AGE) - state.getOpacity(world, pos) && this.increaseAge(state, world, pos)) {
            try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
                for (Direction direction : Direction.values()) {
                    pooledMutable.set(pos).setOffset(direction);
                    BlockState blockState = world.getBlockState(pooledMutable);
                    if (blockState.getBlock() != this || this.increaseAge(blockState, world, pooledMutable)) continue;
                    world.getBlockTickScheduler().schedule(pooledMutable, this, MathHelper.nextInt(random, 20, 40));
                }
            }
            return;
        }
        world.getBlockTickScheduler().schedule(pos, this, MathHelper.nextInt(random, 20, 40));
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int i = state.get(AGE);
        if (i < 3) {
            world.setBlockState(pos, (BlockState)state.with(AGE, i + 1), 2);
            return false;
        }
        this.melt(state, world, pos);
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (block == this && this.canMelt(world, pos, 2)) {
            this.melt(state, world, pos);
        }
        super.neighborUpdate(state, world, pos, block, neighborPos, moved);
    }

    private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
        int i = 0;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (Direction direction : Direction.values()) {
                pooledMutable.set(pos).setOffset(direction);
                if (world.getBlockState(pooledMutable).getBlock() != this || ++i < maxNeighbors) continue;
                boolean bl = false;
                return bl;
            }
        }
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }
}

