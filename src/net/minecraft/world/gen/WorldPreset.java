/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;

public class WorldPreset {
    public static final Codec<WorldPreset> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.DIMENSION), DimensionOptions.CODEC).fieldOf("dimensions").forGetter(preset -> preset.dimensions)).apply((Applicative)instance, WorldPreset::new)).flatXmap(WorldPreset::validate, WorldPreset::validate);
    public static final Codec<RegistryEntry<WorldPreset>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.WORLD_PRESET, CODEC);
    private final Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions;

    public WorldPreset(Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions) {
        this.dimensions = dimensions;
    }

    private Registry<DimensionOptions> createDimensionOptionsRegistry() {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(RegistryKeys.DIMENSION, Lifecycle.experimental());
        DimensionOptionsRegistryHolder.streamAll(this.dimensions.keySet().stream()).forEach(registryKey -> {
            DimensionOptions dimensionOptions = this.dimensions.get(registryKey);
            if (dimensionOptions != null) {
                mutableRegistry.add((RegistryKey<DimensionOptions>)registryKey, dimensionOptions, Lifecycle.stable());
            }
        });
        return mutableRegistry.freeze();
    }

    public DimensionOptionsRegistryHolder createDimensionsRegistryHolder() {
        return new DimensionOptionsRegistryHolder(this.createDimensionOptionsRegistry());
    }

    public Optional<DimensionOptions> getOverworld() {
        return Optional.ofNullable(this.dimensions.get(DimensionOptions.OVERWORLD));
    }

    private static DataResult<WorldPreset> validate(WorldPreset preset) {
        if (preset.getOverworld().isEmpty()) {
            return DataResult.error((String)"Missing overworld dimension");
        }
        return DataResult.success((Object)preset, (Lifecycle)Lifecycle.stable());
    }
}

