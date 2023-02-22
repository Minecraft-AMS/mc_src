/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.container.ArrayPropertyDelegate;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.container.Slot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

public class BeaconContainer
extends Container {
    private final Inventory paymentInv = new BasicInventory(1){

        @Override
        public boolean isValidInvStack(int slot, ItemStack stack) {
            return stack.getItem() == Items.EMERALD || stack.getItem() == Items.DIAMOND || stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.IRON_INGOT;
        }

        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }
    };
    private final SlotPayment paymentSlot;
    private final BlockContext context;
    private final PropertyDelegate propertyDelegate;

    public BeaconContainer(int syncId, Inventory inventory) {
        this(syncId, inventory, new ArrayPropertyDelegate(3), BlockContext.EMPTY);
    }

    public BeaconContainer(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, BlockContext blockContext) {
        super(ContainerType.BEACON, syncId);
        int k;
        BeaconContainer.checkContainerDataCount(propertyDelegate, 3);
        this.propertyDelegate = propertyDelegate;
        this.context = blockContext;
        this.paymentSlot = new SlotPayment(this.paymentInv, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addProperties(propertyDelegate);
        int i = 36;
        int j = 137;
        for (k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + k * 9 + 9, 36 + l * 18, 137 + k * 18));
            }
        }
        for (k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 36 + k * 18, 195));
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        if (player.world.isClient) {
            return;
        }
        ItemStack itemStack = this.paymentSlot.takeStack(this.paymentSlot.getMaxStackAmount());
        if (!itemStack.isEmpty()) {
            player.dropItem(itemStack, false);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return BeaconContainer.canUse(this.context, player, Blocks.BEACON);
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (invSlot == 0) {
                if (!this.insertItem(itemStack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (!this.paymentSlot.hasStack() && this.paymentSlot.canInsert(itemStack2) && itemStack2.getCount() == 1 ? !this.insertItem(itemStack2, 0, 1, false) : (invSlot >= 1 && invSlot < 28 ? !this.insertItem(itemStack2, 28, 37, false) : (invSlot >= 28 && invSlot < 37 ? !this.insertItem(itemStack2, 1, 28, false) : !this.insertItem(itemStack2, 1, 37, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    @Environment(value=EnvType.CLIENT)
    public int getProperties() {
        return this.propertyDelegate.get(0);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getPrimaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(1));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getSecondaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(2));
    }

    public void setEffects(int primaryEffectId, int secondaryEffectId) {
        if (this.paymentSlot.hasStack()) {
            this.propertyDelegate.set(1, primaryEffectId);
            this.propertyDelegate.set(2, secondaryEffectId);
            this.paymentSlot.takeStack(1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasPayment() {
        return !this.paymentInv.getInvStack(0).isEmpty();
    }

    class SlotPayment
    extends Slot {
        public SlotPayment(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            Item item = stack.getItem();
            return item == Items.EMERALD || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.IRON_INGOT;
        }

        @Override
        public int getMaxStackAmount() {
            return 1;
        }
    }
}

