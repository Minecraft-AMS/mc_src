/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome.source;

import net.minecraft.SharedConstants;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeLayerSampler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CachingLayerSampler sampler;

    public BiomeLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
        this.sampler = layerFactory.make();
    }

    public Biome[] sample(int x, int y, int width, int height) {
        Biome[] biomes = new Biome[width * height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Biome biome;
                int k = this.sampler.sample(x + j, y + i);
                biomes[j + i * width] = biome = this.getBiome(k);
            }
        }
        return biomes;
    }

    private Biome getBiome(int id) {
        Biome biome = (Biome)Registry.BIOME.get(id);
        if (biome == null) {
            if (SharedConstants.isDevelopment) {
                throw new IllegalStateException("Unknown biome id: " + id);
            }
            LOGGER.warn("Unknown biome id: ", (Object)id);
            return Biomes.DEFAULT;
        }
        return biome;
    }

    public Biome sample(int x, int y) {
        return this.getBiome(this.sampler.sample(x, y));
    }
}

