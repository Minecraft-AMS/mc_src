/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ShipwreckFeature
extends StructureFeature<ShipwreckFeatureConfig> {
    public ShipwreckFeature(Codec<ShipwreckFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(ShipwreckFeature::canGenerate, ShipwreckFeature::addPieces));
    }

    private static boolean canGenerate(StructureGeneratorFactory.Context<ShipwreckFeatureConfig> context) {
        Heightmap.Type type = context.config().isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
        return context.isBiomeValid(type);
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<ShipwreckFeatureConfig> context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        ShipwreckGenerator.addParts(context.structureManager(), blockPos, blockRotation, collector, context.random(), context.config());
    }
}

