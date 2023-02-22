/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk;

import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import org.jetbrains.annotations.Nullable;

public final class ChunkNibbleArray {
    public static final int COPY_TIMES = 16;
    public static final int COPY_BLOCK_SIZE = 128;
    public static final int BYTES_LENGTH = 2048;
    private static final int NIBBLE_BITS = 4;
    @Nullable
    protected byte[] bytes;

    public ChunkNibbleArray() {
    }

    public ChunkNibbleArray(byte[] bytes) {
        this.bytes = bytes;
        if (bytes.length != 2048) {
            throw Util.throwOrPause(new IllegalArgumentException("DataLayer should be 2048 bytes not: " + bytes.length));
        }
    }

    protected ChunkNibbleArray(int size) {
        this.bytes = new byte[size];
    }

    public int get(int x, int y, int z) {
        return this.get(ChunkNibbleArray.getIndex(x, y, z));
    }

    public void set(int x, int y, int z, int value) {
        this.set(ChunkNibbleArray.getIndex(x, y, z), value);
    }

    private static int getIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private int get(int index) {
        if (this.bytes == null) {
            return 0;
        }
        int i = ChunkNibbleArray.getArrayIndex(index);
        int j = ChunkNibbleArray.occupiesSmallerBits(index);
        return this.bytes[i] >> 4 * j & 0xF;
    }

    private void set(int index, int value) {
        if (this.bytes == null) {
            this.bytes = new byte[2048];
        }
        int i = ChunkNibbleArray.getArrayIndex(index);
        int j = ChunkNibbleArray.occupiesSmallerBits(index);
        int k = ~(15 << 4 * j);
        int l = (value & 0xF) << 4 * j;
        this.bytes[i] = (byte)(this.bytes[i] & k | l);
    }

    private static int occupiesSmallerBits(int i) {
        return i & 1;
    }

    private static int getArrayIndex(int i) {
        return i >> 1;
    }

    public byte[] asByteArray() {
        if (this.bytes == null) {
            this.bytes = new byte[2048];
        }
        return this.bytes;
    }

    public ChunkNibbleArray copy() {
        if (this.bytes == null) {
            return new ChunkNibbleArray();
        }
        return new ChunkNibbleArray((byte[])this.bytes.clone());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4096; ++i) {
            stringBuilder.append(Integer.toHexString(this.get(i)));
            if ((i & 0xF) == 15) {
                stringBuilder.append("\n");
            }
            if ((i & 0xFF) != 255) continue;
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Debug
    public String bottomToString(int unused) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 256; ++i) {
            stringBuilder.append(Integer.toHexString(this.get(i)));
            if ((i & 0xF) != 15) continue;
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean isUninitialized() {
        return this.bytes == null;
    }
}

