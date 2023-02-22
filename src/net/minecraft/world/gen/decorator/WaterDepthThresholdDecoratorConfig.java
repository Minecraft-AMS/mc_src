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

public class WaterDepthThresholdDecoratorConfig
implements DecoratorConfig {
    public static final Codec<WaterDepthThresholdDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("max_water_depth").forGetter(waterDepthThresholdDecoratorConfig -> waterDepthThresholdDecoratorConfig.maxWaterDepth)).apply((Applicative)instance, WaterDepthThresholdDecoratorConfig::new));
    public final int maxWaterDepth;

    public WaterDepthThresholdDecoratorConfig(int maxWaterDepth) {
        this.maxWaterDepth = maxWaterDepth;
    }
}

