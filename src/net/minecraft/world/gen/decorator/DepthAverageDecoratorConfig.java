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

public class DepthAverageDecoratorConfig
implements DecoratorConfig {
    public static final Codec<DepthAverageDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("baseline").forGetter(depthAverageDecoratorConfig -> depthAverageDecoratorConfig.baseline), (App)Codec.INT.fieldOf("spread").forGetter(depthAverageDecoratorConfig -> depthAverageDecoratorConfig.spread)).apply((Applicative)instance, DepthAverageDecoratorConfig::new));
    public final int baseline;
    public final int spread;

    public DepthAverageDecoratorConfig(int baseline, int spread) {
        this.baseline = baseline;
        this.spread = spread;
    }
}

