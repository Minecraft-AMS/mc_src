/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

public interface IndexedIterable<T>
extends Iterable<T> {
    @Nullable
    public T get(int var1);
}

