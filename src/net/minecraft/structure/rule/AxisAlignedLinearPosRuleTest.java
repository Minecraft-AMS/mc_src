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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class AxisAlignedLinearPosRuleTest
extends PosRuleTest {
    public static final Codec<AxisAlignedLinearPosRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(axisAlignedLinearPosRuleTest -> Float.valueOf(axisAlignedLinearPosRuleTest.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(axisAlignedLinearPosRuleTest -> Float.valueOf(axisAlignedLinearPosRuleTest.maxChance)), (App)Codec.INT.fieldOf("min_dist").orElse((Object)0).forGetter(axisAlignedLinearPosRuleTest -> axisAlignedLinearPosRuleTest.minDistance), (App)Codec.INT.fieldOf("max_dist").orElse((Object)0).forGetter(axisAlignedLinearPosRuleTest -> axisAlignedLinearPosRuleTest.maxDistance), (App)Direction.Axis.CODEC.fieldOf("axis").orElse((Object)Direction.Axis.Y).forGetter(axisAlignedLinearPosRuleTest -> axisAlignedLinearPosRuleTest.axis)).apply((Applicative)instance, AxisAlignedLinearPosRuleTest::new));
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;
    private final Direction.Axis axis;

    public AxisAlignedLinearPosRuleTest(float minChance, float maxChance, int minDistance, int maxDistance, Direction.Axis axis) {
        if (minDistance >= maxDistance) {
            throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
        }
        this.minChance = minChance;
        this.maxChance = maxChance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.axis = axis;
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        float f = Math.abs((blockPos2.getX() - blockPos3.getX()) * direction.getOffsetX());
        float g = Math.abs((blockPos2.getY() - blockPos3.getY()) * direction.getOffsetY());
        float h = Math.abs((blockPos2.getZ() - blockPos3.getZ()) * direction.getOffsetZ());
        int i = (int)(f + g + h);
        float j = random.nextFloat();
        return (double)j <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS;
    }
}

