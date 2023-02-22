/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeSource {
    private static final List<Biome> SPAWN_BIOMES = Lists.newArrayList((Object[])new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
    protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.newHashMap();
    protected final Set<BlockState> topMaterials = Sets.newHashSet();

    protected BiomeSource() {
    }

    public List<Biome> getSpawnBiomes() {
        return SPAWN_BIOMES;
    }

    public Biome getBiome(BlockPos pos) {
        return this.getBiome(pos.getX(), pos.getZ());
    }

    public abstract Biome getBiome(int var1, int var2);

    public Biome getBiomeForNoiseGen(int x, int z) {
        return this.getBiome(x << 2, z << 2);
    }

    public Biome[] sampleBiomes(int x, int z, int width, int height) {
        return this.sampleBiomes(x, z, width, height, true);
    }

    public abstract Biome[] sampleBiomes(int var1, int var2, int var3, int var4, boolean var5);

    public abstract Set<Biome> getBiomesInArea(int var1, int var2, int var3);

    @Nullable
    public abstract BlockPos locateBiome(int var1, int var2, int var3, List<Biome> var4, Random var5);

    public float method_8757(int i, int j) {
        return 0.0f;
    }

    public abstract boolean hasStructureFeature(StructureFeature<?> var1);

    public abstract Set<BlockState> getTopMaterials();
}

