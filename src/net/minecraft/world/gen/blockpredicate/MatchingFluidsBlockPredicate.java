/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

class MatchingFluidsBlockPredicate
extends OffsetPredicate {
    private final RegistryEntryList<Fluid> fluids;
    public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingFluidsBlockPredicate.registerOffsetField(instance).and((App)RegistryCodecs.entryList(RegistryKeys.FLUID).fieldOf("fluids").forGetter(predicate -> predicate.fluids)).apply((Applicative)instance, MatchingFluidsBlockPredicate::new));

    public MatchingFluidsBlockPredicate(Vec3i offset, RegistryEntryList<Fluid> fluids) {
        super(offset);
        this.fluids = fluids;
    }

    @Override
    protected boolean test(BlockState state) {
        return state.getFluidState().isIn(this.fluids);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_FLUIDS;
    }
}

