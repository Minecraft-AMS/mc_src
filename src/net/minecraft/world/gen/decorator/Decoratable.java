/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.decorator;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public interface Decoratable<R> {
    public R decorate(ConfiguredDecorator<?> var1);

    default public R applyChance(int chance) {
        return this.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(chance)));
    }

    default public R repeat(IntProvider count) {
        return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
    }

    default public R repeat(int count) {
        return this.repeat(ConstantIntProvider.create(count));
    }

    default public R repeatRandomly(int maxCount) {
        return this.repeat(UniformIntProvider.create(0, maxCount));
    }

    default public R uniformRange(YOffset min, YOffset max) {
        return this.range(new RangeDecoratorConfig(UniformHeightProvider.create(min, max)));
    }

    default public R triangleRange(YOffset min, YOffset max) {
        return this.range(new RangeDecoratorConfig(TrapezoidHeightProvider.create(min, max)));
    }

    default public R range(RangeDecoratorConfig config) {
        return this.decorate(Decorator.RANGE.configure(config));
    }

    default public R spreadHorizontally() {
        return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
    }
}

