/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GeodeCrackConfig;
import net.minecraft.world.gen.feature.GeodeFeatureConfig;
import net.minecraft.world.gen.feature.GeodeLayerConfig;
import net.minecraft.world.gen.feature.GeodeLayerThicknessConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GeodeFeature
extends Feature<GeodeFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public GeodeFeature(Codec<GeodeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<GeodeFeatureConfig> context) {
        BlockState blockState;
        int o;
        int n;
        GeodeFeatureConfig geodeFeatureConfig = context.getConfig();
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        int i = geodeFeatureConfig.minGenOffset;
        int j = geodeFeatureConfig.maxGenOffset;
        LinkedList list = Lists.newLinkedList();
        int k = geodeFeatureConfig.distributionPoints.get(random);
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(structureWorldAccess.getSeed()));
        DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(chunkRandom, -4, 1.0);
        LinkedList list2 = Lists.newLinkedList();
        double d = (double)k / (double)geodeFeatureConfig.outerWallDistance.getMax();
        GeodeLayerThicknessConfig geodeLayerThicknessConfig = geodeFeatureConfig.layerThicknessConfig;
        GeodeLayerConfig geodeLayerConfig = geodeFeatureConfig.layerConfig;
        GeodeCrackConfig geodeCrackConfig = geodeFeatureConfig.crackConfig;
        double e = 1.0 / Math.sqrt(geodeLayerThicknessConfig.filling);
        double f = 1.0 / Math.sqrt(geodeLayerThicknessConfig.innerLayer + d);
        double g = 1.0 / Math.sqrt(geodeLayerThicknessConfig.middleLayer + d);
        double h = 1.0 / Math.sqrt(geodeLayerThicknessConfig.outerLayer + d);
        double l = 1.0 / Math.sqrt(geodeCrackConfig.baseCrackSize + random.nextDouble() / 2.0 + (k > 3 ? d : 0.0));
        boolean bl = (double)random.nextFloat() < geodeCrackConfig.generateCrackChance;
        int m = 0;
        for (n = 0; n < k; ++n) {
            int q;
            int p;
            o = geodeFeatureConfig.outerWallDistance.get(random);
            BlockPos blockPos2 = blockPos.add(o, p = geodeFeatureConfig.outerWallDistance.get(random), q = geodeFeatureConfig.outerWallDistance.get(random));
            blockState = structureWorldAccess.getBlockState(blockPos2);
            if ((blockState.isAir() || blockState.isIn(BlockTags.GEODE_INVALID_BLOCKS)) && ++m > geodeFeatureConfig.invalidBlocksThreshold) {
                return false;
            }
            list.add(Pair.of((Object)blockPos2, (Object)geodeFeatureConfig.pointOffset.get(random)));
        }
        if (bl) {
            n = random.nextInt(4);
            o = k * 2 + 1;
            if (n == 0) {
                list2.add(blockPos.add(o, 7, 0));
                list2.add(blockPos.add(o, 5, 0));
                list2.add(blockPos.add(o, 1, 0));
            } else if (n == 1) {
                list2.add(blockPos.add(0, 7, o));
                list2.add(blockPos.add(0, 5, o));
                list2.add(blockPos.add(0, 1, o));
            } else if (n == 2) {
                list2.add(blockPos.add(o, 7, o));
                list2.add(blockPos.add(o, 5, o));
                list2.add(blockPos.add(o, 1, o));
            } else {
                list2.add(blockPos.add(0, 7, 0));
                list2.add(blockPos.add(0, 5, 0));
                list2.add(blockPos.add(0, 1, 0));
            }
        }
        ArrayList list3 = Lists.newArrayList();
        Predicate<BlockState> predicate = GeodeFeature.notInBlockTagPredicate(geodeFeatureConfig.layerConfig.cannotReplace);
        for (BlockPos blockPos3 : BlockPos.iterate(blockPos.add(i, i, i), blockPos.add(j, j, j))) {
            double r = doublePerlinNoiseSampler.sample(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()) * geodeFeatureConfig.noiseMultiplier;
            double s = 0.0;
            double t = 0.0;
            for (Pair pair : list) {
                s += MathHelper.fastInverseSqrt(blockPos3.getSquaredDistance((Vec3i)pair.getFirst()) + (double)((Integer)pair.getSecond()).intValue()) + r;
            }
            for (BlockPos blockPos4 : list2) {
                t += MathHelper.fastInverseSqrt(blockPos3.getSquaredDistance(blockPos4) + (double)geodeCrackConfig.crackPointOffset) + r;
            }
            if (s < h) continue;
            if (bl && t >= l && s < e) {
                this.setBlockStateIf(structureWorldAccess, blockPos3, Blocks.AIR.getDefaultState(), predicate);
                for (Direction direction : DIRECTIONS) {
                    BlockPos blockPos5 = blockPos3.offset(direction);
                    FluidState fluidState = structureWorldAccess.getFluidState(blockPos5);
                    if (fluidState.isEmpty()) continue;
                    structureWorldAccess.createAndScheduleFluidTick(blockPos5, fluidState.getFluid(), 0);
                }
                continue;
            }
            if (s >= e) {
                this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.fillingProvider.getBlockState(random, blockPos3), predicate);
                continue;
            }
            if (s >= f) {
                boolean bl2;
                boolean bl3 = bl2 = (double)random.nextFloat() < geodeFeatureConfig.useAlternateLayer0Chance;
                if (bl2) {
                    this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.alternateInnerLayerProvider.getBlockState(random, blockPos3), predicate);
                } else {
                    this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.innerLayerProvider.getBlockState(random, blockPos3), predicate);
                }
                if (geodeFeatureConfig.placementsRequireLayer0Alternate && !bl2 || !((double)random.nextFloat() < geodeFeatureConfig.usePotentialPlacementsChance)) continue;
                list3.add(blockPos3.toImmutable());
                continue;
            }
            if (s >= g) {
                this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.middleLayerProvider.getBlockState(random, blockPos3), predicate);
                continue;
            }
            if (!(s >= h)) continue;
            this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.outerLayerProvider.getBlockState(random, blockPos3), predicate);
        }
        List<BlockState> list4 = geodeLayerConfig.innerBlocks;
        block5: for (BlockPos blockPos2 : list3) {
            blockState = Util.getRandom(list4, random);
            for (Direction direction2 : DIRECTIONS) {
                if (blockState.contains(Properties.FACING)) {
                    blockState = (BlockState)blockState.with(Properties.FACING, direction2);
                }
                BlockPos blockPos6 = blockPos2.offset(direction2);
                BlockState blockState2 = structureWorldAccess.getBlockState(blockPos6);
                if (blockState.contains(Properties.WATERLOGGED)) {
                    blockState = (BlockState)blockState.with(Properties.WATERLOGGED, blockState2.getFluidState().isStill());
                }
                if (!BuddingAmethystBlock.canGrowIn(blockState2)) continue;
                this.setBlockStateIf(structureWorldAccess, blockPos6, blockState, predicate);
                continue block5;
            }
        }
        return true;
    }
}

