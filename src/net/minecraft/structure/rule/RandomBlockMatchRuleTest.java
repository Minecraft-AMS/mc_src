/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;

public class RandomBlockMatchRuleTest
extends RuleTest {
    public static final Codec<RandomBlockMatchRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.BLOCK.fieldOf("block").forGetter(randomBlockMatchRuleTest -> randomBlockMatchRuleTest.block), (App)Codec.FLOAT.fieldOf("probability").forGetter(randomBlockMatchRuleTest -> Float.valueOf(randomBlockMatchRuleTest.probability))).apply((Applicative)instance, RandomBlockMatchRuleTest::new));
    private final Block block;
    private final float probability;

    public RandomBlockMatchRuleTest(Block block, float probability) {
        this.block = block;
        this.probability = probability;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state.isOf(this.block) && random.nextFloat() < this.probability;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.RANDOM_BLOCK_MATCH;
    }
}

