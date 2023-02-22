/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

public interface Clearable {
    public void clear();

    public static void clear(@Nullable Object object) {
        if (object instanceof Clearable) {
            ((Clearable)object).clear();
        }
    }
}

