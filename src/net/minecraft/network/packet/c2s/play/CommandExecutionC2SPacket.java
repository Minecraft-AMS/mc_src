/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.packet.Packet;

public record CommandExecutionC2SPacket(String command, Instant timestamp, long salt, ArgumentSignatureDataMap argumentSignatures, LastSeenMessageList.Acknowledgment acknowledgment) implements Packet<ServerPlayPacketListener>
{
    public CommandExecutionC2SPacket(PacketByteBuf buf) {
        this(buf.readString(256), buf.readInstant(), buf.readLong(), new ArgumentSignatureDataMap(buf), new LastSeenMessageList.Acknowledgment(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.command, 256);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
        this.argumentSignatures.write(buf);
        this.acknowledgment.write(buf);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onCommandExecution(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CommandExecutionC2SPacket.class, "command;timeStamp;salt;argumentSignatures;lastSeenMessages", "command", "timestamp", "salt", "argumentSignatures", "acknowledgment"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CommandExecutionC2SPacket.class, "command;timeStamp;salt;argumentSignatures;lastSeenMessages", "command", "timestamp", "salt", "argumentSignatures", "acknowledgment"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CommandExecutionC2SPacket.class, "command;timeStamp;salt;argumentSignatures;lastSeenMessages", "command", "timestamp", "salt", "argumentSignatures", "acknowledgment"}, this, object);
    }
}

