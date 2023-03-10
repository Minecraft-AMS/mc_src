/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class StrongholdFeature
extends StructureFeature<DefaultFeatureConfig> {
    public StrongholdFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(StrongholdFeature::canGenerate, StrongholdFeature::addPieces));
    }

    private static boolean canGenerate(StructureGeneratorFactory.Context<DefaultFeatureConfig> context) {
        return true;
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
        StrongholdGenerator.Start start;
        int i = 0;
        do {
            collector.clear();
            context.random().setCarverSeed(context.seed() + (long)i++, context.chunkPos().x, context.chunkPos().z);
            StrongholdGenerator.init();
            start = new StrongholdGenerator.Start(context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2));
            collector.addPiece(start);
            start.fillOpenings(start, collector, context.random());
            List<StructurePiece> list = start.pieces;
            while (!list.isEmpty()) {
                int j = context.random().nextInt(list.size());
                StructurePiece structurePiece = list.remove(j);
                structurePiece.fillOpenings(start, collector, context.random());
            }
            collector.shiftInto(context.chunkGenerator().getSeaLevel(), context.chunkGenerator().getMinimumY(), context.random(), 10);
        } while (collector.isEmpty() || start.portalRoom == null);
    }
}

