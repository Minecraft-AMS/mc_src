/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest
extends RuleTest {
    public static final Codec<BlockMatchRuleTest> CODEC = Registry.BLOCK.fieldOf("block").xmap(BlockMatchRuleTest::new, blockMatchRuleTest -> blockMatchRuleTest.block).codec();
    private final Block block;

    public BlockMatchRuleTest(Block block) {
        this.block = block;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state.isOf(this.block);
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.BLOCK_MATCH;
    }
}

