/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public abstract class BlockStateProvider {
    public static final Codec<BlockStateProvider> TYPE_CODEC = Registry.BLOCK_STATE_PROVIDER_TYPE.getCodec().dispatch(BlockStateProvider::getType, BlockStateProviderType::getCodec);

    public static SimpleBlockStateProvider of(BlockState state) {
        return new SimpleBlockStateProvider(state);
    }

    public static SimpleBlockStateProvider of(Block block) {
        return new SimpleBlockStateProvider(block.getDefaultState());
    }

    protected abstract BlockStateProviderType<?> getType();

    public abstract BlockState getBlockState(Random var1, BlockPos var2);
}

