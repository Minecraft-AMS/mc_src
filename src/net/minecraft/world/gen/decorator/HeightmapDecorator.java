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
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class HeightmapDecorator
extends Decorator<NopeDecoratorConfig> {
    public HeightmapDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
        int i = random.nextInt(16);
        int j = random.nextInt(16);
        int k = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + i, blockPos.getZ() + j);
        return Stream.of(new BlockPos(blockPos.getX() + i, k, blockPos.getZ() + j));
    }
}

