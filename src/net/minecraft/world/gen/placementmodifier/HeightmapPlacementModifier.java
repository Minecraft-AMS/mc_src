/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class HeightmapPlacementModifier
extends PlacementModifier {
    public static final Codec<HeightmapPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(heightmapPlacementModifier -> heightmapPlacementModifier.heightmap)).apply((Applicative)instance, HeightmapPlacementModifier::new));
    private final Heightmap.Type heightmap;

    private HeightmapPlacementModifier(Heightmap.Type heightmap) {
        this.heightmap = heightmap;
    }

    public static HeightmapPlacementModifier of(Heightmap.Type heightmap) {
        return new HeightmapPlacementModifier(heightmap);
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        int j;
        int i = pos.getX();
        int k = context.getTopY(this.heightmap, i, j = pos.getZ());
        if (k > context.getBottomY()) {
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.of(new BlockPos[0]);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.HEIGHTMAP;
    }
}

