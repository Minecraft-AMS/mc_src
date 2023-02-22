/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerationSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GenerationSettings INSTANCE = new GenerationSettings(() -> ConfiguredSurfaceBuilders.NOPE, (Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>>)ImmutableMap.of(), (List<List<Supplier<ConfiguredFeature<?, ?>>>>)ImmutableList.of(), (List<Supplier<ConfiguredStructureFeature<?, ?>>>)ImmutableList.of());
    public static final MapCodec<GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ConfiguredSurfaceBuilder.REGISTRY_CODEC.fieldOf("surface_builder").forGetter(generationSettings -> generationSettings.surfaceBuilder), (App)Codec.simpleMap(GenerationStep.Carver.CODEC, (Codec)ConfiguredCarver.field_26755.promotePartial(Util.method_29188("Carver: ", arg_0 -> ((Logger)LOGGER).error(arg_0))), (Keyable)StringIdentifiable.method_28142(GenerationStep.Carver.values())).fieldOf("carvers").forGetter(generationSettings -> generationSettings.carvers), (App)ConfiguredFeature.field_26756.promotePartial(Util.method_29188("Feature: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).listOf().fieldOf("features").forGetter(generationSettings -> generationSettings.features), (App)ConfiguredStructureFeature.field_26757.promotePartial(Util.method_29188("Structure start: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).fieldOf("starts").forGetter(generationSettings -> generationSettings.structureFeatures)).apply((Applicative)instance, GenerationSettings::new));
    private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
    private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
    private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
    private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures;
    private final List<ConfiguredFeature<?, ?>> flowerFeatures;

    private GenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder, Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers, List<List<Supplier<ConfiguredFeature<?, ?>>>> features, List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures) {
        this.surfaceBuilder = surfaceBuilder;
        this.carvers = carvers;
        this.features = features;
        this.structureFeatures = structureFeatures;
        this.flowerFeatures = (List)features.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(ConfiguredFeature::method_30648).filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER).collect(ImmutableList.toImmutableList());
    }

    public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carverStep) {
        return (List)this.carvers.getOrDefault(carverStep, (List<Supplier<ConfiguredCarver<?>>>)ImmutableList.of());
    }

    public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
        return this.structureFeatures.stream().anyMatch(supplier -> ((ConfiguredStructureFeature)supplier.get()).feature == structureFeature);
    }

    public Collection<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureFeatures() {
        return this.structureFeatures;
    }

    public ConfiguredStructureFeature<?, ?> method_30978(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        return (ConfiguredStructureFeature)DataFixUtils.orElse(this.structureFeatures.stream().map(Supplier::get).filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature).findAny(), configuredStructureFeature);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
        return this.features;
    }

    public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public SurfaceConfig getSurfaceConfig() {
        return this.surfaceBuilder.get().getConfig();
    }

    public static class Builder {
        private Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = Optional.empty();
        private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers = Maps.newLinkedHashMap();
        private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = Lists.newArrayList();
        private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures = Lists.newArrayList();

        public Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
            return this.surfaceBuilder(() -> surfaceBuilder);
        }

        public Builder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilderSupplier) {
            this.surfaceBuilder = Optional.of(surfaceBuilderSupplier);
            return this;
        }

        public Builder feature(GenerationStep.Feature featureStep, ConfiguredFeature<?, ?> feature) {
            return this.feature(featureStep.ordinal(), () -> feature);
        }

        public Builder feature(int stepIndex, Supplier<ConfiguredFeature<?, ?>> featureSupplier) {
            this.addFeatureStep(stepIndex);
            this.features.get(stepIndex).add(featureSupplier);
            return this;
        }

        public <C extends CarverConfig> Builder carver(GenerationStep.Carver carverStep, ConfiguredCarver<C> carver2) {
            this.carvers.computeIfAbsent(carverStep, carver -> Lists.newArrayList()).add(() -> carver2);
            return this;
        }

        public Builder structureFeature(ConfiguredStructureFeature<?, ?> structureFeature) {
            this.structureFeatures.add(() -> structureFeature);
            return this;
        }

        private void addFeatureStep(int stepIndex) {
            while (this.features.size() <= stepIndex) {
                this.features.add(Lists.newArrayList());
            }
        }

        public GenerationSettings build() {
            return new GenerationSettings(this.surfaceBuilder.orElseThrow(() -> new IllegalStateException("Missing surface builder")), (Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableList.copyOf((Collection)((Collection)entry.getValue())))), (List)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()), (List)ImmutableList.copyOf(this.structureFeatures));
        }
    }
}

