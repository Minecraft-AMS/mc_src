/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class NetherFortressFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList((Object[])new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)});

    public NetherFortressFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ) {
        int i = chunkX >> 4;
        int j = chunkZ >> 4;
        random.setSeed((long)(i ^ j << 4) ^ chunkGenerator.getSeed());
        random.nextInt();
        if (random.nextInt(3) != 0) {
            return false;
        }
        if (chunkX != (i << 4) + 4 + random.nextInt(8)) {
            return false;
        }
        if (chunkZ != (j << 4) + 4 + random.nextInt(8)) {
            return false;
        }
        Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
        return chunkGenerator.hasStructure(biome, Feature.NETHER_BRIDGE);
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Fortress";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, Biome biome, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, biome, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(this.random, (x << 4) + 2, (z << 4) + 2);
            this.children.add(start);
            start.method_14918(start, this.children, this.random);
            List<StructurePiece> list = start.field_14505;
            while (!list.isEmpty()) {
                int i = this.random.nextInt(list.size());
                StructurePiece structurePiece = list.remove(i);
                structurePiece.method_14918(start, this.children, this.random);
            }
            this.setBoundingBoxFromChildren();
            this.method_14976(this.random, 48, 70);
        }
    }
}

