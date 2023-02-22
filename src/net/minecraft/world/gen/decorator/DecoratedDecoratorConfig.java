/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class DecoratedDecoratorConfig
implements DecoratorConfig {
    public static final Codec<DecoratedDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredDecorator.CODEC.fieldOf("outer").forGetter(DecoratedDecoratorConfig::getOuter), (App)ConfiguredDecorator.CODEC.fieldOf("inner").forGetter(DecoratedDecoratorConfig::getInner)).apply((Applicative)instance, DecoratedDecoratorConfig::new));
    private final ConfiguredDecorator<?> outer;
    private final ConfiguredDecorator<?> inner;

    public DecoratedDecoratorConfig(ConfiguredDecorator<?> outer, ConfiguredDecorator<?> inner) {
        this.outer = outer;
        this.inner = inner;
    }

    public ConfiguredDecorator<?> getOuter() {
        return this.outer;
    }

    public ConfiguredDecorator<?> getInner() {
        return this.inner;
    }
}

