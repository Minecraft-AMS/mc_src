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
package net.minecraft.data.server;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path path;

    public BiomeParametersProvider(DataGenerator dataGenerator) {
        this.path = dataGenerator.resolveRootDirectoryPath(DataGenerator.OutputType.REPORTS).resolve("biome_parameters");
    }

    @Override
    public void run(DataWriter writer) {
        DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.BUILTIN.get();
        RegistryOps dynamicOps = RegistryOps.of(JsonOps.INSTANCE, immutable);
        Registry<Biome> registry = immutable.get(Registry.BIOME_KEY);
        MultiNoiseBiomeSource.Preset.streamPresets().forEach(pair -> {
            MultiNoiseBiomeSource multiNoiseBiomeSource = ((MultiNoiseBiomeSource.Preset)pair.getSecond()).getBiomeSource(registry, false);
            BiomeParametersProvider.method_42030(this.resolvePath((Identifier)pair.getFirst()), writer, dynamicOps, MultiNoiseBiomeSource.CODEC, multiNoiseBiomeSource);
        });
    }

    private static <E> void method_42030(Path path, DataWriter dataWriter, DynamicOps<JsonElement> dynamicOps, Encoder<E> encoder, E object) {
        try {
            Optional optional = encoder.encodeStart(dynamicOps, object).resultOrPartial(string -> LOGGER.error("Couldn't serialize element {}: {}", (Object)path, string));
            if (optional.isPresent()) {
                DataProvider.writeToPath(dataWriter, (JsonElement)optional.get(), path);
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save element {}", (Object)path, (Object)iOException);
        }
    }

    private Path resolvePath(Identifier id) {
        return this.path.resolve(id.getNamespace()).resolve(id.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Biome Parameters";
    }
}

