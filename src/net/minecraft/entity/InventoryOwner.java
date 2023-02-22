/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.util.annotation.Debug;

public interface InventoryOwner {
    @Debug
    public Inventory getInventory();
}

