/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class StructureProcessorRule {
    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag tag;

    public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, BlockState blockState) {
        this(ruleTest, ruleTest2, blockState, null);
    }

    public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, BlockState blockState, @Nullable CompoundTag compoundTag) {
        this.inputPredicate = ruleTest;
        this.locationPredicate = ruleTest2;
        this.outputState = blockState;
        this.tag = compoundTag;
    }

    public boolean test(BlockState input, BlockState location, Random random) {
        return this.inputPredicate.test(input, random) && this.locationPredicate.test(location, random);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public <T> Dynamic<T> method_16764(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("input_predicate"), (Object)this.inputPredicate.serializeWithId(dynamicOps).getValue(), (Object)dynamicOps.createString("location_predicate"), (Object)this.locationPredicate.serializeWithId(dynamicOps).getValue(), (Object)dynamicOps.createString("output_state"), (Object)BlockState.serialize(dynamicOps, this.outputState).getValue()));
        if (this.tag == null) {
            return new Dynamic(dynamicOps, object);
        }
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)this.tag).convert(dynamicOps).getValue()));
    }

    public static <T> StructureProcessorRule method_16765(Dynamic<T> dynamic2) {
        Dynamic dynamic22 = dynamic2.get("input_predicate").orElseEmptyMap();
        Dynamic dynamic3 = dynamic2.get("location_predicate").orElseEmptyMap();
        RuleTest ruleTest = DynamicDeserializer.deserialize(dynamic22, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        RuleTest ruleTest2 = DynamicDeserializer.deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        BlockState blockState = BlockState.deserialize(dynamic2.get("output_state").orElseEmptyMap());
        CompoundTag compoundTag = dynamic2.get("output_nbt").map(dynamic -> (Tag)dynamic.convert((DynamicOps)NbtOps.INSTANCE).getValue()).orElse(null);
        return new StructureProcessorRule(ruleTest, ruleTest2, blockState, compoundTag);
    }
}

