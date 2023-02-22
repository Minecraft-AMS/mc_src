/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class StructureProcessor {
    @Nullable
    public abstract Structure.StructureBlockInfo process(WorldView var1, BlockPos var2, BlockPos var3, Structure.StructureBlockInfo var4, Structure.StructureBlockInfo var5, StructurePlacementData var6);

    protected abstract StructureProcessorType<?> getType();
}

