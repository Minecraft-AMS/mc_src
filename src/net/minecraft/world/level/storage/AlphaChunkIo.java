/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.level.storage.AlphaChunkDataArray;

public class AlphaChunkIo {
    public static AlphaChunk readAlphaChunk(CompoundTag tag) {
        int i = tag.getInt("xPos");
        int j = tag.getInt("zPos");
        AlphaChunk alphaChunk = new AlphaChunk(i, j);
        alphaChunk.blocks = tag.getByteArray("Blocks");
        alphaChunk.data = new AlphaChunkDataArray(tag.getByteArray("Data"), 7);
        alphaChunk.skyLight = new AlphaChunkDataArray(tag.getByteArray("SkyLight"), 7);
        alphaChunk.blockLight = new AlphaChunkDataArray(tag.getByteArray("BlockLight"), 7);
        alphaChunk.heightMap = tag.getByteArray("HeightMap");
        alphaChunk.terrainPopulated = tag.getBoolean("TerrainPopulated");
        alphaChunk.entities = tag.getList("Entities", 10);
        alphaChunk.blockEntities = tag.getList("TileEntities", 10);
        alphaChunk.blockTicks = tag.getList("TileTicks", 10);
        try {
            alphaChunk.lastUpdate = tag.getLong("LastUpdate");
        }
        catch (ClassCastException classCastException) {
            alphaChunk.lastUpdate = tag.getInt("LastUpdate");
        }
        return alphaChunk;
    }

    public static void convertAlphaChunk(AlphaChunk alphaChunk, CompoundTag tag, BiomeSource biomeSource) {
        int k;
        tag.putInt("xPos", alphaChunk.x);
        tag.putInt("zPos", alphaChunk.z);
        tag.putLong("LastUpdate", alphaChunk.lastUpdate);
        int[] is = new int[alphaChunk.heightMap.length];
        for (int i = 0; i < alphaChunk.heightMap.length; ++i) {
            is[i] = alphaChunk.heightMap[i];
        }
        tag.putIntArray("HeightMap", is);
        tag.putBoolean("TerrainPopulated", alphaChunk.terrainPopulated);
        ListTag listTag = new ListTag();
        for (int j = 0; j < 8; ++j) {
            int o;
            boolean bl = true;
            for (k = 0; k < 16 && bl; ++k) {
                block3: for (int l = 0; l < 16 && bl; ++l) {
                    for (int m = 0; m < 16; ++m) {
                        int n = k << 11 | m << 7 | l + (j << 4);
                        o = alphaChunk.blocks[n];
                        if (o == 0) continue;
                        bl = false;
                        continue block3;
                    }
                }
            }
            if (bl) continue;
            byte[] bs = new byte[4096];
            ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray();
            ChunkNibbleArray chunkNibbleArray2 = new ChunkNibbleArray();
            ChunkNibbleArray chunkNibbleArray3 = new ChunkNibbleArray();
            for (o = 0; o < 16; ++o) {
                for (int p = 0; p < 16; ++p) {
                    for (int q = 0; q < 16; ++q) {
                        int r = o << 11 | q << 7 | p + (j << 4);
                        byte s = alphaChunk.blocks[r];
                        bs[p << 8 | q << 4 | o] = (byte)(s & 0xFF);
                        chunkNibbleArray.set(o, p, q, alphaChunk.data.get(o, p + (j << 4), q));
                        chunkNibbleArray2.set(o, p, q, alphaChunk.skyLight.get(o, p + (j << 4), q));
                        chunkNibbleArray3.set(o, p, q, alphaChunk.blockLight.get(o, p + (j << 4), q));
                    }
                }
            }
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Y", (byte)(j & 0xFF));
            compoundTag.putByteArray("Blocks", bs);
            compoundTag.putByteArray("Data", chunkNibbleArray.asByteArray());
            compoundTag.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
            compoundTag.putByteArray("BlockLight", chunkNibbleArray3.asByteArray());
            listTag.add(compoundTag);
        }
        tag.put("Sections", listTag);
        byte[] cs = new byte[256];
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                mutable.set(alphaChunk.x << 4 | k, 0, alphaChunk.z << 4 | l);
                cs[l << 4 | k] = (byte)(Registry.BIOME.getRawId(biomeSource.getBiome(mutable)) & 0xFF);
            }
        }
        tag.putByteArray("Biomes", cs);
        tag.put("Entities", alphaChunk.entities);
        tag.put("TileEntities", alphaChunk.blockEntities);
        if (alphaChunk.blockTicks != null) {
            tag.put("TileTicks", alphaChunk.blockTicks);
        }
        tag.putBoolean("convertedFromAlphaFormat", true);
    }

    public static class AlphaChunk {
        public long lastUpdate;
        public boolean terrainPopulated;
        public byte[] heightMap;
        public AlphaChunkDataArray blockLight;
        public AlphaChunkDataArray skyLight;
        public AlphaChunkDataArray data;
        public byte[] blocks;
        public ListTag entities;
        public ListTag blockEntities;
        public ListTag blockTicks;
        public final int x;
        public final int z;

        public AlphaChunk(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}
