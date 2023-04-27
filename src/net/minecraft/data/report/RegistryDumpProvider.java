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
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistryDumpProvider
implements DataProvider {
    private final DataOutput output;

    public RegistryDumpProvider(DataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        JsonObject jsonObject = new JsonObject();
        Registries.REGISTRIES.streamEntries().forEach(entry -> jsonObject.add(entry.registryKey().getValue().toString(), RegistryDumpProvider.toJson((Registry)entry.value())));
        Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("registries.json");
        return DataProvider.writeToPath(writer, (JsonElement)jsonObject, path);
    }

    private static <T> JsonElement toJson(Registry<T> registry) {
        JsonObject jsonObject = new JsonObject();
        if (registry instanceof DefaultedRegistry) {
            Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
            jsonObject.addProperty("default", identifier.toString());
        }
        int i = Registries.REGISTRIES.getRawId(registry);
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

