/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 *  com.google.gson.JsonElement
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.slf4j.Logger
 */
package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataWriter;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public interface DataProvider {
    public static final ToIntFunction<String> JSON_KEY_SORT_ORDER = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), map -> {
        map.put((Object)"type", 0);
        map.put((Object)"parent", 1);
        map.defaultReturnValue(2);
    });
    public static final Comparator<String> JSON_KEY_SORTING_COMPARATOR = Comparator.comparingInt(JSON_KEY_SORT_ORDER).thenComparing(key -> key);
    public static final Logger LOGGER = LogUtils.getLogger();

    public CompletableFuture<?> run(DataWriter var1);

    public String getName();

    public static CompletableFuture<?> writeToPath(DataWriter writer, JsonElement json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), (OutputStream)byteArrayOutputStream);
                try (JsonWriter jsonWriter = new JsonWriter((Writer)new OutputStreamWriter((OutputStream)hashingOutputStream, StandardCharsets.UTF_8));){
                    jsonWriter.setSerializeNulls(false);
                    jsonWriter.setIndent("  ");
                    JsonHelper.writeSorted(jsonWriter, json, JSON_KEY_SORTING_COMPARATOR);
                }
                writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to save file to {}", (Object)path, (Object)iOException);
            }
        }, Util.getMainWorkerExecutor());
    }

    @FunctionalInterface
    public static interface Factory<T extends DataProvider> {
        public T create(DataOutput var1);
    }
}

