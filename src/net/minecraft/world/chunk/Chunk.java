/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.SerializableTickScheduler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Chunk
implements BlockView,
BiomeAccess.Storage,
StructureHolder {
    private static final Logger field_34548 = LogUtils.getLogger();
    private static final LongSet EMPTY_STRUCTURE_REFERENCES = new LongOpenHashSet();
    protected final ShortList[] postProcessingLists;
    protected volatile boolean needsSaving;
    private volatile boolean lightOn;
    protected final ChunkPos pos;
    private long inhabitedTime;
    @Nullable
    @Deprecated
    private RegistryEntry<Biome> biome;
    @Nullable
    protected ChunkNoiseSampler chunkNoiseSampler;
    protected final UpgradeData upgradeData;
    @Nullable
    protected BlendingData blendingData;
    protected final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private final Map<ConfiguredStructureFeature<?, ?>, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<ConfiguredStructureFeature<?, ?>, LongSet> structureReferences = Maps.newHashMap();
    protected final Map<BlockPos, NbtCompound> blockEntityNbts = Maps.newHashMap();
    protected final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    protected final HeightLimitView heightLimitView;
    protected final ChunkSection[] sectionArray;

    public Chunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biome, long inhabitedTime, @Nullable ChunkSection[] sectionArrayInitializer, @Nullable BlendingData blendingData) {
        this.pos = pos;
        this.upgradeData = upgradeData;
        this.heightLimitView = heightLimitView;
        this.sectionArray = new ChunkSection[heightLimitView.countVerticalSections()];
        this.inhabitedTime = inhabitedTime;
        this.postProcessingLists = new ShortList[heightLimitView.countVerticalSections()];
        this.blendingData = blendingData;
        if (sectionArrayInitializer != null) {
            if (this.sectionArray.length == sectionArrayInitializer.length) {
                System.arraycopy(sectionArrayInitializer, 0, this.sectionArray, 0, this.sectionArray.length);
            } else {
                field_34548.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)sectionArrayInitializer.length, (Object)this.sectionArray.length);
            }
        }
        Chunk.fillSectionArray(heightLimitView, biome, this.sectionArray);
    }

    private static void fillSectionArray(HeightLimitView world, Registry<Biome> biome, ChunkSection[] sectionArray) {
        for (int i = 0; i < sectionArray.length; ++i) {
            if (sectionArray[i] != null) continue;
            sectionArray[i] = new ChunkSection(world.sectionIndexToCoord(i), biome);
        }
    }

    public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
        return GameEventDispatcher.EMPTY;
    }

    @Nullable
    public abstract BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

    public abstract void setBlockEntity(BlockEntity var1);

    public abstract void addEntity(Entity var1);

    @Nullable
    public ChunkSection getHighestNonEmptySection() {
        ChunkSection[] chunkSections = this.getSectionArray();
        for (int i = chunkSections.length - 1; i >= 0; --i) {
            ChunkSection chunkSection = chunkSections[i];
            if (chunkSection.isEmpty()) continue;
            return chunkSection;
        }
        return null;
    }

    public int getHighestNonEmptySectionYOffset() {
        ChunkSection chunkSection = this.getHighestNonEmptySection();
        return chunkSection == null ? this.getBottomY() : chunkSection.getYOffset();
    }

    public Set<BlockPos> getBlockEntityPositions() {
        HashSet set = Sets.newHashSet(this.blockEntityNbts.keySet());
        set.addAll(this.blockEntities.keySet());
        return set;
    }

    public ChunkSection[] getSectionArray() {
        return this.sectionArray;
    }

    public ChunkSection getSection(int yIndex) {
        return this.getSectionArray()[yIndex];
    }

    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public void setHeightmap(Heightmap.Type type, long[] heightmap) {
        this.getHeightmap(type).setTo(this, type, heightmap);
    }

    public Heightmap getHeightmap(Heightmap.Type type2) {
        return this.heightmaps.computeIfAbsent(type2, type -> new Heightmap(this, (Heightmap.Type)type));
    }

    public boolean hasHeightmap(Heightmap.Type type) {
        return this.heightmaps.get(type) != null;
    }

    public int sampleHeightmap(Heightmap.Type type, int x, int z) {
        Heightmap heightmap = this.heightmaps.get(type);
        if (heightmap == null) {
            if (SharedConstants.isDevelopment && this instanceof WorldChunk) {
                field_34548.error("Unprimed heightmap: " + type + " " + x + " " + z);
            }
            Heightmap.populateHeightmaps(this, EnumSet.of(type));
            heightmap = this.heightmaps.get(type);
        }
        return heightmap.get(x & 0xF, z & 0xF) - 1;
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        return this.structureStarts.get(configuredStructureFeature);
    }

    @Override
    public void setStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureStart start) {
        this.structureStarts.put(configuredStructureFeature, start);
        this.needsSaving = true;
    }

    public Map<ConfiguredStructureFeature<?, ?>, StructureStart> getStructureStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    public void setStructureStarts(Map<ConfiguredStructureFeature<?, ?>, StructureStart> structureStarts) {
        this.structureStarts.clear();
        this.structureStarts.putAll(structureStarts);
        this.needsSaving = true;
    }

    @Override
    public LongSet getStructureReferences(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        return this.structureReferences.getOrDefault(configuredStructureFeature, EMPTY_STRUCTURE_REFERENCES);
    }

    @Override
    public void addStructureReference(ConfiguredStructureFeature<?, ?> configuredStructureFeature2, long reference) {
        this.structureReferences.computeIfAbsent(configuredStructureFeature2, configuredStructureFeature -> new LongOpenHashSet()).add(reference);
        this.needsSaving = true;
    }

    @Override
    public Map<ConfiguredStructureFeature<?, ?>, LongSet> getStructureReferences() {
        return Collections.unmodifiableMap(this.structureReferences);
    }

    @Override
    public void setStructureReferences(Map<ConfiguredStructureFeature<?, ?>, LongSet> structureReferences) {
        this.structureReferences.clear();
        this.structureReferences.putAll(structureReferences);
        this.needsSaving = true;
    }

    public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
        if (lowerHeight < this.getBottomY()) {
            lowerHeight = this.getBottomY();
        }
        if (upperHeight >= this.getTopY()) {
            upperHeight = this.getTopY() - 1;
        }
        for (int i = lowerHeight; i <= upperHeight; i += 16) {
            if (this.getSection(this.getSectionIndex(i)).isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void setNeedsSaving(boolean needsSaving) {
        this.needsSaving = needsSaving;
    }

    public boolean needsSaving() {
        return this.needsSaving;
    }

    public abstract ChunkStatus getStatus();

    public abstract void removeBlockEntity(BlockPos var1);

    public void markBlockForPostProcessing(BlockPos pos) {
        field_34548.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)pos);
    }

    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }

    public void markBlockForPostProcessing(short packedPos, int index) {
        Chunk.getList(this.getPostProcessingLists(), index).add(packedPos);
    }

    public void addPendingBlockEntityNbt(NbtCompound nbt) {
        this.blockEntityNbts.put(BlockEntity.posFromNbt(nbt), nbt);
    }

    @Nullable
    public NbtCompound getBlockEntityNbt(BlockPos pos) {
        return this.blockEntityNbts.get(pos);
    }

    @Nullable
    public abstract NbtCompound getPackedBlockEntityNbt(BlockPos var1);

    public abstract Stream<BlockPos> getLightSourcesStream();

    public abstract BasicTickScheduler<Block> getBlockTickScheduler();

    public abstract BasicTickScheduler<Fluid> getFluidTickScheduler();

    public abstract TickSchedulers getTickSchedulers();

    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    public boolean usesOldNoise() {
        return this.blendingData != null && this.blendingData.usesOldNoise();
    }

    @Nullable
    public BlendingData getBlendingData() {
        return this.blendingData;
    }

    public void setBlendingData(BlendingData blendingData) {
        this.blendingData = blendingData;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public void increaseInhabitedTime(long delta) {
        this.inhabitedTime += delta;
    }

    public void setInhabitedTime(long inhabitedTime) {
        this.inhabitedTime = inhabitedTime;
    }

    public static ShortList getList(ShortList[] lists, int index) {
        if (lists[index] == null) {
            lists[index] = new ShortArrayList();
        }
        return lists[index];
    }

    public boolean isLightOn() {
        return this.lightOn;
    }

    public void setLightOn(boolean lightOn) {
        this.lightOn = lightOn;
        this.setNeedsSaving(true);
    }

    @Override
    public int getBottomY() {
        return this.heightLimitView.getBottomY();
    }

    @Override
    public int getHeight() {
        return this.heightLimitView.getHeight();
    }

    public ChunkNoiseSampler getOrCreateChunkNoiseSampler(NoiseRouter noiseColumnSampler, Supplier<DensityFunctionTypes.class_7050> columnSampler, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
        if (this.chunkNoiseSampler == null) {
            this.chunkNoiseSampler = ChunkNoiseSampler.create(this, noiseColumnSampler, columnSampler, chunkGeneratorSettings, fluidLevelSampler, blender);
        }
        return this.chunkNoiseSampler;
    }

    @Deprecated
    public RegistryEntry<Biome> setBiomeIfAbsent(Supplier<RegistryEntry<Biome>> biomeSupplier) {
        if (this.biome == null) {
            this.biome = biomeSupplier.get();
        }
        return this.biome;
    }

    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        try {
            int i = BiomeCoords.fromBlock(this.getBottomY());
            int j = i + BiomeCoords.fromBlock(this.getHeight()) - 1;
            int k = MathHelper.clamp(biomeY, i, j);
            int l = this.getSectionIndex(BiomeCoords.toBlock(k));
            return this.sectionArray[l].getBiome(biomeX & 3, k & 3, biomeZ & 3);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting biome");
            CrashReportSection crashReportSection = crashReport.addElement("Biome being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString((HeightLimitView)this, biomeX, biomeY, biomeZ));
            throw new CrashException(crashReport);
        }
    }

    public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
        ChunkPos chunkPos = this.getPos();
        int i = BiomeCoords.fromBlock(chunkPos.getStartX());
        int j = BiomeCoords.fromBlock(chunkPos.getStartZ());
        HeightLimitView heightLimitView = this.getHeightLimitView();
        for (int k = heightLimitView.getBottomSectionCoord(); k < heightLimitView.getTopSectionCoord(); ++k) {
            ChunkSection chunkSection = this.getSection(this.sectionCoordToIndex(k));
            chunkSection.populateBiomes(biomeSupplier, sampler, i, j);
        }
    }

    public boolean hasStructureReferences() {
        return !this.getStructureReferences().isEmpty();
    }

    @Nullable
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return null;
    }

    public boolean hasBelowZeroRetrogen() {
        return this.getBelowZeroRetrogen() != null;
    }

    public HeightLimitView getHeightLimitView() {
        return this;
    }

    public record TickSchedulers(SerializableTickScheduler<Block> blocks, SerializableTickScheduler<Fluid> fluids) {
    }
}

