/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import org.jetbrains.annotations.Nullable;

public abstract class RegionBasedStorage
implements AutoCloseable {
    protected final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap();
    private final File directory;

    protected RegionBasedStorage(File directory) {
        this.directory = directory;
    }

    private RegionFile getRegionFile(ChunkPos pos) throws IOException {
        long l = ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ());
        RegionFile regionFile = (RegionFile)this.cachedRegionFiles.getAndMoveToFirst(l);
        if (regionFile != null) {
            return regionFile;
        }
        if (this.cachedRegionFiles.size() >= 256) {
            ((RegionFile)this.cachedRegionFiles.removeLast()).close();
        }
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        File file = new File(this.directory, "r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
        RegionFile regionFile2 = new RegionFile(file);
        this.cachedRegionFiles.putAndMoveToFirst(l, (Object)regionFile2);
        return regionFile2;
    }

    @Nullable
    public CompoundTag getTagAt(ChunkPos pos) throws IOException {
        RegionFile regionFile = this.getRegionFile(pos);
        try (DataInputStream dataInputStream = regionFile.getChunkDataInputStream(pos);){
            if (dataInputStream == null) {
                CompoundTag compoundTag = null;
                return compoundTag;
            }
            CompoundTag compoundTag = NbtIo.read(dataInputStream);
            return compoundTag;
        }
    }

    protected void setTagAt(ChunkPos pos, CompoundTag tag) throws IOException {
        RegionFile regionFile = this.getRegionFile(pos);
        try (DataOutputStream dataOutputStream = regionFile.getChunkDataOutputStream(pos);){
            NbtIo.write(tag, (DataOutput)dataOutputStream);
        }
    }

    @Override
    public void close() throws IOException {
        for (RegionFile regionFile : this.cachedRegionFiles.values()) {
            regionFile.close();
        }
    }
}

