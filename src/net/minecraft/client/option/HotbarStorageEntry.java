/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.google.common.collect.ForwardingList;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class HotbarStorageEntry
extends ForwardingList<ItemStack> {
    private final DefaultedList<ItemStack> delegate = DefaultedList.ofSize(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);

    protected List<ItemStack> delegate() {
        return this.delegate;
    }

    public NbtList toNbtList() {
        NbtList nbtList = new NbtList();
        for (ItemStack itemStack : this.delegate()) {
            nbtList.add(itemStack.writeNbt(new NbtCompound()));
        }
        return nbtList;
    }

    public void readNbtList(NbtList list) {
        Collection list2 = this.delegate();
        for (int i = 0; i < list2.size(); ++i) {
            list2.set(i, ItemStack.fromNbt(list.getCompound(i)));
        }
    }

    public boolean isEmpty() {
        for (ItemStack itemStack : this.delegate()) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }
}

