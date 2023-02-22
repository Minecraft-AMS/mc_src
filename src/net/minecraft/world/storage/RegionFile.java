/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class RegionFile
implements AutoCloseable {
    private static final byte[] EMPTY_SECTOR = new byte[4096];
    private final RandomAccessFile file;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private final List<Boolean> sectorFree;

    public RegionFile(File file) throws IOException {
        int k;
        int j;
        int i;
        this.file = new RandomAccessFile(file, "rw");
        if (this.file.length() < 4096L) {
            this.file.write(EMPTY_SECTOR);
            this.file.write(EMPTY_SECTOR);
        }
        if ((this.file.length() & 0xFFFL) != 0L) {
            i = 0;
            while ((long)i < (this.file.length() & 0xFFFL)) {
                this.file.write(0);
                ++i;
            }
        }
        i = (int)this.file.length() / 4096;
        this.sectorFree = Lists.newArrayListWithCapacity((int)i);
        for (j = 0; j < i; ++j) {
            this.sectorFree.add(true);
        }
        this.sectorFree.set(0, false);
        this.sectorFree.set(1, false);
        this.file.seek(0L);
        for (j = 0; j < 1024; ++j) {
            this.offsets[j] = k = this.file.readInt();
            if (k == 0 || (k >> 8) + (k & 0xFF) > this.sectorFree.size()) continue;
            for (int l = 0; l < (k & 0xFF); ++l) {
                this.sectorFree.set((k >> 8) + l, false);
            }
        }
        for (j = 0; j < 1024; ++j) {
            this.chunkTimestamps[j] = k = this.file.readInt();
        }
    }

    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(ChunkPos pos) throws IOException {
        int i = this.getSectorData(pos);
        if (i == 0) {
            return null;
        }
        int j = i >> 8;
        int k = i & 0xFF;
        if (j + k > this.sectorFree.size()) {
            return null;
        }
        this.file.seek(j * 4096);
        int l = this.file.readInt();
        if (l > 4096 * k) {
            return null;
        }
        if (l <= 0) {
            return null;
        }
        byte b = this.file.readByte();
        if (b == 1) {
            byte[] bs = new byte[l - 1];
            this.file.read(bs);
            return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bs))));
        }
        if (b == 2) {
            byte[] bs = new byte[l - 1];
            this.file.read(bs);
            return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bs))));
        }
        return null;
    }

    public boolean isChunkPresent(ChunkPos pos) {
        int i = this.getSectorData(pos);
        if (i == 0) {
            return false;
        }
        int j = i >> 8;
        int k = i & 0xFF;
        if (j + k > this.sectorFree.size()) {
            return false;
        }
        try {
            this.file.seek(j * 4096);
            int l = this.file.readInt();
            if (l > 4096 * k) {
                return false;
            }
            if (l <= 0) {
                return false;
            }
        }
        catch (IOException iOException) {
            return false;
        }
        return true;
    }

    public DataOutputStream getChunkDataOutputStream(ChunkPos chunkPos) {
        return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ChunkBuffer(chunkPos))));
    }

    protected synchronized void write(ChunkPos chunkPos, byte[] bs, int i) throws IOException {
        int j = this.getSectorData(chunkPos);
        int k = j >> 8;
        int l = j & 0xFF;
        int m = (i + 5) / 4096 + 1;
        if (m >= 256) {
            throw new RuntimeException(String.format("Too big to save, %d > 1048576", i));
        }
        if (k != 0 && l == m) {
            this.write(k, bs, i);
        } else {
            int p;
            int n;
            for (n = 0; n < l; ++n) {
                this.sectorFree.set(k + n, true);
            }
            n = this.sectorFree.indexOf(true);
            int o = 0;
            if (n != -1) {
                for (p = n; p < this.sectorFree.size(); ++p) {
                    if (o != 0) {
                        o = this.sectorFree.get(p).booleanValue() ? ++o : 0;
                    } else if (this.sectorFree.get(p).booleanValue()) {
                        n = p;
                        o = 1;
                    }
                    if (o >= m) break;
                }
            }
            if (o >= m) {
                k = n;
                this.setOffset(chunkPos, k << 8 | m);
                for (p = 0; p < m; ++p) {
                    this.sectorFree.set(k + p, false);
                }
                this.write(k, bs, i);
            } else {
                this.file.seek(this.file.length());
                k = this.sectorFree.size();
                for (p = 0; p < m; ++p) {
                    this.file.write(EMPTY_SECTOR);
                    this.sectorFree.add(false);
                }
                this.write(k, bs, i);
                this.setOffset(chunkPos, k << 8 | m);
            }
        }
        this.setTimestamp(chunkPos, (int)(Util.getEpochTimeMs() / 1000L));
    }

    private void write(int sectorNumber, byte[] data, int i) throws IOException {
        this.file.seek(sectorNumber * 4096);
        this.file.writeInt(i + 1);
        this.file.writeByte(2);
        this.file.write(data, 0, i);
    }

    private int getSectorData(ChunkPos pos) {
        return this.offsets[this.getIndex(pos)];
    }

    public boolean hasChunk(ChunkPos pos) {
        return this.getSectorData(pos) != 0;
    }

    private void setOffset(ChunkPos chunkPos, int i) throws IOException {
        int j = this.getIndex(chunkPos);
        this.offsets[j] = i;
        this.file.seek(j * 4);
        this.file.writeInt(i);
    }

    private int getIndex(ChunkPos pos) {
        return pos.getRegionRelativeX() + pos.getRegionRelativeZ() * 32;
    }

    private void setTimestamp(ChunkPos chunkPos, int i) throws IOException {
        int j = this.getIndex(chunkPos);
        this.chunkTimestamps[j] = i;
        this.file.seek(4096 + j * 4);
        this.file.writeInt(i);
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }

    class ChunkBuffer
    extends ByteArrayOutputStream {
        private final ChunkPos pos;

        public ChunkBuffer(ChunkPos chunkPos) {
            super(8096);
            this.pos = chunkPos;
        }

        @Override
        public void close() throws IOException {
            RegionFile.this.write(this.pos, this.buf, this.count);
        }
    }
}

