/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.random.Random;

public class BlockStateMatchRuleTest
extends RuleTest {
    public static final Codec<BlockStateMatchRuleTest> CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchRuleTest::new, ruleTest -> ruleTest.blockState).codec();
    private final BlockState blockState;

    public BlockStateMatchRuleTest(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state == this.blockState;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.BLOCKSTATE_MATCH;
    }
}

