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
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class LightGemChanceDecorator
extends SimpleDecorator<CountDecoratorConfig> {
    public LightGemChanceDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
        return IntStream.range(0, random.nextInt(random.nextInt(countDecoratorConfig.count) + 1)).mapToObj(i -> {
            int j = random.nextInt(16) + blockPos.getX();
            int k = random.nextInt(16) + blockPos.getZ();
            int l = random.nextInt(120) + 4;
            return new BlockPos(j, l, k);
        });
    }
}

