/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class StructurePools {
    public static final RegistryKey<StructurePool> EMPTY = RegistryKey.of(Registry.TEMPLATE_POOL_WORLDGEN, new Identifier("empty"));
    private static final StructurePool INVALID = StructurePools.register(new StructurePool(EMPTY.getValue(), EMPTY.getValue(), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructurePool.Projection.RIGID));

    public static StructurePool register(StructurePool templatePool) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_POOL, templatePool.getId(), templatePool);
    }

    public static StructurePool initDefaultPools() {
        BastionRemnantGenerator.init();
        PillagerOutpostGenerator.init();
        VillageGenerator.init();
        return INVALID;
    }

    static {
        StructurePools.initDefaultPools();
    }
}

