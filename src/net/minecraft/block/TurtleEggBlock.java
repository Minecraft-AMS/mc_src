/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TurtleEggBlock
extends Block {
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntProperty HATCH = Properties.HATCH;
    public static final IntProperty EGGS = Properties.EGGS;

    public TurtleEggBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HATCH, 0)).with(EGGS, 1));
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        this.tryBreakEgg(world, pos, entity, 100);
        super.onSteppedOn(world, pos, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        if (!(entity instanceof ZombieEntity)) {
            this.tryBreakEgg(world, pos, entity, 3);
        }
        super.onLandedUpon(world, pos, entity, distance);
    }

    private void tryBreakEgg(World world, BlockPos pos, Entity entity, int inverseChance) {
        if (!this.breaksEgg(world, entity)) {
            super.onSteppedOn(world, pos, entity);
            return;
        }
        if (!world.isClient && world.random.nextInt(inverseChance) == 0) {
            this.breakEgg(world, pos, world.getBlockState(pos));
        }
    }

    private void breakEgg(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        int i = state.get(EGGS);
        if (i <= 1) {
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, (BlockState)state.with(EGGS, i - 1), 2);
            world.playLevelEvent(2001, pos, Block.getRawIdFromState(state));
        }
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (this.shouldHatchProgress(world) && this.isSand(world, pos)) {
            int i = state.get(HATCH);
            if (i < 2) {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.setBlockState(pos, (BlockState)state.with(HATCH, i + 1), 2);
            } else {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.removeBlock(pos, false);
                if (!world.isClient) {
                    for (int j = 0; j < state.get(EGGS); ++j) {
                        world.playLevelEvent(2001, pos, Block.getRawIdFromState(state));
                        TurtleEntity turtleEntity = EntityType.TURTLE.create(world);
                        turtleEntity.setBreedingAge(-24000);
                        turtleEntity.setHomePos(pos);
                        turtleEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, pos.getY(), (double)pos.getZ() + 0.3, 0.0f, 0.0f);
                        world.spawnEntity(turtleEntity);
                    }
                }
            }
        }
    }

    private boolean isSand(BlockView world, BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock() == Blocks.SAND;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (this.isSand(world, pos) && !world.isClient) {
            world.playLevelEvent(2005, pos, 0);
        }
    }

    private boolean shouldHatchProgress(World world) {
        float f = world.getSkyAngle(1.0f);
        if ((double)f < 0.69 && (double)f > 0.65) {
            return true;
        }
        return world.random.nextInt(500) == 0;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        this.breakEgg(world, pos, state);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        if (ctx.getStack().getItem() == this.asItem() && state.get(EGGS) < 4) {
            return true;
        }
        return super.canReplace(state, ctx);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.getBlock() == this) {
            return (BlockState)blockState.with(EGGS, Math.min(4, blockState.get(EGGS) + 1));
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(EGGS) > 1) {
            return LARGE_SHAPE;
        }
        return SMALL_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean breaksEgg(World world, Entity entity) {
        if (entity instanceof TurtleEntity) {
            return false;
        }
        if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
            return world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
        }
        return true;
    }
}

