/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TagContainer<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private Map<Identifier, Tag<T>> entries = ImmutableMap.of();
    private final Function<Identifier, Optional<T>> getter;
    private final String dataType;
    private final boolean ordered;
    private final String entryType;

    public TagContainer(Function<Identifier, Optional<T>> getter, String dataType, boolean ordered, String entryType) {
        this.getter = getter;
        this.dataType = dataType;
        this.ordered = ordered;
        this.entryType = entryType;
    }

    @Nullable
    public Tag<T> get(Identifier id) {
        return this.entries.get(id);
    }

    public Tag<T> getOrCreate(Identifier id) {
        Tag<T> tag = this.entries.get(id);
        if (tag == null) {
            return new Tag(id);
        }
        return tag;
    }

    public Collection<Identifier> getKeys() {
        return this.entries.keySet();
    }

    @Environment(value=EnvType.CLIENT)
    public Collection<Identifier> getTagsFor(T object) {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.entries.entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    public CompletableFuture<Map<Identifier, Tag.Builder<T>>> prepareReload(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap map = Maps.newHashMap();
            for (Identifier identifier2 : manager.findResources(this.dataType, string -> string.endsWith(".json"))) {
                String string2 = identifier2.getPath();
                Identifier identifier22 = new Identifier(identifier2.getNamespace(), string2.substring(this.dataType.length() + 1, string2.length() - JSON_EXTENSION_LENGTH));
                try {
                    for (Resource resource : manager.getAllResources(identifier2)) {
                        try {
                            InputStream inputStream = resource.getInputStream();
                            Throwable throwable = null;
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                                Throwable throwable2 = null;
                                try {
                                    JsonObject jsonObject = JsonHelper.deserialize(GSON, (Reader)reader, JsonObject.class);
                                    if (jsonObject == null) {
                                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", (Object)this.entryType, (Object)identifier22, (Object)identifier2, (Object)resource.getResourcePackName());
                                        continue;
                                    }
                                    map.computeIfAbsent(identifier22, identifier -> Util.make(Tag.Builder.create(), builder -> builder.ordered(this.ordered))).fromJson(this.getter, jsonObject);
                                }
                                catch (Throwable throwable3) {
                                    throwable2 = throwable3;
                                    throw throwable3;
                                }
                                finally {
                                    if (reader == null) continue;
                                    if (throwable2 != null) {
                                        try {
                                            ((Reader)reader).close();
                                        }
                                        catch (Throwable throwable4) {
                                            throwable2.addSuppressed(throwable4);
                                        }
                                        continue;
                                    }
                                    ((Reader)reader).close();
                                }
                            }
                            catch (Throwable throwable5) {
                                throwable = throwable5;
                                throw throwable5;
                            }
                            finally {
                                if (inputStream == null) continue;
                                if (throwable != null) {
                                    try {
                                        inputStream.close();
                                    }
                                    catch (Throwable throwable6) {
                                        throwable.addSuppressed(throwable6);
                                    }
                                    continue;
                                }
                                inputStream.close();
                            }
                        }
                        catch (IOException | RuntimeException exception) {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", (Object)this.entryType, (Object)identifier22, (Object)identifier2, (Object)resource.getResourcePackName(), (Object)exception);
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)resource);
                        }
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Couldn't read {} tag list {} from {}", (Object)this.entryType, (Object)identifier22, (Object)identifier2, (Object)iOException);
                }
            }
            return map;
        }, executor);
    }

    public void applyReload(Map<Identifier, Tag.Builder<T>> preparedBuilders) {
        HashMap map = Maps.newHashMap();
        while (!preparedBuilders.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Tag.Builder<T>>> iterator = preparedBuilders.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Tag.Builder<T>> entry = iterator.next();
                Tag.Builder builder2 = entry.getValue();
                if (!builder2.applyTagGetter(map::get)) continue;
                bl = true;
                Identifier identifier2 = entry.getKey();
                map.put(identifier2, builder2.build(identifier2));
                iterator.remove();
            }
            if (bl) continue;
            preparedBuilders.forEach((identifier, builder) -> LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", (Object)this.entryType, identifier));
            break;
        }
        preparedBuilders.forEach((identifier, builder) -> map.put(identifier, builder.build((Identifier)identifier)));
        this.setEntries(map);
    }

    protected void setEntries(Map<Identifier, Tag<T>> entries) {
        this.entries = ImmutableMap.copyOf(entries);
    }

    public Map<Identifier, Tag<T>> getEntries() {
        return this.entries;
    }
}

