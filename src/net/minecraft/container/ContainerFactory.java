/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ContainerFactory {
    @Nullable
    public Container createMenu(int var1, PlayerInventory var2, PlayerEntity var3);
}

