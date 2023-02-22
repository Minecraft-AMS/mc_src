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

public class ChanceHeightmapDoubleDecorator
extends Decorator<class_3267> {
    public ChanceHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends class_3267> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, class_3267 arg, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)arg.field_14192) {
            int j;
            int i = random.nextInt(16);
            int k = iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(i, 0, j = random.nextInt(16))).getY() * 2;
            if (k <= 0) {
                return Stream.empty();
            }
            int l = random.nextInt(k);
            return Stream.of(blockPos.add(i, l, j));
        }
        return Stream.empty();
    }
}

