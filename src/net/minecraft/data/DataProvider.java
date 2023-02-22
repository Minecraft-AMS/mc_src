/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 *  com.google.gson.JsonElement
 *  com.google.gson.stream.JsonWriter
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import net.minecraft.data.DataWriter;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;

public interface DataProvider {
    public static final ToIntFunction<String> JSON_KEY_SORT_ORDER = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), map -> {
        map.put((Object)"type", 0);
        map.put((Object)"parent", 1);
        map.defaultReturnValue(2);
    });
    public static final Comparator<String> JSON_KEY_SORTING_COMPARATOR = Comparator.comparingInt(JSON_KEY_SORT_ORDER).thenComparing(key -> key);

    public void run(DataWriter var1) throws IOException;

    public String getName();

    public static void writeToPath(DataWriter writer, JsonElement json, Path path) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), (OutputStream)byteArrayOutputStream);
        OutputStreamWriter writer2 = new OutputStreamWriter((OutputStream)hashingOutputStream, StandardCharsets.UTF_8);
        JsonWriter jsonWriter = new JsonWriter((Writer)writer2);
        jsonWriter.setSerializeNulls(false);
        jsonWriter.setIndent("  ");
        JsonHelper.writeSorted(jsonWriter, json, JSON_KEY_SORTING_COMPARATOR);
        jsonWriter.close();
        writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
    }
}

