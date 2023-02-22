/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> {
    public static final Logger log = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public ConfiguredFeature(F feature, FC config) {
        this.feature = feature;
        this.config = config;
    }

    public ConfiguredFeature(F feature, Dynamic<?> dynamic) {
        this(feature, ((Feature)feature).deserializeConfig(dynamic));
    }

    public ConfiguredFeature<?, ?> createDecoratedFeature(ConfiguredDecorator<?> configuredDecorator) {
        Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
        return feature.configure(new DecoratedFeatureConfig(this, configuredDecorator));
    }

    public RandomFeatureEntry<FC> withChance(float chance) {
        return new RandomFeatureEntry(this, chance);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("name"), (Object)ops.createString(Registry.FEATURE.getId((Feature<?>)this.feature).toString()), (Object)ops.createString("config"), (Object)this.config.serialize(ops).getValue())));
    }

    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos blockPos) {
        return ((Feature)this.feature).generate(world, generator, random, blockPos, this.config);
    }

    public static <T> ConfiguredFeature<?, ?> deserialize(Dynamic<T> dynamic) {
        String string = dynamic.get("name").asString("");
        Feature<?> feature = Registry.FEATURE.get(new Identifier(string));
        try {
            return new ConfiguredFeature(feature, (Dynamic<?>)dynamic.get("config").orElseEmptyMap());
        }
        catch (RuntimeException runtimeException) {
            log.warn("Error while deserializing {}", (Object)string);
            return new ConfiguredFeature<DefaultFeatureConfig, Feature<DefaultFeatureConfig>>(Feature.NO_OP, DefaultFeatureConfig.DEFAULT);
        }
    }
}

