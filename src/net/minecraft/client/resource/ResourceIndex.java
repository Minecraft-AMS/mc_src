/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ResourceIndex {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, File> rootIndex = Maps.newHashMap();
    private final Map<Identifier, File> field_21556 = Maps.newHashMap();

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
                    this.field_21556.put(new Identifier(strings[0], strings[1]), file3);
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
        return this.field_21556.get(identifier);
    }

    @Nullable
    public File findFile(String path) {
        return this.rootIndex.get(path);
    }

    public Collection<Identifier> getFilesRecursively(String string, String string2, int i, Predicate<String> predicate) {
        return this.field_21556.keySet().stream().filter(identifier -> {
            String string3 = identifier.getPath();
            return identifier.getNamespace().equals(string2) && !string3.endsWith(".mcmeta") && string3.startsWith(string + "/") && predicate.test(string3);
        }).collect(Collectors.toList());
    }
}

