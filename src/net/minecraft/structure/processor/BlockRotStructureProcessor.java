/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockRotStructureProcessor
extends StructureProcessor {
    public static final Codec<BlockRotStructureProcessor> CODEC = Codec.FLOAT.fieldOf("integrity").orElse((Object)Float.valueOf(1.0f)).xmap(BlockRotStructureProcessor::new, blockRotStructureProcessor -> Float.valueOf(blockRotStructureProcessor.integrity)).codec();
    private final float integrity;

    public BlockRotStructureProcessor(float integrity) {
        this.integrity = integrity;
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos blockPos, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        Random random = structurePlacementData.getRandom(structureBlockInfo2.pos);
        if (this.integrity >= 1.0f || random.nextFloat() <= this.integrity) {
            return structureBlockInfo2;
        }
        return null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}

