/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.placementmodifier;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public abstract class AbstractCountPlacementModifier
extends PlacementModifier {
    protected abstract int getCount(Random var1, BlockPos var2);

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        return IntStream.range(0, this.getCount(random, pos)).mapToObj(i -> pos);
    }
}

