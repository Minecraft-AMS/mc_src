/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.gen.decorator.AbstractRangeDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public class RangeDecorator
extends AbstractRangeDecorator<RangeDecoratorConfig> {
    public RangeDecorator(Codec<RangeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getY(DecoratorContext decoratorContext, Random random, RangeDecoratorConfig rangeDecoratorConfig, int i) {
        return rangeDecoratorConfig.heightProvider.get(random, decoratorContext);
    }
}

