/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ResourceIndex {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, File> rootIndex = Maps.newHashMap();
    private final Map<Identifier, File> namespacedIndex = Maps.newHashMap();

    protected ResourceIndex() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResourceIndex(File directory, String indexName) {
        File file = new File(directory, "objects");
        File file2 = new File(directory, "indexes/" + indexName + ".json");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newReader((File)file2, (Charset)StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "objects", null);
            if (jsonObject2 != null) {
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    JsonObject jsonObject3 = (JsonObject)entry.getValue();
                    String string = (String)entry.getKey();
                    String[] strings = string.split("/", 2);
                    String string2 = JsonHelper.getString(jsonObject3, "hash");
                    File file3 = new File(file, string2.substring(0, 2) + "/" + string2);
                    if (strings.length == 1) {
                        this.rootIndex.put(strings[0], file3);
                        continue;
                    }
                    this.namespacedIndex.put(new Identifier(strings[0], strings[1]), file3);
                }
            }
        }
        catch (JsonParseException jsonParseException) {
            LOGGER.error("Unable to parse resource index file: {}", (Object)file2);
        }
        catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error("Can't find the resource index file: {}", (Object)file2);
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader);
        }
    }

    @Nullable
    public File getResource(Identifier identifier) {
        return this.namespacedIndex.get(identifier);
    }

    @Nullable
    public File findFile(String path) {
        return this.rootIndex.get(path);
    }

    public Collection<Identifier> getFilesRecursively(String prefix, String namespace, int maxDepth, Predicate<String> pathFilter) {
        return this.namespacedIndex.keySet().stream().filter(id -> {
            String string3 = id.getPath();
            return id.getNamespace().equals(namespace) && !string3.endsWith(".mcmeta") && string3.startsWith(prefix + "/") && pathFilter.test(string3);
        }).collect(Collectors.toList());
    }
}

