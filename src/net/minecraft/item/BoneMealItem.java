/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;

public class BoneMealItem
extends Item {
    public BoneMealItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(context.getSide());
        if (BoneMealItem.useOnFertilizable(context.getStack(), world, blockPos)) {
            if (!world.isClient) {
                world.syncWorldEvent(2005, blockPos, 0);
            }
            return ActionResult.success(world.isClient);
        }
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isSideSolidFullSquare(world, blockPos, context.getSide());
        if (bl && BoneMealItem.useOnGround(context.getStack(), world, blockPos2, context.getSide())) {
            if (!world.isClient) {
                world.syncWorldEvent(2005, blockPos2, 0);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
        Fertilizable fertilizable;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof Fertilizable && (fertilizable = (Fertilizable)((Object)blockState.getBlock())).isFertilizable(world, pos, blockState, world.isClient)) {
            if (world instanceof ServerWorld) {
                if (fertilizable.canGrow(world, world.random, pos, blockState)) {
                    fertilizable.grow((ServerWorld)world, world.random, pos, blockState);
                }
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack stack, World world, BlockPos blockPos, @Nullable Direction facing) {
        if (!world.getBlockState(blockPos).isOf(Blocks.WATER) || world.getFluidState(blockPos).getLevel() != 8) {
            return false;
        }
        if (!(world instanceof ServerWorld)) {
            return true;
        }
        block0: for (int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;
            BlockState blockState = Blocks.SEAGRASS.getDefaultState();
            for (int j = 0; j < i / 16; ++j) {
                if (world.getBlockState(blockPos2 = blockPos2.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1)).isFullCube(world, blockPos2)) continue block0;
            }
            Optional<RegistryKey<Biome>> optional = world.getBiomeKey(blockPos2);
            if (Objects.equals(optional, Optional.of(BiomeKeys.WARM_OCEAN)) || Objects.equals(optional, Optional.of(BiomeKeys.DEEP_WARM_OCEAN))) {
                if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
                    blockState = (BlockState)((Block)BlockTags.WALL_CORALS.getRandom(world.random)).getDefaultState().with(DeadCoralWallFanBlock.FACING, facing);
                } else if (RANDOM.nextInt(4) == 0) {
                    blockState = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandom(RANDOM)).getDefaultState();
                }
            }
            if (blockState.getBlock().isIn(BlockTags.WALL_CORALS)) {
                for (int k = 0; !blockState.canPlaceAt(world, blockPos2) && k < 4; ++k) {
                    blockState = (BlockState)blockState.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(RANDOM));
                }
            }
            if (!blockState.canPlaceAt(world, blockPos2)) continue;
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.isOf(Blocks.WATER) && world.getFluidState(blockPos2).getLevel() == 8) {
                world.setBlockState(blockPos2, blockState, 3);
                continue;
            }
            if (!blockState2.isOf(Blocks.SEAGRASS) || RANDOM.nextInt(10) != 0) continue;
            ((Fertilizable)((Object)Blocks.SEAGRASS)).grow((ServerWorld)world, RANDOM, blockPos2, blockState2);
        }
        stack.decrement(1);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static void createParticles(WorldAccess world, BlockPos pos, int count) {
        double e;
        BlockState blockState;
        if (count == 0) {
            count = 15;
        }
        if ((blockState = world.getBlockState(pos)).isAir()) {
            return;
        }
        double d = 0.5;
        if (blockState.isOf(Blocks.WATER)) {
            count *= 3;
            e = 1.0;
            d = 3.0;
        } else if (blockState.isOpaqueFullCube(world, pos)) {
            pos = pos.up();
            count *= 3;
            d = 3.0;
            e = 1.0;
        } else {
            e = blockState.getOutlineShape(world, pos).getMax(Direction.Axis.Y);
        }
        world.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        for (int i = 0; i < count; ++i) {
            double m;
            double l;
            double f = RANDOM.nextGaussian() * 0.02;
            double g = RANDOM.nextGaussian() * 0.02;
            double h = RANDOM.nextGaussian() * 0.02;
            double j = 0.5 - d;
            double k = (double)pos.getX() + j + RANDOM.nextDouble() * d * 2.0;
            if (world.getBlockState(new BlockPos(k, l = (double)pos.getY() + RANDOM.nextDouble() * e, m = (double)pos.getZ() + j + RANDOM.nextDouble() * d * 2.0).down()).isAir()) continue;
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, k, l, m, f, g, h);
        }
    }
}

