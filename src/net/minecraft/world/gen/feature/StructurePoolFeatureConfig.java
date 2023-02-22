/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class StructurePoolFeatureConfig
implements FeatureConfig {
    public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool), (App)Codec.intRange((int)0, (int)7).fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)).apply((Applicative)instance, StructurePoolFeatureConfig::new));
    private final RegistryEntry<StructurePool> startPool;
    private final int size;

    public StructurePoolFeatureConfig(RegistryEntry<StructurePool> startPool, int size) {
        this.startPool = startPool;
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public RegistryEntry<StructurePool> getStartPool() {
        return this.startPool;
    }
}

