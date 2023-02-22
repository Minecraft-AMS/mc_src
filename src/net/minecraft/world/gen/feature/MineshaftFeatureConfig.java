/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig
implements FeatureConfig {
    public static final Codec<MineshaftFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").forGetter(mineshaftFeatureConfig -> Float.valueOf(mineshaftFeatureConfig.probability)), (App)MineshaftFeature.Type.CODEC.fieldOf("type").forGetter(mineshaftFeatureConfig -> mineshaftFeatureConfig.type)).apply((Applicative)instance, MineshaftFeatureConfig::new));
    public final float probability;
    public final MineshaftFeature.Type type;

    public MineshaftFeatureConfig(float probability, MineshaftFeature.Type type) {
        this.probability = probability;
        this.type = type;
    }
}

