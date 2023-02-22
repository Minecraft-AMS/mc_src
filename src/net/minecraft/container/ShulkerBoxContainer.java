/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.ShulkerBoxSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxContainer
extends Container {
    private final Inventory inventory;

    public ShulkerBoxContainer(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new BasicInventory(27));
    }

    public ShulkerBoxContainer(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ContainerType.SHULKER_BOX, syncId);
        int l;
        int k;
        ShulkerBoxContainer.checkContainerSize(inventory, 27);
        this.inventory = inventory;
        inventory.onInvOpen(playerInventory.player);
        int i = 3;
        int j = 9;
        for (k = 0; k < 3; ++k) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new ShulkerBoxSlot(inventory, l + k * 9, 8 + l * 18, 18 + k * 18));
            }
        }
        for (k = 0; k < 3; ++k) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUseInv(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (invSlot < this.inventory.getInvSize() ? !this.insertItem(itemStack2, this.inventory.getInvSize(), this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.inventory.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onInvClose(player);
    }
}
