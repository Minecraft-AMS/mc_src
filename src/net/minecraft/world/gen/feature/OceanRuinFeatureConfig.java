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
import net.minecraft.world.gen.feature.OceanRuinFeature;

public class OceanRuinFeatureConfig
implements FeatureConfig {
    public static final Codec<OceanRuinFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)OceanRuinFeature.BiomeType.CODEC.fieldOf("biome_temp").forGetter(config -> config.biomeType), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("large_probability").forGetter(config -> Float.valueOf(config.largeProbability)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("cluster_probability").forGetter(config -> Float.valueOf(config.clusterProbability))).apply((Applicative)instance, OceanRuinFeatureConfig::new));
    public final OceanRuinFeature.BiomeType biomeType;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType biomeType, float largeProbability, float clusterProbability) {
        this.biomeType = biomeType;
        this.largeProbability = largeProbability;
        this.clusterProbability = clusterProbability;
    }
}

