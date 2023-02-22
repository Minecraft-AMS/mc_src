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

public class ContainerPropertyUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int propertyId;
    private int value;

    public ContainerPropertyUpdateS2CPacket() {
    }

    public ContainerPropertyUpdateS2CPacket(int syncId, int propertyId, int value) {
        this.syncId = syncId;
        this.propertyId = propertyId;
        this.value = value;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onContainerPropertyUpdate(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.syncId = buf.readUnsignedByte();
        this.propertyId = buf.readShort();
        this.value = buf.readShort();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeShort(this.propertyId);
        buf.writeShort(this.value);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getPropertyId() {
        return this.propertyId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getValue() {
        return this.value;
    }
}

