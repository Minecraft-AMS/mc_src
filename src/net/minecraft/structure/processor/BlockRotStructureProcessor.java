/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
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
    private final float integrity;

    public BlockRotStructureProcessor(float integrity) {
        this.integrity = integrity;
    }

    public BlockRotStructureProcessor(Dynamic<?> dynamic) {
        this(dynamic.get("integrity").asFloat(1.0f));
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos pos, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData placementData) {
        Random random = placementData.getRandom(structureBlockInfo2.pos);
        if (this.integrity >= 1.0f || random.nextFloat() <= this.integrity) {
            return structureBlockInfo2;
        }
        return null;
    }

    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.BLOCK_ROT;
    }

    @Override
    protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("integrity"), (Object)dynamicOps.createFloat(this.integrity))));
    }
}

