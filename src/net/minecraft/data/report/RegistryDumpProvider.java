/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryDumpProvider
implements DataProvider {
    private final DataGenerator generator;

    public RegistryDumpProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DataWriter writer) throws IOException {
        JsonObject jsonObject = new JsonObject();
        Registry.REGISTRIES.streamEntries().forEach(entry -> jsonObject.add(entry.registryKey().getValue().toString(), RegistryDumpProvider.toJson((Registry)entry.value())));
        Path path = this.generator.resolveRootDirectoryPath(DataGenerator.OutputType.REPORTS).resolve("registries.json");
        DataProvider.writeToPath(writer, (JsonElement)jsonObject, path);
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

