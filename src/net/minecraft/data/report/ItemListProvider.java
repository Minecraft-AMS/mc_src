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
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class ItemListProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator root;

    public ItemListProvider(DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }

    @Override
    public void run(DataCache dataCache) throws IOException {
        JsonObject jsonObject = new JsonObject();
        Registry.REGISTRIES.getIds().forEach(identifier -> jsonObject.add(identifier.toString(), ItemListProvider.toJson((MutableRegistry)Registry.REGISTRIES.get((Identifier)identifier))));
        Path path = this.root.getOutput().resolve("reports/registries.json");
        DataProvider.writeToPath(GSON, dataCache, (JsonElement)jsonObject, path);
    }

    private static <T> JsonElement toJson(MutableRegistry<T> mutableRegistry) {
        JsonObject jsonObject = new JsonObject();
        if (mutableRegistry instanceof DefaultedRegistry) {
            Identifier identifier = ((DefaultedRegistry)mutableRegistry).getDefaultId();
            jsonObject.addProperty("default", identifier.toString());
        }
        int i = Registry.REGISTRIES.getRawId(mutableRegistry);
        jsonObject.addProperty("protocol_id", (Number)i);
        JsonObject jsonObject2 = new JsonObject();
        for (Identifier identifier2 : mutableRegistry.getIds()) {
            Object object = mutableRegistry.get(identifier2);
            int j = mutableRegistry.getRawId(object);
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("protocol_id", (Number)j);
            jsonObject2.add(identifier2.toString(), (JsonElement)jsonObject3);
        }
        jsonObject.add("entries", (JsonElement)jsonObject2);
        return jsonObject;
    }

    @Override
    public String getName() {
        return "Registry Dump";
    }
}

