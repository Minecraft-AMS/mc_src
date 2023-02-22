/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.login;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginCompressionS2CPacket
implements Packet<ClientLoginPacketListener> {
    private int compressionThreshold;

    public LoginCompressionS2CPacket() {
    }

    public LoginCompressionS2CPacket(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.compressionThreshold = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.compressionThreshold);
    }

    @Override
    public void apply(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.onCompression(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}

