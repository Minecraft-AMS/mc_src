/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

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
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ConfiguredDecorator<DC extends DecoratorConfig> {
    public final Decorator<DC> decorator;
    public final DC config;

    public ConfiguredDecorator(Decorator<DC> decorator, Dynamic<?> dynamic) {
        this(decorator, decorator.deserialize(dynamic));
    }

    public ConfiguredDecorator(Decorator<DC> decorator, DC decoratorConfig) {
        this.decorator = decorator;
        this.config = decoratorConfig;
    }

    public <FC extends FeatureConfig> boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, ConfiguredFeature<FC> configuredFeature) {
        return this.decorator.generate(world, generator, random, pos, this.config, configuredFeature);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("name"), (Object)dynamicOps.createString(Registry.DECORATOR.getId(this.decorator).toString()), (Object)dynamicOps.createString("config"), (Object)this.config.serialize(dynamicOps).getValue())));
    }

    public static <T> ConfiguredDecorator<?> deserialize(Dynamic<T> dynamic) {
        Decorator<?> decorator = Registry.DECORATOR.get(new Identifier(dynamic.get("name").asString("")));
        return new ConfiguredDecorator<Dynamic>((Decorator<Dynamic>)decorator, dynamic.get("config").orElseEmptyMap());
    }
}

