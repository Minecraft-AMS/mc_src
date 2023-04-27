/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class WoodlandMansionStructure
extends Structure {
    public static final Codec<WoodlandMansionStructure> CODEC = WoodlandMansionStructure.createCodec(WoodlandMansionStructure::new);

    public WoodlandMansionStructure(Structure.Config config) {
        super(config);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (blockPos.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new Structure.StructurePosition(blockPos, collector -> this.addPieces((StructurePiecesCollector)collector, context, blockPos, blockRotation)));
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context, BlockPos pos, BlockRotation rotation) {
        LinkedList list = Lists.newLinkedList();
        WoodlandMansionGenerator.addPieces(context.structureTemplateManager(), pos, rotation, list, context.random());
        list.forEach(collector::addPiece);
    }

    @Override
    public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = world.getBottomY();
        BlockBox blockBox = pieces.getBoundingBox();
        int j = blockBox.getMinY();
        for (int k = box.getMinX(); k <= box.getMaxX(); ++k) {
            block1: for (int l = box.getMinZ(); l <= box.getMaxZ(); ++l) {
                mutable.set(k, j, l);
                if (world.isAir(mutable) || !blockBox.contains(mutable) || !pieces.contains(mutable)) continue;
                for (int m = j - 1; m > i; --m) {
                    mutable.setY(m);
                    if (!world.isAir(mutable) && !world.getBlockState(mutable).isLiquid()) continue block1;
                    world.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
                }
            }
        }
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.WOODLAND_MANSION;
    }
}

