/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RuleStructureProcessor
extends StructureProcessor {
    public static final Codec<RuleStructureProcessor> CODEC = StructureProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleStructureProcessor::new, processor -> processor.rules).codec();
    private final ImmutableList<StructureProcessorRule> rules;

    public RuleStructureProcessor(List<? extends StructureProcessorRule> rules) {
        this.rules = ImmutableList.copyOf(rules);
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        Random random = Random.create(MathHelper.hashCode(currentBlockInfo.pos()));
        BlockState blockState = world.getBlockState(currentBlockInfo.pos());
        for (StructureProcessorRule structureProcessorRule : this.rules) {
            if (!structureProcessorRule.test(currentBlockInfo.state(), blockState, originalBlockInfo.pos(), currentBlockInfo.pos(), pivot, random)) continue;
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), structureProcessorRule.getOutputState(), structureProcessorRule.getOutputNbt(random, currentBlockInfo.nbt()));
        }
        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.RULE;
    }
}

