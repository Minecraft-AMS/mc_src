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
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RangeDecoratorConfig
implements DecoratorConfig,
FeatureConfig {
    public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)HeightProvider.CODEC.fieldOf("height").forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.heightProvider)).apply((Applicative)instance, RangeDecoratorConfig::new));
    public final HeightProvider heightProvider;

    public RangeDecoratorConfig(HeightProvider heightProvider) {
        this.heightProvider = heightProvider;
    }
}

