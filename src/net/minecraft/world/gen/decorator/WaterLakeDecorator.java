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
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class WaterLakeDecorator
extends Decorator<ChanceDecoratorConfig> {
    public WaterLakeDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
        if (random.nextInt(chanceDecoratorConfig.chance) == 0) {
            int i = random.nextInt(16);
            int j = random.nextInt(chunkGenerator.getMaxY());
            int k = random.nextInt(16);
            return Stream.of(blockPos.add(i, j, k));
        }
        return Stream.empty();
    }
}

