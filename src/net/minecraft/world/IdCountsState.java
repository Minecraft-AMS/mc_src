/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class IdCountsState
extends PersistentState {
    public static final String IDCOUNTS_KEY = "idcounts";
    private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap();

    public IdCountsState() {
        this.idCounts.defaultReturnValue(-1);
    }

    public static IdCountsState fromNbt(NbtCompound nbt) {
        IdCountsState idCountsState = new IdCountsState();
        for (String string : nbt.getKeys()) {
            if (!nbt.contains(string, 99)) continue;
            idCountsState.idCounts.put((Object)string, nbt.getInt(string));
        }
        return idCountsState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (Object2IntMap.Entry entry : this.idCounts.object2IntEntrySet()) {
            nbt.putInt((String)entry.getKey(), entry.getIntValue());
        }
        return nbt;
    }

    public int getNextMapId() {
        int i = this.idCounts.getInt((Object)"map") + 1;
        this.idCounts.put((Object)"map", i);
        this.markDirty();
        return i;
    }
}

