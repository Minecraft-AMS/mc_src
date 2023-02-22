/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.server.network.ServerPlayerEntity;

public record ChatMessageC2SPacket(String chatMessage, Instant timestamp, long salt, MessageSignatureData signature, boolean signedPreview, LastSeenMessageList.Acknowledgment acknowledgment) implements Packet<ServerPlayPacketListener>
{
    public ChatMessageC2SPacket(PacketByteBuf buf) {
        this(buf.readString(256), buf.readInstant(), buf.readLong(), new MessageSignatureData(buf), buf.readBoolean(), new LastSeenMessageList.Acknowledgment(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.chatMessage, 256);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
        this.signature.write(buf);
        buf.writeBoolean(this.signedPreview);
        this.acknowledgment.write(buf);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onChatMessage(this);
    }

    public MessageMetadata getMetadata(ServerPlayerEntity sender) {
        return new MessageMetadata(sender.getUuid(), this.timestamp, this.salt);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatMessageC2SPacket.class, "message;timeStamp;salt;signature;signedPreview;lastSeenMessages", "chatMessage", "timestamp", "salt", "signature", "signedPreview", "acknowledgment"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatMessageC2SPacket.class, "message;timeStamp;salt;signature;signedPreview;lastSeenMessages", "chatMessage", "timestamp", "salt", "signature", "signedPreview", "acknowledgment"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatMessageC2SPacket.class, "message;timeStamp;salt;signature;signedPreview;lastSeenMessages", "chatMessage", "timestamp", "salt", "signature", "signedPreview", "acknowledgment"}, this, object);
    }
}

