/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerConfigList<K, V extends ServerConfigEntry<K>> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Gson GSON;
    private final File file;
    private final Map<String, V> map = Maps.newHashMap();
    private boolean enabled = true;
    private static final ParameterizedType field_14369 = new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{ServerConfigEntry.class};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    public ServerConfigList(File file) {
        this.file = file;
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeHierarchyAdapter(ServerConfigEntry.class, (Object)new DeSerializer());
        this.GSON = gsonBuilder.create();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public File getFile() {
        return this.file;
    }

    public void add(V serverConfigEntry) {
        this.map.put(this.toString(((ServerConfigEntry)serverConfigEntry).getKey()), serverConfigEntry);
        try {
            this.save();
        }
        catch (IOException iOException) {
            LOGGER.warn("Could not save the list after adding a user.", (Throwable)iOException);
        }
    }

    @Nullable
    public V get(K object) {
        this.removeInvalidEntries();
        return (V)((ServerConfigEntry)this.map.get(this.toString(object)));
    }

    public void remove(K object) {
        this.map.remove(this.toString(object));
        try {
            this.save();
        }
        catch (IOException iOException) {
            LOGGER.warn("Could not save the list after removing a user.", (Throwable)iOException);
        }
    }

    public void removeEntry(ServerConfigEntry<K> serverConfigEntry) {
        this.remove(serverConfigEntry.getKey());
    }

    public String[] getNames() {
        return this.map.keySet().toArray(new String[this.map.size()]);
    }

    public boolean isEmpty() {
        return this.map.size() < 1;
    }

    protected String toString(K profile) {
        return profile.toString();
    }

    protected boolean contains(K object) {
        return this.map.containsKey(this.toString(object));
    }

    private void removeInvalidEntries() {
        ArrayList list = Lists.newArrayList();
        for (ServerConfigEntry serverConfigEntry : this.map.values()) {
            if (!serverConfigEntry.isInvalid()) continue;
            list.add(serverConfigEntry.getKey());
        }
        for (Object object : list) {
            this.map.remove(this.toString(object));
        }
    }

    protected ServerConfigEntry<K> fromJson(JsonObject jsonObject) {
        return new ServerConfigEntry<Object>(null, jsonObject);
    }

    public Collection<V> values() {
        return this.map.values();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save() throws IOException {
        Collection<V> collection = this.map.values();
        String string = this.GSON.toJson(collection);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newWriter((File)this.file, (Charset)StandardCharsets.UTF_8);
            bufferedWriter.write(string);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedWriter);
            throw throwable;
        }
        IOUtils.closeQuietly((Writer)bufferedWriter);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() throws FileNotFoundException {
        if (!this.file.exists()) {
            return;
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newReader((File)this.file, (Charset)StandardCharsets.UTF_8);
            Collection collection = (Collection)JsonHelper.deserialize(this.GSON, (Reader)bufferedReader, (Type)field_14369);
            if (collection != null) {
                this.map.clear();
                for (ServerConfigEntry serverConfigEntry : collection) {
                    if (serverConfigEntry.getKey() == null) continue;
                    this.map.put(this.toString(serverConfigEntry.getKey()), serverConfigEntry);
                }
            }
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedReader);
            throw throwable;
        }
        IOUtils.closeQuietly((Reader)bufferedReader);
    }

    class DeSerializer
    implements JsonDeserializer<ServerConfigEntry<K>>,
    JsonSerializer<ServerConfigEntry<K>> {
        private DeSerializer() {
        }

        public JsonElement serialize(ServerConfigEntry<K> serverConfigEntry, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            serverConfigEntry.serialize(jsonObject);
            return jsonObject;
        }

        public ServerConfigEntry<K> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return ServerConfigList.this.fromJson(jsonObject);
            }
            return null;
        }

        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((ServerConfigEntry)entry, unused, context);
        }

        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

