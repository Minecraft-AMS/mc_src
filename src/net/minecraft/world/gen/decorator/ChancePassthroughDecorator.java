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
import net.minecraft.class_3267;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChancePassthroughDecorator
extends SimpleDecorator<class_3267> {
    public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends class_3267> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, class_3267 arg, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)arg.field_14192) {
            return Stream.of(blockPos);
        }
        return Stream.empty();
    }
}

