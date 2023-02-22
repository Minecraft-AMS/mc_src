/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.c2s.login;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class LoginQueryResponseC2SPacket
implements Packet<ServerLoginPacketListener> {
    private int queryId;
    private PacketByteBuf response;

    public LoginQueryResponseC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public LoginQueryResponseC2SPacket(int i, @Nullable PacketByteBuf packetByteBuf) {
        this.queryId = i;
        this.response = packetByteBuf;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.queryId = buf.readVarInt();
        if (buf.readBoolean()) {
            int i = buf.readableBytes();
            if (i < 0 || i > 0x100000) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.response = new PacketByteBuf(buf.readBytes(i));
        } else {
            this.response = null;
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.queryId);
        if (this.response != null) {
            buf.writeBoolean(true);
            buf.writeBytes(this.response.copy());
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void apply(ServerLoginPacketListener serverLoginPacketListener) {
        serverLoginPacketListener.onQueryResponse(this);
    }
}

