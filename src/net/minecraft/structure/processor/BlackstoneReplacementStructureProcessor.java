/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.processor;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlackstoneReplacementStructureProcessor
extends StructureProcessor {
    public static final Codec<BlackstoneReplacementStructureProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public static final BlackstoneReplacementStructureProcessor INSTANCE = new BlackstoneReplacementStructureProcessor();
    private final Map<Block, Block> replacementMap = Util.make(Maps.newHashMap(), replacements -> {
        replacements.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
        replacements.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
        replacements.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
        replacements.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        replacements.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        replacements.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        replacements.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        replacements.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
        replacements.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        replacements.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        replacements.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        replacements.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        replacements.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        replacements.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        replacements.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        replacements.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        replacements.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        replacements.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        replacements.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        replacements.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        replacements.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
        replacements.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        replacements.put(Blocks.IRON_BARS, Blocks.CHAIN);
    });

    private BlackstoneReplacementStructureProcessor() {
    }

    @Override
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData data) {
        Block block = this.replacementMap.get(structureBlockInfo2.state.getBlock());
        if (block == null) {
            return structureBlockInfo2;
        }
        BlockState blockState = structureBlockInfo2.state;
        BlockState blockState2 = block.getDefaultState();
        if (blockState.contains(StairsBlock.FACING)) {
            blockState2 = (BlockState)blockState2.with(StairsBlock.FACING, blockState.get(StairsBlock.FACING));
        }
        if (blockState.contains(StairsBlock.HALF)) {
            blockState2 = (BlockState)blockState2.with(StairsBlock.HALF, blockState.get(StairsBlock.HALF));
        }
        if (blockState.contains(SlabBlock.TYPE)) {
            blockState2 = (BlockState)blockState2.with(SlabBlock.TYPE, blockState.get(SlabBlock.TYPE));
        }
        return new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockState2, structureBlockInfo2.nbt);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLACKSTONE_REPLACE;
    }
}

