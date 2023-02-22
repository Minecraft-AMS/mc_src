/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonDataLoader
extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int FILE_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String dataType;

    public JsonDataLoader(Gson gson, String dataType) {
        this.gson = gson;
        this.dataType = dataType;
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
        HashMap map = Maps.newHashMap();
        int i = this.dataType.length() + 1;
        for (Identifier identifier : resourceManager.findResources(this.dataType, string -> string.endsWith(".json"))) {
            String string2 = identifier.getPath();
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string2.substring(i, string2.length() - FILE_SUFFIX_LENGTH));
            try {
                Resource resource = resourceManager.getResource(identifier);
                Throwable throwable = null;
                try {
                    InputStream inputStream = resource.getInputStream();
                    Throwable throwable2 = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        Throwable throwable3 = null;
                        try {
                            JsonElement jsonElement = JsonHelper.deserialize(this.gson, (Reader)reader, JsonElement.class);
                            if (jsonElement != null) {
                                JsonElement jsonElement2 = map.put(identifier2, jsonElement);
                                if (jsonElement2 == null) continue;
                                throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
                            }
                            LOGGER.error("Couldn't load data file {} from {} as it's null or empty", (Object)identifier2, (Object)identifier);
                        }
                        catch (Throwable throwable4) {
                            throwable3 = throwable4;
                            throw throwable4;
                        }
                        finally {
                            if (reader == null) continue;
                            if (throwable3 != null) {
                                try {
                                    ((Reader)reader).close();
                                }
                                catch (Throwable throwable5) {
                                    throwable3.addSuppressed(throwable5);
                                }
                                continue;
                            }
                            ((Reader)reader).close();
                        }
                    }
                    catch (Throwable throwable6) {
                        throwable2 = throwable6;
                        throw throwable6;
                    }
                    finally {
                        if (inputStream == null) continue;
                        if (throwable2 != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable7) {
                                throwable2.addSuppressed(throwable7);
                            }
                            continue;
                        }
                        inputStream.close();
                    }
                }
                catch (Throwable throwable8) {
                    throwable = throwable8;
                    throw throwable8;
                }
                finally {
                    if (resource == null) continue;
                    if (throwable != null) {
                        try {
                            resource.close();
                        }
                        catch (Throwable throwable9) {
                            throwable.addSuppressed(throwable9);
                        }
                        continue;
                    }
                    resource.close();
                }
            }
            catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", (Object)identifier2, (Object)identifier, (Object)exception);
            }
        }
        return map;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

