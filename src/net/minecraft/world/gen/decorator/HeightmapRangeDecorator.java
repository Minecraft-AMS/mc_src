/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapRangeDecoratorConfig;

public class HeightmapRangeDecorator
extends Decorator<HeightmapRangeDecoratorConfig> {
    public HeightmapRangeDecorator(Function<Dynamic<?>, ? extends HeightmapRangeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, HeightmapRangeDecoratorConfig heightmapRangeDecoratorConfig, BlockPos blockPos) {
        int i2 = random.nextInt(heightmapRangeDecoratorConfig.max - heightmapRangeDecoratorConfig.min) + heightmapRangeDecoratorConfig.min;
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16);
            int k = random.nextInt(16);
            int l = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + j, blockPos.getZ() + k);
            return new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
        });
    }
}

