/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class DeepFrozenOceanBiome
extends Biome {
    protected static final OctaveSimplexNoiseSampler field_9484 = new OctaveSimplexNoiseSampler(new Random(3456L), 3);

    public DeepFrozenOceanBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.FROZEN_OCEAN, SurfaceBuilder.GRASS_CONFIG).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.OCEAN).depth(-1.8f).scale(0.1f).temperature(0.5f).downfall(0.5f).waterColor(3750089).waterFogColor(329011).parent(null));
        this.addStructureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
        this.addStructureFeature(Feature.OCEAN_MONUMENT, FeatureConfig.DEFAULT);
        this.addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
        this.addStructureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false));
        DefaultBiomeFeatures.addOceanCarvers(this);
        DefaultBiomeFeatures.addDefaultStructures(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addIcebergs(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addBlueIce(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
        this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        this.addSpawn(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
    }

    @Override
    protected float computeTemperature(BlockPos blockPos) {
        double h;
        double e;
        float f = this.getTemperature();
        double d = field_9484.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05);
        double g = d + (e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2));
        if (g < 0.3 && (h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09)) < 0.8) {
            f = 0.2f;
        }
        if (blockPos.getY() > 64) {
            float i = (float)(TEMPERATURE_NOISE.sample((float)blockPos.getX() / 8.0f, (float)blockPos.getZ() / 8.0f) * 4.0);
            return f - (i + (float)blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return f;
    }
}

