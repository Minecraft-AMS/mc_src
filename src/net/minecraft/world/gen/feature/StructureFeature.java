/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.feature.BastionRemnantFeature;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.NetherFossilFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.RangeFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.VillageFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StructureFeature<C extends FeatureConfig> {
    private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final StructureFeature<StructurePoolFeatureConfig> PILLAGER_OUTPOST = StructureFeature.register("pillager_outpost", new PillagerOutpostFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = StructureFeature.register("mineshaft", new MineshaftFeature(MineshaftFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> MANSION = StructureFeature.register("mansion", new WoodlandMansionFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> JUNGLE_PYRAMID = StructureFeature.register("jungle_pyramid", new JungleTempleFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = StructureFeature.register("desert_pyramid", new DesertPyramidFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> IGLOO = StructureFeature.register("igloo", new IglooFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = StructureFeature.register("ruined_portal", new RuinedPortalFeature(RuinedPortalFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = StructureFeature.register("shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> SWAMP_HUT = StructureFeature.register("swamp_hut", new SwampHutFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = StructureFeature.register("stronghold", new StrongholdFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.STRONGHOLDS);
    public static final StructureFeature<DefaultFeatureConfig> MONUMENT = StructureFeature.register("monument", new OceanMonumentFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = StructureFeature.register("ocean_ruin", new OceanRuinFeature(OceanRuinFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> FORTRESS = StructureFeature.register("fortress", new NetherFortressFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<DefaultFeatureConfig> ENDCITY = StructureFeature.register("endcity", new EndCityFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<ProbabilityConfig> BURIED_TREASURE = StructureFeature.register("buried_treasure", new BuriedTreasureFeature(ProbabilityConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = StructureFeature.register("village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<RangeFeatureConfig> NETHER_FOSSIL = StructureFeature.register("nether_fossil", new NetherFossilFeature(RangeFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<StructurePoolFeatureConfig> BASTION_REMNANT = StructureFeature.register("bastion_remnant", new BastionRemnantFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final int field_31518 = 8;
    private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec = RecordCodecBuilder.create(instance -> instance.group((App)configCodec.fieldOf("config").forGetter(configuredStructureFeature -> configuredStructureFeature.config), (App)RegistryCodecs.entryList(Registry.BIOME_KEY).fieldOf("biomes").forGetter(ConfiguredStructureFeature::getBiomes), (App)Codec.BOOL.optionalFieldOf("adapt_noise", (Object)false).forGetter(configuredStructureFeature -> configuredStructureFeature.field_37144), (App)Codec.simpleMap(SpawnGroup.CODEC, StructureSpawns.CODEC, (Keyable)StringIdentifiable.toKeyable(SpawnGroup.values())).fieldOf("spawn_overrides").forGetter(configuredStructureFeature -> configuredStructureFeature.field_37143)).apply((Applicative)instance, (featureConfig, registryEntryList, boolean_, map) -> new ConfiguredStructureFeature<FeatureConfig, StructureFeature>(this, (FeatureConfig)featureConfig, (RegistryEntryList<Biome>)registryEntryList, (boolean)boolean_, (Map<SpawnGroup, StructureSpawns>)map)));
    private final StructureGeneratorFactory<C> piecesGenerator;
    private final PostPlacementProcessor postProcessor;

    private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
        STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
        return (F)Registry.register(Registry.STRUCTURE_FEATURE, name, structureFeature);
    }

    public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator) {
        this(configCodec, piecesGenerator, PostPlacementProcessor.EMPTY);
    }

    public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator, PostPlacementProcessor postPlacementProcessor) {
        this.piecesGenerator = piecesGenerator;
        this.postProcessor = postPlacementProcessor;
    }

    public GenerationStep.Feature getGenerationStep() {
        return STRUCTURE_TO_GENERATION_STEP.get(this);
    }

    public static void init() {
    }

    @Nullable
    public static StructureStart readStructureStart(StructureContext context, NbtCompound nbt, long worldSeed) {
        String string = nbt.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.DEFAULT;
        }
        Registry<ConfiguredStructureFeature<?, ?>> registry = context.registryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        ConfiguredStructureFeature<?, ?> configuredStructureFeature = registry.get(new Identifier(string));
        if (configuredStructureFeature == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        ChunkPos chunkPos = new ChunkPos(nbt.getInt("ChunkX"), nbt.getInt("ChunkZ"));
        int i = nbt.getInt("references");
        NbtList nbtList = nbt.getList("Children", 10);
        try {
            StructurePiecesList structurePiecesList = StructurePiecesList.fromNbt(nbtList, context);
            if (configuredStructureFeature.feature == MONUMENT) {
                structurePiecesList = OceanMonumentFeature.modifyPiecesOnRead(chunkPos, worldSeed, structurePiecesList);
            }
            return new StructureStart(configuredStructureFeature, chunkPos, i, structurePiecesList);
        }
        catch (Exception exception) {
            LOGGER.error("Failed Start with id {}", (Object)string, (Object)exception);
            return null;
        }
    }

    public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> getCodec() {
        return this.codec;
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config, TagKey<Biome> biomeTag) {
        return this.configure(config, biomeTag, false);
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config, TagKey<Biome> biomeTag, boolean bl) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, config, BuiltinRegistries.BIOME.getOrCreateEntryList(biomeTag), bl, Map.of());
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config, TagKey<Biome> biomeTag, Map<SpawnGroup, StructureSpawns> map) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, config, BuiltinRegistries.BIOME.getOrCreateEntryList(biomeTag), false, map);
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config, TagKey<Biome> biomeTag, boolean bl, Map<SpawnGroup, StructureSpawns> map) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, config, BuiltinRegistries.BIOME.getOrCreateEntryList(biomeTag), bl, map);
    }

    public static BlockPos getLocatedPos(RandomSpreadStructurePlacement placement, ChunkPos chunkPos) {
        return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()).add(placement.locateOffset());
    }

    public boolean canGenerate(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long worldSeed, ChunkPos pos, C config, HeightLimitView world, Predicate<RegistryEntry<Biome>> biomePredicate) {
        return this.piecesGenerator.createGenerator(new StructureGeneratorFactory.Context<C>(chunkGenerator, biomeSource, worldSeed, pos, config, world, biomePredicate, structureManager, registryManager)).isPresent();
    }

    public StructureGeneratorFactory<C> method_41138() {
        return this.piecesGenerator;
    }

    public PostPlacementProcessor getPostProcessor() {
        return this.postProcessor;
    }
}

