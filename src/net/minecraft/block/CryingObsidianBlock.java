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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CryingObsidianBlock
extends Block {
    public CryingObsidianBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(5) != 0) {
            return;
        }
        Direction direction = Direction.random(random);
        if (direction == Direction.UP) {
            return;
        }
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (state.isOpaque() && blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
            return;
        }
        double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5 + (double)direction.getOffsetX() * 0.6;
        double e = direction.getOffsetY() == 0 ? random.nextDouble() : 0.5 + (double)direction.getOffsetY() * 0.6;
        double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5 + (double)direction.getOffsetZ() * 0.6;
        world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + f, 0.0, 0.0, 0.0);
    }
}

