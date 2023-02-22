/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class WeightedBlockStateProvider
extends BlockStateProvider {
    public static final Codec<WeightedBlockStateProvider> CODEC = DataPool.createCodec(BlockState.CODEC).comapFlatMap(WeightedBlockStateProvider::wrap, weightedBlockStateProvider -> weightedBlockStateProvider.states).fieldOf("entries").codec();
    private final DataPool<BlockState> states;

    private static DataResult<WeightedBlockStateProvider> wrap(DataPool<BlockState> states) {
        if (states.isEmpty()) {
            return DataResult.error((String)"WeightedStateProvider with no states");
        }
        return DataResult.success((Object)new WeightedBlockStateProvider(states));
    }

    public WeightedBlockStateProvider(DataPool<BlockState> states) {
        this.states = states;
    }

    public WeightedBlockStateProvider(DataPool.Builder<BlockState> states) {
        this(states.build());
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        return this.states.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
    }
}

