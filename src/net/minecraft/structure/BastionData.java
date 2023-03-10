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
import net.minecraft.util.Identifier;

public class BastionData {
    public static void init() {
    }

    static {
        StructurePools.register(new StructurePool(new Identifier("bastion/mobs/piglin"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin"), (Object)1), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/sword_piglin"), (Object)4), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/crossbow_piglin"), (Object)4), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/empty"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/mobs/hoglin"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/hoglin"), (Object)2), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/empty"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/blocks/gold"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofSingle("bastion/blocks/air"), (Object)3), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/blocks/gold"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePools.register(new StructurePool(new Identifier("bastion/mobs/piglin_melee"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin_always"), (Object)1), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin"), (Object)5), (Object)Pair.of(StructurePoolElement.ofSingle("bastion/mobs/sword_piglin"), (Object)1)), StructurePool.Projection.RIGID));
    }
}

