/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class FossilFeature
extends Feature<DefaultFeatureConfig> {
    private static final Identifier SPINE_1 = new Identifier("fossil/spine_1");
    private static final Identifier SPINE_2 = new Identifier("fossil/spine_2");
    private static final Identifier SPINE_3 = new Identifier("fossil/spine_3");
    private static final Identifier SPINE_4 = new Identifier("fossil/spine_4");
    private static final Identifier SPINE_1_COAL = new Identifier("fossil/spine_1_coal");
    private static final Identifier SPINE_2_COAL = new Identifier("fossil/spine_2_coal");
    private static final Identifier SPINE_3_COAL = new Identifier("fossil/spine_3_coal");
    private static final Identifier SPINE_4_COAL = new Identifier("fossil/spine_4_coal");
    private static final Identifier SKULL_1 = new Identifier("fossil/skull_1");
    private static final Identifier SKULL_2 = new Identifier("fossil/skull_2");
    private static final Identifier SKULL_3 = new Identifier("fossil/skull_3");
    private static final Identifier SKULL_4 = new Identifier("fossil/skull_4");
    private static final Identifier SKULL_1_COAL = new Identifier("fossil/skull_1_coal");
    private static final Identifier SKULL_2_COAL = new Identifier("fossil/skull_2_coal");
    private static final Identifier SKULL_3_COAL = new Identifier("fossil/skull_3_coal");
    private static final Identifier SKULL_4_COAL = new Identifier("fossil/skull_4_coal");
    private static final Identifier[] FOSSILS = new Identifier[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
    private static final Identifier[] COAL_FOSSILS = new Identifier[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};

    public FossilFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        int m;
        Random random2 = iWorld.getRandom();
        BlockRotation[] blockRotations = BlockRotation.values();
        BlockRotation blockRotation = blockRotations[random2.nextInt(blockRotations.length)];
        int i = random2.nextInt(FOSSILS.length);
        StructureManager structureManager = ((ServerWorld)iWorld.getWorld()).getSaveHandler().getStructureManager();
        Structure structure = structureManager.getStructureOrBlank(FOSSILS[i]);
        Structure structure2 = structureManager.getStructureOrBlank(COAL_FOSSILS[i]);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        BlockBox blockBox = new BlockBox(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 256, chunkPos.getEndZ());
        StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(blockRotation).setBoundingBox(blockBox).setRandom(random2).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        BlockPos blockPos2 = structure.method_15166(blockRotation);
        int j = random2.nextInt(16 - blockPos2.getX());
        int k = random2.nextInt(16 - blockPos2.getZ());
        int l = 256;
        for (m = 0; m < blockPos2.getX(); ++m) {
            for (int n = 0; n < blockPos2.getZ(); ++n) {
                l = Math.min(l, iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + m + j, blockPos.getZ() + n + k));
            }
        }
        m = Math.max(l - 15 - random2.nextInt(10), 10);
        BlockPos blockPos3 = structure.method_15167(blockPos.add(j, m, k), BlockMirror.NONE, blockRotation);
        BlockRotStructureProcessor blockRotStructureProcessor = new BlockRotStructureProcessor(0.9f);
        structurePlacementData.clearProcessors().addProcessor(blockRotStructureProcessor);
        structure.method_15172(iWorld, blockPos3, structurePlacementData, 4);
        structurePlacementData.removeProcessor(blockRotStructureProcessor);
        BlockRotStructureProcessor blockRotStructureProcessor2 = new BlockRotStructureProcessor(0.1f);
        structurePlacementData.clearProcessors().addProcessor(blockRotStructureProcessor2);
        structure2.method_15172(iWorld, blockPos3, structurePlacementData, 4);
        return true;
    }
}
