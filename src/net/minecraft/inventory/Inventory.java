/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Inventory
extends Clearable {
    public static final int MAX_COUNT_PER_STACK = 64;
    public static final int field_42619 = 8;

    public int size();

    public boolean isEmpty();

    public ItemStack getStack(int var1);

    public ItemStack removeStack(int var1, int var2);

    public ItemStack removeStack(int var1);

    public void setStack(int var1, ItemStack var2);

    default public int getMaxCountPerStack() {
        return 64;
    }

    public void markDirty();

    public boolean canPlayerUse(PlayerEntity var1);

    default public void onOpen(PlayerEntity player) {
    }

    default public void onClose(PlayerEntity player) {
    }

    default public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    default public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return true;
    }

    default public int count(Item item) {
        int i = 0;
        for (int j = 0; j < this.size(); ++j) {
            ItemStack itemStack = this.getStack(j);
            if (!itemStack.getItem().equals(item)) continue;
            i += itemStack.getCount();
        }
        return i;
    }

    default public boolean containsAny(Set<Item> items) {
        return this.containsAny((ItemStack stack) -> !stack.isEmpty() && items.contains(stack.getItem()));
    }

    default public boolean containsAny(Predicate<ItemStack> predicate) {
        for (int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!predicate.test(itemStack)) continue;
            return true;
        }
        return false;
    }

    public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player) {
        return Inventory.canPlayerUse(blockEntity, player, 8);
    }

    public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player, int range) {
        World world = blockEntity.getWorld();
        BlockPos blockPos = blockEntity.getPos();
        if (world == null) {
            return false;
        }
        if (world.getBlockEntity(blockPos) != blockEntity) {
            return false;
        }
        return player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= (double)(range * range);
    }
}

