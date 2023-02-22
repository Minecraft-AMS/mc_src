/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;

public class DragonEggBlock
extends FallingBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public DragonEggBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        this.teleport(state, world, pos);
        return true;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        this.teleport(state, world, pos);
    }

    private void teleport(BlockState state, World world, BlockPos pos) {
        for (int i = 0; i < 1000; ++i) {
            BlockPos blockPos = pos.add(world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16));
            if (!world.getBlockState(blockPos).isAir()) continue;
            if (world.isClient) {
                for (int j = 0; j < 128; ++j) {
                    double d = world.random.nextDouble();
                    float f = (world.random.nextFloat() - 0.5f) * 0.2f;
                    float g = (world.random.nextFloat() - 0.5f) * 0.2f;
                    float h = (world.random.nextFloat() - 0.5f) * 0.2f;
                    double e = MathHelper.lerp(d, (double)blockPos.getX(), (double)pos.getX()) + (world.random.nextDouble() - 0.5) + 0.5;
                    double k = MathHelper.lerp(d, (double)blockPos.getY(), (double)pos.getY()) + world.random.nextDouble() - 0.5;
                    double l = MathHelper.lerp(d, (double)blockPos.getZ(), (double)pos.getZ()) + (world.random.nextDouble() - 0.5) + 0.5;
                    world.addParticle(ParticleTypes.PORTAL, e, k, l, f, g, h);
                }
            } else {
                world.setBlockState(blockPos, state, 2);
                world.removeBlock(pos, false);
            }
            return;
        }
    }

    @Override
    public int getTickRate(CollisionView world) {
        return 5;
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}
