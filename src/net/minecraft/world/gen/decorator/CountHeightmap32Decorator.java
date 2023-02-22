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

public class CountHeightmap32Decorator
extends Decorator<CountDecoratorConfig> {
    public CountHeightmap32Decorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
        return IntStream.range(0, countDecoratorConfig.count).mapToObj(i -> {
            int k;
            int j = random.nextInt(16);
            int l = iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(j, 0, k = random.nextInt(16))).getY() + 32;
            if (l <= 0) {
                return null;
            }
            int m = random.nextInt(l);
            return blockPos.add(j, m, k);
        }).filter(Objects::nonNull);
    }
}

