/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Unmodifiable
 */
package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class ContainerLock {
    public static final ContainerLock EMPTY = new ContainerLock("");
    private final String key;

    public ContainerLock(String key) {
        this.key = key;
    }

    public boolean canOpen(ItemStack stack) {
        return this.key.isEmpty() || !stack.isEmpty() && stack.hasCustomName() && this.key.equals(stack.getName().getString());
    }

    public void toTag(CompoundTag tag) {
        if (!this.key.isEmpty()) {
            tag.putString("Lock", this.key);
        }
    }

    public static ContainerLock fromTag(CompoundTag tag) {
        if (tag.contains("Lock", 8)) {
            return new ContainerLock(tag.getString("Lock"));
        }
        return EMPTY;
    }
}

