/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.util.registry.DynamicRegistryManager;

public record ChatMessageS2CPacket(SignedMessage message, MessageType.Serialized serializedParameters) implements Packet<ClientPlayPacketListener>
{
    public ChatMessageS2CPacket(PacketByteBuf buf) {
        this(new SignedMessage(buf), new MessageType.Serialized(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        this.message.write(buf);
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

    public Optional<MessageType.Parameters> getParameters(DynamicRegistryManager dynamicRegistryManager) {
        return this.serializedParameters.toParameters(dynamicRegistryManager);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatMessageS2CPacket.class, "message;chatType", "message", "serializedParameters"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatMessageS2CPacket.class, "message;chatType", "message", "serializedParameters"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatMessageS2CPacket.class, "message;chatType", "message", "serializedParameters"}, this, object);
    }
}

