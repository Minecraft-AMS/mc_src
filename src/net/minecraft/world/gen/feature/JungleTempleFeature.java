/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class JungleTempleFeature
extends StructureFeature<DefaultFeatureConfig> {
    public JungleTempleFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(JungleTempleFeature::canGenerate, JungleTempleFeature::addPieces));
    }

    private static <C extends FeatureConfig> boolean canGenerate(StructureGeneratorFactory.Context<C> context) {
        if (!context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG)) {
            return false;
        }
        return context.getMinCornerHeight(12, 15) >= context.chunkGenerator().getSeaLevel();
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
        collector.addPiece(new JungleTempleGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
    }
}

