/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class AbstractTreeFeature<T extends FeatureConfig>
extends Feature<T> {
    public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> configFactory, boolean emitNeighborBlockUpdates) {
        super(configFactory, emitNeighborBlockUpdates);
    }

    protected static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Block block = blockState.getBlock();
            return blockState.isAir() || blockState.matches(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || Block.isNaturalDirt(block) || block.matches(BlockTags.LOGS) || block.matches(BlockTags.SAPLINGS) || block == Blocks.VINE;
        });
    }

    protected static boolean isAir(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, BlockState::isAir);
    }

    protected static boolean isNaturalDirt(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> Block.isNaturalDirt(blockState.getBlock()));
    }

    protected static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.WATER);
    }

    protected static boolean isLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.matches(BlockTags.LEAVES));
    }

    protected static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
    }

    protected static boolean isNaturalDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Block block = blockState.getBlock();
            return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK;
        });
    }

    protected static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Block block = blockState.getBlock();
            return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND;
        });
    }

    protected static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Material material = blockState.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    protected void setToDirt(ModifiableTestableWorld world, BlockPos pos) {
        if (!AbstractTreeFeature.isNaturalDirt(world, pos)) {
            this.setBlockState(world, pos, Blocks.DIRT.getDefaultState());
        }
    }

    @Override
    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }

    protected final void setBlockState(Set<BlockPos> logPositions, ModifiableWorld world, BlockPos pos, BlockState state, BlockBox blockBox) {
        this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
        blockBox.encompass(new BlockBox(pos, pos));
        if (BlockTags.LOGS.contains(state.getBlock())) {
            logPositions.add(pos.toImmutable());
        }
    }

    private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
        if (this.emitNeighborBlockUpdates) {
            modifiableWorld.setBlockState(blockPos, blockState, 19);
        } else {
            modifiableWorld.setBlockState(blockPos, blockState, 18);
        }
    }

    @Override
    public final boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, T config) {
        HashSet set = Sets.newHashSet();
        BlockBox blockBox = BlockBox.empty();
        boolean bl = this.generate(set, world, random, pos, blockBox);
        if (blockBox.minX > blockBox.maxX) {
            return false;
        }
        ArrayList list = Lists.newArrayList();
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(blockBox.getBlockCountX(), blockBox.getBlockCountY(), blockBox.getBlockCountZ());
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            if (bl && !set.isEmpty()) {
                for (BlockPos blockPos : Lists.newArrayList((Iterable)set)) {
                    if (blockBox.contains(blockPos)) {
                        ((VoxelSet)voxelSet).set(blockPos.getX() - blockBox.minX, blockPos.getY() - blockBox.minY, blockPos.getZ() - blockBox.minZ, true, true);
                    }
                    for (Direction direction : Direction.values()) {
                        BlockState blockState;
                        pooledMutable.set(blockPos).setOffset(direction);
                        if (set.contains(pooledMutable) || !(blockState = world.getBlockState(pooledMutable)).contains(Properties.DISTANCE_1_7)) continue;
                        ((Set)list.get(0)).add(pooledMutable.toImmutable());
                        this.setBlockStateWithoutUpdatingNeighbors(world, pooledMutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                        if (!blockBox.contains(pooledMutable)) continue;
                        ((VoxelSet)voxelSet).set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
                    }
                }
            }
            for (int k = 1; k < 6; ++k) {
                Set set2 = (Set)list.get(k - 1);
                Set set3 = (Set)list.get(k);
                for (BlockPos blockPos2 : set2) {
                    if (blockBox.contains(blockPos2)) {
                        ((VoxelSet)voxelSet).set(blockPos2.getX() - blockBox.minX, blockPos2.getY() - blockBox.minY, blockPos2.getZ() - blockBox.minZ, true, true);
                    }
                    for (Direction direction2 : Direction.values()) {
                        int l;
                        BlockState blockState2;
                        pooledMutable.set(blockPos2).setOffset(direction2);
                        if (set2.contains(pooledMutable) || set3.contains(pooledMutable) || !(blockState2 = world.getBlockState(pooledMutable)).contains(Properties.DISTANCE_1_7) || (l = blockState2.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                        BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                        this.setBlockStateWithoutUpdatingNeighbors(world, pooledMutable, blockState3);
                        if (blockBox.contains(pooledMutable)) {
                            ((VoxelSet)voxelSet).set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
                        }
                        set3.add(pooledMutable.toImmutable());
                    }
                }
            }
        }
        Structure.method_20532(world, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
        return bl;
    }

    protected abstract boolean generate(Set<BlockPos> var1, ModifiableTestableWorld var2, Random var3, BlockPos var4, BlockBox var5);
}

