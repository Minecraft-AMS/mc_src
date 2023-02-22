/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class StrongholdFeature
extends StructureFeature<DefaultFeatureConfig> {
    private boolean stateStillValid;
    private ChunkPos[] startPositions;
    private final List<StructureStart> starts = Lists.newArrayList();
    private long lastSeed;

    public StrongholdFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ) {
        if (this.lastSeed != chunkGenerator.getSeed()) {
            this.invalidateState();
        }
        if (!this.stateStillValid) {
            this.initialize(chunkGenerator);
            this.stateStillValid = true;
        }
        for (ChunkPos chunkPos : this.startPositions) {
            if (chunkX != chunkPos.x || chunkZ != chunkPos.z) continue;
            return true;
        }
        return false;
    }

    private void invalidateState() {
        this.stateStillValid = false;
        this.startPositions = null;
        this.starts.clear();
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Stronghold";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    @Override
    @Nullable
    public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean skipExistingChunks) {
        if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
            return null;
        }
        if (this.lastSeed != world.getSeed()) {
            this.invalidateState();
        }
        if (!this.stateStillValid) {
            this.initialize(chunkGenerator);
            this.stateStillValid = true;
        }
        BlockPos blockPos2 = null;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double d = Double.MAX_VALUE;
        for (ChunkPos chunkPos : this.startPositions) {
            mutable.set((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
            double e = mutable.getSquaredDistance(blockPos);
            if (blockPos2 == null) {
                blockPos2 = new BlockPos(mutable);
                d = e;
                continue;
            }
            if (!(e < d)) continue;
            blockPos2 = new BlockPos(mutable);
            d = e;
        }
        return blockPos2;
    }

    private void initialize(ChunkGenerator<?> chunkGenerator) {
        this.lastSeed = chunkGenerator.getSeed();
        ArrayList list = Lists.newArrayList();
        for (Biome biome : Registry.BIOME) {
            if (biome == null || !chunkGenerator.hasStructure(biome, Feature.STRONGHOLD)) continue;
            list.add(biome);
        }
        int i = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getStrongholdDistance();
        int j = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getStrongholdCount();
        int k = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getStrongholdSpread();
        this.startPositions = new ChunkPos[j];
        int l = 0;
        for (StructureStart structureStart : this.starts) {
            if (l >= this.startPositions.length) continue;
            this.startPositions[l++] = new ChunkPos(structureStart.getChunkX(), structureStart.getChunkZ());
        }
        Random random = new Random();
        random.setSeed(chunkGenerator.getSeed());
        double d = random.nextDouble() * Math.PI * 2.0;
        int m = l;
        if (m < this.startPositions.length) {
            int n = 0;
            int o = 0;
            for (int p = 0; p < this.startPositions.length; ++p) {
                double e = (double)(4 * i + i * o * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
                int q = (int)Math.round(Math.cos(d) * e);
                int r = (int)Math.round(Math.sin(d) * e);
                BlockPos blockPos = chunkGenerator.getBiomeSource().locateBiome((q << 4) + 8, (r << 4) + 8, 112, list, random);
                if (blockPos != null) {
                    q = blockPos.getX() >> 4;
                    r = blockPos.getZ() >> 4;
                }
                if (p >= m) {
                    this.startPositions[p] = new ChunkPos(q, r);
                }
                d += Math.PI * 2 / (double)k;
                if (++n != k) continue;
                n = 0;
                k += 2 * k / (++o + 1);
                k = Math.min(k, this.startPositions.length - p);
                d += random.nextDouble() * Math.PI * 2.0;
            }
        }
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, Biome biome, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, biome, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            StrongholdGenerator.Start start;
            int i = 0;
            long l = chunkGenerator.getSeed();
            do {
                this.children.clear();
                this.boundingBox = BlockBox.empty();
                this.random.setStructureSeed(l + (long)i++, x, z);
                StrongholdGenerator.method_14855();
                start = new StrongholdGenerator.Start(this.random, (x << 4) + 2, (z << 4) + 2);
                this.children.add(start);
                start.method_14918(start, this.children, this.random);
                List<StructurePiece> list = start.field_15282;
                while (!list.isEmpty()) {
                    int j = this.random.nextInt(list.size());
                    StructurePiece structurePiece = list.remove(j);
                    structurePiece.method_14918(start, this.children, this.random);
                }
                this.setBoundingBoxFromChildren();
                this.method_14978(chunkGenerator.getSeaLevel(), this.random, 10);
            } while (this.children.isEmpty() || start.field_15283 == null);
            ((StrongholdFeature)this.getFeature()).starts.add(this);
        }
    }
}
