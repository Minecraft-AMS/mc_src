/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CactusBlock
extends Block {
    public static final IntProperty AGE = Properties.AGE_15;
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    protected CactusBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }
        BlockPos blockPos = pos.up();
        if (!world.isAir(blockPos)) {
            return;
        }
        int i = 1;
        while (world.getBlockState(pos.down(i)).getBlock() == this) {
            ++i;
        }
        if (i >= 3) {
            return;
        }
        int j = state.get(AGE);
        if (j == 15) {
            world.setBlockState(blockPos, this.getDefaultState());
            BlockState blockState = (BlockState)state.with(AGE, 0);
            world.setBlockState(pos, blockState, 4);
            blockState.neighborUpdate(world, blockPos, this, pos, false);
        } else {
            world.setBlockState(pos, (BlockState)state.with(AGE, j + 1), 4);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public boolean isOpaque(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockState blockState = world.getBlockState(pos.offset(direction));
            Material material = blockState.getMaterial();
            if (!material.isSolid() && !world.getFluidState(pos.offset(direction)).matches(FluidTags.LAVA)) continue;
            return false;
        }
        Block block = world.getBlockState(pos.down()).getBlock();
        return (block == Blocks.CACTUS || block == Blocks.SAND || block == Blocks.RED_SAND) && !world.getBlockState(pos.up()).getMaterial().isLiquid();
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.damage(DamageSource.CACTUS, 1.0f);
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}
