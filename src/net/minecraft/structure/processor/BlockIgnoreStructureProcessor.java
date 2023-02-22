/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class BlockIgnoreStructureProcessor
extends StructureProcessor {
    public static final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.STRUCTURE_BLOCK));
    public static final BlockIgnoreStructureProcessor IGNORE_AIR = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.AIR));
    public static final BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.AIR, (Object)Blocks.STRUCTURE_BLOCK));
    private final ImmutableList<Block> blocks;

    public BlockIgnoreStructureProcessor(List<Block> list) {
        this.blocks = ImmutableList.copyOf(list);
    }

    public BlockIgnoreStructureProcessor(Dynamic<?> dynamic2) {
        this(dynamic2.get("blocks").asList(dynamic -> BlockState.deserialize(dynamic).getBlock()));
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(CollisionView world, BlockPos pos, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData placementData) {
        if (this.blocks.contains((Object)structureBlockInfo2.state.getBlock())) {
            return null;
        }
        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }

    @Override
    protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("blocks"), (Object)dynamicOps.createList(this.blocks.stream().map(block -> BlockState.serialize(dynamicOps, block.getDefaultState()).getValue())))));
    }
}
