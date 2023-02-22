/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record ChatMessageS2CPacket(UUID sender, int index, @Nullable MessageSignatureData signature, MessageBody.Serialized body, @Nullable Text unsignedContent, FilterMask filterMask, MessageType.Serialized serializedParameters) implements Packet<ClientPlayPacketListener>
{
    public ChatMessageS2CPacket(PacketByteBuf buf) {
        this(buf.readUuid(), buf.readVarInt(), (MessageSignatureData)buf.readNullable(MessageSignatureData::fromBuf), new MessageBody.Serialized(buf), (Text)buf.readNullable(PacketByteBuf::readText), FilterMask.readMask(buf), new MessageType.Serialized(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.sender);
        buf.writeVarInt(this.index);
        buf.writeNullable(this.signature, MessageSignatureData::write);
        this.body.write(buf);
        buf.writeNullable(this.unsignedContent, PacketByteBuf::writeText);
        FilterMask.writeMask(buf, this.filterMask);
        this.serializedParameters.write(buf);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onChatMessage(this);
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return true;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatMessageS2CPacket.class, "sender;index;signature;body;unsignedContent;filterMask;chatType", "sender", "index", "signature", "body", "unsignedContent", "filterMask", "serializedParameters"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatMessageS2CPacket.class, "sender;index;signature;body;unsignedContent;filterMask;chatType", "sender", "index", "signature", "body", "unsignedContent", "filterMask", "serializedParameters"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatMessageS2CPacket.class, "sender;index;signature;body;unsignedContent;filterMask;chatType", "sender", "index", "signature", "body", "unsignedContent", "filterMask", "serializedParameters"}, this, object);
    }
}

