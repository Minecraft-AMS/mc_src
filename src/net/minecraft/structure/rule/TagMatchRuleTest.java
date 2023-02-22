/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagMatchRuleTest
extends RuleTest {
    private final Tag<Block> tag;

    public TagMatchRuleTest(Tag<Block> tag) {
        this.tag = tag;
    }

    public <T> TagMatchRuleTest(Dynamic<T> dynamic) {
        this(BlockTags.getContainer().get(new Identifier(dynamic.get("tag").asString(""))));
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state.matches(this.tag);
    }

    @Override
    protected RuleTestType getType() {
        return RuleTestType.TAG_MATCH;
    }

    @Override
    protected <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("tag"), (Object)ops.createString(this.tag.getId().toString()))));
    }
}

