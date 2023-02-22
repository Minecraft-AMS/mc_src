/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlantedFeatureConfig;

public class MushroomPlantBlock
extends PlantBlock
implements Fertilizable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public MushroomPlantBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;
            for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (world.getBlockState(blockPos).getBlock() != this || --i > 0) continue;
                return;
            }
            BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                    pos = blockPos2;
                }
                blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                world.setBlockState(blockPos2, state, 2);
            }
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        return floor.isFullOpaque(view, pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.MYCELIUM || block == Blocks.PODZOL) {
            return true;
        }
        return world.getLightLevel(pos, 0) < 13 && this.canPlantOnTop(blockState, world, blockPos);
    }

    public boolean trySpawningBigMushroom(IWorld world, BlockPos pos, BlockState state, Random random) {
        world.removeBlock(pos, false);
        Feature<PlantedFeatureConfig> feature = null;
        if (this == Blocks.BROWN_MUSHROOM) {
            feature = Feature.HUGE_BROWN_MUSHROOM;
        } else if (this == Blocks.RED_MUSHROOM) {
            feature = Feature.HUGE_RED_MUSHROOM;
        }
        if (feature != null && feature.generate(world, world.getChunkManager().getChunkGenerator(), random, pos, new PlantedFeatureConfig(true))) {
            return true;
        }
        world.setBlockState(pos, state, 3);
        return false;
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(World world, Random random, BlockPos pos, BlockState state) {
        this.trySpawningBigMushroom(world, pos, state, random);
    }

    @Override
    public boolean shouldPostProcess(BlockState state, BlockView view, BlockPos pos) {
        return true;
    }
}

