/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart<C extends FeatureConfig> {
    public static final StructureStart<?> DEFAULT = new StructureStart<MineshaftFeatureConfig>(StructureFeature.MINESHAFT, 0, 0, BlockBox.empty(), 0, 0L){

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, MineshaftFeatureConfig mineshaftFeatureConfig) {
        }
    };
    private final StructureFeature<C> feature;
    protected final List<StructurePiece> children = Lists.newArrayList();
    protected BlockBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    private int references;
    protected final ChunkRandom random;

    public StructureStart(StructureFeature<C> feature, int chunkX, int chunkZ, BlockBox box, int references, long seed) {
        this.feature = feature;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.references = references;
        this.random = new ChunkRandom();
        this.random.setCarverSeed(seed, chunkX, chunkZ);
        this.boundingBox = box;
    }

    public abstract void init(DynamicRegistryManager var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, C var7);

    public BlockBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<StructurePiece> getChildren() {
        return this.children;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generateStructure(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos) {
        List<StructurePiece> list = this.children;
        synchronized (list) {
            if (this.children.isEmpty()) {
                return;
            }
            BlockBox blockBox = this.children.get((int)0).boundingBox;
            Vec3i vec3i = blockBox.getCenter();
            BlockPos blockPos = new BlockPos(vec3i.getX(), blockBox.minY, vec3i.getZ());
            Iterator<StructurePiece> iterator = this.children.iterator();
            while (iterator.hasNext()) {
                StructurePiece structurePiece = iterator.next();
                if (!structurePiece.getBoundingBox().intersects(box) || structurePiece.generate(world, structureAccessor, chunkGenerator, random, box, chunkPos, blockPos)) continue;
                iterator.remove();
            }
            this.setBoundingBoxFromChildren();
        }
    }

    protected void setBoundingBoxFromChildren() {
        this.boundingBox = BlockBox.empty();
        for (StructurePiece structurePiece : this.children) {
            this.boundingBox.encompass(structurePiece.getBoundingBox());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public NbtCompound toTag(int chunkX, int chunkZ) {
        NbtCompound nbtCompound = new NbtCompound();
        if (!this.hasChildren()) {
            nbtCompound.putString("id", "INVALID");
            return nbtCompound;
        }
        nbtCompound.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
        nbtCompound.putInt("ChunkX", chunkX);
        nbtCompound.putInt("ChunkZ", chunkZ);
        nbtCompound.putInt("references", this.references);
        nbtCompound.put("BB", this.boundingBox.toNbt());
        NbtList nbtList = new NbtList();
        List<StructurePiece> list = this.children;
        synchronized (list) {
            for (StructurePiece structurePiece : this.children) {
                nbtList.add(structurePiece.getTag());
            }
        }
        nbtCompound.put("Children", nbtList);
        return nbtCompound;
    }

    protected void randomUpwardTranslation(int seaLevel, Random random, int minSeaLevelDistance) {
        int i = seaLevel - minSeaLevelDistance;
        int j = this.boundingBox.getBlockCountY() + 1;
        if (j < i) {
            j += random.nextInt(i - j);
        }
        int k = j - this.boundingBox.maxY;
        this.boundingBox.move(0, k, 0);
        for (StructurePiece structurePiece : this.children) {
            structurePiece.translate(0, k, 0);
        }
    }

    protected void randomUpwardTranslation(Random random, int minY, int maxY) {
        int i = maxY - minY + 1 - this.boundingBox.getBlockCountY();
        int j = i > 1 ? minY + random.nextInt(i) : minY;
        int k = j - this.boundingBox.minY;
        this.boundingBox.move(0, k, 0);
        for (StructurePiece structurePiece : this.children) {
            structurePiece.translate(0, k, 0);
        }
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
    }

    public boolean isInExistingChunk() {
        return this.references < this.getReferenceCountToBeInExistingChunk();
    }

    public void incrementReferences() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getReferenceCountToBeInExistingChunk() {
        return 1;
    }

    public StructureFeature<?> getFeature() {
        return this.feature;
    }
}

