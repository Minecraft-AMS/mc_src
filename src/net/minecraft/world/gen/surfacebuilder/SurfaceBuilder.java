/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.surfacebuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class SurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();
    private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.getDefaultState();
    private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.getDefaultState();
    private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.getDefaultState();
    private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
    private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
    private static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
    private final BlockState defaultState;
    private final int seaLevel;
    private final BlockState[] terracottaBands;
    private final DoublePerlinNoiseSampler terracottaBandsOffsetNoise;
    private final DoublePerlinNoiseSampler badlandsPillarNoise;
    private final DoublePerlinNoiseSampler badlandsPillarRoofNoise;
    private final DoublePerlinNoiseSampler badlandsSurfaceNoise;
    private final DoublePerlinNoiseSampler icebergPillarNoise;
    private final DoublePerlinNoiseSampler icebergPillarRoofNoise;
    private final DoublePerlinNoiseSampler icebergSurfaceNoise;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    private final Map<RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>, DoublePerlinNoiseSampler> noiseSamplers = new ConcurrentHashMap<RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>, DoublePerlinNoiseSampler>();
    private final Map<Identifier, RandomDeriver> randomDerivers = new ConcurrentHashMap<Identifier, RandomDeriver>();
    private final RandomDeriver randomDeriver;
    private final DoublePerlinNoiseSampler surfaceNoise;
    private final DoublePerlinNoiseSampler surfaceSecondaryNoise;

    public SurfaceBuilder(Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BlockState defaultState, int seaLevel, long seed, ChunkRandom.RandomProvider randomProvider) {
        this.noiseRegistry = noiseRegistry;
        this.defaultState = defaultState;
        this.seaLevel = seaLevel;
        this.randomDeriver = randomProvider.create(seed).createRandomDeriver();
        this.terracottaBandsOffsetNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.CLAY_BANDS_OFFSET);
        this.terracottaBands = SurfaceBuilder.createTerracottaBands(this.randomDeriver.createRandom(new Identifier("clay_bands")));
        this.surfaceNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.SURFACE);
        this.surfaceSecondaryNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.SURFACE_SECONDARY);
        this.badlandsPillarNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.BADLANDS_PILLAR);
        this.badlandsPillarRoofNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.BADLANDS_PILLAR_ROOF);
        this.badlandsSurfaceNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.BADLANDS_SURFACE);
        this.icebergPillarNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.ICEBERG_PILLAR);
        this.icebergPillarRoofNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.ICEBERG_PILLAR_ROOF);
        this.icebergSurfaceNoise = NoiseParametersKeys.createNoiseSampler(noiseRegistry, this.randomDeriver, NoiseParametersKeys.ICEBERG_SURFACE);
    }

    protected DoublePerlinNoiseSampler getNoiseSampler(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise) {
        return this.noiseSamplers.computeIfAbsent(noise, registryKey2 -> NoiseParametersKeys.createNoiseSampler(this.noiseRegistry, this.randomDeriver, noise));
    }

    protected RandomDeriver getRandomDeriver(Identifier id) {
        return this.randomDerivers.computeIfAbsent(id, i -> this.randomDeriver.createRandom(id).createRandomDeriver());
    }

    public void buildSurface(BiomeAccess biomeAccess, Registry<Biome> biomeRegistry, boolean useLegacyRandom, HeightContext context, final Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, MaterialRules.MaterialRule surfaceRule) {
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        final ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        BlockColumn blockColumn = new BlockColumn(){

            @Override
            public BlockState getState(int y) {
                return chunk.getBlockState(mutable.setY(y));
            }

            @Override
            public void setState(int y, BlockState state) {
                HeightLimitView heightLimitView = chunk.getHeightLimitView();
                if (y >= heightLimitView.getBottomY() && y < heightLimitView.getTopY()) {
                    chunk.setBlockState(mutable.setY(y), state, false);
                    if (!state.getFluidState().isEmpty()) {
                        chunk.markBlockForPostProcessing(mutable);
                    }
                }
            }

            public String toString() {
                return "ChunkBlockColumn " + chunkPos;
            }
        };
        MaterialRules.MaterialRuleContext materialRuleContext = new MaterialRules.MaterialRuleContext(this, chunk, chunkNoiseSampler, biomeAccess::getBiome, biomeRegistry, context);
        MaterialRules.BlockStateRule blockStateRule = (MaterialRules.BlockStateRule)surfaceRule.apply(materialRuleContext);
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int m = i + k;
                int n = j + l;
                int o = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
                mutable.setX(m).setZ(n);
                RegistryEntry<Biome> registryEntry = biomeAccess.getBiome(mutable2.set(m, useLegacyRandom ? 0 : o, n));
                if (registryEntry.matchesKey(BiomeKeys.ERODED_BADLANDS)) {
                    this.placeBadlandsPillar(blockColumn, m, n, o, chunk);
                }
                int p = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
                materialRuleContext.initHorizontalContext(m, n);
                int q = 0;
                int r = Integer.MIN_VALUE;
                int s = Integer.MAX_VALUE;
                int t = chunk.getBottomY();
                for (int u = p; u >= t; --u) {
                    BlockState blockState2;
                    int v;
                    BlockState blockState = blockColumn.getState(u);
                    if (blockState.isAir()) {
                        q = 0;
                        r = Integer.MIN_VALUE;
                        continue;
                    }
                    if (!blockState.getFluidState().isEmpty()) {
                        if (r != Integer.MIN_VALUE) continue;
                        r = u + 1;
                        continue;
                    }
                    if (s >= u) {
                        s = DimensionType.field_35479;
                        for (v = u - 1; v >= t - 1; --v) {
                            blockState2 = blockColumn.getState(v);
                            if (this.isDefaultBlock(blockState2)) continue;
                            s = v + 1;
                            break;
                        }
                    }
                    v = u - s + 1;
                    materialRuleContext.initVerticalContext(++q, v, r, m, u, n);
                    if (blockState != this.defaultState || (blockState2 = blockStateRule.tryApply(m, u, n)) == null) continue;
                    blockColumn.setState(u, blockState2);
                }
                if (!registryEntry.matchesKey(BiomeKeys.FROZEN_OCEAN) && !registryEntry.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN)) continue;
                this.placeIceberg(materialRuleContext.method_39551(), registryEntry.value(), blockColumn, mutable2, m, n, o);
            }
        }
    }

    protected int method_39552(int i, int j) {
        double d = this.surfaceNoise.sample(i, 0.0, j);
        return (int)(d * 2.75 + 3.0 + this.randomDeriver.createRandom(i, 0, j).nextDouble() * 0.25);
    }

    protected double method_39555(int i, int j) {
        return this.surfaceSecondaryNoise.sample(i, 0.0, j);
    }

    private boolean isDefaultBlock(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty();
    }

    @Deprecated
    public Optional<BlockState> applyMaterialRule(MaterialRules.MaterialRule rule, CarverContext context, Function<BlockPos, RegistryEntry<Biome>> posToBiome, Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, BlockPos pos, boolean hasFluid) {
        MaterialRules.MaterialRuleContext materialRuleContext = new MaterialRules.MaterialRuleContext(this, chunk, chunkNoiseSampler, posToBiome, context.getRegistryManager().get(Registry.BIOME_KEY), context);
        MaterialRules.BlockStateRule blockStateRule = (MaterialRules.BlockStateRule)rule.apply(materialRuleContext);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        materialRuleContext.initHorizontalContext(i, k);
        materialRuleContext.initVerticalContext(1, 1, hasFluid ? j + 1 : Integer.MIN_VALUE, i, j, k);
        BlockState blockState = blockStateRule.tryApply(i, j, k);
        return Optional.ofNullable(blockState);
    }

    private void placeBadlandsPillar(BlockColumn column, int x, int z, int surfaceY, HeightLimitView chunk) {
        BlockState blockState;
        int k;
        double d = 0.2;
        double e = Math.min(Math.abs(this.badlandsSurfaceNoise.sample(x, 0.0, z) * 8.25), this.badlandsPillarNoise.sample((double)x * 0.2, 0.0, (double)z * 0.2) * 15.0);
        if (e <= 0.0) {
            return;
        }
        double f = 0.75;
        double g = 1.5;
        double h = Math.abs(this.badlandsPillarRoofNoise.sample((double)x * 0.75, 0.0, (double)z * 0.75) * 1.5);
        double i = 64.0 + Math.min(e * e * 2.5, Math.ceil(h * 50.0) + 24.0);
        int j = MathHelper.floor(i);
        if (surfaceY > j) {
            return;
        }
        for (k = j; k >= chunk.getBottomY() && !(blockState = column.getState(k)).isOf(this.defaultState.getBlock()); --k) {
            if (!blockState.isOf(Blocks.WATER)) continue;
            return;
        }
        for (k = j; k >= chunk.getBottomY() && column.getState(k).isAir(); --k) {
            column.setState(k, this.defaultState);
        }
    }

    private void placeIceberg(int minY, Biome biome, BlockColumn column, BlockPos.Mutable mutablePos, int x, int z, int surfaceY) {
        double j;
        double d = 1.28;
        double e = Math.min(Math.abs(this.icebergSurfaceNoise.sample(x, 0.0, z) * 8.25), this.icebergPillarNoise.sample((double)x * 1.28, 0.0, (double)z * 1.28) * 15.0);
        if (e <= 1.8) {
            return;
        }
        double f = 1.17;
        double g = 1.5;
        double h = Math.abs(this.icebergPillarRoofNoise.sample((double)x * 1.17, 0.0, (double)z * 1.17) * 1.5);
        double i = Math.min(e * e * 1.2, Math.ceil(h * 40.0) + 14.0);
        if (biome.shouldGenerateLowerFrozenOceanSurface(mutablePos.set(x, 63, z))) {
            i -= 2.0;
        }
        if (i > 2.0) {
            j = (double)this.seaLevel - i - 7.0;
            i += (double)this.seaLevel;
        } else {
            i = 0.0;
            j = 0.0;
        }
        double k = i;
        AbstractRandom abstractRandom = this.randomDeriver.createRandom(x, 0, z);
        int l = 2 + abstractRandom.nextInt(4);
        int m = this.seaLevel + 18 + abstractRandom.nextInt(10);
        int n = 0;
        for (int o = Math.max(surfaceY, (int)k + 1); o >= minY; --o) {
            if (!(column.getState(o).isAir() && o < (int)k && abstractRandom.nextDouble() > 0.01) && (column.getState(o).getMaterial() != Material.WATER || o <= (int)j || o >= this.seaLevel || j == 0.0 || !(abstractRandom.nextDouble() > 0.15))) continue;
            if (n <= l && o > m) {
                column.setState(o, SNOW_BLOCK);
                ++n;
                continue;
            }
            column.setState(o, PACKED_ICE);
        }
    }

    private static BlockState[] createTerracottaBands(AbstractRandom random) {
        int i;
        Object[] blockStates = new BlockState[192];
        Arrays.fill(blockStates, TERRACOTTA);
        for (i = 0; i < blockStates.length; ++i) {
            if ((i += random.nextInt(5) + 1) >= blockStates.length) continue;
            blockStates[i] = ORANGE_TERRACOTTA;
        }
        SurfaceBuilder.addTerracottaBands(random, (BlockState[])blockStates, 1, YELLOW_TERRACOTTA);
        SurfaceBuilder.addTerracottaBands(random, (BlockState[])blockStates, 2, BROWN_TERRACOTTA);
        SurfaceBuilder.addTerracottaBands(random, (BlockState[])blockStates, 1, RED_TERRACOTTA);
        i = random.nextBetween(9, 15);
        int j = 0;
        for (int k = 0; j < i && k < blockStates.length; ++j, k += random.nextInt(16) + 4) {
            blockStates[k] = WHITE_TERRACOTTA;
            if (k - 1 > 0 && random.nextBoolean()) {
                blockStates[k - 1] = LIGHT_GRAY_TERRACOTTA;
            }
            if (k + 1 >= blockStates.length || !random.nextBoolean()) continue;
            blockStates[k + 1] = LIGHT_GRAY_TERRACOTTA;
        }
        return blockStates;
    }

    private static void addTerracottaBands(AbstractRandom random, BlockState[] terracottaBands, int minBandSize, BlockState state) {
        int i = random.nextBetween(6, 15);
        for (int j = 0; j < i; ++j) {
            int k = minBandSize + random.nextInt(3);
            int l = random.nextInt(terracottaBands.length);
            for (int m = 0; l + m < terracottaBands.length && m < k; ++m) {
                terracottaBands[l + m] = state;
            }
        }
    }

    protected BlockState getTerracottaBlock(int x, int y, int z) {
        int i = (int)Math.round(this.terracottaBandsOffsetNoise.sample(x, 0.0, z) * 4.0);
        return this.terracottaBands[(y + i + this.terracottaBands.length) % this.terracottaBands.length];
    }
}

