/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadC2SPacket
implements Packet<ServerPlayPacketListener> {
    public static final Identifier BRAND = new Identifier("brand");
    private Identifier channel;
    private PacketByteBuf data;

    public CustomPayloadC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public CustomPayloadC2SPacket(Identifier channel, PacketByteBuf packetByteBuf) {
        this.channel = channel;
        this.data = packetByteBuf;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.channel = buf.readIdentifier();
        int i = buf.readableBytes();
        if (i < 0 || i > Short.MAX_VALUE) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new PacketByteBuf(buf.readBytes(i));
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeIdentifier(this.channel);
        buf.writeBytes(this.data);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
}
