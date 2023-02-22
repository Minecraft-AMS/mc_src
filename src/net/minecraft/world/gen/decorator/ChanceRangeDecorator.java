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
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChanceRangeDecorator
extends SimpleDecorator<ChanceRangeDecoratorConfig> {
    public ChanceRangeDecorator(Function<Dynamic<?>, ? extends ChanceRangeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, ChanceRangeDecoratorConfig chanceRangeDecoratorConfig, BlockPos blockPos) {
        if (random.nextFloat() < chanceRangeDecoratorConfig.chance) {
            int i = random.nextInt(16);
            int j = random.nextInt(chanceRangeDecoratorConfig.top - chanceRangeDecoratorConfig.topOffset) + chanceRangeDecoratorConfig.bottomOffset;
            int k = random.nextInt(16);
            return Stream.of(blockPos.add(i, j, k));
        }
        return Stream.empty();
    }
}
