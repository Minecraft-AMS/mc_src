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
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import org.slf4j.Logger;

public class DynamicRegistriesProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public DynamicRegistriesProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.registryLookupFuture = registryLookupFuture;
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookupFuture.thenCompose(lookup -> {
            RegistryOps dynamicOps = RegistryOps.of(JsonOps.INSTANCE, lookup);
            return CompletableFuture.allOf((CompletableFuture[])RegistryLoader.DYNAMIC_REGISTRIES.stream().flatMap(entry -> this.writeRegistryEntries(writer, (RegistryWrapper.WrapperLookup)lookup, dynamicOps, (RegistryLoader.Entry)entry).stream()).toArray(CompletableFuture[]::new));
        });
    }

    private <T> Optional<CompletableFuture<?>> writeRegistryEntries(DataWriter writer, RegistryWrapper.WrapperLookup lookup, DynamicOps<JsonElement> ops, RegistryLoader.Entry<T> registry) {
        RegistryKey registryKey = registry.key();
        return lookup.getOptionalWrapper(registryKey).map(wrapper -> {
            DataOutput.PathResolver pathResolver = this.output.getResolver(DataOutput.OutputType.DATA_PACK, registryKey.getValue().getPath());
            return CompletableFuture.allOf((CompletableFuture[])wrapper.streamEntries().map(entry -> DynamicRegistriesProvider.writeToPath(pathResolver.resolveJson(entry.registryKey().getValue()), writer, ops, registry.elementCodec(), entry.value())).toArray(CompletableFuture[]::new));
        });
    }

    private static <E> CompletableFuture<?> writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
        Optional optional = encoder.encodeStart(json, value).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", (Object)path, error));
        if (optional.isPresent()) {
            return DataProvider.writeToPath(cache, (JsonElement)optional.get(), path);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public final String getName() {
        return "Registries";
    }
}

