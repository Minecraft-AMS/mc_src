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
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CropBlock
extends PlantBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_7;
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    protected CropBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        return floor.getBlock() == Blocks.FARMLAND;
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return (BlockState)this.getDefaultState().with(this.getAgeProperty(), age);
    }

    public boolean isMature(BlockState state) {
        return state.get(this.getAgeProperty()) >= this.getMaxAge();
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        float f;
        int i;
        super.onScheduledTick(state, world, pos, random);
        if (world.getLightLevel(pos, 0) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && random.nextInt((int)(25.0f / (f = CropBlock.getAvailableMoisture(this, world, pos))) + 1) == 0) {
            world.setBlockState(pos, this.withAge(i + 1), 2);
        }
    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int j;
        int i = this.getAge(state) + this.getGrowthAmount(world);
        if (i > (j = this.getMaxAge())) {
            i = j;
        }
        world.setBlockState(pos, this.withAge(i), 2);
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }

    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        boolean bl2;
        float f = 1.0f;
        BlockPos blockPos = pos.down();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0f;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.getBlock() == Blocks.FARMLAND) {
                    g = 1.0f;
                    if (blockState.get(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    g /= 4.0f;
                }
                f += g;
            }
        }
        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = block == world.getBlockState(blockPos4).getBlock() || block == world.getBlockState(blockPos5).getBlock();
        boolean bl3 = bl2 = block == world.getBlockState(blockPos2).getBlock() || block == world.getBlockState(blockPos3).getBlock();
        if (bl && bl2) {
            f /= 2.0f;
        } else {
            boolean bl32;
            boolean bl4 = bl32 = block == world.getBlockState(blockPos4.north()).getBlock() || block == world.getBlockState(blockPos5.north()).getBlock() || block == world.getBlockState(blockPos5.south()).getBlock() || block == world.getBlockState(blockPos4.south()).getBlock();
            if (bl32) {
                f /= 2.0f;
            }
        }
        return f;
    }

    @Override
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        return (world.getLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            world.breakBlock(pos, true);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Environment(value=EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this.getSeedsItem());
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !this.isMature(state);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos pos, BlockState state) {
        this.applyGrowth(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
