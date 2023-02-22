/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
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
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SinglePoolElement
extends StructurePoolElement {
    private static final Codec<Either<Identifier, Structure>> field_24951 = Codec.of(SinglePoolElement::method_28877, (Decoder)Identifier.CODEC.map(Either::left));
    public static final Codec<SinglePoolElement> field_24952 = RecordCodecBuilder.create(instance -> instance.group(SinglePoolElement.method_28882(), SinglePoolElement.method_28880(), SinglePoolElement.method_28883()).apply((Applicative)instance, SinglePoolElement::new));
    protected final Either<Identifier, Structure> location;
    protected final RegistryEntry<StructureProcessorList> processors;

    private static <T> DataResult<T> method_28877(Either<Identifier, Structure> either, DynamicOps<T> dynamicOps, T object) {
        Optional optional = either.left();
        if (!optional.isPresent()) {
            return DataResult.error((String)"Can not serialize a runtime pool element");
        }
        return Identifier.CODEC.encode((Object)((Identifier)optional.get()), dynamicOps, object);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, RegistryEntry<StructureProcessorList>> method_28880() {
        return StructureProcessorType.REGISTRY_CODEC.fieldOf("processors").forGetter(singlePoolElement -> singlePoolElement.processors);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<Identifier, Structure>> method_28882() {
        return field_24951.fieldOf("location").forGetter(singlePoolElement -> singlePoolElement.location);
    }

    protected SinglePoolElement(Either<Identifier, Structure> location, RegistryEntry<StructureProcessorList> registryEntry, StructurePool.Projection projection) {
        super(projection);
        this.location = location;
        this.processors = registryEntry;
    }

    public SinglePoolElement(Structure structure) {
        this((Either<Identifier, Structure>)Either.right((Object)structure), StructureProcessorLists.EMPTY, StructurePool.Projection.RIGID);
    }

    @Override
    public Vec3i getStart(StructureManager structureManager, BlockRotation rotation) {
        Structure structure = this.getStructure(structureManager);
        return structure.getRotatedSize(rotation);
    }

    private Structure getStructure(StructureManager structureManager) {
        return (Structure)this.location.map(structureManager::getStructureOrBlank, Function.identity());
    }

    public List<Structure.StructureBlockInfo> getDataStructureBlocks(StructureManager structureManager, BlockPos pos, BlockRotation rotation, boolean mirroredAndRotated) {
        Structure structure = this.getStructure(structureManager);
        List<Structure.StructureBlockInfo> list = structure.getInfosForBlock(pos, new StructurePlacementData().setRotation(rotation), Blocks.STRUCTURE_BLOCK, mirroredAndRotated);
        ArrayList list2 = Lists.newArrayList();
        for (Structure.StructureBlockInfo structureBlockInfo : list) {
            StructureBlockMode structureBlockMode;
            if (structureBlockInfo.nbt == null || (structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.nbt.getString("mode"))) != StructureBlockMode.DATA) continue;
            list2.add(structureBlockInfo);
        }
        return list2;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        Structure structure = this.getStructure(structureManager);
        List<Structure.StructureBlockInfo> list = structure.getInfosForBlock(pos, new StructurePlacementData().setRotation(rotation), Blocks.JIGSAW, true);
        Collections.shuffle(list, random);
        return list;
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        Structure structure = this.getStructure(structureManager);
        return structure.calculateBoundingBox(new StructurePlacementData().setRotation(rotation), pos);
    }

    @Override
    public boolean generate(StructureManager structureManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos blockPos, BlockRotation rotation, BlockBox box, Random random, boolean keepJigsaws) {
        StructurePlacementData structurePlacementData;
        Structure structure = this.getStructure(structureManager);
        if (structure.place(world, pos, blockPos, structurePlacementData = this.createPlacementData(rotation, box, keepJigsaws), random, 18)) {
            List<Structure.StructureBlockInfo> list = Structure.process(world, pos, blockPos, structurePlacementData, this.getDataStructureBlocks(structureManager, pos, rotation, false));
            for (Structure.StructureBlockInfo structureBlockInfo : list) {
                this.method_16756(world, structureBlockInfo, pos, rotation, random, box);
            }
            return true;
        }
        return false;
    }

    protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, boolean keepJigsaws) {
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setBoundingBox(box);
        structurePlacementData.setRotation(rotation);
        structurePlacementData.setUpdateNeighbors(true);
        structurePlacementData.setIgnoreEntities(false);
        structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        structurePlacementData.setInitializeMobs(true);
        if (!keepJigsaws) {
            structurePlacementData.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
        }
        this.processors.value().getList().forEach(structurePlacementData::addProcessor);
        this.getProjection().getProcessors().forEach(structurePlacementData::addProcessor);
        return structurePlacementData;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.SINGLE_POOL_ELEMENT;
    }

    public String toString() {
        return "Single[" + this.location + "]";
    }
}

