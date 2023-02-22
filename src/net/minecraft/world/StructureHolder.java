/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import net.minecraft.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

public interface StructureHolder {
    @Nullable
    public StructureStart getStructureStart(String var1);

    public void setStructureStart(String var1, StructureStart var2);

    public LongSet getStructureReferences(String var1);

    public void addStructureReference(String var1, long var2);

    public Map<String, LongSet> getStructureReferences();

    public void setStructureReferences(Map<String, LongSet> var1);
}

