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
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosRuleTest
extends PosRuleTest {
    public static final Codec<LinearPosRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(linearPosRuleTest -> Float.valueOf(linearPosRuleTest.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(linearPosRuleTest -> Float.valueOf(linearPosRuleTest.maxChance)), (App)Codec.INT.fieldOf("min_dist").orElse((Object)0).forGetter(linearPosRuleTest -> linearPosRuleTest.minDistance), (App)Codec.INT.fieldOf("max_dist").orElse((Object)0).forGetter(linearPosRuleTest -> linearPosRuleTest.maxDistance)).apply((Applicative)instance, LinearPosRuleTest::new));
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;

    public LinearPosRuleTest(float minChance, float maxChance, int minDistance, int maxDistance) {
        if (minDistance >= maxDistance) {
            throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
        }
        this.minChance = minChance;
        this.maxChance = maxChance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos pivot, Random random) {
        int i = blockPos2.getManhattanDistance(pivot);
        float f = random.nextFloat();
        return f <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.LINEAR_POS;
    }
}

