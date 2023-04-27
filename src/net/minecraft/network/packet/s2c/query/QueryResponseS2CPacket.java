/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.query;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.ServerMetadata;

public record QueryResponseS2CPacket(ServerMetadata metadata) implements Packet<ClientQueryPacketListener>
{
    public QueryResponseS2CPacket(PacketByteBuf buf) {
        this(buf.decodeAsJson(ServerMetadata.CODEC));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.encodeAsJson(ServerMetadata.CODEC, this.metadata);
    }

    @Override
    public void apply(ClientQueryPacketListener clientQueryPacketListener) {
        clientQueryPacketListener.onResponse(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{QueryResponseS2CPacket.class, "status", "metadata"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{QueryResponseS2CPacket.class, "status", "metadata"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{QueryResponseS2CPacket.class, "status", "metadata"}, this, object);
    }
}

