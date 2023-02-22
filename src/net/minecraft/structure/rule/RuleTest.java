/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

public abstract class RuleTest {
    public static final Codec<RuleTest> TYPE_CODEC = Registry.RULE_TEST.getCodec().dispatch("predicate_type", RuleTest::getType, RuleTestType::codec);

    public abstract boolean test(BlockState var1, Random var2);

    protected abstract RuleTestType<?> getType();
}

