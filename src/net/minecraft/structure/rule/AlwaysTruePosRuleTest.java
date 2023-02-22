/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class AlwaysTruePosRuleTest
extends PosRuleTest {
    public static final Codec<AlwaysTruePosRuleTest> CODEC = Codec.unit(() -> INSTANCE);
    public static final AlwaysTruePosRuleTest INSTANCE = new AlwaysTruePosRuleTest();

    private AlwaysTruePosRuleTest() {
    }

    @Override
    public boolean test(BlockPos originalPos, BlockPos currentPos, BlockPos pivot, Random random) {
        return true;
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.ALWAYS_TRUE;
    }
}

