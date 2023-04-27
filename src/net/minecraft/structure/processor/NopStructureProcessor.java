/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;

public class NopStructureProcessor
extends StructureProcessor {
    public static final Codec<NopStructureProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

    private NopStructureProcessor() {
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.NOP;
    }
}

