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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountHeight64Decorator
extends Decorator<CountDecoratorConfig> {
    public CountHeight64Decorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
        return IntStream.range(0, countDecoratorConfig.count).mapToObj(i -> {
            int j = random.nextInt(16);
            int k = 64;
            int l = random.nextInt(16);
            return blockPos.add(j, 64, l);
        });
    }
}

