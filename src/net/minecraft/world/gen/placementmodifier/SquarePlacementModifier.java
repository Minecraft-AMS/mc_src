/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class SquarePlacementModifier
extends PlacementModifier {
    private static final SquarePlacementModifier INSTANCE = new SquarePlacementModifier();
    public static final Codec<SquarePlacementModifier> MODIFIER_CODEC = Codec.unit(() -> INSTANCE);

    public static SquarePlacementModifier of() {
        return INSTANCE;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        int i = random.nextInt(16) + pos.getX();
        int j = random.nextInt(16) + pos.getZ();
        return Stream.of(new BlockPos(i, pos.getY(), j));
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.IN_SQUARE;
    }
}

