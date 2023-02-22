/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockRotStructureProcessor
extends StructureProcessor {
    public static final Codec<BlockRotStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryCodecs.entryList(Registry.BLOCK_KEY).optionalFieldOf("rottable_blocks").forGetter(processor -> processor.rottableBlocks), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("integrity").forGetter(processor -> Float.valueOf(processor.integrity))).apply((Applicative)instance, BlockRotStructureProcessor::new));
    private Optional<RegistryEntryList<Block>> rottableBlocks;
    private final float integrity;

    public BlockRotStructureProcessor(TagKey<Block> rottableBlocks, float integrity) {
        this(Optional.of(Registry.BLOCK.getOrCreateEntryList(rottableBlocks)), integrity);
    }

    public BlockRotStructureProcessor(float integrity) {
        this(Optional.empty(), integrity);
    }

    private BlockRotStructureProcessor(Optional<RegistryEntryList<Block>> rottableBlocks, float integrity) {
        this.integrity = integrity;
        this.rottableBlocks = rottableBlocks;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        Random random = data.getRandom(currentBlockInfo.pos);
        if (this.rottableBlocks.isPresent() && !originalBlockInfo.state.isIn(this.rottableBlocks.get()) || random.nextFloat() <= this.integrity) {
            return currentBlockInfo;
        }
        return null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}

