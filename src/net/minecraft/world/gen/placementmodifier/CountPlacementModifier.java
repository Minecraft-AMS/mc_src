/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class CountPlacementModifier
extends AbstractCountPlacementModifier {
    public static final Codec<CountPlacementModifier> MODIFIER_CODEC = IntProvider.createValidatingCodec(0, 256).fieldOf("count").xmap(CountPlacementModifier::new, countPlacementModifier -> countPlacementModifier.count).codec();
    private final IntProvider count;

    private CountPlacementModifier(IntProvider count) {
        this.count = count;
    }

    public static CountPlacementModifier of(IntProvider count) {
        return new CountPlacementModifier(count);
    }

    public static CountPlacementModifier of(int count) {
        return CountPlacementModifier.of(ConstantIntProvider.create(count));
    }

    @Override
    protected int getCount(Random random, BlockPos pos) {
        return this.count.get(random);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.COUNT;
    }
}

