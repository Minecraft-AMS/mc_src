/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class PlainsFlowerBlockStateProvider
extends BlockStateProvider {
    private static final BlockState[] tulips = new BlockState[]{Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()};
    private static final BlockState[] flowers = new BlockState[]{Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()};

    public PlainsFlowerBlockStateProvider() {
        super(BlockStateProviderType.PLAIN_FLOWER_PROVIDER);
    }

    public <T> PlainsFlowerBlockStateProvider(Dynamic<T> configDeserializer) {
        this();
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0, false);
        if (d < -0.8) {
            return tulips[random.nextInt(tulips.length)];
        }
        if (random.nextInt(3) > 0) {
            return flowers[random.nextInt(flowers.length)];
        }
        return Blocks.DANDELION.getDefaultState();
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString()));
        return (T)new Dynamic(ops, ops.createMap((Map)builder.build())).getValue();
    }
}

