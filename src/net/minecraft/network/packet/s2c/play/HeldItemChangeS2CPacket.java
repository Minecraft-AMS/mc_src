/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HeldItemChangeS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int slot;

    public HeldItemChangeS2CPacket() {
    }

    public HeldItemChangeS2CPacket(int slot) {
        this.slot = slot;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.slot = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.slot);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onHeldItemChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }
}
