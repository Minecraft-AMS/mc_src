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
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;

public class TagMatchRuleTest
extends RuleTest {
    public static final Codec<TagMatchRuleTest> CODEC = Tag.codec(() -> ServerTagManagerHolder.getTagManager().getBlocks()).fieldOf("tag").xmap(TagMatchRuleTest::new, tagMatchRuleTest -> tagMatchRuleTest.tag).codec();
    private final Tag<Block> tag;

    public TagMatchRuleTest(Tag<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state.isIn(this.tag);
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.TAG_MATCH;
    }
}

