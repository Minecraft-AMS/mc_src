/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 */
package net.minecraft.util.profiler;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

public interface ProfileLocationInfo {
    public long getTotalTime();

    public long getMaxTime();

    public long getVisitCount();

    public Object2LongMap<String> getCounts();
}

