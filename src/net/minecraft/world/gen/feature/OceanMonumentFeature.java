/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Set;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;

public class OceanMonumentFeature
extends StructureFeature<DefaultFeatureConfig> {
    public OceanMonumentFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(OceanMonumentFeature::canGenerate, OceanMonumentFeature::addPieces));
    }

    private static boolean canGenerate(StructureGeneratorFactory.Context<DefaultFeatureConfig> context) {
        int i = context.chunkPos().getOffsetX(9);
        int j = context.chunkPos().getOffsetZ(9);
        Set<RegistryEntry<Biome>> set = context.biomeSource().getBiomesInArea(i, context.chunkGenerator().getSeaLevel(), j, 29, context.chunkGenerator().getMultiNoiseSampler());
        for (RegistryEntry<Biome> registryEntry : set) {
            if (Biome.getCategory(registryEntry) == Biome.Category.OCEAN || Biome.getCategory(registryEntry) == Biome.Category.RIVER) continue;
            return false;
        }
        return context.isBiomeValid(Heightmap.Type.OCEAN_FLOOR_WG);
    }

    private static StructurePiece createBasePiece(ChunkPos pos, ChunkRandom random) {
        int i = pos.getStartX() - 29;
        int j = pos.getStartZ() - 29;
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        return new OceanMonumentGenerator.Base(random, i, j, direction);
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
        collector.addPiece(OceanMonumentFeature.createBasePiece(context.chunkPos(), context.random()));
    }

    public static StructurePiecesList modifyPiecesOnRead(ChunkPos pos, long worldSeed, StructurePiecesList pieces) {
        if (pieces.isEmpty()) {
            return pieces;
        }
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
        chunkRandom.setCarverSeed(worldSeed, pos.x, pos.z);
        StructurePiece structurePiece = pieces.pieces().get(0);
        BlockBox blockBox = structurePiece.getBoundingBox();
        int i = blockBox.getMinX();
        int j = blockBox.getMinZ();
        Direction direction = Direction.Type.HORIZONTAL.random(chunkRandom);
        Direction direction2 = Objects.requireNonNullElse(structurePiece.getFacing(), direction);
        OceanMonumentGenerator.Base structurePiece2 = new OceanMonumentGenerator.Base(chunkRandom, i, j, direction2);
        StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
        structurePiecesCollector.addPiece(structurePiece2);
        return structurePiecesCollector.toList();
    }
}

