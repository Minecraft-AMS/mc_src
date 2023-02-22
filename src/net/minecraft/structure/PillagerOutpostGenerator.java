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

public class PillagerOutpostGenerator {
    public static final StructurePool field_26252 = StructurePools.register(new StructurePool(new Identifier("pillager_outpost/base_plates"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/base_plate"), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
    }

    static {
        StructurePools.register(new StructurePool(new Identifier("pillager_outpost/towers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30429((List<Function<StructurePool.Projection, ? extends StructurePoolElement>>)ImmutableList.of(StructurePoolElement.method_30425("pillager_outpost/watchtower"), StructurePoolElement.method_30426("pillager_outpost/watchtower_overgrown", StructureProcessorLists.OUTPOST_ROT))), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("pillager_outpost/feature_plates"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_plate"), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePools.register(new StructurePool(new Identifier("pillager_outpost/features"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_cage1"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_cage2"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_logs"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_tent1"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_tent2"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_targets"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30438(), (Object)6)), StructurePool.Projection.RIGID));
    }
}

