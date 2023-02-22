/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;

public interface Saddleable {
    public boolean canBeSaddled();

    public void saddle(@Nullable SoundCategory var1);

    public boolean isSaddled();
}

