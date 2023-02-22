/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;

public class BasicInventory
implements Inventory,
RecipeInputProvider {
    private final int size;
    private final DefaultedList<ItemStack> stackList;
    private List<InventoryListener> listeners;

    public BasicInventory(int size) {
        this.size = size;
        this.stackList = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public BasicInventory(ItemStack ... items) {
        this.size = items.length;
        this.stackList = DefaultedList.copyOf(ItemStack.EMPTY, items);
    }

    public void addListener(InventoryListener inventoryListener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(inventoryListener);
    }

    public void removeListener(InventoryListener inventoryListener) {
        this.listeners.remove(inventoryListener);
    }

    @Override
    public ItemStack getInvStack(int slot) {
        if (slot < 0 || slot >= this.stackList.size()) {
            return ItemStack.EMPTY;
        }
        return this.stackList.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.stackList, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    public ItemStack poll(Item item, int count) {
        ItemStack itemStack = new ItemStack(item, 0);
        for (int i = this.size - 1; i >= 0; --i) {
            ItemStack itemStack2 = this.getInvStack(i);
            if (!itemStack2.getItem().equals(item)) continue;
            int j = count - itemStack.getCount();
            ItemStack itemStack3 = itemStack2.split(j);
            itemStack.increment(itemStack3.getCount());
            if (itemStack.getCount() == count) break;
        }
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    public ItemStack add(ItemStack itemStack) {
        ItemStack itemStack2 = itemStack.copy();
        this.addToExistingSlot(itemStack2);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.addToNewSlot(itemStack2);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return itemStack2;
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        ItemStack itemStack = this.stackList.get(slot);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stackList.set(slot, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        this.stackList.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
            stack.setCount(this.getInvMaxStackAmount());
        }
        this.markDirty();
    }

    @Override
    public int getInvSize() {
        return this.size;
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.stackList) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void markDirty() {
        if (this.listeners != null) {
            for (InventoryListener inventoryListener : this.listeners) {
                inventoryListener.onInvChange(this);
            }
        }
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stackList.clear();
        this.markDirty();
    }

    @Override
    public void provideRecipeInputs(RecipeFinder recipeFinder) {
        for (ItemStack itemStack : this.stackList) {
            recipeFinder.addItem(itemStack);
        }
    }

    public String toString() {
        return this.stackList.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList()).toString();
    }

    private void addToNewSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getInvStack(i);
            if (!itemStack.isEmpty()) continue;
            this.setInvStack(i, stack.copy());
            stack.setCount(0);
            return;
        }
    }

    private void addToExistingSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getInvStack(i);
            if (!ItemStack.areItemsEqualIgnoreDamage(itemStack, stack)) continue;
            this.transfer(stack, itemStack);
            if (!stack.isEmpty()) continue;
            return;
        }
    }

    private void transfer(ItemStack source, ItemStack target) {
        int i = Math.min(this.getInvMaxStackAmount(), target.getMaxCount());
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.increment(j);
            source.decrement(j);
            this.markDirty();
        }
    }
}

