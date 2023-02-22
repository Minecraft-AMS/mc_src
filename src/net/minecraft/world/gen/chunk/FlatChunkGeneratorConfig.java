/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.slf4j.Logger;

public class FlatChunkGeneratorConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biomeRegistry), (App)RegistryCodecs.entryList(Registry.STRUCTURE_SET_KEY).optionalFieldOf("structure_overrides").forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.field_37145), (App)FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers), (App)Codec.BOOL.fieldOf("lakes").orElse((Object)false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasLakes), (App)Codec.BOOL.fieldOf("features").orElse((Object)false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasFeatures), (App)Biome.REGISTRY_CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter(flatChunkGeneratorConfig -> Optional.of(flatChunkGeneratorConfig.biome))).apply((Applicative)instance, FlatChunkGeneratorConfig::new)).comapFlatMap(FlatChunkGeneratorConfig::checkHeight, Function.identity()).stable();
    private final Registry<Biome> biomeRegistry;
    private final Optional<RegistryEntryList<StructureSet>> field_37145;
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private RegistryEntry<Biome> biome;
    private final List<BlockState> layerBlocks;
    private boolean hasNoTerrain;
    private boolean hasFeatures;
    private boolean hasLakes;

    private static DataResult<FlatChunkGeneratorConfig> checkHeight(FlatChunkGeneratorConfig config) {
        int i = config.layers.stream().mapToInt(FlatChunkGeneratorLayer::getThickness).sum();
        if (i > DimensionType.MAX_HEIGHT) {
            return DataResult.error((String)("Sum of layer heights is > " + DimensionType.MAX_HEIGHT), (Object)config);
        }
        return DataResult.success((Object)config);
    }

    private FlatChunkGeneratorConfig(Registry<Biome> biomeRegistry, Optional<RegistryEntryList<StructureSet>> optional, List<FlatChunkGeneratorLayer> layers, boolean hasLakes, boolean hasFeatures, Optional<RegistryEntry<Biome>> biome) {
        this(optional, biomeRegistry);
        if (hasLakes) {
            this.enableLakes();
        }
        if (hasFeatures) {
            this.enableFeatures();
        }
        this.layers.addAll(layers);
        this.updateLayerBlocks();
        if (biome.isEmpty()) {
            LOGGER.error("Unknown biome, defaulting to plains");
            this.biome = biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS);
        } else {
            this.biome = biome.get();
        }
    }

    public FlatChunkGeneratorConfig(Optional<RegistryEntryList<StructureSet>> optional, Registry<Biome> biomeRegistry) {
        this.biomeRegistry = biomeRegistry;
        this.field_37145 = optional;
        this.biome = biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS);
        this.layerBlocks = Lists.newArrayList();
    }

    public FlatChunkGeneratorConfig withLayers(List<FlatChunkGeneratorLayer> layers, Optional<RegistryEntryList<StructureSet>> optional) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(optional, this.biomeRegistry);
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : layers) {
            flatChunkGeneratorConfig.layers.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
            flatChunkGeneratorConfig.updateLayerBlocks();
        }
        flatChunkGeneratorConfig.setBiome(this.biome);
        if (this.hasFeatures) {
            flatChunkGeneratorConfig.enableFeatures();
        }
        if (this.hasLakes) {
            flatChunkGeneratorConfig.enableLakes();
        }
        return flatChunkGeneratorConfig;
    }

    public void enableFeatures() {
        this.hasFeatures = true;
    }

    public void enableLakes() {
        this.hasLakes = true;
    }

    public RegistryEntry<Biome> createBiome() {
        int i;
        List<Object> list;
        boolean bl;
        Biome biome = this.getBiome().value();
        GenerationSettings generationSettings = biome.getGenerationSettings();
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        if (this.hasLakes) {
            builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND);
            builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_SURFACE);
        }
        boolean bl2 = bl = (!this.hasNoTerrain || this.biome.matchesKey(BiomeKeys.THE_VOID)) && this.hasFeatures;
        if (bl) {
            list = generationSettings.getFeatures();
            for (i = 0; i < list.size(); ++i) {
                if (i == GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() || i == GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) continue;
                RegistryEntryList registryEntryList = (RegistryEntryList)list.get(i);
                for (RegistryEntry registryEntry : registryEntryList) {
                    builder.feature(i, (RegistryEntry<PlacedFeature>)registryEntry);
                }
            }
        }
        list = this.getLayerBlocks();
        for (i = 0; i < list.size(); ++i) {
            BlockState blockState = (BlockState)list.get(i);
            if (Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) continue;
            list.set(i, null);
            builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, PlacedFeatures.createEntry(Feature.FILL_LAYER, new FillLayerFeatureConfig(i, blockState), new PlacementModifier[0]));
        }
        return RegistryEntry.of(Biome.Builder.copy(biome).generationSettings(builder.build()).build());
    }

    public Optional<RegistryEntryList<StructureSet>> method_41139() {
        return this.field_37145;
    }

    public RegistryEntry<Biome> getBiome() {
        return this.biome;
    }

    public void setBiome(RegistryEntry<Biome> registryEntry) {
        this.biome = registryEntry;
    }

    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }

    public List<BlockState> getLayerBlocks() {
        return this.layerBlocks;
    }

    public void updateLayerBlocks() {
        this.layerBlocks.clear();
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
            for (int i = 0; i < flatChunkGeneratorLayer.getThickness(); ++i) {
                this.layerBlocks.add(flatChunkGeneratorLayer.getBlockState());
            }
        }
        this.hasNoTerrain = this.layerBlocks.stream().allMatch(state -> state.isOf(Blocks.AIR));
    }

    public static FlatChunkGeneratorConfig getDefaultConfig(Registry<Biome> biomeRegistry, Registry<StructureSet> registry) {
        RegistryEntryList.Direct registryEntryList = RegistryEntryList.of(registry.entryOf(StructureSetKeys.STRONGHOLDS), registry.entryOf(StructureSetKeys.VILLAGES));
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(Optional.of(registryEntryList), biomeRegistry);
        flatChunkGeneratorConfig.biome = biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS);
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
        flatChunkGeneratorConfig.updateLayerBlocks();
        return flatChunkGeneratorConfig;
    }
}

