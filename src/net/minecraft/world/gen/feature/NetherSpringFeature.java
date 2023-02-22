/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NetherSpringFeatureConfig;

public class NetherSpringFeature
extends Feature<NetherSpringFeatureConfig> {
    private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();

    public NetherSpringFeature(Function<Dynamic<?>, ? extends NetherSpringFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, NetherSpringFeatureConfig netherSpringFeatureConfig) {
        if (iWorld.getBlockState(blockPos.up()) != NETHERRACK) {
            return false;
        }
        if (!iWorld.getBlockState(blockPos).isAir() && iWorld.getBlockState(blockPos) != NETHERRACK) {
            return false;
        }
        int i = 0;
        if (iWorld.getBlockState(blockPos.west()) == NETHERRACK) {
            ++i;
        }
        if (iWorld.getBlockState(blockPos.east()) == NETHERRACK) {
            ++i;
        }
        if (iWorld.getBlockState(blockPos.north()) == NETHERRACK) {
            ++i;
        }
        if (iWorld.getBlockState(blockPos.south()) == NETHERRACK) {
            ++i;
        }
        if (iWorld.getBlockState(blockPos.down()) == NETHERRACK) {
            ++i;
        }
        int j = 0;
        if (iWorld.isAir(blockPos.west())) {
            ++j;
        }
        if (iWorld.isAir(blockPos.east())) {
            ++j;
        }
        if (iWorld.isAir(blockPos.north())) {
            ++j;
        }
        if (iWorld.isAir(blockPos.south())) {
            ++j;
        }
        if (iWorld.isAir(blockPos.down())) {
            ++j;
        }
        if (!netherSpringFeatureConfig.insideRock && i == 4 && j == 1 || i == 5) {
            iWorld.setBlockState(blockPos, Blocks.LAVA.getDefaultState(), 2);
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.LAVA, 0);
        }
        return true;
    }
}

