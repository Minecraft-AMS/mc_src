/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.query;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryPingC2SPacket
implements Packet<ServerQueryPacketListener> {
    private long startTime;

    public QueryPingC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public QueryPingC2SPacket(long l) {
        this.startTime = l;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.startTime = buf.readLong();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeLong(this.startTime);
    }

    @Override
    public void apply(ServerQueryPacketListener serverQueryPacketListener) {
        serverQueryPacketListener.onPing(this);
    }

    public long getStartTime() {
        return this.startTime;
    }
}

