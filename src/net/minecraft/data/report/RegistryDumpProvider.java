/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryDumpProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public RegistryDumpProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        JsonObject jsonObject = new JsonObject();
        Registry.REGISTRIES.streamEntries().forEach(entry -> jsonObject.add(entry.registryKey().getValue().toString(), RegistryDumpProvider.toJson((Registry)entry.value())));
        Path path = this.generator.getOutput().resolve("reports/registries.json");
        DataProvider.writeToPath(GSON, cache, (JsonElement)jsonObject, path);
    }

    private static <T> JsonElement toJson(Registry<T> registry) {
        JsonObject jsonObject = new JsonObject();
        if (registry instanceof DefaultedRegistry) {
            Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
            jsonObject.addProperty("default", identifier.toString());
        }
        int i = Registry.REGISTRIES.getRawId(registry);
        jsonObject.addProperty("protocol_id", (Number)i);
        JsonObject jsonObject2 = new JsonObject();
        registry.streamEntries().forEach(entry -> {
            Object object = entry.value();
            int i = registry.getRawId(object);
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("protocol_id", (Number)i);
            jsonObject2.add(entry.registryKey().getValue().toString(), (JsonElement)jsonObject2);
        });
        jsonObject.add("entries", (JsonElement)jsonObject2);
        return jsonObject;
    }

    @Override
    public String getName() {
        return "Registry Dump";
    }
}

