/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class OceanMonumentStructure
extends Structure {
    public static final Codec<OceanMonumentStructure> CODEC = OceanMonumentStructure.createCodec(OceanMonumentStructure::new);

    public OceanMonumentStructure(Structure.Config config) {
        super(config);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        int i = context.chunkPos().getOffsetX(9);
        int j = context.chunkPos().getOffsetZ(9);
        Set<RegistryEntry<Biome>> set = context.biomeSource().getBiomesInArea(i, context.chunkGenerator().getSeaLevel(), j, 29, context.noiseConfig().getMultiNoiseSampler());
        for (RegistryEntry<Biome> registryEntry : set) {
            if (registryEntry.isIn(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING)) continue;
            return Optional.empty();
        }
        return OceanMonumentStructure.getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, collector -> OceanMonumentStructure.addPieces(collector, context));
    }

    private static StructurePiece createBasePiece(ChunkPos pos, ChunkRandom random) {
        int i = pos.getStartX() - 29;
        int j = pos.getStartZ() - 29;
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        return new OceanMonumentGenerator.Base(random, i, j, direction);
    }

    private static void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        collector.addPiece(OceanMonumentStructure.createBasePiece(context.chunkPos(), context.random()));
    }

    public static StructurePiecesList modifyPiecesOnRead(ChunkPos pos, long worldSeed, StructurePiecesList pieces) {
        if (pieces.isEmpty()) {
            return pieces;
        }
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(RandomSeed.getSeed()));
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

    @Override
    public StructureType<?> getType() {
        return StructureType.OCEAN_MONUMENT;
    }
}

