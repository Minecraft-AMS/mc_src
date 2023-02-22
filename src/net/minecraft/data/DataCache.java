/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.minecraft.GameVersion;
import net.minecraft.data.DataWriter;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DataCache {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String HEADER = "// ";
    private final Path root;
    private final Path cachePath;
    private final String versionName;
    private final Map<String, CachedData> cachedDatas;
    private final Set<String> dataWriters = new HashSet<String>();
    private final Set<Path> paths = new HashSet<Path>();
    private final int totalSize;
    private int totalCacheMissCount;

    private Path getPath(String providerName) {
        return this.cachePath.resolve(Hashing.sha1().hashString((CharSequence)providerName, StandardCharsets.UTF_8).toString());
    }

    public DataCache(Path root, Collection<String> providerNames, GameVersion gameVersion) throws IOException {
        this.versionName = gameVersion.getName();
        this.root = root;
        this.cachePath = root.resolve(".cache");
        Files.createDirectories(this.cachePath, new FileAttribute[0]);
        HashMap<String, CachedData> map = new HashMap<String, CachedData>();
        int i = 0;
        for (String string : providerNames) {
            Path path = this.getPath(string);
            this.paths.add(path);
            CachedData cachedData = DataCache.parseOrCreateCache(root, path);
            map.put(string, cachedData);
            i += cachedData.size();
        }
        this.cachedDatas = map;
        this.totalSize = i;
    }

    private static CachedData parseOrCreateCache(Path root, Path dataProviderPath) {
        if (Files.isReadable(dataProviderPath)) {
            try {
                return CachedData.parseCache(root, dataProviderPath);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to parse cache {}, discarding", (Object)dataProviderPath, (Object)exception);
            }
        }
        return new CachedData("unknown", (ImmutableMap<Path, HashCode>)ImmutableMap.of());
    }

    public boolean isVersionDifferent(String providerName) {
        CachedData cachedData = this.cachedDatas.get(providerName);
        return cachedData == null || !cachedData.version.equals(this.versionName);
    }

    public CompletableFuture<RunResult> run(String providerName, Runner runner) {
        CachedData cachedData = this.cachedDatas.get(providerName);
        if (cachedData == null) {
            throw new IllegalStateException("Provider not registered: " + providerName);
        }
        CachedDataWriter cachedDataWriter = new CachedDataWriter(providerName, this.versionName, cachedData);
        return runner.update(cachedDataWriter).thenApply(void_ -> cachedDataWriter.finish());
    }

    public void store(RunResult runResult) {
        this.cachedDatas.put(runResult.providerName(), runResult.cache());
        this.dataWriters.add(runResult.providerName());
        this.totalCacheMissCount += runResult.cacheMissCount();
    }

    public void write() throws IOException {
        HashSet<Path> set = new HashSet<Path>();
        this.cachedDatas.forEach((providerName, cachedData) -> {
            if (this.dataWriters.contains(providerName)) {
                Path path = this.getPath((String)providerName);
                cachedData.write(this.root, path, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + providerName);
            }
            set.addAll((Collection<Path>)cachedData.data().keySet());
        });
        set.add(this.root.resolve("version.json"));
        MutableInt mutableInt = new MutableInt();
        MutableInt mutableInt2 = new MutableInt();
        try (Stream<Path> stream = Files.walk(this.root, new FileVisitOption[0]);){
            stream.forEach(path -> {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    return;
                }
                if (this.paths.contains(path)) {
                    return;
                }
                mutableInt.increment();
                if (set.contains(path)) {
                    return;
                }
                try {
                    Files.delete(path);
                }
                catch (IOException iOException) {
                    LOGGER.warn("Failed to delete file {}", path, (Object)iOException);
                }
                mutableInt2.increment();
            });
        }
        LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{mutableInt, this.totalSize, set.size(), mutableInt2, this.totalCacheMissCount});
    }

    static final class CachedData
    extends Record {
        final String version;
        private final ImmutableMap<Path, HashCode> data;

        CachedData(String string, ImmutableMap<Path, HashCode> immutableMap) {
            this.version = string;
            this.data = immutableMap;
        }

        @Nullable
        public HashCode get(Path path) {
            return (HashCode)this.data.get((Object)path);
        }

        public int size() {
            return this.data.size();
        }

        public static CachedData parseCache(Path root, Path dataProviderPath) throws IOException {
            try (BufferedReader bufferedReader = Files.newBufferedReader(dataProviderPath, StandardCharsets.UTF_8);){
                String string = bufferedReader.readLine();
                if (!string.startsWith(DataCache.HEADER)) {
                    throw new IllegalStateException("Missing cache file header");
                }
                String[] strings = string.substring(DataCache.HEADER.length()).split("\t", 2);
                String string2 = strings[0];
                ImmutableMap.Builder builder = ImmutableMap.builder();
                bufferedReader.lines().forEach(line -> {
                    int i = line.indexOf(32);
                    builder.put((Object)root.resolve(line.substring(i + 1)), (Object)HashCode.fromString((String)line.substring(0, i)));
                });
                CachedData cachedData = new CachedData(string2, (ImmutableMap<Path, HashCode>)builder.build());
                return cachedData;
            }
        }

        public void write(Path root, Path dataProviderPath, String description) {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(dataProviderPath, StandardCharsets.UTF_8, new OpenOption[0]);){
                bufferedWriter.write(DataCache.HEADER);
                bufferedWriter.write(this.version);
                bufferedWriter.write(9);
                bufferedWriter.write(description);
                bufferedWriter.newLine();
                for (Map.Entry entry : this.data.entrySet()) {
                    bufferedWriter.write(((HashCode)entry.getValue()).toString());
                    bufferedWriter.write(32);
                    bufferedWriter.write(root.relativize((Path)entry.getKey()).toString());
                    bufferedWriter.newLine();
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Unable write cachefile {}: {}", (Object)dataProviderPath, (Object)iOException);
            }
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CachedData.class, "version;data", "version", "data"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CachedData.class, "version;data", "version", "data"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CachedData.class, "version;data", "version", "data"}, this, object);
        }

        public String version() {
            return this.version;
        }

        public ImmutableMap<Path, HashCode> data() {
            return this.data;
        }
    }

    class CachedDataWriter
    implements DataWriter {
        private final String providerName;
        private final CachedData oldCache;
        private final IntermediaryCache newCache;
        private final AtomicInteger cacheMissCount = new AtomicInteger();
        private volatile boolean closed;

        CachedDataWriter(String providerName, String version, CachedData oldCache) {
            this.providerName = providerName;
            this.oldCache = oldCache;
            this.newCache = new IntermediaryCache(version);
        }

        private boolean isCacheInvalid(Path path, HashCode hashCode) {
            return !Objects.equals(this.oldCache.get(path), hashCode) || !Files.exists(path, new LinkOption[0]);
        }

        @Override
        public void write(Path path, byte[] data, HashCode hashCode) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("Cannot write to cache as it has already been closed");
            }
            if (this.isCacheInvalid(path, hashCode)) {
                this.cacheMissCount.incrementAndGet();
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                Files.write(path, data, new OpenOption[0]);
            }
            this.newCache.put(path, hashCode);
        }

        public RunResult finish() {
            this.closed = true;
            return new RunResult(this.providerName, this.newCache.toCachedData(), this.cacheMissCount.get());
        }
    }

    @FunctionalInterface
    public static interface Runner {
        public CompletableFuture<?> update(DataWriter var1);
    }

    public record RunResult(String providerName, CachedData cache, int cacheMissCount) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RunResult.class, "providerId;cache;writes", "providerName", "cache", "cacheMissCount"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RunResult.class, "providerId;cache;writes", "providerName", "cache", "cacheMissCount"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RunResult.class, "providerId;cache;writes", "providerName", "cache", "cacheMissCount"}, this, object);
        }
    }

    record IntermediaryCache(String version, ConcurrentMap<Path, HashCode> data) {
        IntermediaryCache(String version) {
            this(version, new ConcurrentHashMap<Path, HashCode>());
        }

        public void put(Path path, HashCode hashCode) {
            this.data.put(path, hashCode);
        }

        public CachedData toCachedData() {
            return new CachedData(this.version, (ImmutableMap<Path, HashCode>)ImmutableMap.copyOf(this.data));
        }
    }
}

