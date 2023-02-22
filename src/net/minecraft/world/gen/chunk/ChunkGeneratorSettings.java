/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorSettings {
    public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorSettings::getStructuresConfig), (App)GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::getGenerationShapeConfig), (App)BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::getDefaultBlock), (App)BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::getDefaultFluid), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorSettings::getBedrockCeilingY), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorSettings::getBedrockFloorY), (App)Codec.intRange((int)0, (int)255).fieldOf("sea_level").forGetter(ChunkGeneratorSettings::getSeaLevel), (App)Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorSettings::isMobGenerationDisabled)).apply((Applicative)instance, ChunkGeneratorSettings::new));
    public static final Codec<Supplier<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.NOISE_SETTINGS_WORLDGEN, CODEC);
    private final StructuresConfig structuresConfig;
    private final GenerationShapeConfig generationShapeConfig;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final int bedrockCeilingY;
    private final int bedrockFloorY;
    private final int seaLevel;
    private final boolean mobGenerationDisabled;
    public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("overworld"));
    public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("amplified"));
    public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("nether"));
    public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("end"));
    public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("caves"));
    public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("floating_islands"));
    private static final ChunkGeneratorSettings INSTANCE = ChunkGeneratorSettings.register(OVERWORLD, ChunkGeneratorSettings.createSurfaceSettings(new StructuresConfig(true), false, OVERWORLD.getValue()));

    private ChunkGeneratorSettings(StructuresConfig structuresConfig, GenerationShapeConfig generationShapeConfig, BlockState defaultBlock, BlockState defaultFluid, int bedrockCeilingY, int bedrockFloorY, int seaLevel, boolean mobGenerationDisabled) {
        this.structuresConfig = structuresConfig;
        this.generationShapeConfig = generationShapeConfig;
        this.defaultBlock = defaultBlock;
        this.defaultFluid = defaultFluid;
        this.bedrockCeilingY = bedrockCeilingY;
        this.bedrockFloorY = bedrockFloorY;
        this.seaLevel = seaLevel;
        this.mobGenerationDisabled = mobGenerationDisabled;
    }

    public StructuresConfig getStructuresConfig() {
        return this.structuresConfig;
    }

    public GenerationShapeConfig getGenerationShapeConfig() {
        return this.generationShapeConfig;
    }

    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }

    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }

    public int getBedrockCeilingY() {
        return this.bedrockCeilingY;
    }

    public int getBedrockFloorY() {
        return this.bedrockFloorY;
    }

    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Deprecated
    protected boolean isMobGenerationDisabled() {
        return this.mobGenerationDisabled;
    }

    public boolean equals(RegistryKey<ChunkGeneratorSettings> registryKey) {
        return Objects.equals(this, BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(registryKey));
    }

    private static ChunkGeneratorSettings register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
        BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
        return settings;
    }

    public static ChunkGeneratorSettings getInstance() {
        return INSTANCE;
    }

    private static ChunkGeneratorSettings createIslandSettings(StructuresConfig structuresConfig, BlockState defaultBlock, BlockState defaultFluid, Identifier id, boolean mobGenerationDisabled, boolean islandNoiseOverride) {
        return new ChunkGeneratorSettings(structuresConfig, new GenerationShapeConfig(128, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-3000, 64, -46), new SlideConfig(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, islandNoiseOverride, false), defaultBlock, defaultFluid, -10, -10, 0, mobGenerationDisabled);
    }

    private static ChunkGeneratorSettings createUndergroundSettings(StructuresConfig structuresConfig, BlockState defaultBlock, BlockState defaultFluid, Identifier id) {
        HashMap map = Maps.newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
        map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
        return new ChunkGeneratorSettings(new StructuresConfig(Optional.ofNullable(structuresConfig.getStronghold()), map), new GenerationShapeConfig(128, new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0), new SlideConfig(120, 3, 0), new SlideConfig(320, 4, -1), 1, 2, 0.0, 0.019921875, false, false, false, false), defaultBlock, defaultFluid, 0, 0, 32, false);
    }

    private static ChunkGeneratorSettings createSurfaceSettings(StructuresConfig structuresConfig, boolean amplified, Identifier id) {
        double d = 0.9999999814507745;
        return new ChunkGeneratorSettings(structuresConfig, new GenerationShapeConfig(256, new NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 160.0), new SlideConfig(-10, 3, 0), new SlideConfig(-30, 0, 0), 1, 2, 1.0, -0.46875, true, true, false, amplified), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), -10, 0, 63, false);
    }

    static {
        ChunkGeneratorSettings.register(AMPLIFIED, ChunkGeneratorSettings.createSurfaceSettings(new StructuresConfig(true), true, AMPLIFIED.getValue()));
        ChunkGeneratorSettings.register(NETHER, ChunkGeneratorSettings.createUndergroundSettings(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), NETHER.getValue()));
        ChunkGeneratorSettings.register(END, ChunkGeneratorSettings.createIslandSettings(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), END.getValue(), true, true));
        ChunkGeneratorSettings.register(CAVES, ChunkGeneratorSettings.createUndergroundSettings(new StructuresConfig(true), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), CAVES.getValue()));
        ChunkGeneratorSettings.register(FLOATING_ISLANDS, ChunkGeneratorSettings.createIslandSettings(new StructuresConfig(true), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), FLOATING_ISLANDS.getValue(), false, false));
    }
}

