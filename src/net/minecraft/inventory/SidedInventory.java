/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface SidedInventory
extends Inventory {
    public int[] getInvAvailableSlots(Direction var1);

    public boolean canInsertInvStack(int var1, ItemStack var2, @Nullable Direction var3);

    public boolean canExtractInvStack(int var1, ItemStack var2, Direction var3);
}

