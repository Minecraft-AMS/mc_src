/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;

public class BiomeSourceType<C extends BiomeSourceConfig, T extends BiomeSource> {
    public static final BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> CHECKERBOARD = BiomeSourceType.register("checkerboard", CheckerboardBiomeSource::new, CheckerboardBiomeSourceConfig::new);
    public static final BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> FIXED = BiomeSourceType.register("fixed", FixedBiomeSource::new, FixedBiomeSourceConfig::new);
    public static final BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> VANILLA_LAYERED = BiomeSourceType.register("vanilla_layered", VanillaLayeredBiomeSource::new, VanillaLayeredBiomeSourceConfig::new);
    public static final BiomeSourceType<TheEndBiomeSourceConfig, TheEndBiomeSource> THE_END = BiomeSourceType.register("the_end", TheEndBiomeSource::new, TheEndBiomeSourceConfig::new);
    private final Function<C, T> biomeSource;
    private final Supplier<C> config;

    private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> register(String id, Function<C, T> biomeSource, Supplier<C> config) {
        return Registry.register(Registry.BIOME_SOURCE_TYPE, id, new BiomeSourceType<C, T>(biomeSource, config));
    }

    public BiomeSourceType(Function<C, T> biomeSource, Supplier<C> config) {
        this.biomeSource = biomeSource;
        this.config = config;
    }

    public T applyConfig(C config) {
        return (T)((BiomeSource)this.biomeSource.apply(config));
    }

    public C getConfig() {
        return (C)((BiomeSourceConfig)this.config.get());
    }
}

