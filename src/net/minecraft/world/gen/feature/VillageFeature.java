/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class VillageFeature
extends StructureFeature<VillageFeatureConfig> {
    public VillageFeature(Function<Dynamic<?>, ? extends VillageFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
        int m = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getVillageDistance();
        int n = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getVillageSeparation();
        int o = i + m * k;
        int p = j + m * l;
        int q = o < 0 ? o - m + 1 : o;
        int r = p < 0 ? p - m + 1 : p;
        int s = q / m;
        int t = r / m;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387312);
        s *= m;
        t *= m;
        return new ChunkPos(s += random.nextInt(m - n), t += random.nextInt(m - n));
    }

    @Override
    public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ) {
        ChunkPos chunkPos = this.getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkPos.x && chunkZ == chunkPos.z) {
            Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return chunkGenerator.hasStructure(biome, Feature.VILLAGE);
        }
        return false;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Village";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start
    extends VillageStructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, Biome biome, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, biome, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            VillageFeatureConfig villageFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.VILLAGE);
            BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
            VillageGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, villageFeatureConfig);
            this.setBoundingBoxFromChildren();
        }
    }
}
