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
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class IcebergDecorator
extends Decorator<class_3267> {
    public IcebergDecorator(Function<Dynamic<?>, ? extends class_3267> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, class_3267 arg, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)arg.field_14192) {
            int i = random.nextInt(8) + 4;
            int j = random.nextInt(8) + 4;
            return Stream.of(iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(i, 0, j)));
        }
        return Stream.empty();
    }
}

