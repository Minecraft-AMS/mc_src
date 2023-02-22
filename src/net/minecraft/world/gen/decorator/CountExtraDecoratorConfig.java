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
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountExtraDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountExtraDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("count").forGetter(countExtraDecoratorConfig -> countExtraDecoratorConfig.count), (App)Codec.FLOAT.fieldOf("extra_chance").forGetter(countExtraDecoratorConfig -> Float.valueOf(countExtraDecoratorConfig.extraChance)), (App)Codec.INT.fieldOf("extra_count").forGetter(countExtraDecoratorConfig -> countExtraDecoratorConfig.extraCount)).apply((Applicative)instance, CountExtraDecoratorConfig::new));
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public CountExtraDecoratorConfig(int count, float extraChance, int extraCount) {
        this.count = count;
        this.extraChance = extraChance;
        this.extraCount = extraCount;
    }
}

