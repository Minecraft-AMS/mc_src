/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.collection.DefaultedList;

public class InventoryS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private List<ItemStack> contents;

    public InventoryS2CPacket() {
    }

    public InventoryS2CPacket(int syncId, DefaultedList<ItemStack> contents) {
        this.syncId = syncId;
        this.contents = DefaultedList.ofSize(contents.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.contents.size(); ++i) {
            this.contents.set(i, contents.get(i).copy());
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.syncId = buf.readUnsignedByte();
        int i = buf.readShort();
        this.contents = DefaultedList.ofSize(i, ItemStack.EMPTY);
        for (int j = 0; j < i; ++j) {
            this.contents.set(j, buf.readItemStack());
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeShort(this.contents.size());
        for (ItemStack itemStack : this.contents) {
            buf.writeItemStack(itemStack);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onInventory(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public List<ItemStack> getContents() {
        return this.contents;
    }
}

