/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;

public class StructurePoolRegistry {
    private final Map<Identifier, StructurePool> pools = Maps.newHashMap();

    public StructurePoolRegistry() {
        this.add(StructurePool.EMPTY);
    }

    public void add(StructurePool pool) {
        this.pools.put(pool.getId(), pool);
    }

    public StructurePool get(Identifier id) {
        StructurePool structurePool = this.pools.get(id);
        return structurePool != null ? structurePool : StructurePool.INVALID;
    }
}

