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
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class RandomCountRangeDecorator
extends SimpleDecorator<RangeDecoratorConfig> {
    public RandomCountRangeDecorator(Function<Dynamic<?>, ? extends RangeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
        int i2 = random.nextInt(Math.max(rangeDecoratorConfig.count, 1));
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16) + blockPos.getX();
            int k = random.nextInt(16) + blockPos.getZ();
            int l = random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset;
            return new BlockPos(j, l, k);
        });
    }
}

