/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class DepthAverageDecorator
extends SimpleDecorator<DepthAverageDecoratorConfig> {
    public DepthAverageDecorator(Codec<DepthAverageDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, DepthAverageDecoratorConfig depthAverageDecoratorConfig, BlockPos blockPos) {
        int i = depthAverageDecoratorConfig.baseline;
        int j = depthAverageDecoratorConfig.spread;
        int k = blockPos.getX();
        int l = blockPos.getZ();
        int m = random.nextInt(j) + random.nextInt(j) - j + i;
        return Stream.of(new BlockPos(k, m, l));
    }
}

