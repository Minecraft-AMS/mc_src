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
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature_true").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue), (App)ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature_false").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)).apply((Applicative)instance, RandomBooleanFeatureConfig::new));
    public final Supplier<ConfiguredFeature<?, ?>> featureTrue;
    public final Supplier<ConfiguredFeature<?, ?>> featureFalse;

    public RandomBooleanFeatureConfig(Supplier<ConfiguredFeature<?, ?>> featureTrue, Supplier<ConfiguredFeature<?, ?>> featureFalse) {
        this.featureTrue = featureTrue;
        this.featureFalse = featureFalse;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> method_30649() {
        return Stream.concat(this.featureTrue.get().method_30648(), this.featureFalse.get().method_30648());
    }
}

