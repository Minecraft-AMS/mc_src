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
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RangeFeatureConfig
implements FeatureConfig {
    public static final Codec<RangeFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)HeightProvider.CODEC.fieldOf("height").forGetter(config -> config.heightProvider)).apply((Applicative)instance, RangeFeatureConfig::new));
    public final HeightProvider heightProvider;

    public RangeFeatureConfig(HeightProvider heightProvider) {
        this.heightProvider = heightProvider;
    }
}

