/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SnbtProvider
implements DataProvider {
    @Nullable
    private static final Path DEBUG_OUTPUT_DIRECTORY = null;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataOutput output;
    private final Iterable<Path> paths;
    private final List<Tweaker> write = Lists.newArrayList();

    public SnbtProvider(DataOutput output, Iterable<Path> paths) {
        this.output = output;
        this.paths = paths;
    }

    public SnbtProvider addWriter(Tweaker tweaker) {
        this.write.add(tweaker);
        return this;
    }

    private NbtCompound write(String key, NbtCompound compound) {
        NbtCompound nbtCompound = compound;
        for (Tweaker tweaker : this.write) {
            nbtCompound = tweaker.write(key, nbtCompound);
        }
        return nbtCompound;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Path path = this.output.getPath();
        ArrayList list = Lists.newArrayList();
        for (Path path2 : this.paths) {
            list.add(CompletableFuture.supplyAsync(() -> {
                CompletableFuture<Void> completableFuture;
                block8: {
                    Stream<Path> stream = Files.walk(path2, new FileVisitOption[0]);
                    try {
                        completableFuture = CompletableFuture.allOf((CompletableFuture[])stream.filter(path -> path.toString().endsWith(".snbt")).map(path -> CompletableFuture.runAsync(() -> {
                            CompressedData compressedData = this.toCompressedNbt((Path)path, this.getFileName(path2, (Path)path));
                            this.write(writer, compressedData, path);
                        }, Util.getMainWorkerExecutor())).toArray(CompletableFuture[]::new));
                        if (stream == null) break block8;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (stream != null) {
                                try {
                                    stream.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception exception) {
                            throw new RuntimeException("Failed to read structure input directory, aborting", exception);
                        }
                    }
                    stream.close();
                }
                return completableFuture;
            }, Util.getMainWorkerExecutor()).thenCompose(future -> future));
        }
        return Util.combine(list);
    }

    @Override
    public final String getName() {
        return "SNBT -> NBT";
    }

    private String getFileName(Path root, Path file) {
        String string = root.relativize(file).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".snbt".length());
    }

    private CompressedData toCompressedNbt(Path path, String name) {
        CompressedData compressedData;
        block8: {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            try {
                String string = IOUtils.toString((Reader)bufferedReader);
                NbtCompound nbtCompound = this.write(name, NbtHelper.fromNbtProviderString(string));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), (OutputStream)byteArrayOutputStream);
                NbtIo.writeCompressed(nbtCompound, (OutputStream)hashingOutputStream);
                byte[] bs = byteArrayOutputStream.toByteArray();
                HashCode hashCode = hashingOutputStream.hash();
                String string2 = DEBUG_OUTPUT_DIRECTORY != null ? NbtHelper.toNbtProviderString(nbtCompound) : null;
                compressedData = new CompressedData(name, bs, string2, hashCode);
                if (bufferedReader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Throwable throwable3) {
                    throw new CompressionException(path, throwable3);
                }
            }
            bufferedReader.close();
        }
        return compressedData;
    }

    private void write(DataWriter cache, CompressedData data, Path root) {
        Path path;
        if (data.snbtContent != null) {
            path = DEBUG_OUTPUT_DIRECTORY.resolve(data.name + ".snbt");
            try {
                NbtProvider.writeTo(DataWriter.UNCACHED, path, data.snbtContent);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", new Object[]{data.name, path, iOException});
            }
        }
        path = root.resolve(data.name + ".nbt");
        try {
            cache.write(path, data.bytes, data.sha1);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't write structure {} at {}", new Object[]{data.name, path, iOException});
        }
    }

    @FunctionalInterface
    public static interface Tweaker {
        public NbtCompound write(String var1, NbtCompound var2);
    }

    static final class CompressedData
    extends Record {
        final String name;
        final byte[] bytes;
        @Nullable
        final String snbtContent;
        final HashCode sha1;

        CompressedData(String name, byte[] bytes, @Nullable String snbtContent, HashCode hashCode) {
            this.name = name;
            this.bytes = bytes;
            this.snbtContent = snbtContent;
            this.sha1 = hashCode;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CompressedData.class, "name;payload;snbtPayload;hash", "name", "bytes", "snbtContent", "sha1"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CompressedData.class, "name;payload;snbtPayload;hash", "name", "bytes", "snbtContent", "sha1"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CompressedData.class, "name;payload;snbtPayload;hash", "name", "bytes", "snbtContent", "sha1"}, this, object);
        }

        public String name() {
            return this.name;
        }

        public byte[] bytes() {
            return this.bytes;
        }

        @Nullable
        public String snbtContent() {
            return this.snbtContent;
        }

        public HashCode sha1() {
            return this.sha1;
        }
    }

    static class CompressionException
    extends RuntimeException {
        public CompressionException(Path path, Throwable cause) {
            super(path.toAbsolutePath().toString(), cause);
        }
    }
}

