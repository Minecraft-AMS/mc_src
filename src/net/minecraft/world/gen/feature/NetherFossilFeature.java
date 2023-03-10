/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.RangeFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherFossilFeature
extends StructureFeature<RangeFeatureConfig> {
    public NetherFossilFeature(Codec<RangeFeatureConfig> configCodec) {
        super(configCodec, NetherFossilFeature::addPieces);
    }

    private static Optional<StructurePiecesGenerator<RangeFeatureConfig>> addPieces(StructureGeneratorFactory.Context<RangeFeatureConfig> context) {
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getStartX() + chunkRandom.nextInt(16);
        int j = context.chunkPos().getStartZ() + chunkRandom.nextInt(16);
        int k = context.chunkGenerator().getSeaLevel();
        HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.world());
        int l = context.config().heightProvider.get(chunkRandom, heightContext);
        VerticalBlockSample verticalBlockSample = context.chunkGenerator().getColumnSample(i, j, context.world());
        BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j);
        while (l > k) {
            BlockState blockState = verticalBlockSample.getState(l);
            BlockState blockState2 = verticalBlockSample.getState(--l);
            if (!blockState.isAir() || !blockState2.isOf(Blocks.SOUL_SAND) && !blockState2.isSideSolidFullSquare(EmptyBlockView.INSTANCE, mutable.setY(l), Direction.UP)) continue;
            break;
        }
        if (l <= k) {
            return Optional.empty();
        }
        if (!context.validBiome().test(context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(l), BiomeCoords.fromBlock(j)))) {
            return Optional.empty();
        }
        BlockPos blockPos = new BlockPos(i, l, j);
        return Optional.of((structurePiecesCollector, context2) -> NetherFossilGenerator.addPieces(context.structureManager(), structurePiecesCollector, chunkRandom, blockPos));
    }
}

