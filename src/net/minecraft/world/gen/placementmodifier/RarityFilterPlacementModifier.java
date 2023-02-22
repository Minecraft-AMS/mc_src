/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class RarityFilterPlacementModifier
extends AbstractConditionalPlacementModifier {
    public static final Codec<RarityFilterPlacementModifier> MODIFIER_CODEC = Codecs.POSITIVE_INT.fieldOf("chance").xmap(RarityFilterPlacementModifier::new, rarityFilterPlacementModifier -> rarityFilterPlacementModifier.chance).codec();
    private final int chance;

    private RarityFilterPlacementModifier(int chance) {
        this.chance = chance;
    }

    public static RarityFilterPlacementModifier of(int chance) {
        return new RarityFilterPlacementModifier(chance);
    }

    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        return random.nextFloat() < 1.0f / (float)this.chance;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.RARITY_FILTER;
    }
}

