/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.util.FixedBufferInputStream;
import org.jetbrains.annotations.Nullable;

public class ChunkStreamVersion {
    private static final Int2ObjectMap<ChunkStreamVersion> VERSIONS = new Int2ObjectOpenHashMap();
    public static final ChunkStreamVersion GZIP = ChunkStreamVersion.add(new ChunkStreamVersion(1, stream -> new FixedBufferInputStream(new GZIPInputStream((InputStream)stream)), stream -> new BufferedOutputStream(new GZIPOutputStream((OutputStream)stream))));
    public static final ChunkStreamVersion DEFLATE = ChunkStreamVersion.add(new ChunkStreamVersion(2, stream -> new FixedBufferInputStream(new InflaterInputStream((InputStream)stream)), stream -> new BufferedOutputStream(new DeflaterOutputStream((OutputStream)stream))));
    public static final ChunkStreamVersion UNCOMPRESSED = ChunkStreamVersion.add(new ChunkStreamVersion(3, stream -> stream, stream -> stream));
    private final int id;
    private final Wrapper<InputStream> inputStreamWrapper;
    private final Wrapper<OutputStream> outputStreamWrapper;

    private ChunkStreamVersion(int id, Wrapper<InputStream> inputStreamWrapper, Wrapper<OutputStream> outputStreamWrapper) {
        this.id = id;
        this.inputStreamWrapper = inputStreamWrapper;
        this.outputStreamWrapper = outputStreamWrapper;
    }

    private static ChunkStreamVersion add(ChunkStreamVersion version) {
        VERSIONS.put(version.id, (Object)version);
        return version;
    }

    @Nullable
    public static ChunkStreamVersion get(int id) {
        return (ChunkStreamVersion)VERSIONS.get(id);
    }

    public static boolean exists(int id) {
        return VERSIONS.containsKey(id);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream outputStream) throws IOException {
        return this.outputStreamWrapper.wrap(outputStream);
    }

    public InputStream wrap(InputStream inputStream) throws IOException {
        return this.inputStreamWrapper.wrap(inputStream);
    }

    @FunctionalInterface
    static interface Wrapper<O> {
        public O wrap(O var1) throws IOException;
    }
}

