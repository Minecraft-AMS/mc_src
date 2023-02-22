/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndSpikeFeature
extends Feature<EndSpikeFeatureConfig> {
    private static final LoadingCache<Long, List<Spike>> CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build((CacheLoader)new SpikeCache());

    public EndSpikeFeature(Function<Dynamic<?>, ? extends EndSpikeFeatureConfig> configFactory) {
        super(configFactory);
    }

    public static List<Spike> getSpikes(IWorld iWorld) {
        Random random = new Random(iWorld.getSeed());
        long l = random.nextLong() & 0xFFFFL;
        return (List)CACHE.getUnchecked((Object)l);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, EndSpikeFeatureConfig endSpikeFeatureConfig) {
        List<Spike> list = endSpikeFeatureConfig.getSpikes();
        if (list.isEmpty()) {
            list = EndSpikeFeature.getSpikes(iWorld);
        }
        for (Spike spike : list) {
            if (!spike.isInChunk(blockPos)) continue;
            this.generateSpike(iWorld, random, endSpikeFeatureConfig, spike);
        }
        return true;
    }

    private void generateSpike(IWorld world, Random random, EndSpikeFeatureConfig config, Spike spike) {
        int i = spike.getRadius();
        for (BlockPos blockPos : BlockPos.iterate(new BlockPos(spike.getCenterX() - i, 0, spike.getCenterZ() - i), new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i))) {
            if (blockPos.getSquaredDistance(spike.getCenterX(), blockPos.getY(), spike.getCenterZ(), false) <= (double)(i * i + 1) && blockPos.getY() < spike.getHeight()) {
                this.setBlockState(world, blockPos, Blocks.OBSIDIAN.getDefaultState());
                continue;
            }
            if (blockPos.getY() <= 65) continue;
            this.setBlockState(world, blockPos, Blocks.AIR.getDefaultState());
        }
        if (spike.isGuarded()) {
            int j = -2;
            int k = 2;
            int l = 3;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int m = -2; m <= 2; ++m) {
                for (int n = -2; n <= 2; ++n) {
                    for (int o = 0; o <= 3; ++o) {
                        boolean bl3;
                        boolean bl = MathHelper.abs(m) == 2;
                        boolean bl2 = MathHelper.abs(n) == 2;
                        boolean bl4 = bl3 = o == 3;
                        if (!bl && !bl2 && !bl3) continue;
                        boolean bl42 = m == -2 || m == 2 || bl3;
                        boolean bl5 = n == -2 || n == 2 || bl3;
                        BlockState blockState = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, bl42 && n != -2)).with(PaneBlock.SOUTH, bl42 && n != 2)).with(PaneBlock.WEST, bl5 && m != -2)).with(PaneBlock.EAST, bl5 && m != 2);
                        this.setBlockState(world, mutable.set(spike.getCenterX() + m, spike.getHeight() + o, spike.getCenterZ() + n), blockState);
                    }
                }
            }
        }
        EnderCrystalEntity enderCrystalEntity = EntityType.END_CRYSTAL.create(world.getWorld());
        enderCrystalEntity.setBeamTarget(config.getPos());
        enderCrystalEntity.setInvulnerable(config.isCrystalInvulerable());
        enderCrystalEntity.refreshPositionAndAngles((float)spike.getCenterX() + 0.5f, spike.getHeight() + 1, (float)spike.getCenterZ() + 0.5f, random.nextFloat() * 360.0f, 0.0f);
        world.spawnEntity(enderCrystalEntity);
        this.setBlockState(world, new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.getDefaultState());
    }

    static class SpikeCache
    extends CacheLoader<Long, List<Spike>> {
        private SpikeCache() {
        }

        public List<Spike> load(Long long_) {
            List list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list, new Random(long_));
            ArrayList list2 = Lists.newArrayList();
            for (int i = 0; i < 10; ++i) {
                int j = MathHelper.floor(42.0 * Math.cos(2.0 * (-Math.PI + 0.3141592653589793 * (double)i)));
                int k = MathHelper.floor(42.0 * Math.sin(2.0 * (-Math.PI + 0.3141592653589793 * (double)i)));
                int l = (Integer)list.get(i);
                int m = 2 + l / 3;
                int n = 76 + l * 3;
                boolean bl = l == 1 || l == 2;
                list2.add(new Spike(j, k, m, n, bl));
            }
            return list2;
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((Long)object);
        }
    }

    public static class Spike {
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final Box boundingBox;

        public Spike(int centerX, int centerZ, int radius, int height, boolean bl) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.radius = radius;
            this.height = height;
            this.guarded = bl;
            this.boundingBox = new Box(centerX - radius, 0.0, centerZ - radius, centerX + radius, 256.0, centerZ + radius);
        }

        public boolean isInChunk(BlockPos pos) {
            return pos.getX() >> 4 == this.centerX >> 4 && pos.getZ() >> 4 == this.centerZ >> 4;
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public Box getBoundingBox() {
            return this.boundingBox;
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.put(dynamicOps.createString("centerX"), dynamicOps.createInt(this.centerX));
            builder.put(dynamicOps.createString("centerZ"), dynamicOps.createInt(this.centerZ));
            builder.put(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius));
            builder.put(dynamicOps.createString("height"), dynamicOps.createInt(this.height));
            builder.put(dynamicOps.createString("guarded"), dynamicOps.createBoolean(this.guarded));
            return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
        }

        public static <T> Spike deserialize(Dynamic<T> dynamic) {
            return new Spike(dynamic.get("centerX").asInt(0), dynamic.get("centerZ").asInt(0), dynamic.get("radius").asInt(0), dynamic.get("height").asInt(0), dynamic.get("guarded").asBoolean(false));
        }
    }
}

