/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.stat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;

public class StatHandler {
    protected final Object2IntMap<Stat<?>> statMap = Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap());

    public StatHandler() {
        this.statMap.defaultReturnValue(0);
    }

    public void increaseStat(PlayerEntity player, Stat<?> stat, int value) {
        int i = (int)Math.min((long)this.getStat(stat) + (long)value, Integer.MAX_VALUE);
        this.setStat(player, stat, i);
    }

    public void setStat(PlayerEntity player, Stat<?> stat, int value) {
        this.statMap.put(stat, value);
    }

    public <T> int getStat(StatType<T> type, T stat) {
        return type.hasStat(stat) ? this.getStat(type.getOrCreateStat(stat)) : 0;
    }

    public int getStat(Stat<?> stat) {
        return this.statMap.getInt(stat);
    }
}

