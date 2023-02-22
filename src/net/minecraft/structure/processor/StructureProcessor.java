/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public abstract class StructureProcessor {
    @Nullable
    public abstract Structure.StructureBlockInfo process(CollisionView var1, BlockPos var2, Structure.StructureBlockInfo var3, Structure.StructureBlockInfo var4, StructurePlacementData var5);

    protected abstract StructureProcessorType getType();

    protected abstract <T> Dynamic<T> method_16666(DynamicOps<T> var1);

    public <T> Dynamic<T> method_16771(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(this.method_16666(dynamicOps).getValue(), dynamicOps.createString("processor_type"), dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getType()).toString())));
    }
}

