/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.JsonOps
 *  org.slf4j.Logger
 */
package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public class WorldgenProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataGenerator generator;

    public WorldgenProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DataWriter writer) {
        DynamicRegistryManager dynamicRegistryManager = DynamicRegistryManager.BUILTIN.get();
        RegistryOps dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
        DynamicRegistryManager.getInfos().forEach(info -> this.writeRegistryEntries(writer, dynamicRegistryManager, dynamicOps, (DynamicRegistryManager.Info)info));
    }

    private <T> void writeRegistryEntries(DataWriter dataWriter, DynamicRegistryManager dynamicRegistryManager, DynamicOps<JsonElement> dynamicOps, DynamicRegistryManager.Info<T> info) {
        RegistryKey<Registry<T>> registryKey = info.registry();
        Registry<T> registry = dynamicRegistryManager.getManaged(registryKey);
        DataGenerator.PathResolver pathResolver = this.generator.createPathResolver(DataGenerator.OutputType.REPORTS, registryKey.getValue().getPath());
        for (Map.Entry<RegistryKey<T>, T> entry : registry.getEntrySet()) {
            WorldgenProvider.writeToPath(pathResolver.resolveJson(entry.getKey().getValue()), dataWriter, dynamicOps, info.entryCodec(), entry.getValue());
        }
    }

    private static <E> void writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
        try {
            Optional optional = encoder.encodeStart(json, value).resultOrPartial(string -> LOGGER.error("Couldn't serialize element {}: {}", (Object)path, string));
            if (optional.isPresent()) {
                DataProvider.writeToPath(cache, (JsonElement)optional.get(), path);
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save element {}", (Object)path, (Object)iOException);
        }
    }

    @Override
    public String getName() {
        return "Worldgen";
    }
}

