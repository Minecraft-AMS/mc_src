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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.PacketByteBuf;

public class InventoryS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int guiId;
    private List<ItemStack> slotStackList;

    public InventoryS2CPacket() {
    }

    public InventoryS2CPacket(int guiId, DefaultedList<ItemStack> slotStackList) {
        this.guiId = guiId;
        this.slotStackList = DefaultedList.ofSize(slotStackList.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.slotStackList.size(); ++i) {
            this.slotStackList.set(i, slotStackList.get(i).copy());
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.guiId = buf.readUnsignedByte();
        int i = buf.readShort();
        this.slotStackList = DefaultedList.ofSize(i, ItemStack.EMPTY);
        for (int j = 0; j < i; ++j) {
            this.slotStackList.set(j, buf.readItemStack());
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.guiId);
        buf.writeShort(this.slotStackList.size());
        for (ItemStack itemStack : this.slotStackList) {
            buf.writeItemStack(itemStack);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onInventory(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getGuiId() {
        return this.guiId;
    }

    @Environment(value=EnvType.CLIENT)
    public List<ItemStack> getSlotStacks() {
        return this.slotStackList;
    }
}

