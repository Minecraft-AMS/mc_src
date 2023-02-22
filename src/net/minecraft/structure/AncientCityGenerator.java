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
import net.minecraft.structure.AncientCityOutskirtsGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

public class AncientCityGenerator {
    public static final RegistryEntry<StructurePool> CITY_CENTER = StructurePools.register(new StructurePool(new Identifier("ancient_city/city_center"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_1", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_2", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_3", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
        AncientCityOutskirtsGenerator.init();
    }
}

