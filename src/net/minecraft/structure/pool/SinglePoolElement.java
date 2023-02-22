/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class SinglePoolElement
extends StructurePoolElement {
    protected final Identifier location;
    protected final ImmutableList<StructureProcessor> processors;

    @Deprecated
    public SinglePoolElement(String location, List<StructureProcessor> processors) {
        this(location, processors, StructurePool.Projection.RIGID);
    }

    public SinglePoolElement(String string, List<StructureProcessor> list, StructurePool.Projection projection) {
        super(projection);
        this.location = new Identifier(string);
        this.processors = ImmutableList.copyOf(list);
    }

    @Deprecated
    public SinglePoolElement(String string) {
        this(string, (List<StructureProcessor>)ImmutableList.of());
    }

    public SinglePoolElement(Dynamic<?> dynamic2) {
        super(dynamic2);
        this.location = new Identifier(dynamic2.get("location").asString(""));
        this.processors = ImmutableList.copyOf((Collection)dynamic2.get("processors").asList(dynamic -> DynamicDeserializer.deserialize(dynamic, Registry.STRUCTURE_PROCESSOR, "processor_type", NopStructureProcessor.INSTANCE)));
    }

    public List<Structure.StructureBlockInfo> method_16614(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
        Structure structure = structureManager.getStructureOrBlank(this.location);
        List<Structure.StructureBlockInfo> list = structure.method_15165(blockPos, new StructurePlacementData().setRotation(blockRotation), Blocks.STRUCTURE_BLOCK, bl);
        ArrayList list2 = Lists.newArrayList();
        for (Structure.StructureBlockInfo structureBlockInfo : list) {
            StructureBlockMode structureBlockMode;
            if (structureBlockInfo.tag == null || (structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"))) != StructureBlockMode.DATA) continue;
            list2.add(structureBlockInfo);
        }
        return list2;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        Structure structure = structureManager.getStructureOrBlank(this.location);
        List<Structure.StructureBlockInfo> list = structure.method_15165(pos, new StructurePlacementData().setRotation(rotation), Blocks.JIGSAW, true);
        Collections.shuffle(list, random);
        return list;
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        Structure structure = structureManager.getStructureOrBlank(this.location);
        return structure.calculateBoundingBox(new StructurePlacementData().setRotation(rotation), pos);
    }

    @Override
    public boolean generate(StructureManager structureManager, IWorld world, BlockPos pos, BlockRotation rotation, BlockBox boundingBox, Random random) {
        StructurePlacementData structurePlacementData;
        Structure structure = structureManager.getStructureOrBlank(this.location);
        if (structure.method_15172(world, pos, structurePlacementData = this.method_16616(rotation, boundingBox), 18)) {
            List<Structure.StructureBlockInfo> list = Structure.process(world, pos, structurePlacementData, this.method_16614(structureManager, pos, rotation, false));
            for (Structure.StructureBlockInfo structureBlockInfo : list) {
                this.method_16756(world, structureBlockInfo, pos, rotation, random, boundingBox);
            }
            return true;
        }
        return false;
    }

    protected StructurePlacementData method_16616(BlockRotation blockRotation, BlockBox blockBox) {
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setBoundingBox(blockBox);
        structurePlacementData.setRotation(blockRotation);
        structurePlacementData.method_15131(true);
        structurePlacementData.setIgnoreEntities(false);
        structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        structurePlacementData.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
        this.processors.forEach(structurePlacementData::addProcessor);
        this.getProjection().getProcessors().forEach(structurePlacementData::addProcessor);
        return structurePlacementData;
    }

    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.SINGLE_POOL_ELEMENT;
    }

    @Override
    public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("location"), (Object)dynamicOps.createString(this.location.toString()), (Object)dynamicOps.createString("processors"), (Object)dynamicOps.createList(this.processors.stream().map(structureProcessor -> structureProcessor.method_16771(dynamicOps).getValue())))));
    }

    public String toString() {
        return "Single[" + this.location + "]";
    }
}

