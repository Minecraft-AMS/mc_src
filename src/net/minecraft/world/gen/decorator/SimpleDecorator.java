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
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public abstract class SimpleDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public SimpleDecorator(Function<Dynamic<?>, ? extends DC> function) {
        super(function);
    }

    @Override
    public final Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, DC config, BlockPos pos) {
        return this.getPositions(random, config, pos);
    }

    protected abstract Stream<BlockPos> getPositions(Random var1, DC var2, BlockPos var3);
}
