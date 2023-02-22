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
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;

public class BlockStateMatchRuleTest
extends RuleTest {
    private final BlockState blockState;

    public BlockStateMatchRuleTest(BlockState blockState) {
        this.blockState = blockState;
    }

    public <T> BlockStateMatchRuleTest(Dynamic<T> dynamic) {
        this(BlockState.deserialize(dynamic.get("blockstate").orElseEmptyMap()));
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state == this.blockState;
    }

    @Override
    protected RuleTestType getType() {
        return RuleTestType.BLOCKSTATE_MATCH;
    }

    @Override
    protected <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("blockstate"), (Object)BlockState.serialize(ops, this.blockState).getValue())));
    }
}

