/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountHeightmapDoubleDecorator
extends Decorator<CountDecoratorConfig> {
    public CountHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
        return IntStream.range(0, countDecoratorConfig.count).mapToObj(i -> {
            int k;
            int j = random.nextInt(16) + blockPos.getX();
            int l = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k = random.nextInt(16) + blockPos.getZ()) * 2;
            if (l <= 0) {
                return null;
            }
            return new BlockPos(j, random.nextInt(l), k);
        }).filter(Objects::nonNull);
    }
}

