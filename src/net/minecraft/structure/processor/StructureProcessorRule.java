/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class StructureProcessorRule {
    public static final Codec<StructureProcessorRule> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RuleTest.TYPE_CODEC.fieldOf("input_predicate").forGetter(structureProcessorRule -> structureProcessorRule.inputPredicate), (App)RuleTest.TYPE_CODEC.fieldOf("location_predicate").forGetter(structureProcessorRule -> structureProcessorRule.locationPredicate), (App)PosRuleTest.field_25007.optionalFieldOf("position_predicate", (Object)AlwaysTruePosRuleTest.INSTANCE).forGetter(structureProcessorRule -> structureProcessorRule.positionPredicate), (App)BlockState.CODEC.fieldOf("output_state").forGetter(structureProcessorRule -> structureProcessorRule.outputState), (App)NbtCompound.CODEC.optionalFieldOf("output_nbt").forGetter(structureProcessorRule -> Optional.ofNullable(structureProcessorRule.outputNbt))).apply((Applicative)instance, StructureProcessorRule::new));
    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final PosRuleTest positionPredicate;
    private final BlockState outputState;
    @Nullable
    private final NbtCompound outputNbt;

    public StructureProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, BlockState state) {
        this(inputPredicate, locationPredicate, AlwaysTruePosRuleTest.INSTANCE, state, Optional.empty());
    }

    public StructureProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, BlockState state) {
        this(inputPredicate, locationPredicate, positionPredicate, state, Optional.empty());
    }

    public StructureProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, BlockState outputState, Optional<NbtCompound> nbt) {
        this.inputPredicate = inputPredicate;
        this.locationPredicate = locationPredicate;
        this.positionPredicate = positionPredicate;
        this.outputState = outputState;
        this.outputNbt = nbt.orElse(null);
    }

    public boolean test(BlockState input, BlockState location, BlockPos blockPos, BlockPos blockPos2, BlockPos pivot, Random random) {
        return this.inputPredicate.test(input, random) && this.locationPredicate.test(location, random) && this.positionPredicate.test(blockPos, blockPos2, pivot, random);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public NbtCompound getOutputNbt() {
        return this.outputNbt;
    }
}

