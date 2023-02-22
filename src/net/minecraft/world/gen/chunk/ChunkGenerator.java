/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkGenerator {
    public static final Codec<ChunkGenerator> CODEC;
    protected final BiomeSource populationSource;
    protected final BiomeSource biomeSource;
    private final StructuresConfig structuresConfig;
    private final long worldSeed;
    private final List<ChunkPos> strongholds = Lists.newArrayList();

    public ChunkGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig) {
        this(biomeSource, biomeSource, structuresConfig, 0L);
    }

    public ChunkGenerator(BiomeSource populationSource, BiomeSource biomeSource, StructuresConfig structuresConfig, long worldSeed) {
        this.populationSource = populationSource;
        this.biomeSource = biomeSource;
        this.structuresConfig = structuresConfig;
        this.worldSeed = worldSeed;
    }

    private void generateStrongholdPositions() {
        if (!this.strongholds.isEmpty()) {
            return;
        }
        StrongholdConfig strongholdConfig = this.structuresConfig.getStronghold();
        if (strongholdConfig == null || strongholdConfig.getCount() == 0) {
            return;
        }
        ArrayList list = Lists.newArrayList();
        for (Biome biome : this.populationSource.getBiomes()) {
            if (!biome.getGenerationSettings().hasStructureFeature(StructureFeature.STRONGHOLD)) continue;
            list.add(biome);
        }
        int i = strongholdConfig.getDistance();
        int j = strongholdConfig.getCount();
        int k = strongholdConfig.getSpread();
        Random random = new Random();
        random.setSeed(this.worldSeed);
        double d = random.nextDouble() * Math.PI * 2.0;
        int l = 0;
        int m = 0;
        for (int n = 0; n < j; ++n) {
            double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
            int o = (int)Math.round(Math.cos(d) * e);
            int p = (int)Math.round(Math.sin(d) * e);
            BlockPos blockPos = this.populationSource.locateBiome((o << 4) + 8, 0, (p << 4) + 8, 112, list::contains, random);
            if (blockPos != null) {
                o = blockPos.getX() >> 4;
                p = blockPos.getZ() >> 4;
            }
            this.strongholds.add(new ChunkPos(o, p));
            d += Math.PI * 2 / (double)k;
            if (++l != k) continue;
            l = 0;
            k += 2 * k / (++m + 1);
            k = Math.min(k, j - n);
            d += random.nextDouble() * Math.PI * 2.0;
        }
    }

    protected abstract Codec<? extends ChunkGenerator> getCodec();

    @Environment(value=EnvType.CLIENT)
    public abstract ChunkGenerator withSeed(long var1);

    public void populateBiomes(Registry<Biome> biomeRegistry, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        ((ProtoChunk)chunk).setBiomes(new BiomeArray(biomeRegistry, chunkPos, this.biomeSource));
    }

    public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
        BiomeAccess biomeAccess = access.withSource(this.populationSource);
        ChunkRandom chunkRandom = new ChunkRandom();
        int i = 8;
        ChunkPos chunkPos = chunk.getPos();
        int j = chunkPos.x;
        int k = chunkPos.z;
        GenerationSettings generationSettings = this.populationSource.getBiomeForNoiseGen(chunkPos.x << 2, 0, chunkPos.z << 2).getGenerationSettings();
        BitSet bitSet = ((ProtoChunk)chunk).getOrCreateCarvingMask(carver);
        for (int l = j - 8; l <= j + 8; ++l) {
            for (int m = k - 8; m <= k + 8; ++m) {
                List<Supplier<ConfiguredCarver<?>>> list = generationSettings.getCarversForStep(carver);
                ListIterator<Supplier<ConfiguredCarver<?>>> listIterator = list.listIterator();
                while (listIterator.hasNext()) {
                    int n = listIterator.nextIndex();
                    ConfiguredCarver<?> configuredCarver = listIterator.next().get();
                    chunkRandom.setCarverSeed(seed + (long)n, l, m);
                    if (!configuredCarver.shouldCarve(chunkRandom, l, m)) continue;
                    configuredCarver.carve(chunk, biomeAccess::getBiome, chunkRandom, this.getSeaLevel(), l, m, j, k, bitSet);
                }
            }
        }
    }

    @Nullable
    public BlockPos locateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
        if (!this.populationSource.hasStructureFeature(feature)) {
            return null;
        }
        if (feature == StructureFeature.STRONGHOLD) {
            this.generateStrongholdPositions();
            BlockPos blockPos = null;
            double d = Double.MAX_VALUE;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (ChunkPos chunkPos : this.strongholds) {
                mutable.set((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
                double e = mutable.getSquaredDistance(center);
                if (blockPos == null) {
                    blockPos = new BlockPos(mutable);
                    d = e;
                    continue;
                }
                if (!(e < d)) continue;
                blockPos = new BlockPos(mutable);
                d = e;
            }
            return blockPos;
        }
        StructureConfig structureConfig = this.structuresConfig.getForType(feature);
        if (structureConfig == null) {
            return null;
        }
        return feature.locateStructure(world, world.getStructureAccessor(), center, radius, skipExistingChunks, world.getSeed(), structureConfig);
    }

    public void generateFeatures(ChunkRegion region, StructureAccessor accessor) {
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        int k = i * 16;
        int l = j * 16;
        BlockPos blockPos = new BlockPos(k, 0, l);
        Biome biome = this.populationSource.getBiomeForNoiseGen((i << 2) + 2, 2, (j << 2) + 2);
        ChunkRandom chunkRandom = new ChunkRandom();
        long m = chunkRandom.setPopulationSeed(region.getSeed(), k, l);
        try {
            biome.generateFeatureStep(accessor, this, region, m, chunkRandom, blockPos);
        }
        catch (Exception exception) {
            CrashReport crashReport = CrashReport.create(exception, "Biome decoration");
            crashReport.addElement("Generation").add("CenterX", i).add("CenterZ", j).add("Seed", m).add("Biome", biome);
            throw new CrashException(crashReport);
        }
    }

    public abstract void buildSurface(ChunkRegion var1, Chunk var2);

    public void populateEntities(ChunkRegion region) {
    }

    public StructuresConfig getStructuresConfig() {
        return this.structuresConfig;
    }

    public int getSpawnHeight() {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public int getWorldHeight() {
        return 256;
    }

    public List<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        return biome.getSpawnSettings().getSpawnEntry(group);
    }

    public void setStructureStarts(DynamicRegistryManager registryManager, StructureAccessor accessor, Chunk chunk, StructureManager structureManager, long worldSeed) {
        ChunkPos chunkPos = chunk.getPos();
        Biome biome = this.populationSource.getBiomeForNoiseGen((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
        this.setStructureStart(ConfiguredStructureFeatures.STRONGHOLD, registryManager, accessor, chunk, structureManager, worldSeed, chunkPos, biome);
        for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructureFeatures()) {
            this.setStructureStart(supplier.get(), registryManager, accessor, chunk, structureManager, worldSeed, chunkPos, biome);
        }
    }

    private void setStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature, DynamicRegistryManager dynamicRegistryManager, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long worldSeed, ChunkPos chunkPos, Biome biome) {
        StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), (StructureFeature<?>)configuredStructureFeature.feature, chunk);
        int i = structureStart != null ? structureStart.getReferences() : 0;
        StructureConfig structureConfig = this.structuresConfig.getForType((StructureFeature<?>)configuredStructureFeature.feature);
        if (structureConfig != null) {
            StructureStart<?> structureStart2 = configuredStructureFeature.tryPlaceStart(dynamicRegistryManager, this, this.populationSource, structureManager, worldSeed, chunkPos, biome, i, structureConfig);
            structureAccessor.setStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), (StructureFeature<?>)configuredStructureFeature.feature, structureStart2, chunk);
        }
    }

    public void addStructureReferences(StructureWorldAccess world, StructureAccessor accessor, Chunk chunk) {
        int i = 8;
        int j = chunk.getPos().x;
        int k = chunk.getPos().z;
        int l = j << 4;
        int m = k << 4;
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), 0);
        for (int n = j - 8; n <= j + 8; ++n) {
            for (int o = k - 8; o <= k + 8; ++o) {
                long p = ChunkPos.toLong(n, o);
                for (StructureStart<?> structureStart : world.getChunk(n, o).getStructureStarts().values()) {
                    try {
                        if (structureStart == StructureStart.DEFAULT || !structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) continue;
                        accessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
                        DebugInfoSender.sendStructureStart(world, structureStart);
                    }
                    catch (Exception exception) {
                        CrashReport crashReport = CrashReport.create(exception, "Generating structure reference");
                        CrashReportSection crashReportSection = crashReport.addElement("Structure");
                        crashReportSection.add("Id", () -> Registry.STRUCTURE_FEATURE.getId(structureStart.getFeature()).toString());
                        crashReportSection.add("Name", () -> structureStart.getFeature().getName());
                        crashReportSection.add("Class", () -> structureStart.getFeature().getClass().getCanonicalName());
                        throw new CrashException(crashReport);
                    }
                }
            }
        }
    }

    public abstract void populateNoise(WorldAccess var1, StructureAccessor var2, Chunk var3);

    public int getSeaLevel() {
        return 63;
    }

    public abstract int getHeight(int var1, int var2, Heightmap.Type var3);

    public abstract BlockView getColumnSample(int var1, int var2);

    public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType) {
        return this.getHeight(x, z, heightmapType);
    }

    public int getHeightInGround(int x, int z, Heightmap.Type heightmapType) {
        return this.getHeight(x, z, heightmapType) - 1;
    }

    public boolean isStrongholdStartingChunk(ChunkPos pos) {
        this.generateStrongholdPositions();
        return this.strongholds.contains(pos);
    }

    static {
        Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
        CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::getCodec, Function.identity());
    }
}

