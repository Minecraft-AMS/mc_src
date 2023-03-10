/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.structure.BastionBridgeData;
import net.minecraft.structure.BastionData;
import net.minecraft.structure.BastionTreasureData;
import net.minecraft.structure.BastionUnitsData;
import net.minecraft.structure.HoglinStableData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

public class BastionRemnantGenerator {
    public static final RegistryEntry<StructurePool> STRUCTURE_POOLS = StructurePools.register(new StructurePool(new Identifier("bastion/starts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/air_base", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/air_base", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/big_air_full", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/starting_pieces/entrance_base", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
        BastionUnitsData.init();
        HoglinStableData.init();
        BastionTreasureData.init();
        BastionBridgeData.init();
        BastionData.init();
    }
}

