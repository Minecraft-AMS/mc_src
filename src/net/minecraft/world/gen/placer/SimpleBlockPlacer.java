/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class SimpleBlockPlacer
extends BlockPlacer {
    public static final Codec<SimpleBlockPlacer> CODEC = Codec.unit(() -> INSTANCE);
    public static final SimpleBlockPlacer INSTANCE = new SimpleBlockPlacer();

    @Override
    protected BlockPlacerType<?> getType() {
        return BlockPlacerType.SIMPLE_BLOCK_PLACER;
    }

    @Override
    public void generate(WorldAccess world, BlockPos pos, BlockState state, Random random) {
        world.setBlockState(pos, state, 2);
    }
}
