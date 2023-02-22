/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.NetherSpringFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NetherBiome
extends Biome {
    protected NetherBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.NETHER, SurfaceBuilder.NETHER_CONFIG).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addStructureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT);
        this.addCarver(GenerationStep.Carver.AIR, NetherBiome.configureCarver(Carver.HELL_CAVE, new ProbabilityConfig(0.2f)));
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, NetherBiome.configureFeature(Feature.SPRING_FEATURE, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.COUNT_VERY_BIASED_RANGE, new RangeDecoratorConfig(20, 8, 16, 256)));
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.NETHER_SPRING, new NetherSpringFeatureConfig(false), Decorator.COUNT_RANGE, new RangeDecoratorConfig(8, 4, 8, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.HELL_FIRE, FeatureConfig.DEFAULT, Decorator.HELL_FIRE, new CountDecoratorConfig(10)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.GLOWSTONE_BLOB, FeatureConfig.DEFAULT, Decorator.LIGHT_GEM_CHANCE, new CountDecoratorConfig(10)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.GLOWSTONE_BLOB, FeatureConfig.DEFAULT, Decorator.COUNT_RANGE, new RangeDecoratorConfig(10, 0, 0, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.BUSH, new SingleStateFeatureConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Decorator.CHANCE_RANGE, new ChanceRangeDecoratorConfig(0.5f, 0, 0, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.BUSH, new SingleStateFeatureConfig(Blocks.RED_MUSHROOM.getDefaultState()), Decorator.CHANCE_RANGE, new ChanceRangeDecoratorConfig(0.5f, 0, 0, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14), Decorator.COUNT_RANGE, new RangeDecoratorConfig(16, 10, 20, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getDefaultState(), 33), Decorator.MAGMA, new CountDecoratorConfig(4)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherBiome.configureFeature(Feature.NETHER_SPRING, new NetherSpringFeatureConfig(true), Decorator.COUNT_RANGE, new RangeDecoratorConfig(16, 10, 20, 128)));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
    }
}
