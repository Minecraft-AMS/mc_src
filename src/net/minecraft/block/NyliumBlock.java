/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;

public class NyliumBlock
extends Block
implements Fertilizable {
    public NyliumBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private static boolean stayAlive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
        return i < world.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!NyliumBlock.stayAlive(state, world, pos)) {
            world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos);
        BlockPos blockPos = pos.up();
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        Registry<ConfiguredFeature<?, ?>> registry = world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
        if (blockState.isOf(Blocks.CRIMSON_NYLIUM)) {
            this.generate(registry, NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, world, chunkGenerator, random, blockPos);
        } else if (blockState.isOf(Blocks.WARPED_NYLIUM)) {
            this.generate(registry, NetherConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, world, chunkGenerator, random, blockPos);
            this.generate(registry, NetherConfiguredFeatures.NETHER_SPROUTS_BONEMEAL, world, chunkGenerator, random, blockPos);
            if (random.nextInt(8) == 0) {
                this.generate(registry, NetherConfiguredFeatures.TWISTING_VINES_BONEMEAL, world, chunkGenerator, random, blockPos);
            }
        }
    }

    private void generate(Registry<ConfiguredFeature<?, ?>> registry, RegistryKey<ConfiguredFeature<?, ?>> key, ServerWorld world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
        registry.getEntry(key).ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate(world, chunkGenerator, random, pos));
    }
}

