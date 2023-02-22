/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.world.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.slf4j.Logger;

public class GenerationSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GenerationSettings INSTANCE = new GenerationSettings((Map<GenerationStep.Carver, RegistryEntryList<ConfiguredCarver<?>>>)ImmutableMap.of(), (List<RegistryEntryList<PlacedFeature>>)ImmutableList.of());
    public static final MapCodec<GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.simpleMap(GenerationStep.Carver.CODEC, (Codec)ConfiguredCarver.LIST_CODEC.promotePartial(Util.addPrefix("Carver: ", arg_0 -> ((Logger)LOGGER).error(arg_0))), (Keyable)StringIdentifiable.toKeyable(GenerationStep.Carver.values())).fieldOf("carvers").forGetter(generationSettings -> generationSettings.carvers), (App)PlacedFeature.LISTS_CODEC.promotePartial(Util.addPrefix("Features: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).fieldOf("features").forGetter(generationSettings -> generationSettings.features)).apply((Applicative)instance, GenerationSettings::new));
    private final Map<GenerationStep.Carver, RegistryEntryList<ConfiguredCarver<?>>> carvers;
    private final List<RegistryEntryList<PlacedFeature>> features;
    private final Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures;
    private final Supplier<Set<PlacedFeature>> allowedFeatures;

    GenerationSettings(Map<GenerationStep.Carver, RegistryEntryList<ConfiguredCarver<?>>> carvers, List<RegistryEntryList<PlacedFeature>> features) {
        this.carvers = carvers;
        this.features = features;
        this.flowerFeatures = Suppliers.memoize(() -> (List)features.stream().flatMap(RegistryEntryList::stream).map(RegistryEntry::value).flatMap(PlacedFeature::getDecoratedFeatures).filter(configuredFeature -> configuredFeature.feature() == Feature.FLOWER).collect(ImmutableList.toImmutableList()));
        this.allowedFeatures = Suppliers.memoize(() -> features.stream().flatMap(RegistryEntryList::stream).map(RegistryEntry::value).collect(Collectors.toSet()));
    }

    public Iterable<RegistryEntry<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carverStep) {
        return Objects.requireNonNullElseGet((Iterable)this.carvers.get(carverStep), List::of);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures.get();
    }

    public List<RegistryEntryList<PlacedFeature>> getFeatures() {
        return this.features;
    }

    public boolean isFeatureAllowed(PlacedFeature feature) {
        return this.allowedFeatures.get().contains(feature);
    }

    public static class Builder {
        private final Map<GenerationStep.Carver, List<RegistryEntry<ConfiguredCarver<?>>>> carvers = Maps.newLinkedHashMap();
        private final List<List<RegistryEntry<PlacedFeature>>> features = Lists.newArrayList();

        public Builder feature(GenerationStep.Feature featureStep, RegistryEntry<PlacedFeature> feature) {
            return this.feature(featureStep.ordinal(), feature);
        }

        public Builder feature(int stepIndex, RegistryEntry<PlacedFeature> featureEntry) {
            this.addFeatureStep(stepIndex);
            this.features.get(stepIndex).add(featureEntry);
            return this;
        }

        public Builder carver(GenerationStep.Carver carverStep, RegistryEntry<? extends ConfiguredCarver<?>> carver2) {
            this.carvers.computeIfAbsent(carverStep, carver -> Lists.newArrayList()).add(RegistryEntry.upcast(carver2));
            return this;
        }

        private void addFeatureStep(int stepIndex) {
            while (this.features.size() <= stepIndex) {
                this.features.add(Lists.newArrayList());
            }
        }

        public GenerationSettings build() {
            return new GenerationSettings((Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> RegistryEntryList.of((List)entry.getValue()))), (List)this.features.stream().map(RegistryEntryList::of).collect(ImmutableList.toImmutableList()));
        }
    }
}

