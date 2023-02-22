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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.ThrowableDeliverer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import org.jetbrains.annotations.Nullable;

public final class RegionBasedStorage
implements AutoCloseable {
    public static final String MCA_EXTENSION = ".mca";
    private static final int MAX_CACHE_SIZE = 256;
    private final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap();
    private final Path directory;
    private final boolean dsync;

    RegionBasedStorage(Path directory, boolean dsync) {
        this.directory = directory;
        this.dsync = dsync;
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
        Files.createDirectories(this.directory, new FileAttribute[0]);
        Path path = this.directory.resolve("r." + pos.getRegionX() + "." + pos.getRegionZ() + MCA_EXTENSION);
        RegionFile regionFile2 = new RegionFile(path, this.directory, this.dsync);
        this.cachedRegionFiles.putAndMoveToFirst(l, (Object)regionFile2);
        return regionFile2;
    }

    @Nullable
    public NbtCompound getTagAt(ChunkPos pos) throws IOException {
        RegionFile regionFile = this.getRegionFile(pos);
        try (DataInputStream dataInputStream = regionFile.getChunkInputStream(pos);){
            if (dataInputStream == null) {
                NbtCompound nbtCompound = null;
                return nbtCompound;
            }
            NbtCompound nbtCompound = NbtIo.read(dataInputStream);
            return nbtCompound;
        }
    }

    public void method_39802(ChunkPos chunkPos, NbtScanner nbtScanner) throws IOException {
        RegionFile regionFile = this.getRegionFile(chunkPos);
        try (DataInputStream dataInputStream = regionFile.getChunkInputStream(chunkPos);){
            if (dataInputStream != null) {
                NbtIo.scan(dataInputStream, nbtScanner);
            }
        }
    }

    protected void write(ChunkPos pos, @Nullable NbtCompound nbt) throws IOException {
        RegionFile regionFile = this.getRegionFile(pos);
        if (nbt == null) {
            regionFile.method_31740(pos);
        } else {
            try (DataOutputStream dataOutputStream = regionFile.getChunkOutputStream(pos);){
                NbtIo.write(nbt, (DataOutput)dataOutputStream);
            }
        }
    }

    @Override
    public void close() throws IOException {
        ThrowableDeliverer<IOException> throwableDeliverer = new ThrowableDeliverer<IOException>();
        for (RegionFile regionFile : this.cachedRegionFiles.values()) {
            try {
                regionFile.close();
            }
            catch (IOException iOException) {
                throwableDeliverer.add(iOException);
            }
        }
        throwableDeliverer.deliver();
    }

    public void sync() throws IOException {
        for (RegionFile regionFile : this.cachedRegionFiles.values()) {
            regionFile.sync();
        }
    }
}

