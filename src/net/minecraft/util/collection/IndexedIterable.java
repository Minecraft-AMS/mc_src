/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.collection;

import org.jetbrains.annotations.Nullable;

public interface IndexedIterable<T>
extends Iterable<T> {
    public int getRawId(T var1);

    @Nullable
    public T get(int var1);
}

