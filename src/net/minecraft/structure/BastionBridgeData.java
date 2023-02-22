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
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class BastionBridgeData {
    public static void init() {
    }

    static {
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/starting_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/starting_pieces/entrance", StructureProcessorLists.ENTRANCE_REPLACEMENT), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/starting_pieces/entrance_face", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/bridge_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/bridge_pieces/bridge", StructureProcessorLists.BRIDGE), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/legs"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/legs/leg_0", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/legs/leg_1", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/walls"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/walls/wall_base_0", StructureProcessorLists.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/walls/wall_base_1", StructureProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/ramparts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/ramparts/rampart_0", StructureProcessorLists.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/ramparts/rampart_1", StructureProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/rampart_plates"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/rampart_plates/plate_0", StructureProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/bridge/connectors"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/connectors/back_bridge_top", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/connectors/back_bridge_bottom", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
    }
}

