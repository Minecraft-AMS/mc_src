/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.util;

import com.mojang.datafixers.types.DynamicOps;

public interface DynamicSerializable {
    public <T> T serialize(DynamicOps<T> var1);
}

