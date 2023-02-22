/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkSerializer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ProtoChunk deserialize(ServerWorld serverWorld, StructureManager structureManager, PointOfInterestStorage pointOfInterestStorage, ChunkPos chunkPos, CompoundTag compoundTag) {
        int p;
        ListTag listTag3;
        Chunk chunk;
        ChunkGenerator<?> chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        ChunkPos chunkPos2 = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
        if (!Objects.equals(chunkPos, chunkPos2)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", (Object)chunkPos, (Object)chunkPos, (Object)chunkPos2);
        }
        Biome[] biomes = new Biome[256];
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        if (compoundTag2.contains("Biomes", 11)) {
            int[] is = compoundTag2.getIntArray("Biomes");
            for (int i = 0; i < is.length; ++i) {
                biomes[i] = (Biome)Registry.BIOME.get(is[i]);
                if (biomes[i] != null) continue;
                biomes[i] = biomeSource.getBiome(mutable.set((i & 0xF) + chunkPos.getStartX(), 0, (i >> 4 & 0xF) + chunkPos.getStartZ()));
            }
        } else {
            for (int j = 0; j < biomes.length; ++j) {
                biomes[j] = biomeSource.getBiome(mutable.set((j & 0xF) + chunkPos.getStartX(), 0, (j >> 4 & 0xF) + chunkPos.getStartZ()));
            }
        }
        UpgradeData upgradeData = compoundTag2.contains("UpgradeData", 10) ? new UpgradeData(compoundTag2.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;
        ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<Block>(block -> block == null || block.getDefaultState().isAir(), chunkPos, compoundTag2.getList("ToBeTicked", 9));
        ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9));
        boolean bl = compoundTag2.getBoolean("isLightOn");
        ListTag listTag = compoundTag2.getList("Sections", 10);
        int k = 16;
        ChunkSection[] chunkSections = new ChunkSection[16];
        boolean bl2 = serverWorld.getDimension().hasSkyLight();
        ServerChunkManager chunkManager = serverWorld.getChunkManager();
        LightingProvider lightingProvider = ((ChunkManager)chunkManager).getLightingProvider();
        if (bl) {
            lightingProvider.method_20601(chunkPos, true);
        }
        for (int l = 0; l < listTag.size(); ++l) {
            CompoundTag compoundTag3 = listTag.getCompound(l);
            byte m = compoundTag3.getByte("Y");
            if (compoundTag3.contains("Palette", 9) && compoundTag3.contains("BlockStates", 12)) {
                ChunkSection chunkSection = new ChunkSection(m << 4);
                chunkSection.getContainer().read(compoundTag3.getList("Palette", 10), compoundTag3.getLongArray("BlockStates"));
                chunkSection.calculateCounts();
                if (!chunkSection.isEmpty()) {
                    chunkSections[m] = chunkSection;
                }
                pointOfInterestStorage.initForPalette(chunkPos, chunkSection);
            }
            if (!bl) continue;
            if (compoundTag3.contains("BlockLight", 7)) {
                lightingProvider.queueData(LightType.BLOCK, ChunkSectionPos.from(chunkPos, m), new ChunkNibbleArray(compoundTag3.getByteArray("BlockLight")));
            }
            if (!bl2 || !compoundTag3.contains("SkyLight", 7)) continue;
            lightingProvider.queueData(LightType.SKY, ChunkSectionPos.from(chunkPos, m), new ChunkNibbleArray(compoundTag3.getByteArray("SkyLight")));
        }
        long n = compoundTag2.getLong("InhabitedTime");
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkType(compoundTag);
        if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
            TickScheduler<Block> tickScheduler = compoundTag2.contains("TileTicks", 9) ? SimpleTickScheduler.fromNbt(compoundTag2.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get) : chunkTickScheduler;
            TickScheduler<Fluid> tickScheduler2 = compoundTag2.contains("LiquidTicks", 9) ? SimpleTickScheduler.fromNbt(compoundTag2.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get) : chunkTickScheduler2;
            chunk = new WorldChunk(serverWorld.getWorld(), chunkPos, biomes, upgradeData, tickScheduler, tickScheduler2, n, chunkSections, worldChunk -> ChunkSerializer.writeEntities(compoundTag2, worldChunk));
        } else {
            ProtoChunk protoChunk = new ProtoChunk(chunkPos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2);
            chunk = protoChunk;
            chunk.setBiomeArray(biomes);
            chunk.setInhabitedTime(n);
            protoChunk.setStatus(ChunkStatus.get(compoundTag2.getString("Status")));
            if (chunk.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
                protoChunk.setLightingProvider(lightingProvider);
            }
            if (!bl && chunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
                for (BlockPos blockPos : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
                    if (chunk.getBlockState(blockPos).getLuminance() == 0) continue;
                    protoChunk.addLightSource(blockPos);
                }
            }
        }
        chunk.setLightOn(bl);
        CompoundTag compoundTag4 = compoundTag2.getCompound("Heightmaps");
        EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);
        for (Heightmap.Type type : chunk.getStatus().getHeightmapTypes()) {
            String string = type.getName();
            if (compoundTag4.contains(string, 12)) {
                chunk.setHeightmap(type, compoundTag4.getLongArray(string));
                continue;
            }
            enumSet.add(type);
        }
        Heightmap.populateHeightmaps(chunk, enumSet);
        CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        chunk.setStructureStarts(ChunkSerializer.readStructureStarts(chunkGenerator, structureManager, biomeSource, compoundTag3));
        chunk.setStructureReferences(ChunkSerializer.readStructureReferences(compoundTag3));
        if (compoundTag2.getBoolean("shouldSave")) {
            chunk.setShouldSave(true);
        }
        ListTag listTag2 = compoundTag2.getList("PostProcessing", 9);
        for (int o = 0; o < listTag2.size(); ++o) {
            listTag3 = listTag2.getList(o);
            for (p = 0; p < listTag3.size(); ++p) {
                chunk.markBlockForPostProcessing(listTag3.getShort(p), o);
            }
        }
        if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ReadOnlyChunk((WorldChunk)chunk);
        }
        ProtoChunk protoChunk2 = (ProtoChunk)chunk;
        listTag3 = compoundTag2.getList("Entities", 10);
        for (p = 0; p < listTag3.size(); ++p) {
            protoChunk2.addEntity(listTag3.getCompound(p));
        }
        ListTag listTag4 = compoundTag2.getList("TileEntities", 10);
        for (int q = 0; q < listTag4.size(); ++q) {
            CompoundTag compoundTag6 = listTag4.getCompound(q);
            chunk.addPendingBlockEntityTag(compoundTag6);
        }
        ListTag listTag5 = compoundTag2.getList("Lights", 9);
        for (int r = 0; r < listTag5.size(); ++r) {
            ListTag listTag6 = listTag5.getList(r);
            for (int s = 0; s < listTag6.size(); ++s) {
                protoChunk2.addLightSource(listTag6.getShort(s), r);
            }
        }
        CompoundTag compoundTag6 = compoundTag2.getCompound("CarvingMasks");
        for (String string2 : compoundTag6.getKeys()) {
            GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
            protoChunk2.setCarvingMask(carver, BitSet.valueOf(compoundTag6.getByteArray(string2)));
        }
        return protoChunk2;
    }

    public static CompoundTag serialize(ServerWorld serverWorld, Chunk chunk) {
        Biome[] biomes;
        int[] is;
        CompoundTag compoundTag3;
        ChunkPos chunkPos = chunk.getPos();
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        compoundTag.put("Level", compoundTag2);
        compoundTag2.putInt("xPos", chunkPos.x);
        compoundTag2.putInt("zPos", chunkPos.z);
        compoundTag2.putLong("LastUpdate", serverWorld.getTime());
        compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
        compoundTag2.putString("Status", chunk.getStatus().getId());
        UpgradeData upgradeData = chunk.getUpgradeData();
        if (!upgradeData.method_12349()) {
            compoundTag2.put("UpgradeData", upgradeData.toTag());
        }
        ChunkSection[] chunkSections = chunk.getSectionArray();
        ListTag listTag = new ListTag();
        ServerLightingProvider lightingProvider = serverWorld.getChunkManager().getLightingProvider();
        boolean bl = chunk.isLightOn();
        for (int i = -1; i < 17; ++i) {
            int j = i;
            ChunkSection chunkSection2 = Arrays.stream(chunkSections).filter(chunkSection -> chunkSection != null && chunkSection.getYOffset() >> 4 == j).findFirst().orElse(WorldChunk.EMPTY_SECTION);
            ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(chunkPos, j));
            ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightArray(ChunkSectionPos.from(chunkPos, j));
            if (chunkSection2 == WorldChunk.EMPTY_SECTION && chunkNibbleArray == null && chunkNibbleArray2 == null) continue;
            compoundTag3 = new CompoundTag();
            compoundTag3.putByte("Y", (byte)(j & 0xFF));
            if (chunkSection2 != WorldChunk.EMPTY_SECTION) {
                chunkSection2.getContainer().write(compoundTag3, "Palette", "BlockStates");
            }
            if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
                compoundTag3.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
            }
            if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
                compoundTag3.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
            }
            listTag.add(compoundTag3);
        }
        compoundTag2.put("Sections", listTag);
        if (bl) {
            compoundTag2.putBoolean("isLightOn", true);
        }
        int[] nArray = is = (biomes = chunk.getBiomeArray()) != null ? new int[biomes.length] : new int[]{};
        if (biomes != null) {
            for (int k = 0; k < biomes.length; ++k) {
                is[k] = Registry.BIOME.getRawId(biomes[k]);
            }
        }
        compoundTag2.putIntArray("Biomes", is);
        ListTag listTag2 = new ListTag();
        for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
            compoundTag3 = chunk.method_20598(blockPos);
            if (compoundTag3 == null) continue;
            listTag2.add(compoundTag3);
        }
        compoundTag2.put("TileEntities", listTag2);
        ListTag listTag3 = new ListTag();
        if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            WorldChunk worldChunk = (WorldChunk)chunk;
            worldChunk.setUnsaved(false);
            for (int l = 0; l < worldChunk.getEntitySectionArray().length; ++l) {
                for (Entity entity : worldChunk.getEntitySectionArray()[l]) {
                    CompoundTag compoundTag4;
                    if (!entity.saveToTag(compoundTag4 = new CompoundTag())) continue;
                    worldChunk.setUnsaved(true);
                    listTag3.add(compoundTag4);
                }
            }
        } else {
            ProtoChunk protoChunk = (ProtoChunk)chunk;
            listTag3.addAll(protoChunk.getEntities());
            compoundTag2.put("Lights", ChunkSerializer.toNbt(protoChunk.getLightSourcesBySection()));
            compoundTag3 = new CompoundTag();
            for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
                compoundTag3.putByteArray(carver.toString(), chunk.getCarvingMask(carver).toByteArray());
            }
            compoundTag2.put("CarvingMasks", compoundTag3);
        }
        compoundTag2.put("Entities", listTag3);
        TickScheduler<Block> tickScheduler = chunk.getBlockTickScheduler();
        if (tickScheduler instanceof ChunkTickScheduler) {
            compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)tickScheduler).toNbt());
        } else if (tickScheduler instanceof SimpleTickScheduler) {
            compoundTag2.put("TileTicks", ((SimpleTickScheduler)tickScheduler).toNbt(serverWorld.getTime()));
        } else {
            compoundTag2.put("TileTicks", ((ServerTickScheduler)serverWorld.getBlockTickScheduler()).toTag(chunkPos));
        }
        TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
        if (tickScheduler2 instanceof ChunkTickScheduler) {
            compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)tickScheduler2).toNbt());
        } else if (tickScheduler2 instanceof SimpleTickScheduler) {
            compoundTag2.put("LiquidTicks", ((SimpleTickScheduler)tickScheduler2).toNbt(serverWorld.getTime()));
        } else {
            compoundTag2.put("LiquidTicks", ((ServerTickScheduler)serverWorld.getFluidTickScheduler()).toTag(chunkPos));
        }
        compoundTag2.put("PostProcessing", ChunkSerializer.toNbt(chunk.getPostProcessingLists()));
        CompoundTag compoundTag5 = new CompoundTag();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
            if (!chunk.getStatus().getHeightmapTypes().contains((Object)entry.getKey())) continue;
            compoundTag5.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
        }
        compoundTag2.put("Heightmaps", compoundTag5);
        compoundTag2.put("Structures", ChunkSerializer.writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
        return compoundTag;
    }

    public static ChunkStatus.ChunkType getChunkType(@Nullable CompoundTag tag) {
        ChunkStatus chunkStatus;
        if (tag != null && (chunkStatus = ChunkStatus.get(tag.getCompound("Level").getString("Status"))) != null) {
            return chunkStatus.getChunkType();
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }

    private static void writeEntities(CompoundTag tag, WorldChunk chunk) {
        ListTag listTag = tag.getList("Entities", 10);
        World world = chunk.getWorld();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            EntityType.loadEntityWithPassengers(compoundTag, world, entity -> {
                chunk.addEntity((Entity)entity);
                return entity;
            });
            chunk.setUnsaved(true);
        }
        ListTag listTag2 = tag.getList("TileEntities", 10);
        for (int j = 0; j < listTag2.size(); ++j) {
            CompoundTag compoundTag2 = listTag2.getCompound(j);
            boolean bl = compoundTag2.getBoolean("keepPacked");
            if (bl) {
                chunk.addPendingBlockEntityTag(compoundTag2);
                continue;
            }
            BlockEntity blockEntity = BlockEntity.createFromTag(compoundTag2);
            if (blockEntity == null) continue;
            chunk.addBlockEntity(blockEntity);
        }
    }

    private static CompoundTag writeStructures(ChunkPos pos, Map<String, StructureStart> structureStarts, Map<String, LongSet> structureReferences) {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag2 = new CompoundTag();
        for (Map.Entry<String, StructureStart> entry : structureStarts.entrySet()) {
            compoundTag2.put(entry.getKey(), entry.getValue().toTag(pos.x, pos.z));
        }
        compoundTag.put("Starts", compoundTag2);
        CompoundTag compoundTag3 = new CompoundTag();
        for (Map.Entry<String, LongSet> entry2 : structureReferences.entrySet()) {
            compoundTag3.put(entry2.getKey(), new LongArrayTag(entry2.getValue()));
        }
        compoundTag.put("References", compoundTag3);
        return compoundTag;
    }

    private static Map<String, StructureStart> readStructureStarts(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag tag) {
        HashMap map = Maps.newHashMap();
        CompoundTag compoundTag = tag.getCompound("Starts");
        for (String string : compoundTag.getKeys()) {
            map.put(string, StructureFeatures.readStructureStart(chunkGenerator, structureManager, biomeSource, compoundTag.getCompound(string)));
        }
        return map;
    }

    private static Map<String, LongSet> readStructureReferences(CompoundTag tag) {
        HashMap map = Maps.newHashMap();
        CompoundTag compoundTag = tag.getCompound("References");
        for (String string : compoundTag.getKeys()) {
            map.put(string, new LongOpenHashSet(compoundTag.getLongArray(string)));
        }
        return map;
    }

    public static ListTag toNbt(ShortList[] lists) {
        ListTag listTag = new ListTag();
        for (ShortList shortList : lists) {
            ListTag listTag2 = new ListTag();
            if (shortList != null) {
                for (Short short_ : shortList) {
                    listTag2.add(new ShortTag(short_));
                }
            }
            listTag.add(listTag2);
        }
        return listTag;
    }
}

