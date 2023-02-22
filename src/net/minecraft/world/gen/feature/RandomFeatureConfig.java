/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.apply2(RandomFeatureConfig::new, (App)RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter(randomFeatureConfig -> randomFeatureConfig.features), (App)ConfiguredFeature.REGISTRY_CODEC.fieldOf("default").forGetter(randomFeatureConfig -> randomFeatureConfig.defaultFeature)));
    public final List<RandomFeatureEntry> features;
    public final Supplier<ConfiguredFeature<?, ?>> defaultFeature;

    public RandomFeatureConfig(List<RandomFeatureEntry> features, ConfiguredFeature<?, ?> defaultFeature) {
        this(features, () -> defaultFeature);
    }

    private RandomFeatureConfig(List<RandomFeatureEntry> features, Supplier<ConfiguredFeature<?, ?>> defaultFeature) {
        this.features = features;
        this.defaultFeature = defaultFeature;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> method_30649() {
        return Stream.concat(this.features.stream().flatMap(randomFeatureEntry -> randomFeatureEntry.feature.get().method_30648()), this.defaultFeature.get().method_30648());
    }
}

