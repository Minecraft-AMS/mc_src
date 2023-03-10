/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class DebugChunkGenerator
extends ChunkGenerator {
    public static final Codec<DebugChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> DebugChunkGenerator.method_41042(instance).and((App)RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(debugChunkGenerator -> debugChunkGenerator.biomeRegistry)).apply((Applicative)instance, instance.stable(DebugChunkGenerator::new)));
    private static final int field_31467 = 2;
    private static final List<BlockState> BLOCK_STATES = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateManager().getStates().stream()).collect(Collectors.toList());
    private static final int X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt(BLOCK_STATES.size()));
    private static final int Z_SIDE_LENGTH = MathHelper.ceil((float)BLOCK_STATES.size() / (float)X_SIDE_LENGTH);
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();
    public static final int field_31465 = 70;
    public static final int field_31466 = 60;
    private final Registry<Biome> biomeRegistry;

    public DebugChunkGenerator(Registry<StructureSet> registry, Registry<Biome> registry2) {
        super(registry, Optional.empty(), new FixedBiomeSource(registry2.getOrCreateEntry(BiomeKeys.PLAINS)));
        this.biomeRegistry = registry2;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return this;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int m = ChunkSectionPos.getOffsetPos(i, k);
                int n = ChunkSectionPos.getOffsetPos(j, l);
                world.setBlockState(mutable.set(m, 60, n), BARRIER, 2);
                BlockState blockState = DebugChunkGenerator.getBlockState(m, n);
                world.setBlockState(mutable.set(m, 70, n), blockState, 2);
            }
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, BlockPos pos) {
    }

    public static BlockState getBlockState(int x, int z) {
        int i;
        BlockState blockState = AIR;
        if (x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0 && (x /= 2) <= X_SIDE_LENGTH && (z /= 2) <= Z_SIDE_LENGTH && (i = MathHelper.abs(x * X_SIDE_LENGTH + z)) < BLOCK_STATES.size()) {
            blockState = BLOCK_STATES.get(i);
        }
        return blockState;
    }

    @Override
    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return MultiNoiseUtil.method_40443();
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }
}

