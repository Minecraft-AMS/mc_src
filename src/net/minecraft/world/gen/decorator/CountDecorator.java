/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.AbstractCountDecorator;

public class CountDecorator
extends AbstractCountDecorator<CountConfig> {
    public CountDecorator(Codec<CountConfig> codec) {
        super(codec);
    }

    @Override
    protected int getCount(Random random, CountConfig countConfig, BlockPos blockPos) {
        return countConfig.getCount().get(random);
    }
}

