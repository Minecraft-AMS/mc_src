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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;

public class SimpleBlockFeature
extends Feature<SimpleBlockFeatureConfig> {
    public SimpleBlockFeature(Function<Dynamic<?>, ? extends SimpleBlockFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SimpleBlockFeatureConfig simpleBlockFeatureConfig) {
        if (simpleBlockFeatureConfig.placeOn.contains(iWorld.getBlockState(blockPos.down())) && simpleBlockFeatureConfig.placeIn.contains(iWorld.getBlockState(blockPos)) && simpleBlockFeatureConfig.placeUnder.contains(iWorld.getBlockState(blockPos.up()))) {
            iWorld.setBlockState(blockPos, simpleBlockFeatureConfig.toPlace, 2);
            return true;
        }
        return false;
    }
}
